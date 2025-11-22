package com.tinylink.backend.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;


import com.tinylink.backend.model.Link;
import com.tinylink.backend.repo.LinkRepo;
import com.tinylink.backend.service.LinkService;

@RestController
@CrossOrigin("https://tinylink-frontend-07pe.onrender.com")
public class LinkController {
    
    @Autowired
    private LinkService service;

    @Autowired
    private LinkRepo linkRepo;

    @Value("${app.base.url}")
    private String baseUrl;
    
    @PostMapping("/api/links")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> body) {

        String originalUrl = body.get("url");
        String custom = body.get("customCode");

        if (originalUrl == null || originalUrl.isBlank() || originalUrl.equals("undefined")) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "URL is required")
            );
        }

        String code;

        if (custom != null && !custom.isBlank() && !custom.equals("undefined")) {

            boolean exists = service.codeExists(custom);

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Custom code already used! Try another."));
            }

            code = custom;
        } 
        else {
            code = service.generateRandomCode();

            while (service.codeExists(code)) {
                code = service.generateRandomCode();
            }
        }

        service.saveLink(originalUrl, code);
        

        return ResponseEntity.ok(
            Map.of(
                "code", code,
                "shortUrl", baseUrl + "/" + code
            )
        );
    }

    @GetMapping("/api/links")
    public List<Link> listLinks() {
        return linkRepo.findAll();
    }

    @GetMapping("/api/links/{code}")
    public ResponseEntity<?> getStats(@PathVariable String code) {
        return linkRepo.findById(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/links/{code}")
    public ResponseEntity<?> deleteLink(@PathVariable String code) {
        if (!linkRepo.existsById(code)) {
            return ResponseEntity.notFound().build();
        }
        linkRepo.deleteById(code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {
        Optional<Link> optional = linkRepo.findById(code);

        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body("Not found");
        }

        Link link = optional.get();
        link.setClicks(link.getClicks() + 1);
        link.setLastClicked(LocalDateTime.now());
        linkRepo.save(link);

        return ResponseEntity
                .status(302)
                .location(URI.create(link.getUrl()))
                .build();
    }

    @GetMapping("/healthz")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("version", "1.0");
        return response;
    }
}


