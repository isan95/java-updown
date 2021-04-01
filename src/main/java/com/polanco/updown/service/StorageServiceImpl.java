package com.polanco.updown.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.polanco.updown.entity.FileInfo;
import com.polanco.updown.entity.User;
import com.polanco.updown.repository.FileInfoRepository;
import com.polanco.updown.repository.UserRepository;
import com.polanco.updown.util.JwtUtil;



@Service

public class StorageServiceImpl implements StorageService{
	
	private final Path root = Paths.get("Files");
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private FileInfoRepository fileInfoRepository;
	
	@Autowired 
	UserRepository userRepository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Override
	public void init() {
		
		try {
			Files.createDirectory(root);
		}catch(IOException e) {
			throw new RuntimeException("No se pudo crear el directorio"); 
		}
		
	}

	@Override
	public void save(MultipartFile file) {
		
		FileInfo fileInfo = new FileInfo();
		User user = new User();
		
		Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (auth instanceof UserDetails) {
			String username = ((UserDetails)auth).getUsername();
			user.setUsername(username);
			}
		
		Path path = root.resolve(file.getOriginalFilename());
		
		try {
			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
			
			fileInfo.setName(file.getOriginalFilename());
			fileInfo.setUrl(path.toString());
			//fileInfo.setUser(user);
			fileInfoRepository.save(fileInfo);
		}catch(IOException e) {
			throw new RuntimeException("No se pudo almacenar el archivo "+e.getMessage());
		}
		
	}

	@Override
	public Resource load(String fileName) {
		
		try {
			Path file = root.resolve(fileName);
			Resource resource = new UrlResource(file.toUri());
			
			if(resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new RuntimeException("No fue posible leer el archivo");
			}
			
		}catch(MalformedURLException e) {
			throw new RuntimeException("Error: "+e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		
		FileSystemUtils.deleteRecursively(root.toFile());
	}
	
	@Override
	public void delete(String filename) {
		try {
			Path file = root.resolve(filename);
			Files.delete(file);
		}catch(IOException e) {
			throw new RuntimeException ("Error al eliminar el archivo "+ e.getMessage());
		}
		
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root :: relativize);
		}catch(IOException e) {
			throw new RuntimeException("No se pueden cargar los archivos");
		}
	}
	
	
	

}
