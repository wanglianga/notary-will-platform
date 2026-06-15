package com.notary.will.service;

import com.notary.will.entity.PropertyInventory;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.PropertyInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyInventoryService {

    private final PropertyInventoryRepository propertyInventoryRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public PropertyInventory create(Long caseId, PropertyInventory property) {
        willCaseService.getCaseById(caseId);
        property.setCaseId(caseId);
        return propertyInventoryRepository.save(property);
    }

    public List<PropertyInventory> getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return propertyInventoryRepository.findByCaseId(caseId);
    }

    public PropertyInventory getById(Long id) {
        return propertyInventoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("财产信息不存在"));
    }

    @Transactional
    public PropertyInventory update(Long id, PropertyInventory update) {
        PropertyInventory property = getById(id);
        if (update.getType() != null) property.setType(update.getType());
        if (update.getValue() != null) property.setValue(update.getValue());
        if (update.getAddress() != null) property.setAddress(update.getAddress());
        if (update.getArea() != null) property.setArea(update.getArea());
        if (update.getCertificateNo() != null) property.setCertificateNo(update.getCertificateNo());
        if (update.getBank() != null) property.setBank(update.getBank());
        if (update.getAccountNo() != null) property.setAccountNo(update.getAccountNo());
        if (update.getCompanyName() != null) property.setCompanyName(update.getCompanyName());
        if (update.getShareRatio() != null) property.setShareRatio(update.getShareRatio());
        if (update.getDescription() != null) property.setDescription(update.getDescription());
        if (update.getRemarks() != null) property.setRemarks(update.getRemarks());
        return propertyInventoryRepository.save(property);
    }

    @Transactional
    public void delete(Long id) {
        propertyInventoryRepository.deleteById(id);
    }
}
