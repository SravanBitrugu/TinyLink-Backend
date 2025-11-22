package com.tinylink.backend.service;


import java.util.Random;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tinylink.backend.model.Link;
import com.tinylink.backend.repo.LinkRepo;

@Service
public class LinkService {
	
	@Autowired
	private LinkRepo linkRepo;
	
	public Link saveLink(String url, String code) {
	    Link link = new Link();
	    link.setUrl(url);
	    link.setCode(code);
	    link.setClicks(0);
	    return linkRepo.save(link);
	}
	
	public String generateRandomCode() {
		String character="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random=new Random();
		return random.ints(6,0,character.length())
				.mapToObj(i -> ""+character.charAt(i))
				.collect(Collectors.joining());
	}
	
	public boolean codeExists(String code) {
	    return linkRepo.findByCode(code).isPresent();
	}


}
