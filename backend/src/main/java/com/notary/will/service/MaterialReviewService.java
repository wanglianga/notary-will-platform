package com.notary.will.service;

import com.notary.will.entity.MaterialReview;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.MaterialReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialReviewService {

    private final MaterialReviewRepository materialReviewRepository;

    public List<MaterialReview> getByCaseId(Long caseId) {
        return materialReviewRepository.findByCaseId(caseId);
    }

    public MaterialReview getById(Long id) {
        return materialReviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException("审核记录不存在"));
    }

    @Transactional
    public MaterialReview create(MaterialReview review) {
        return materialReviewRepository.save(review);
    }

    @Transactional
    public MaterialReview update(Long id, MaterialReview update) {
        MaterialReview review = getById(id);
        if (update.getReviewResult() != null) review.setReviewResult(update.getReviewResult());
        if (update.getReviewComment() != null) review.setReviewComment(update.getReviewComment());
        if (update.getIsValid() != null) review.setIsValid(update.getIsValid());
        return materialReviewRepository.save(review);
    }
}
