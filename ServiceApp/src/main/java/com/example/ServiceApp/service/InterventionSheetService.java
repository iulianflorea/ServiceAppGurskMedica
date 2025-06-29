package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.CustomerDto;
import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.*;
import com.example.ServiceApp.mapper.InterventionSheetMapper;
import com.example.ServiceApp.repository.CustomerRepository;
import com.example.ServiceApp.repository.EmployeeRepository;
import com.example.ServiceApp.repository.EquipmentRepository;
import com.example.ServiceApp.repository.InterventionSheetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class InterventionSheetService {

    private final InterventionSheetRepository interventionSheetRepository;
    private final InterventionSheetMapper interventionSheetMapper;
    private final EquipmentRepository equipmentRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;


    public InterventionSheetService(InterventionSheetRepository interventionSheetRepository, InterventionSheetMapper interventionSheetMapper, EquipmentRepository equipmentRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository) throws JsonProcessingException {
        this.interventionSheetRepository = interventionSheetRepository;
        this.interventionSheetMapper = interventionSheetMapper;
        this.equipmentRepository = equipmentRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
    }

    public InterventionSheetDto create(InterventionSheetDto interventionSheetDto) {
        InterventionSheet interventionSheetToBeSaved = interventionSheetMapper.toInterventionSheet(interventionSheetDto);
        Equipment equipment = equipmentRepository.findById(interventionSheetDto.getEquipmentId()).orElseThrow();
        Customer customer = customerRepository.findById(interventionSheetDto.getCustomerId()).orElseThrow();
        Employee employee = employeeRepository.findById(interventionSheetDto.getEmployeeId()).orElseThrow();
        interventionSheetToBeSaved.setEquipmentId(equipment.getId());
        interventionSheetToBeSaved.setCustomerId(customer.getId());
        interventionSheetToBeSaved.setEmployeeId(employee.getId());
        interventionSheetToBeSaved.setDataOfExpireWarranty(calculeazaExpirareaGarantiei(interventionSheetDto.getDateOfIntervention(),interventionSheetDto.getYearsOfWarranty()));
        if (interventionSheetDto.getId() == null) {
            InterventionSheet interventionSheetSaved = interventionSheetRepository.save(interventionSheetToBeSaved);
            System.out.println(interventionSheetDto.getDateOfIntervention());
            return interventionSheetMapper.toDto(interventionSheetSaved);
        } else {
            update(interventionSheetDto);
        }
        return interventionSheetMapper.toDto(interventionSheetToBeSaved);
    }

    public InterventionSheetDto findById(Long id) {
        InterventionSheet interventionSheet = interventionSheetRepository.findById(id).orElseThrow();
        return interventionSheetMapper.toDto(interventionSheet);
    }

    public List<InterventionSheetDto> findAll() {
        List<InterventionSheet> interventionSheetList = interventionSheetRepository.findAll();
        return interventionSheetMapper.toDtoList(interventionSheetList);
    }

    public InterventionSheetDto update(InterventionSheetDto interventionSheetDto) {
        InterventionSheet interventionSheet = interventionSheetRepository.findById(interventionSheetDto.getId()).orElseThrow();
        interventionSheet.setEmployeeId(interventionSheetDto.getEmployeeId());
        interventionSheet.setDateOfIntervention(interventionSheetDto.getDateOfIntervention());
        interventionSheet.setCustomerId(interventionSheetDto.getCustomerId());
        interventionSheet.setEquipmentId(interventionSheetDto.getEquipmentId());
        interventionSheet.setFixed(interventionSheetDto.getFixed());
        interventionSheet.setSerialNumber(interventionSheetDto.getSerialNumber());
        interventionSheet.setEngineerNote(interventionSheetDto.getEngineerNote());
        interventionSheet.setNoticed(interventionSheetDto.getNoticed());
        interventionSheet.setTypeOfIntervention(interventionSheetDto.getTypeOfIntervention());
        InterventionSheet interventionSheetSaved = interventionSheetRepository.save(interventionSheet);
        return interventionSheetMapper.toDto(interventionSheetSaved);
    }

    public void delete(Long id) {
        interventionSheetRepository.deleteById(id);
    }



    public static LocalDate calculeazaExpirareaGarantiei(LocalDate dataAchizitie, int durataGarantie) {
        return dataAchizitie.plusYears(durataGarantie);
    }

//    public List<InterventionSheetDto> search(String keyword) {
//
//        LocalDate dateOfIntervention = null;
//        LocalDate dataOfExpireWarranty = null;
//        Integer yearsOfWarranty = null;
//
//        // Încearcă să parsezi keyword ca LocalDate
//        try {
//            dateOfIntervention = LocalDate.parse(keyword);
//            dataOfExpireWarranty = dateOfIntervention;
//        } catch (DateTimeParseException e) {
//            // Dacă nu poate fi convertit în LocalDate, lăsăm valorile ca null
//        }
//        List<InterventionSheet> interventionSheetList =  interventionSheetRepository.searchIntervention(keyword, dateOfIntervention, dataOfExpireWarranty, yearsOfWarranty);
//
//        return interventionSheetMapper.toDtoList(interventionSheetList);
//    }

//    public List<InterventionSheetDto> search(String keyword) {
//        LocalDate dateOfIntervention = null;
//        LocalDate dataOfExpireWarranty = null;
//        Integer yearsOfWarranty = null;
//
//        // Încearcă să parsezi keyword ca LocalDate
//        try {
//            dateOfIntervention = LocalDate.parse(keyword);
//            dataOfExpireWarranty = dateOfIntervention;
//        } catch (DateTimeParseException ignored) {}
//
//        // Verificăm că este numeric și scurt (ex. 1–10), nu serial number
//        if (keyword.matches("^\\d{1,2}$")) {
//            yearsOfWarranty = Integer.parseInt(keyword);
//        }
//
//        List<InterventionSheet> interventionSheetList = interventionSheetRepository.searchIntervention(
//                keyword,
//                dateOfIntervention,
//                dataOfExpireWarranty,
//                yearsOfWarranty
//        );
//
//        return interventionSheetMapper.toDtoList(interventionSheetList);
//    }

    public List<InterventionSheetDto> search(String keyword) {
        List<InterventionSheet> interventionSheetList = interventionSheetRepository.searchIntervention(keyword);
        return interventionSheetMapper.toDtoList(interventionSheetList);
    }


    public List<InterventionSheetDto> getTasksLoggedUser(String jwtToken) {
        String userId = parseJWT(jwtToken);
        List<InterventionSheet> allTasks = interventionSheetRepository.findAll();
        List<InterventionSheet> userInterventions = new ArrayList<>();
        for (InterventionSheet interventionSheet : allTasks) {
            if (interventionSheet.getEmployee().getName().equals(userId)) {
                userInterventions.add(interventionSheet);
            }
        }
        return interventionSheetMapper.toDtoList(userInterventions);
    }

    private String parseJWT(String jwtToken) {
        String secretKey = "MzBxJaeWRwNubD+ZS4/zVgK9GPqH8A3Nns2gXmPvMUfAsqtsowARlphR8Z4FwYoKPDl0Sk/ahgauCJGu7bGz4Q==";
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken)
                    .getBody();
            String userId = claims.getSubject();
            return userId;
        } catch (Exception e) {
            return null;
        }
    }


    ObjectMapper objectMapper = new ObjectMapper();
    String enumAsJson = objectMapper.writeValueAsString(TypeOfIntervention.values());



}
