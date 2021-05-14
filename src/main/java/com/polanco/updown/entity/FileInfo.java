package com.polanco.updown.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NamedQuery(name = "FileInfo.findByFilename", 
query = "SELECT f FROM FileInfo f WHERE LOWER(f.name) = LOWER(?1)")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class FileInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String url;
	
	public FileInfo(String filename, String url) {
		this.name = filename;
		this.url = url;
	}
	@ManyToOne()
	@JoinColumn(name="user_id")
	private User user;
	
	private Date experationDate;
}
