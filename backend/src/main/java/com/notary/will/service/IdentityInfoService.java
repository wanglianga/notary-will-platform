package com.notary.will.service;

import com.notary.will.entity.IdentityInfo;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.IdentityInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentityInfoService {

    private final IdentityInfoRepository identityInfoRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public IdentityInfo createOrUpdate(Long caseId, IdentityInfo info) {
        willCaseService.getCaseById(caseId);
        info.setCaseId(caseId);
        return identityInfoRepository.findByCaseId(caseId)
                .map(existing -> {
                    info.setId(existing.getId());
                    return identityInfoRepository.save(info);
                })
                .orElseGet(() -> identityInfoRepository.save(info));
    }

    public IdentityInfo getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return identityInfoRepository.findByCaseId(caseId)
                .orElseThrow(() -> new BusinessException("身份信息不存在"));
    }

    public List<IdentityInfo> getByCaseIds(List<Long> caseIds) {
        return identityInfoRepository.findByCaseIdIn(caseIds);
    }
}
