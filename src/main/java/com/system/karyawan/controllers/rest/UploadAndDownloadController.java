package com.system.karyawan.controllers.rest;

import com.system.karyawan.models.FileDB;
import com.system.karyawan.models.dto.ResponseFile;
import com.system.karyawan.models.dto.ResponseMessage;
import com.system.karyawan.service.FileStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/upload-download-test")
public class UploadAndDownloadController {

  @Autowired
  private FileStorageService storageService;
  private Logger logger = LoggerFactory.getLogger(UploadAndDownloadController.class);

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ResponseMessage> upload(@RequestParam("file") MultipartFile file) {
    String message = "";
    try {
      storageService.store(file);

      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      logger.info("Uploaded the file successfully: " + file.getOriginalFilename());
      return ResponseEntity.ok().body(new ResponseMessage(message));
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename() + "!";
      logger.error(e.getMessage());
      return ResponseEntity.badRequest().body(new ResponseMessage(message));
    }
  }

  @GetMapping("/list-files")
  public ResponseEntity<List<ResponseFile>> getListFiles() {
    List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
              .fromCurrentContextPath()
              .path("/files/")
              .path(dbFile.getId())
              .toUriString();

      return new ResponseFile(
              dbFile.getId(),
              dbFile.getName(),
              fileDownloadUri,
              dbFile.getType(),
              dbFile.getData().length);
    }).collect(Collectors.toList());

    return ResponseEntity.ok().body(files);
  }

  @GetMapping("/download/{id}")
  public ResponseEntity<byte[]> download(@PathVariable String id) {
    FileDB fileDB = storageService.getFile(id);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
            .body(fileDB.getData());
  }
}
