package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.AppointmentSlot;
import com.notary.will.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/available")
    public ApiResponse<List<AppointmentSlot>> getAvailableSlots(
            @RequestParam(required = false) Long notaryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (notaryId != null && date != null) {
            return ApiResponse.ok(appointmentService.getAvailableSlots(notaryId, date));
        }
        return ApiResponse.ok(appointmentService.getAvailableSlots());
    }

    @PostMapping("/slots")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<AppointmentSlot> createSlot(@RequestBody AppointmentSlot slot) {
        return ApiResponse.ok(appointmentService.createSlot(slot));
    }

    @PostMapping("/{slotId}/book")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<AppointmentSlot> bookSlot(@PathVariable Long slotId,
                                                  @RequestParam Long caseId) {
        return ApiResponse.ok(appointmentService.bookSlot(slotId, caseId));
    }

    @PostMapping("/{slotId}/cancel")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<AppointmentSlot> cancelSlot(@PathVariable Long slotId) {
        return ApiResponse.ok(appointmentService.cancelSlot(slotId));
    }

    @PostMapping("/{slotId}/complete")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<AppointmentSlot> completeSlot(@PathVariable Long slotId) {
        return ApiResponse.ok(appointmentService.completeSlot(slotId));
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<List<AppointmentSlot>> getSlotsByCase(@PathVariable Long caseId) {
        return ApiResponse.ok(appointmentService.getSlotsByCaseId(caseId));
    }

    @GetMapping("/notary/{notaryId}/date/{date}")
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER')")
    public ApiResponse<List<AppointmentSlot>> getSlotsByNotaryAndDate(
            @PathVariable Long notaryId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(appointmentService.getSlotsByNotaryAndDate(notaryId, date));
    }

}
