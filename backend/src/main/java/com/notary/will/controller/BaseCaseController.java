package com.notary.will.controller;

import com.notary.will.entity.User;
import com.notary.will.entity.WillCase;
import com.notary.will.enums.UserRole;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.WillCaseRepository;
import com.notary.will.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

public abstract class BaseCaseController {

    @Autowired
    protected WillCaseRepository willCaseRepository;

    @Autowired
    protected UserService userService;

    protected Long getCurrentUserId(Authentication auth) {
        return (Long) auth.getCredentials();
    }

    protected String getCurrentUsername(Authentication auth) {
        return auth.getName();
    }

    protected UserRole getCurrentRole(Authentication auth) {
        String roleStr = auth.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");
        return UserRole.valueOf(roleStr);
    }

    protected WillCase getCaseAndCheckAccess(Long caseId, Authentication auth) {
        WillCase willCase = willCaseRepository.findById(caseId)
                .orElseThrow(() -> new BusinessException("案件不存在"));

        UserRole role = getCurrentRole(auth);
        Long currentUserId = getCurrentUserId(auth);

        if (role == UserRole.APPLICANT) {
            if (!willCase.getApplicantId().equals(currentUserId)) {
                throw new AccessDeniedException("无权访问他人案件数据");
            }
        }

        return willCase;
    }

    protected void checkCaseOwnership(Long caseId, Authentication auth) {
        WillCase willCase = willCaseRepository.findById(caseId)
                .orElseThrow(() -> new BusinessException("案件不存在"));

        UserRole role = getCurrentRole(auth);
        Long currentUserId = getCurrentUserId(auth);

        if (role == UserRole.APPLICANT) {
            if (!willCase.getApplicantId().equals(currentUserId)) {
                throw new AccessDeniedException("无权访问或修改他人案件数据");
            }
        }
    }

    protected User getCurrentUser(Authentication auth) {
        return userService.getUserByUsername(getCurrentUsername(auth));
    }
}
