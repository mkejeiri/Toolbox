package com.elearning.sbf.jmssample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//tells Spring to expect a TaskExecutor class.
@EnableScheduling
//enable running async task out of a task pool.
@EnableAsync
//Scan by spring boot as a configuration bean.
@Configuration
public class TaskConfig {
    //setting up a a bean called Task Executor as a Simple Async TaskExecutor,
    //it will allow us to run async task, along with @EnableScheduling
    //which tells Spring to expect a TaskExecutor class for scheduled tasks,
    //this will set up a schedule task that enables us to send out a message at a periodic basis.
    @Bean
    TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor();
    }
}