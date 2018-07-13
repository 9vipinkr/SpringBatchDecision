package com.example.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	
	public static class JobDecision implements JobExecutionDecider{
        int count=0;
		@Override
		public FlowExecutionStatus decide(JobExecution arg0, StepExecution arg1) {
			count++;
			// TODO Auto-generated method stub
			if(count%2==0) {
				return new FlowExecutionStatus("EVEN");
			}
			else {
				return new FlowExecutionStatus("ODD");
			}
			
		}
		
	}
	@Bean
	public JobDecision decision() {
	 return new JobDecision();
	}
	
	@Bean
	public Step myStep() {
		return stepBuilderFactory.get("myStep")
		.tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("myStep");
				return RepeatStatus.FINISHED;
			}
			
		}).build();
	}
	@Bean
	public Step oddStep() {
		return stepBuilderFactory.get("oddStep")
		.tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("odd step");
				return RepeatStatus.FINISHED;
			}
			
		}).build();
	}
	@Bean
	public Step evenStep() {
		return stepBuilderFactory.get("evenStep")
		.tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("even step");
				return RepeatStatus.FINISHED;
			}
			
		}).build();
	}

	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("firstJob") 
				.start(myStep())
			    .next(decision())
		        .on("ODD").to(oddStep())
			    .on("EVEN").to(evenStep())
			    .on("*").to(decision())
			    .end()
				.build();
	}
	//Add SecondJob from local
	@Bean
	public Job secondJob() {
		return jobBuilderFactory.get("secondJob") 
				.start(myStep())
			    .next(decision())
		        .on("ODD").to(oddStep())
			    .on("EVEN").to(evenStep())
			    .on("*").to(decision())
			    .end()
				.build();
	}
	
}
