package com.Buddymate.pickMate.config;

import com.Buddymate.pickMate.job.ExpiredCheckJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail expiredCheckJobDetail() {
        return JobBuilder.newJob(ExpiredCheckJob.class)
                .withIdentity("expiredCheckJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger expireCheckTrigger(JobDetail expiredCheckJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(expiredCheckJobDetail)
                .withIdentity("expiredCheckTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0))
                .build();
    }
}
