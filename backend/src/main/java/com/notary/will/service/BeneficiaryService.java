package com.notary.will.service;

import com.notary.will.entity.Beneficiary;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    public List<Beneficiary> getByCaseId(Long caseId) {
        return beneficiaryRepository.findByCaseId(caseId);
    }

    public Beneficiary getById(Long id) {
        return beneficiaryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("受益人不存在"));
    }

    @Transactional
    public Beneficiary create(Long caseId, Beneficiary beneficiary) {
        beneficiary.setCaseId(caseId);
        return beneficiaryRepository.save(beneficiary);
    }

    @Transactional
    public Beneficiary update(Long id, Beneficiary update) {
        Beneficiary beneficiary = getById(id);
        if (update.getName() != null) beneficiary.setName(update.getName());
        if (update.getRelation() != null) beneficiary.setRelation(update.getRelation());
        if (update.getIdNumber() != null) beneficiary.setIdNumber(update.getIdNumber());
        if (update.getPhone() != null) beneficiary.setPhone(update.getPhone());
        if (update.getShare() != null) beneficiary.setShare(update.getShare());
        if (update.getRemarks() != null) beneficiary.setRemarks(update.getRemarks());
        return beneficiaryRepository.save(beneficiary);
    }

    @Transactional
    public void delete(Long id) {
        beneficiaryRepository.deleteById(id);
    }
}
