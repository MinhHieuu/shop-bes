package com.beeshop.sd44.controller;

import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class FileController {
    @Value("${beeshop.upload-file.base-uri}")
    private String baseUri;
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @PostMapping("files")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file,
                                 @RequestParam(value = "folder", required = false, defaultValue = "") String folder) throws URISyntaxException {
        this.fileService.createDirectory(baseUri);
        String uploadFile = this.fileService.upload(file, folder);
        return ResponseEntity.ok().body(new ApiResponse<>("upload success", uploadFile));
    }

    @PostMapping("multiple")
    public ResponseEntity<?> uploadMultiple(@RequestParam("file") MultipartFile[] files,
                                            @RequestParam(value = "folder", required = false, defaultValue = "") String folder) throws URISyntaxException {
        this.fileService.createDirectory(baseUri);
        List<String> list = this.fileService.uploads(files, folder);
        return ResponseEntity.ok().body(new ApiResponse<>("upload success", list));
    }
}
