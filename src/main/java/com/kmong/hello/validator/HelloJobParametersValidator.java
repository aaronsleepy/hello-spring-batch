package com.kmong.hello.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class HelloJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (StringUtils.hasText(parameters.getString("name"))) {
            log.info(String.format("JobParameters: name = %s", parameters.getString("name")));
        } else {
            throw new JobParametersInvalidException("JobParameters: name is empty");
        }
    }
}
