package com.coderelated.dbTOCSVTOawss3.service;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
	
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Value("${aws.s3.bucket}")
	private String bucketName;
	
	 public void uploadCSVObject(String bucketName, String key, File content) {
       

       amazonS3.putObject(bucketName,key,content);
   }

}
