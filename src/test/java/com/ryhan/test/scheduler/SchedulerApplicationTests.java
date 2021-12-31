package com.ryhan.test.scheduler;

import com.ryhan.test.scheduler.service.SchedulerJobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class SchedulerApplicationTests {

	@Autowired
	SchedulerJobService schedulerJobService;
	@Test
	void contextLoads() throws SchedulerException {
		//schedulerJobService.getAllTrigger();
	}

	@Test
	void AllTriggerUnitTest() throws SchedulerException {
		print("All trigger-------------------------> Start\n\n");
		print(schedulerJobService.getAllTrigger());
		print("All trigger-------------------------> End\n\n");
	}

	static void print(Object o){
		System.out.println(o);
	}
}
