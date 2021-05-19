package com.polanco.updown.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.threeten.bp.LocalDate;

import com.polanco.updown.entity.FileInfo;
import com.polanco.updown.entity.User;
import com.polanco.updown.entity.util.PagingHeader;
import com.polanco.updown.exception.ResourceNotFoundException;
import com.polanco.updown.filter.UserSesion;
import com.polanco.updown.payload.response.PagingResponse;
import com.polanco.updown.repository.FileInfoRepository;
import com.polanco.updown.repository.UserRepository;
import com.polanco.updown.util.JwtUtil;

@Service

public class StorageServiceImpl implements StorageService {

	private final Path root = Paths.get("Files");

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private FileInfoRepository fileInfoRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Override
	public void init() {

		try {
			Files.createDirectory(root);
		} catch (IOException e) {
			throw new RuntimeException("No se pudo crear el directorio");
		}

	}

	@Override
	public void save(MultipartFile file, String expirationDate) {

		FileInfo fileInfo = new FileInfo();
		String username = UserSesion.getCurrentUsername();
		User user = userRepository.findByUsername(username).orElseThrow(
				()-> new ResourceNotFoundException("Usuario no encontrado"));

		Path path = root.resolve(file.getOriginalFilename());

				
		
		try {
			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));

			fileInfo.setName(file.getOriginalFilename());
			fileInfo.setUrl(path.toString());
			fileInfo.setUser(user);
			fileInfo.setExpirationDate(LocalDateTime.parse(expirationDate+"T00:00:00"));
			
			fileInfoRepository.save(fileInfo);
		} catch (/*IOException*/ Exception e) {
			throw new RuntimeException("No se pudo almacenar el archivo " + e.getMessage());
		}

	}

	@Override
	public Resource load(String fileName) {

		try {
			Path file = root.resolve(fileName);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("No fue posible leer el archivo");
			}

		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {

		FileSystemUtils.deleteRecursively(root.toFile());
		fileInfoRepository.deleteAll();
	}

	@Override
	public void delete(String filename) {
		try {
			Path file = root.resolve(filename);
			Files.delete(file);
			FileInfo fileInfo =  fileInfoRepository.findByFilename(filename).orElseThrow(
					() -> new ResourceNotFoundException("Archivo no encontrado"));
			fileInfoRepository.delete(fileInfo);
		} catch (IOException e) {
			throw new RuntimeException("Error al eliminar el archivo " + e.getMessage());
		}

	}
	
	@Override
	public FileInfo findByFilename(String filename) {
		
		FileInfo file = fileInfoRepository.findByFilename(filename).orElseThrow(() ->
		new ResourceNotFoundException("Archivo no encontrado"));
		
		return file;
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
		} catch (IOException e) {
			throw new RuntimeException("No se pueden cargar los archivos");
		}
	}

	@Override
	public PagingResponse get(Specification<FileInfo> spec, HttpHeaders headers, Sort sort) {
		if (isRequestPaged(headers)) {
			return get(spec, buildPageRequest(headers, sort));
		} else {
			final List<FileInfo> entities = get(spec, sort);
			return new PagingResponse((long) entities.size(), 0L, 0L, 0L, 0L, entities);
		}
	}

	private boolean isRequestPaged(HttpHeaders headers) {
		return headers.containsKey(PagingHeader.PAGE_NUMBER.getName())
				&& headers.containsKey(PagingHeader.PAGE_SIZE.getName());
	}

	private Pageable buildPageRequest(HttpHeaders headers, Sort sort) {
		int page = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeader.PAGE_NUMBER.getName())).get(0));
		int size = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeader.PAGE_SIZE.getName())).get(0));
		return PageRequest.of(page, size, sort);
	}

	public PagingResponse get(Specification<FileInfo> spec, Pageable pageable) {
		Page<FileInfo> page = fileInfoRepository.findAll(spec, pageable);
		List<FileInfo> content = page.getContent();
		return new PagingResponse(page.getTotalElements(), (long) page.getNumber(), (long) page.getNumberOfElements(),
				pageable.getOffset(), (long) page.getTotalPages(), content);
	}

	public List<FileInfo> get(Specification<FileInfo> spec, Sort sort) {
		return fileInfoRepository.findAll(spec, sort);
	}

	@Override
	public LocalDateTime testDate(String date) {
		//OffsetDateTime odt = OffsetDateTime.parse(expirationDate+"T00:00:00");
		LocalDateTime aux = LocalDateTime.parse(date);	
		return aux;
	}

}
