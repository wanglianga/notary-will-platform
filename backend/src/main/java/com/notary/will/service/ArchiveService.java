package com.notary.will.service;

import com.notary.will.entity.ArchiveRecord;
import com.notary.will.enums.ArchiveStatus;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.ArchiveRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRecordRepository archiveRecordRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public ArchiveRecord createArchive(Long caseId, ArchiveRecord record) {
        willCaseService.getCaseById(caseId);
        record.setCaseId(caseId);
        record.setStatus(ArchiveStatus.PENDING);
        return archiveRecordRepository.save(record);
    }

    @Transactional
    public ArchiveRecord seal(Long caseId, Long sealedBy, String physicalLocation, String electronicPath) {
        willCaseService.getCaseById(caseId);

        ArchiveRecord record = archiveRecordRepository.findByCaseId(caseId)
                .orElseGet(() -> {
                    ArchiveRecord r = new ArchiveRecord();
                    r.setCaseId(caseId);
                    return r;
                });

        record.setSealedBy(sealedBy);
        record.setSealedAt(LocalDateTime.now());
        record.setPhysicalLocation(physicalLocation);
        record.setElectronicPath(electronicPath);
        record.setStatus(ArchiveStatus.SEALED);
        return archiveRecordRepository.save(record);
    }

    @Transactional
    public ArchiveRecord requestUnseal(Long caseId) {
        willCaseService.getCaseById(caseId);
        ArchiveRecord record = archiveRecordRepository.findByCaseId(caseId)
                .orElseThrow(() -> new BusinessException("归档记录不存在"));

        if (record.getStatus() != ArchiveStatus.SEALED) {
            throw new BusinessException("只有已封存的案件才能申请拆封");
        }

        record.setStatus(ArchiveStatus.UNSEAL_REQUESTED);
        return archiveRecordRepository.save(record);
    }

    @Transactional
    public ArchiveRecord unseal(Long caseId) {
        willCaseService.getCaseById(caseId);
        ArchiveRecord record = archiveRecordRepository.findByCaseId(caseId)
                .orElseThrow(() -> new BusinessException("归档记录不存在"));

        if (record.getStatus() != ArchiveStatus.UNSEAL_REQUESTED) {
            throw new BusinessException("只有已申请拆封的案件才能拆封");
        }

        record.setStatus(ArchiveStatus.UNSEALED);
        return archiveRecordRepository.save(record);
    }

    public ArchiveRecord getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return archiveRecordRepository.findByCaseId(caseId)
                .orElseThrow(() -> new BusinessException("归档记录不存在"));
    }
}
