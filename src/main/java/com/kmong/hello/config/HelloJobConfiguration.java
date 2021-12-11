package com.kmong.hello.config;

import com.kmong.hello.vo.CustomerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.jsr.item.ItemReaderAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobExecutionListener helloJobExecutionListener;
    private final JobParametersValidator helloJobParametersValidator;

    @Bean
    public Job helloJob(Step helloStep, Step goodByeStep, Step seeYaStep,
                        Step longTimeNoSeeStep, Step whatsUpStep) {
        return jobBuilderFactory.get("helloJob")
                .listener(helloJobExecutionListener)
//                .validator(helloJobParametersValidator)
                .start(helloStep)
                .next(goodByeStep)
                .next(seeYaStep)
                .next(longTimeNoSeeStep)
                .next(whatsUpStep)
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

                        ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();
                        ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();

                        jobExecutionContext.put("job-name", "helloJob");
                        stepExecutionContext.put("step1-name", "helloStep");
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

                    ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();
                    ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();

                    System.out.println(String.format("job-name: %s", jobExecutionContext.get("job-name")));
                    System.out.println(String.format("step1-name: %s", jobExecutionContext.get("step1-name")));
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step seeYaStep() {
        return stepBuilderFactory.get("seeYaStep")
                .<String, String>chunk(5)
                .reader(new ListItemReader<>(Arrays.asList("Daniel", "Mincho", "Morty", "Coo")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return "SeeYa " + item;
                    }
                })
                .writer(items -> {
                    items.forEach(s -> System.out.println(s));
                })
                .build();
    }

    @Bean
    public Step longTimeNoSeeStep(ItemReader<CustomerVO> longTimeNoSeeReader) {
        return stepBuilderFactory.get("longTimeNoSeeStep")
                .<CustomerVO, CustomerVO>chunk(2)
                .reader(longTimeNoSeeReader)
                .writer(items -> {
                    System.out.println("Chunk processed >>>>>>>");
                    items.forEach(System.out::println);
                })
                .build();
    }

    @Bean
    public Step whatsUpStep(ItemReader<Map<String, Object>> whatsUpReader) {
        return stepBuilderFactory.get("whatsUpStep")
                .<Map<String, Object>, Map<String, Object>>chunk(2)
                .reader(whatsUpReader)
                .writer(items -> {
                    System.out.println("Chunk processed >>>>>>>");
                    items.forEach(System.out::println);
                })
                .build();
    }

    @Bean
    public ItemReader<CustomerVO> longTimeNoSeeReader() {
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(",");
        delimitedLineTokenizer.setNames(new String[] { "name", "age", "type"});

        return (new FlatFileItemReaderBuilder<CustomerVO>())
                .name("longTimeNoSeeReader")
                .resource(new FileSystemResource("items/customer.csv"))
//                .lineMapper(new DefaultLineMapper())
                .lineTokenizer(delimitedLineTokenizer)
                .targetType(CustomerVO.class)
                .build();
    }

    @Bean
    public ItemReader<Map<String, Object>> whatsUpReader() {
        return (new FlatFileItemReaderBuilder<Map<String, Object>>())
                .name("longTimeNoSeeReader")
                .resource(new FileSystemResource("items/customer.data"))
                .lineMapper(new JsonLineMapper())
                .build();
    }
}
