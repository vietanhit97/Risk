package com.rpa.rpaui.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rpa.rpaui.entities.MRiskDataCreation;
import com.rpa.rpaui.repositories.MRiskDataCreationRepository;
import com.rpa.rpaui.utils.RpaConstant;

@Service
public class MRiskDataCreationService {
    @Autowired
    MRiskDataCreationRepository mRiskDataCreationRepository;

    public List<MRiskDataCreation> getAll() {
        return mRiskDataCreationRepository.findByIsDeleted(RpaConstant.IS_NOT_DELETE);
    }
}
