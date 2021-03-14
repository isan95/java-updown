package com.polanco.updown.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.polanco.updown.payload.response.FileResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageService {
	
	@Value("${application.bucket.name}")
	private String bucketName;
	
	@Autowired
	private AmazonS3 s3Client;

	private FileResponse fileAux;

	private List<FileResponse> fileResponse;
	
	public String upload(MultipartFile file) {
		
		File fileObj = convertMultipartFileToFile(file);
		String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
		s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
		fileObj.delete();
		return "The "+fileName+" file"+"was uploaded successfully";
	}
	
	public byte[] download(String fileName) {
		S3Object s3Obj = s3Client.getObject(bucketName, fileName);
		S3ObjectInputStream s3ObjInputStream = s3Obj.getObjectContent();
		try {
			byte[] content = IOUtils.toByteArray(s3ObjInputStream);
			s3Obj.close();
			return content;
		}catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String delete(String fileName) {
		
		s3Client.deleteObject(bucketName, fileName);
		return "The "+ fileName+ "was deleted";
	}
	
	public List<S3ObjectSummary> listUploadedFile(){
		ObjectListing objectsList = s3Client.listObjects(bucketName);
		List<S3ObjectSummary> s3ObjectSumary = objectsList.getObjectSummaries();
		
		/*s3ObjectSumary.forEach(i->{
			fileAux.setFileName(i.getKey());
			fileAux.setDate(i.getLastModified());
			fileResponse.add(fileAux);
		});*/
		return s3ObjectSumary;
	}
	
	private File convertMultipartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try(FileOutputStream fileOut = new FileOutputStream(convertedFile)) {			
			fileOut.write(file.getBytes());
		} catch (IOException e) {
			Log.error("Error converting multipartfile to file");
		}
		return convertedFile;
	}

}
