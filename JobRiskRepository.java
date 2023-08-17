package com.rpa.rpaui.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rpa.rpaui.entities.JobRisk;

public interface JobRiskRepository extends JpaRepository<JobRisk,Integer>{
    JobRisk findByAttachment1(String attachment1);
    JobRisk findByAttachment2(String attachment2);
    JobRisk findByJobId(Integer id);
}
