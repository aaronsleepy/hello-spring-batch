package com.kmong.hello.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmong.hello.domain.Customer;
import com.kmong.hello.vo.CustomerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobExecutionListener helloJobExecutionListener;
    private final JobParametersValidator helloJobParametersValidator;
    private final EntityManagerFactory itemEntityManagerFactory;
    private final ObjectMapper mapper;

    @Bean
    public Job helloJob(Step helloStep, Step goodByeStep, Step seeYaStep,
                        Step longTimeNoSeeStep, Step whatsUpStep,
                        Step dropBeatStep,
                        Step boombapStep,
                        Step trapStep) {
        return jobBuilderFactory.get("helloJob")
                .listener(helloJobExecutionListener)
//                .validator(helloJobParametersValidator)
                .start(helloStep)
                .next(goodByeStep)
                .next(seeYaStep)
                .next(longTimeNoSeeStep)
                .next(whatsUpStep)
                .next(dropBeatStep)
                .next(boombapStep)
                .next(trapStep)
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
    public Step dropBeatStep(ItemReader<Customer> dropBeatReader) {
        return stepBuilderFactory.get("dropBeatStep")
                .<Customer, Customer>chunk(5)
                .reader(dropBeatReader)
                .writer(items -> {
                    System.out.println("Chunk processed >>>>>>>");
                    items.forEach(System.out::println);
                })
                .build();
    }

    @Bean
    public Step boombapStep(ItemReader<Customer> dropBeatReader,
                            ItemWriter<String> boombapWriter) {
        return stepBuilderFactory.get("boombapStep")
                .<Customer, String>chunk(5)
                .reader(dropBeatReader)
                .processor(new ItemProcessor<Customer, String>() {
                    @Override
                    public String process(Customer item) throws Exception {
                        return mapper.writeValueAsString(item);
                    }
                })
                .writer(boombapWriter)
                .build();
    }

    @Bean
    public Step trapStep(ItemReader<Customer> dropBeatReader,
                         ItemWriter<? super Customer> trapWriter) {
        return stepBuilderFactory.get("trapStep")
                .<Customer, Customer>chunk(5)
                .reader(dropBeatReader)
                .writer(trapWriter)
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

    @Bean
    public ItemReader<Customer> dropBeatReader() {
        return new JpaPagingItemReaderBuilder()
                .name("dropBeatReader")
                .entityManagerFactory(itemEntityManagerFactory)
                .pageSize(5)
                .queryString("select c from Customer c")
                .build();
    }

    @Bean
    public ItemWriter<String> boombapWriter() {
        return new FlatFileItemWriterBuilder<String>()
                .name("boombapWriter")
                .resource(new FileSystemResource("items/customer.out"))
                .append(false)
                .shouldDeleteIfExists(true)
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> trapWriter() {
        return new FlatFileItemWriterBuilder<>()
                .name("trapWriter")
                .resource(new FileSystemResource("items/customer-trap.out"))
                .append(false)
                .shouldDeleteIfExists(true)
                .delimited().delimiter("|")
                .names("name", "age", "type", "createdAt", "updatedAt")
                .build();
    }
}
