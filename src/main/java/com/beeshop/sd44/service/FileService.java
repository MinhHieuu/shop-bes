package com.beeshop.sd44.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    @Value("${beeshop.upload-file.base-uri}")
    private String baseUri;
    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if(!tmpDir.exists()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println("CREATE NEW DIRECTORY PATH =" + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }
    }

    public String upload(MultipartFile file, String folder) throws URISyntaxException {
        if(file.isEmpty()) {
            return "";
        }
        // Sanitize filename: replace spaces and special URI-unsafe characters with underscores
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String safeName = originalName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._\\-]", "_");
        String finalName = System.currentTimeMillis() + "-" + safeName;
        // Save directly to baseUri root so files are accessible at /images/<filename>
        URI uri = new URI(baseUri + finalName);
        Path path = Paths.get(uri);
        try {
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    return finalName;
    }

    public List<String> uploads(MultipartFile[] files, String folder) throws URISyntaxException {
        List<String> listImg = new ArrayList<>();
        for(MultipartFile file : files) {
            String img = upload(file, folder);
            listImg.add(img);
        }
        return listImg;
    }
}
