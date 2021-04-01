package com.polanco.updown.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.polanco.updown.entity.FileInfo;
import com.polanco.updown.payload.response.FileResponse;
import com.polanco.updown.payload.response.MessageResponse;
import com.polanco.updown.service.StorageServiceImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/file")
public class StorageController {

	@Autowired
	private StorageServiceImpl storageService;
	
	
	@PostMapping("/upload")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<MessageResponse> upload (@RequestParam(value="file")MultipartFile[] files) throws IOException{
		String message = "";
		
		try {
			List<String> fileNames = new ArrayList<>();
			Arrays.asList(files).stream().forEach(file ->{
				storageService.save(file);
				fileNames.add(file.getOriginalFilename());
			});
			
			message = "Archivo subido con exito: "+fileNames;
			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
		}catch(Exception e) {
			message = "Error al subir el archivo";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
		}
		
	}
	
	@GetMapping("/download/{fileName}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Resource> download(@PathVariable String fileName){
		
		Resource data = storageService.load(fileName);
		System.out.println("Se ha llamado para descargar");
		return ResponseEntity.ok()
				.header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\""+ fileName+ "\"")
				.body(data);
	}
	
	
	@DeleteMapping("/delete/{fileName}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<MessageResponse> delete (@PathVariable String fileName){
		String message = "";
		try {
			storageService.delete(fileName);
			message = "Archivo eliminado con exito";
			return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
		}catch(Exception e) {
			message = "Error al elimianr el archivo";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
		}
		 
	}
	
	@GetMapping("/list")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<FileInfo>> fileList(){
		  List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
		      String filename = path.getFileName().toString();
		      String url = MvcUriComponentsBuilder
		          .fromMethodName(StorageController.class, "download", path.getFileName().toString()).build().toString();

		      return new FileInfo(filename, url);
		    }).collect(Collectors.toList());

		    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	}
}
