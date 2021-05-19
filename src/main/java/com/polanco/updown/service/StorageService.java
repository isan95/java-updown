package com.polanco.updown.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;


import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import com.polanco.updown.entity.FileInfo;
import com.polanco.updown.payload.response.PagingResponse;

public interface StorageService {
	
	public void init();
	
	public void save(MultipartFile file, String expirationDate);
	
	public Resource load(String fileName);
	
	public void deleteAll();
	
	public void delete(String fileName);
	
	public Stream<Path> loadAll();
	
	public PagingResponse get(Specification<FileInfo> spec, HttpHeaders headers, Sort sort);
	
	public FileInfo findByFilename(String filename);
	
	public LocalDateTime testDate(String date);
	
}
