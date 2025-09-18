package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.*;
import com.example.ServiceApp.mapper.InterventionSheetMapper;
import com.example.ServiceApp.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InterventionSheetService {

    private final InterventionSheetRepository interventionSheetRepository;
    private final InterventionSheetMapper interventionSheetMapper;
    private final EquipmentRepository equipmentRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;


    public InterventionSheetService(InterventionSheetRepository interventionSheetRepository, InterventionSheetMapper interventionSheetMapper, EquipmentRepository equipmentRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository, UserRepository userRepository) throws JsonProcessingException {
        this.interventionSheetRepository = interventionSheetRepository;
        this.interventionSheetMapper = interventionSheetMapper;
        this.equipmentRepository = equipmentRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    public InterventionSheetDto create(InterventionSheetDto interventionSheetDto) {
        InterventionSheet interventionSheetToBeSaved = interventionSheetMapper.toInterventionSheet(interventionSheetDto);
        Equipment equipment = equipmentRepository.findById(interventionSheetDto.getEquipmentId()).orElseThrow();
        Customer customer = customerRepository.findById(interventionSheetDto.getCustomerId()).orElseThrow();
        User user = userRepository.findById(interventionSheetDto.getEmployeeId()).orElseThrow();
        interventionSheetToBeSaved.setEquipmentId(equipment.getId());
        interventionSheetToBeSaved.setCustomerId(customer.getId());
        interventionSheetToBeSaved.setEmployeeId(user.getId());
        interventionSheetToBeSaved.setSignatureBase64(interventionSheetDto.getSignatureBase64());
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
    Pageable topFifty = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "dateOfIntervention"));
    Page<InterventionSheet> interventionSheetPage = interventionSheetRepository.findAllByOrderByDateOfInterventionDesc(topFifty);
    return interventionSheetMapper.toDtoList(interventionSheetPage.getContent());
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
        return dataAchizitie.plusMonths(durataGarantie);
    }



    public List<InterventionSheetDto> search(String keyword) {
        List<InterventionSheet> interventionSheetList = interventionSheetRepository.searchIntervention(keyword);
        return interventionSheetMapper.toDtoList(interventionSheetList);
    }


    public List<InterventionSheetDto> getTasksLoggedUser(String jwtToken) {
        String userId = parseJWT(jwtToken);
//        List<InterventionSheet> allTasks = interventionSheetRepository.findAll();
        Pageable topFifty = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "dateOfIntervention"));
        Page<InterventionSheet> allTasks = interventionSheetRepository.findAllByOrderByDateOfInterventionDesc(topFifty);
        List<InterventionSheet> userInterventions = new ArrayList<>();
        for (InterventionSheet interventionSheet : allTasks) {
            if (interventionSheet.getEmployee().getUsername().equals(userId)) {
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
