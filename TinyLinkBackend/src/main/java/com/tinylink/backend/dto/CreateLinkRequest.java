package com.tinylink.backend.dto;

import lombok.Data;

@Data
public class CreateLinkRequest {

	private String url;
	private String code;
}
