package com.rpa.rpaui.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rpa.rpaui.entities.MRiskAmountCategory;

public interface MRiskAmountCategoryRepository extends JpaRepository<MRiskAmountCategory, Integer>{
    List<MRiskAmountCategory> findByIsDeleted(short isDelete); 
}
