package com.kmong.hello.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelloJobExecutionListener implements JobExecutionListener {
    private final JobRepository jobRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        JobParameters jobParameters = jobExecution.getJobParameters();

        JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, jobParameters);

        log.info(String.format("jobId: %d", lastJobExecution.getJobId()));
        log.info(String.format("jobInstanceId: %d", lastJobExecution.getJobInstance().getInstanceId()));
        log.info(String.format("jobInstanceId: %s", lastJobExecution.getExitStatus().toString()));

        lastJobExecution.getStepExecutions().stream()
                .forEach(stepExecution -> {
                    log.info(String.format("step: %s", stepExecution.getStepName()));
                });
    }
}
