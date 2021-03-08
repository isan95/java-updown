package com.polanco.updown.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.polanco.updown.payload.response.FileResponse;
import com.polanco.updown.service.StorageService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/file")
public class StorageController {

	@Autowired
	private StorageService storageService;
	
	private List<FileResponse> fileResponse = new ArrayList<FileResponse>();
	
	@PostMapping("/upload")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<String> upload (@RequestParam(value="file")MultipartFile file){
		
		return new ResponseEntity<>(storageService.upload(file), HttpStatus.OK);
	}
	
	@GetMapping("/download/{fileName}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<ByteArrayResource> download(@PathVariable String fileName){
		
		byte[] data = storageService.download(fileName);
		ByteArrayResource byteArrayResource = new ByteArrayResource(data);
		System.out.println("Se ha llamado para descargar");
		return ResponseEntity.ok().contentLength(data.length)
				.header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\""+ fileName+ "\"")
				.body(byteArrayResource);
	}
	
	@DeleteMapping("/delete/{fileName}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<String> delete (@PathVariable String fileName){
		
		return new ResponseEntity<>(storageService.delete(fileName), HttpStatus.OK); 
	}
	
	@GetMapping("/list")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<S3ObjectSummary>> fileList(){
		Object[] fileListResponse = storageService.listUploadedFile().toArray();
		
		List<S3ObjectSummary> list = storageService.listUploadedFile();
		list.forEach(i->{
			fileResponse.add(new FileResponse(i.getKey(), i.getLastModified()));
		});
		return new ResponseEntity<>(storageService.listUploadedFile(), HttpStatus.OK);
	}
}
