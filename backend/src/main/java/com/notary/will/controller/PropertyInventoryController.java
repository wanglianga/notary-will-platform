package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.PropertyInventory;
import com.notary.will.service.PropertyInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/properties")
@RequiredArgsConstructor
public class PropertyInventoryController {

    private final PropertyInventoryService propertyInventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'CASHIER')")
    public ApiResponse<List<PropertyInventory>> getByCaseId(@PathVariable Long caseId) {
        return ApiResponse.ok(propertyInventoryService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<PropertyInventory> create(@PathVariable Long caseId,
                                                  @RequestBody PropertyInventory property) {
        return ApiResponse.ok(propertyInventoryService.create(caseId, property));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<PropertyInventory> update(@PathVariable Long caseId,
                                                  @PathVariable Long id,
                                                  @RequestBody PropertyInventory property) {
        return ApiResponse.ok(propertyInventoryService.update(id, property));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        propertyInventoryService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
