package com.polanco.updown.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	
	public void init();
	
	public void save(MultipartFile file);
	
	public Resource load(String fileName);
	
	public void deleteAll();
	
	public void delete(String fileName);
	
	public Stream<Path> loadAll();
	
}
