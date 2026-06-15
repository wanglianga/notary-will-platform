package com.notary.will.service;

import com.notary.will.entity.AppointmentSlot;
import com.notary.will.enums.SlotStatus;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.AppointmentSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentSlotRepository appointmentSlotRepository;

    @Transactional
    public AppointmentSlot createSlot(AppointmentSlot slot) {
        List<AppointmentSlot> existing = appointmentSlotRepository
                .findByNotaryIdAndDateAndStatus(slot.getNotaryId(), slot.getDate(), SlotStatus.AVAILABLE);

        for (AppointmentSlot s : existing) {
            boolean overlaps = slot.getStartTime().isBefore(s.getEndTime()) && slot.getEndTime().isAfter(s.getStartTime());
            if (overlaps) {
                throw new BusinessException("该时间段与已有预约冲突");
            }
        }

        slot.setStatus(SlotStatus.AVAILABLE);
        return appointmentSlotRepository.save(slot);
    }

    @Transactional
    public AppointmentSlot bookSlot(Long slotId, Long caseId) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new BusinessException("预约时段不存在"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new BusinessException("该时段不可预约");
        }

        slot.setStatus(SlotStatus.BOOKED);
        slot.setCaseId(caseId);
        return appointmentSlotRepository.save(slot);
    }

    @Transactional
    public AppointmentSlot cancelSlot(Long slotId) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new BusinessException("预约时段不存在"));

        if (slot.getStatus() != SlotStatus.BOOKED) {
            throw new BusinessException("只能取消已预约的时段");
        }

        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setCaseId(null);
        return appointmentSlotRepository.save(slot);
    }

    @Transactional
    public AppointmentSlot completeSlot(Long slotId) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new BusinessException("预约时段不存在"));

        slot.setStatus(SlotStatus.COMPLETED);
        return appointmentSlotRepository.save(slot);
    }

    public List<AppointmentSlot> getAvailableSlots(Long notaryId, LocalDate date) {
        return appointmentSlotRepository.findByNotaryIdAndDateAndStatus(notaryId, date, SlotStatus.AVAILABLE);
    }

    public List<AppointmentSlot> getAvailableSlots() {
        return appointmentSlotRepository.findByStatus(SlotStatus.AVAILABLE);
    }

    public List<AppointmentSlot> getSlotsByCaseId(Long caseId) {
        return appointmentSlotRepository.findByCaseId(caseId);
    }

    public List<AppointmentSlot> getSlotsByNotaryAndDate(Long notaryId, LocalDate date) {
        return appointmentSlotRepository.findByNotaryIdAndDate(notaryId, date);
    }
}
