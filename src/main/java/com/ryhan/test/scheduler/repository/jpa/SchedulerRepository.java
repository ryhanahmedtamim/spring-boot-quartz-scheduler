package com.ryhan.test.scheduler.repository.jpa;

import com.ryhan.test.scheduler.repository.schema.SchedulerJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerJobInfo, Long> {

  SchedulerJobInfo findByJobNameAndDeletedIsFalse(String jobName);

}
