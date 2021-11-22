package com.kmong.hello.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BonjourJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public JobParametersValidator bonjourJobParameteresValidator() {
        DefaultJobParametersValidator jobParametersValidator = new DefaultJobParametersValidator();
        jobParametersValidator.setRequiredKeys(new String[] { "name" });
        jobParametersValidator.setOptionalKeys(new String[] { "region" });
        return jobParametersValidator;
    }

    @Bean
    public Job bonjourJob(Step bonjourStep, Step auRevoirStep,
                          JobParametersValidator bonjourJobParameteresValidator) {
        return jobBuilderFactory.get("bonjourJob")
                .validator(bonjourJobParameteresValidator)
                .start(bonjourStep)
                .next(auRevoirStep)
                .build();
    }

    @Bean
    public Step bonjourStep() {
        return stepBuilderFactory.get("bonjourStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
                        String name = jobParameters.getString("name");

                        System.out.println(String.format("Bonjour Spring Batch to %s", name));

                        ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();
                        ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();

                        jobExecutionContext.put("job-name", "bonjourJob");
                        stepExecutionContext.put("step1-name", "bonjourStep");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Step auRevoirStep() {
        return stepBuilderFactory.get("auRevoirStep")
                .tasklet((contribution, chunkContext) -> {
                    JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
                    String name = jobParameters.getString("name");
                    System.out.println(String.format("Au revoir Spring Batch to %s", name));

                    ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();
                    ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();

                    System.out.println(String.format("job-name: %s", jobExecutionContext.get("job-name")));
                    System.out.println(String.format("step1-name: %s", jobExecutionContext.get("step1-name")));
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
