package com.kmong.hello.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job helloJob(Step helloStep, Step goodByeStep) {
        return jobBuilderFactory.get("helloJob")
                .start(helloStep)
                .next(goodByeStep)
                .build();
    }

    @Bean
    public Step helloStep() {
        return stepBuilderFactory.get("helloStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
                        String name = jobParameters.getString("name");

                        System.out.println(String.format("Hello Spring Batch to %s", name));
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Step goodByeStep() {
        return stepBuilderFactory.get("goodByeStep")
                .tasklet((contribution, chunkContext) -> {
                    JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
                    String name = jobParameters.getString("name");

                    System.out.println(String.format("Goodbye Spring Batch to %s", name));
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
