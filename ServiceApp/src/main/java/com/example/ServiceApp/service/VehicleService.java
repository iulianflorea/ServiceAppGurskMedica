package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.*;
import com.example.ServiceApp.entity.*;
import com.example.ServiceApp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleRevisionRepository revisionRepository;
    private final VehicleItpRepository itpRepository;
    private final VehicleInsuranceRepository insuranceRepository;
    private final VehicleEventRepository eventRepository;

    private static final Path UPLOADS = Paths.get("uploads");

    // ── Vehicle CRUD ────────────────────────────────────────────────

    public VehicleDto create(VehicleDto dto, MultipartFile photo) throws IOException {
        Vehicle vehicle = toEntity(dto);
        vehicle = vehicleRepository.save(vehicle);

        if (photo != null && !photo.isEmpty()) {
            String photoName = savePhoto(vehicle.getId(), photo);
            vehicle.setPhotoName(photoName);
            vehicle = vehicleRepository.save(vehicle);
        }

        return toDto(vehicle);
    }

    public List<VehicleDto> findAll() {
        return vehicleRepository.findAll().stream()
                .map(v -> {
                    VehicleDto dto = toDto(v);
                    dto.setItpList(itpRepository.findByVehicleId(v.getId()).stream()
                            .map(this::toItpDto).collect(Collectors.toList()));
                    dto.setInsuranceList(insuranceRepository.findByVehicleId(v.getId()).stream()
                            .map(this::toInsuranceDto).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public VehicleDto findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));
        VehicleDto dto = toDto(vehicle);
        dto.setRevisions(revisionRepository.findByVehicleId(id).stream()
                .map(this::toRevisionDto).collect(Collectors.toList()));
        dto.setItpList(itpRepository.findByVehicleId(id).stream()
                .map(this::toItpDto).collect(Collectors.toList()));
        dto.setInsuranceList(insuranceRepository.findByVehicleId(id).stream()
                .map(this::toInsuranceDto).collect(Collectors.toList()));
        dto.setEvents(eventRepository.findByVehicleId(id).stream()
                .map(this::toEventDto).collect(Collectors.toList()));
        return dto;
    }

    public VehicleDto update(Long id, VehicleDto dto, MultipartFile photo) throws IOException {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));

        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setVin(dto.getVin());
        vehicle.setMake(dto.getMake());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setColor(dto.getColor());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setEngineCapacity(dto.getEngineCapacity());
        vehicle.setPower(dto.getPower());
        vehicle.setCurrentKm(dto.getCurrentKm());
        vehicle.setNotes(dto.getNotes());
        vehicle.setUserId(dto.getUserId());

        if (photo != null && !photo.isEmpty()) {
            String photoName = savePhoto(id, photo);
            vehicle.setPhotoName(photoName);
        }

        vehicle = vehicleRepository.save(vehicle);
        return toDto(vehicle);
    }

    public void delete(Long id) {
        // Delete all child records first
        itpRepository.deleteAll(itpRepository.findByVehicleId(id));
        insuranceRepository.deleteAll(insuranceRepository.findByVehicleId(id));
        revisionRepository.deleteAll(revisionRepository.findByVehicleId(id));
        eventRepository.deleteAll(eventRepository.findByVehicleId(id));

        // Delete all uploaded files (photo + documents)
        try {
            Path vehicleDir = UPLOADS.resolve("vehicle-" + id);
            if (Files.exists(vehicleDir)) {
                Files.walk(vehicleDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> { try { Files.delete(p); } catch (IOException ignored) {} });
            }
        } catch (IOException ignored) {}

        vehicleRepository.deleteById(id);
    }

    public List<VehicleDto> search(String keyword) {
        return vehicleRepository.searchByKeyword(keyword).stream()
                .map(v -> {
                    VehicleDto dto = toDto(v);
                    dto.setItpList(itpRepository.findByVehicleId(v.getId()).stream()
                            .map(this::toItpDto).collect(Collectors.toList()));
                    dto.setInsuranceList(insuranceRepository.findByVehicleId(v.getId()).stream()
                            .map(this::toInsuranceDto).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deletePhoto(Long id) throws IOException {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));
        if (vehicle.getPhotoName() != null) {
            Path photoPath = UPLOADS.resolve("vehicle-" + id).resolve("photo").resolve(vehicle.getPhotoName());
            Files.deleteIfExists(photoPath);
            vehicle.setPhotoName(null);
            vehicleRepository.save(vehicle);
        }
    }

    // ── Revision ────────────────────────────────────────────────────

    public VehicleRevisionDto addRevision(Long vehicleId, VehicleRevisionDto dto) {
        VehicleRevision entity = VehicleRevision.builder()
                .vehicleId(vehicleId)
                .date(dto.getDate())
                .km(dto.getKm())
                .cost(dto.getCost())
                .description(dto.getDescription())
                .build();
        return toRevisionDto(revisionRepository.save(entity));
    }

    public VehicleRevisionDto updateRevision(Long id, VehicleRevisionDto dto) {
        VehicleRevision entity = revisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Revision not found: " + id));
        entity.setDate(dto.getDate());
        entity.setKm(dto.getKm());
        entity.setCost(dto.getCost());
        entity.setDescription(dto.getDescription());
        return toRevisionDto(revisionRepository.save(entity));
    }

    public void deleteRevision(Long id) {
        revisionRepository.deleteById(id);
    }

    // ── ITP ─────────────────────────────────────────────────────────

    public VehicleItpDto addItp(Long vehicleId, VehicleItpDto dto) {
        VehicleItp entity = VehicleItp.builder()
                .vehicleId(vehicleId)
                .date(dto.getDate())
                .validityMonths(dto.getValidityMonths())
                .expiryDate(dto.getDate() != null && dto.getValidityMonths() != null
                        ? dto.getDate().plusMonths(dto.getValidityMonths()) : null)
                .cost(dto.getCost())
                .build();
        return toItpDto(itpRepository.save(entity));
    }

    public VehicleItpDto updateItp(Long id, VehicleItpDto dto) {
        VehicleItp entity = itpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ITP not found: " + id));
        entity.setDate(dto.getDate());
        entity.setValidityMonths(dto.getValidityMonths());
        entity.setExpiryDate(dto.getDate() != null && dto.getValidityMonths() != null
                ? dto.getDate().plusMonths(dto.getValidityMonths()) : null);
        entity.setCost(dto.getCost());
        return toItpDto(itpRepository.save(entity));
    }

    public void deleteItp(Long id) {
        itpRepository.deleteById(id);
    }

    // ── Insurance ───────────────────────────────────────────────────

    public VehicleInsuranceDto addInsurance(Long vehicleId, VehicleInsuranceDto dto) {
        VehicleInsurance entity = VehicleInsurance.builder()
                .vehicleId(vehicleId)
                .date(dto.getDate())
                .validityMonths(dto.getValidityMonths())
                .expiryDate(dto.getDate() != null && dto.getValidityMonths() != null
                        ? dto.getDate().plusMonths(dto.getValidityMonths()) : null)
                .insurer(dto.getInsurer())
                .policyNumber(dto.getPolicyNumber())
                .cost(dto.getCost())
                .build();
        return toInsuranceDto(insuranceRepository.save(entity));
    }

    public VehicleInsuranceDto updateInsurance(Long id, VehicleInsuranceDto dto) {
        VehicleInsurance entity = insuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance not found: " + id));
        entity.setDate(dto.getDate());
        entity.setValidityMonths(dto.getValidityMonths());
        entity.setExpiryDate(dto.getDate() != null && dto.getValidityMonths() != null
                ? dto.getDate().plusMonths(dto.getValidityMonths()) : null);
        entity.setInsurer(dto.getInsurer());
        entity.setPolicyNumber(dto.getPolicyNumber());
        entity.setCost(dto.getCost());
        return toInsuranceDto(insuranceRepository.save(entity));
    }

    public void deleteInsurance(Long id) {
        insuranceRepository.deleteById(id);
    }

    // ── Events ──────────────────────────────────────────────────────

    public VehicleEventDto addEvent(Long vehicleId, VehicleEventDto dto) {
        VehicleEvent entity = VehicleEvent.builder()
                .vehicleId(vehicleId)
                .date(dto.getDate())
                .type(dto.getType())
                .description(dto.getDescription())
                .cost(dto.getCost())
                .build();
        return toEventDto(eventRepository.save(entity));
    }

    public VehicleEventDto updateEvent(Long id, VehicleEventDto dto) {
        VehicleEvent entity = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        entity.setDate(dto.getDate());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        entity.setCost(dto.getCost());
        return toEventDto(eventRepository.save(entity));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private String savePhoto(Long vehicleId, MultipartFile photo) throws IOException {
        Path photoDir = UPLOADS.resolve("vehicle-" + vehicleId).resolve("photo");
        Files.createDirectories(photoDir);
        String filename = StringUtils.cleanPath(photo.getOriginalFilename() != null
                ? photo.getOriginalFilename() : "photo");
        Files.copy(photo.getInputStream(), photoDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    private Vehicle toEntity(VehicleDto dto) {
        return Vehicle.builder()
                .licensePlate(dto.getLicensePlate())
                .vin(dto.getVin())
                .make(dto.getMake())
                .model(dto.getModel())
                .year(dto.getYear())
                .color(dto.getColor())
                .fuelType(dto.getFuelType())
                .engineCapacity(dto.getEngineCapacity())
                .power(dto.getPower())
                .currentKm(dto.getCurrentKm())
                .notes(dto.getNotes())
                .userId(dto.getUserId())
                .build();
    }

    private VehicleDto toDto(Vehicle v) {
        String userName = null;
        if (v.getUser() != null) {
            userName = (v.getUser().getFirstname() != null ? v.getUser().getFirstname() : "") +
                    " " + (v.getUser().getLastname() != null ? v.getUser().getLastname() : "");
            userName = userName.trim();
        }
        return VehicleDto.builder()
                .id(v.getId())
                .licensePlate(v.getLicensePlate())
                .vin(v.getVin())
                .make(v.getMake())
                .model(v.getModel())
                .year(v.getYear())
                .color(v.getColor())
                .fuelType(v.getFuelType())
                .engineCapacity(v.getEngineCapacity())
                .power(v.getPower())
                .currentKm(v.getCurrentKm())
                .notes(v.getNotes())
                .photoName(v.getPhotoName())
                .userId(v.getUserId())
                .userName(userName)
                .build();
    }

    private VehicleRevisionDto toRevisionDto(VehicleRevision r) {
        return VehicleRevisionDto.builder()
                .id(r.getId())
                .vehicleId(r.getVehicleId())
                .date(r.getDate())
                .km(r.getKm())
                .cost(r.getCost())
                .description(r.getDescription())
                .build();
    }

    private VehicleItpDto toItpDto(VehicleItp i) {
        return VehicleItpDto.builder()
                .id(i.getId())
                .vehicleId(i.getVehicleId())
                .date(i.getDate())
                .validityMonths(i.getValidityMonths())
                .expiryDate(i.getExpiryDate())
                .cost(i.getCost())
                .build();
    }

    private VehicleInsuranceDto toInsuranceDto(VehicleInsurance ins) {
        return VehicleInsuranceDto.builder()
                .id(ins.getId())
                .vehicleId(ins.getVehicleId())
                .date(ins.getDate())
                .validityMonths(ins.getValidityMonths())
                .expiryDate(ins.getExpiryDate())
                .insurer(ins.getInsurer())
                .policyNumber(ins.getPolicyNumber())
                .cost(ins.getCost())
                .build();
    }

    private VehicleEventDto toEventDto(VehicleEvent e) {
        return VehicleEventDto.builder()
                .id(e.getId())
                .vehicleId(e.getVehicleId())
                .date(e.getDate())
                .type(e.getType())
                .description(e.getDescription())
                .cost(e.getCost())
                .build();
    }
}
