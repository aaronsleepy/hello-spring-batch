package com.kmong.hello.controller;

import com.kmong.hello.vo.MemberRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobController {
    private final Job helloJob;
    private final JobLauncher jobLauncher;
    private final BatchConfigurer batchConfigurer;

    @PostMapping("/hello")
    public String executeHelloJob(@RequestBody MemberRequest memberRequest) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", memberRequest.getName())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(helloJob, jobParameters);
        log.info(String.format("job %s completed with %s", helloJob.getName(), jobExecution.getExitStatus().toString()));

        return jobExecution.getExitStatus().toString();
    }

    @PostMapping("/hello/async")
    public String executeHelloJobAsync(@RequestBody MemberRequest memberRequest) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", memberRequest.getName())
                .toJobParameters();

        SimpleJobLauncher simpleJobLauncher = (SimpleJobLauncher)batchConfigurer.getJobLauncher();
        simpleJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());

        JobExecution jobExecution = simpleJobLauncher.run(helloJob, jobParameters);
        log.info(String.format("job %s completed with %s", helloJob.getName(), jobExecution.getExitStatus().toString()));

        return jobExecution.getExitStatus().toString();
    }
}
