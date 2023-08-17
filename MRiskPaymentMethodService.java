package com.rpa.rpaui.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rpa.rpaui.entities.MRiskPaymentMethod;
import com.rpa.rpaui.repositories.MRiskPaymentMethodRepository;
import com.rpa.rpaui.utils.RpaConstant;

@Service
public class MRiskPaymentMethodService {
    @Autowired
    MRiskPaymentMethodRepository mRiskPaymentMethodRepository;

    public List<MRiskPaymentMethod> getAll() {
        return mRiskPaymentMethodRepository.findByIsDeleted(RpaConstant.IS_NOT_DELETE);
    }
}
