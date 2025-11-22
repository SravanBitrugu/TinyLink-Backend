	package com.tinylink.backend.model;
	
	import java.time.LocalDateTime;
	
	import jakarta.persistence.Column;
	import jakarta.persistence.Entity;
	import jakarta.persistence.Id;
	import lombok.Data;
	
	@Entity
	@Data
	public class Link {
	
		@Id
		private String code;
		
		@Column(nullable = false , columnDefinition = "text")
		private String url;
		
		private int clicks=0;
		private LocalDateTime lastClicked;
		private LocalDateTime createdAt=LocalDateTime.now();
		
	}

