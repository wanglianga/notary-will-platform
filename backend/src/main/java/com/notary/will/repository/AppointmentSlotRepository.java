package com.notary.will.repository;

import com.notary.will.entity.AppointmentSlot;
import com.notary.will.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByNotaryIdAndDate(Long notaryId, LocalDate date);
    List<AppointmentSlot> findByStatus(SlotStatus status);
    List<AppointmentSlot> findByNotaryIdAndDateAndStatus(Long notaryId, LocalDate date, SlotStatus status);
    List<AppointmentSlot> findByCaseId(Long caseId);
}
