package com.notary.will.service;

import com.notary.will.entity.WitnessVideo;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.WitnessVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WitnessVideoService {

    private final WitnessVideoRepository witnessVideoRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public WitnessVideo create(Long caseId, WitnessVideo video) {
        willCaseService.getCaseById(caseId);
        video.setCaseId(caseId);
        if (video.getRecordedAt() == null) {
            video.setRecordedAt(LocalDateTime.now());
        }
        return witnessVideoRepository.save(video);
    }

    public List<WitnessVideo> getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return witnessVideoRepository.findByCaseId(caseId);
    }

    public WitnessVideo getById(Long id) {
        return witnessVideoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("见证视频不存在"));
    }

    @Transactional
    public void delete(Long id) {
        witnessVideoRepository.deleteById(id);
    }
}
