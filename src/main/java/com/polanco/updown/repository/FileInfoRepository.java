package com.polanco.updown.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.polanco.updown.entity.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long>,
JpaSpecificationExecutor<FileInfo>{

	Optional<FileInfo> findByFilename (String filename);
}
