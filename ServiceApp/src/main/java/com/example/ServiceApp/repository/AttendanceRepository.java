package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.Attendance;
import com.example.ServiceApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByUserOrderByDateDesc(User user);

    List<Attendance> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);

    Optional<Attendance> findByUserAndDate(User user, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.user = :user AND a.date = :date AND a.checkOutTime IS NULL")
    Optional<Attendance> findOpenAttendanceByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    List<Attendance> findAllByOrderByDateDesc();

    List<Attendance> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId ORDER BY a.date DESC")
    List<Attendance> findByUserIdOrderByDateDesc(@Param("userId") Long userId);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.date BETWEEN :startDate AND :endDate ORDER BY a.date DESC")
    List<Attendance> findByUserIdAndDateBetweenOrderByDateDesc(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
