package com.polanco.updown.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polanco.updown.entity.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long>{

}
