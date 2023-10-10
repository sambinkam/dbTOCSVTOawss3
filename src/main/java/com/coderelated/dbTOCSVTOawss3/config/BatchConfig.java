package com.coderelated.dbTOCSVTOawss3.config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.coderelated.dbTOCSVTOawss3.model.Users;
import com.coderelated.dbTOCSVTOawss3.repository.UsersRepository;
import com.coderelated.dbTOCSVTOawss3.service.S3Service;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class BatchConfig {

//    @Autowired
//    private JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("postgresEntityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private S3Service s3Service;
    
    @Value("${aws.s3.bucket}")
    private String s3bucketName;
    
    @Autowired
    private UsersRepository usersRepository;

    @Bean
    @StepScope
    public ItemReader<Users> reader() {
    	 return new RepositoryItemReaderBuilder<Users>()
                 .name("myEntityReader")
                 .repository(usersRepository)
                 .methodName("findAll")
                 .sorts(Map.of("id", Sort.Direction.ASC))
                 .pageSize(10)
                 .saveState(false) // Set to true if you want to save the state between job runs
                 .build();
         
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Users> csvItemwriter() {
        return new FlatFileItemWriterBuilder<Users>()
                .name("csvItemwriter")
                .resource(new FileSystemResource("output.csv"))
                .delimited()
                .names("id", "name")
                .build();
    }
    
//    public ItemProcessor<List<Users>, List<Users>> postgresdbProcessor(){
//    	
//    }
    
    @Bean
    public ItemWriter<Users> s3ItemWriter() {
        return items -> {
            String csvFilePath = "output.csv";
//            writeItemsToCsv(items, csvFilePath);

//            String s3BucketName = "your-s3-bucket";
            String s3ObjectKey = "output.csv";
            s3Service.uploadCSVObject(s3bucketName, s3ObjectKey, new java.io.File(csvFilePath));
        };
    }

    private void writeItemsToCsv(Chunk<? extends Users> items, String filePath) {
        // Implement logic to write items to a CSV file
    	FileWriter writer;
		try {
			System.out.println("executing writeItemsToCSV ...........................!"+items.toString());
			writer = new 
			        FileWriter(filePath);
			ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
			mappingStrategy.setType(Users.class);

			// Arrange column name as provided in below array.
			String[] columns = new String[] { "id", "name", "dob", "createdDate" };
			mappingStrategy.setColumnMapping(columns);

			// Creating StatefulBeanToCsv object
			StatefulBeanToCsvBuilder<Users> builder = new StatefulBeanToCsvBuilder(writer);
			StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();

			// Write list to StatefulBeanToCsv object
			System.out.println(items.getItems());
			try {
				beanWriter.write(items.getItems());
			} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
    }

//    @Bean
//    public Step step(ItemReader<Users> reader, FlatFileItemWriter<Users> writer) {
//        return stepBuilderFactory.get("step")
//                .<Users, Users>chunk(10)
//                .reader(reader)
//                .writer(writer)
//                .build();
//    }
//
//    @Bean
//    public Job job(Step step) {
//        return jobBuilderFactory.get("postgresqlTocsvToS3")
//                .flow(step)
//                .end()
//                .build();
//    }
    

    @Autowired
	private PlatformTransactionManager transactionManager;
    
    @Bean
    public Step uploadToS3Step(JobRepository jobRepository) {
    	return new StepBuilder("exportDBDateToS3AsCSVStep", jobRepository)
    			.<Users,Users>chunk(10, transactionManager)
//    			.reader(reader())
    			.writer(s3ItemWriter())
    			.build();
    }
    
    @Bean
    public Step exportDBDateToS3AsCSVStep(JobRepository jobRepository) {
    	return new StepBuilder("exportDBDateToS3AsCSVStep", jobRepository)
    			.<Users,Users>chunk(10, transactionManager)
    			.reader(reader())
    			.writer(s3ItemWriter())
    			.build();
    }
    
    @Bean
    public Job exportDBDateToS3AsCSV(JobRepository jobRepository) {
    	return new JobBuilder("postgresqlTocsvToS3Job",jobRepository)
    			.incrementer(new RunIdIncrementer())
    			.start(exportDBDateToS3AsCSVStep(jobRepository))
    			.next(uploadToS3Step(jobRepository))
    			.build();
    }
    
    @AfterJob
    public void afterJob() {
    	System.out.println("job done ..................!");
    }
    
    
    
    
    
    
//    @Bean
//	public FlowJobBuilder retrySample(JobRepository jobRepository) {
////		return new JobBuilder("postgresqlTocsvToS3", jobRepository).start(step(jobRepository)).build();
//    	return flowJobBuilder.start(step(reader(),writer())).build();
//	}
    
//    @Bean
//	protected Step step(JobRepository jobRepository) {
//		return new StepBuilder("step", jobRepository).<Users, Object>chunk(1, this.transactionManager)
//			.reader(reader())
//			.writer(writer())
//			.faultTolerant()
//			.retry(Exception.class)
//			.retryLimit(3)
//			.build();
//	}
}
