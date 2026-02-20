package com.example.ServiceApp.service;

import com.example.ServiceApp.entity.Vehicle;
import com.example.ServiceApp.entity.VehicleInsurance;
import com.example.ServiceApp.entity.VehicleItp;
import com.example.ServiceApp.repository.VehicleInsuranceRepository;
import com.example.ServiceApp.repository.VehicleItpRepository;
import com.example.ServiceApp.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleAlertTask {

    private static final String ADMIN_EMAIL = "office@singularity-ai.eu";

    private final VehicleItpRepository itpRepository;
    private final VehicleInsuranceRepository insuranceRepository;
    private final VehicleRepository vehicleRepository;
    private final EmailService emailService;

    /**
     * Rulează zilnic la 08:00.
     * Trimite alertă când mai sunt exact 30 sau 7 zile până la expirare.
     * Poate fi declanșat și manual prin endpoint-ul /api/vehicles/alerts/trigger.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public synchronized void checkExpirations() {
        LocalDate today = LocalDate.now();
        log.info("=== VehicleAlertTask pornit: {} ===", today);

        int itpAlerts = checkItpExpirations(today);
        int insuranceAlerts = checkInsuranceExpirations(today);

        log.info("=== VehicleAlertTask finalizat: {} alerte ITP, {} alerte asigurare ===",
                itpAlerts, insuranceAlerts);
    }

    // Zilele la care se trimit alerte (numărat înainte de expirare)
    private static final int[] ALERT_DAYS = {30, 14, 7, 1};

    private int checkItpExpirations(LocalDate today) {
        int count = 0;
        for (int days : ALERT_DAYS) {
            LocalDate target = today.plusDays(days);
            List<VehicleItp> expiring = itpRepository.findByExpiryDateBetween(target, target);
            log.info("ITP: {} înregistrări care expiră exact în {} zile ({})", expiring.size(), days, target);
            for (VehicleItp itp : expiring) {
                sendItpAlert(itp, days);
                count++;
            }
        }
        return count;
    }

    private int checkInsuranceExpirations(LocalDate today) {
        int count = 0;
        for (int days : ALERT_DAYS) {
            LocalDate target = today.plusDays(days);
            List<VehicleInsurance> expiring = insuranceRepository.findByExpiryDateBetween(target, target);
            log.info("Asigurare: {} înregistrări care expiră exact în {} zile ({})", expiring.size(), days, target);
            for (VehicleInsurance ins : expiring) {
                sendInsuranceAlert(ins, days);
                count++;
            }
        }
        return count;
    }

    private void sendItpAlert(VehicleItp itp, int daysLeft) {
        String licensePlate = getLicensePlate(itp.getVehicleId());
        log.info("Trimit alertă ITP pentru {} ({} zile)", licensePlate, daysLeft);
        emailService.sendItpExpiryAlert(ADMIN_EMAIL, licensePlate, itp.getExpiryDate(), daysLeft);
        getVehicleUserEmail(itp.getVehicleId()).ifPresent(email ->
                emailService.sendItpExpiryAlert(email, licensePlate, itp.getExpiryDate(), daysLeft));
    }

    private void sendInsuranceAlert(VehicleInsurance ins, int daysLeft) {
        String licensePlate = getLicensePlate(ins.getVehicleId());
        log.info("Trimit alertă asigurare pentru {} ({} zile)", licensePlate, daysLeft);
        emailService.sendInsuranceExpiryAlert(ADMIN_EMAIL, licensePlate, ins.getExpiryDate(), daysLeft);
        getVehicleUserEmail(ins.getVehicleId()).ifPresent(email ->
                emailService.sendInsuranceExpiryAlert(email, licensePlate, ins.getExpiryDate(), daysLeft));
    }

    private String getLicensePlate(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .map(Vehicle::getLicensePlate)
                .orElse("N/A");
    }

    private Optional<String> getVehicleUserEmail(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .filter(v -> v.getUser() != null)
                .map(v -> v.getUser().getEmail());
    }
}
