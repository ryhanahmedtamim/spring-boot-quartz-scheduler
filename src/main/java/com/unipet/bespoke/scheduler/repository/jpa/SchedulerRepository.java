package com.unipet.bespoke.scheduler.repository.jpa;

import com.unipet.bespoke.scheduler.repository.schema.SchedulerJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerJobInfo, Long> {

  SchedulerJobInfo findByJobName(String jobName);

}
