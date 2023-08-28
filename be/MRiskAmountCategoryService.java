package com.rpa.rpaui.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rpa.rpaui.entities.MRiskAmountCategory;
import com.rpa.rpaui.repositories.MRiskAmountCategoryRepository;
import com.rpa.rpaui.utils.RpaConstant;

@Service
public class MRiskAmountCategoryService {
    @Autowired
    MRiskAmountCategoryRepository mRiskAmountCategoryRepository;

    public List<MRiskAmountCategory> getAll() {
        return mRiskAmountCategoryRepository.findByIsDeleted(RpaConstant.IS_NOT_DELETE);
    }
}
