package com.Buddymate.pickMate.job;

import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.Study;
import com.Buddymate.pickMate.repository.ProjectRepository;
import com.Buddymate.pickMate.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpiredCheckJob extends QuartzJobBean {

    private final ProjectRepository projectRepository;
    private final StudyRepository studyRepository;


    public ExpiredCheckJob(ProjectRepository projectRepository, StudyRepository studyRepository) {
        this.projectRepository = projectRepository;
        this.studyRepository = studyRepository;
    }


    @Override
    public void executeInternal(JobExecutionContext context) {
        LocalDateTime now =  LocalDateTime.now();

        // 프로젝트 마감 job
        List<Project> expiredProjects = projectRepository.findByDeadlineBeforeAndExpiredStatusFalse(now);
        expiredProjects.forEach(p -> p.setExpiredStatus(true));
        projectRepository.saveAll(expiredProjects);

        // 스터디 마감 job
        List<Study> expiredStudies = studyRepository.findByDeadlineBeforeAndExpiredStatusFalse(now);
        expiredStudies.forEach(p -> p.setExpiredStatus(true));
        studyRepository.saveAll(expiredStudies);

    }
}
