package com.rpa.rpaui.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rpa.rpaui.entities.MRiskDataCreation;


public interface MRiskDataCreationRepository extends JpaRepository<MRiskDataCreation, Integer>{
    List<MRiskDataCreation> findByIsDeleted(short isDelete); 
}
