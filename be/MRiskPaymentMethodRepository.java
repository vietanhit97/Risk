package com.rpa.rpaui.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rpa.rpaui.entities.MRiskPaymentMethod;


public interface MRiskPaymentMethodRepository extends JpaRepository<MRiskPaymentMethod, Integer>{
    List<MRiskPaymentMethod> findByIsDeleted(short isDelete); 
}
