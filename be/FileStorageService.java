package com.rpa.rpaui.services;

import com.rpa.rpaui.entities.JobRelated;
import com.rpa.rpaui.entities.JobRisk;
import com.rpa.rpaui.exceptions.ExceptionRpa;
import com.rpa.rpaui.repositories.JobRelatedRepository;
import com.rpa.rpaui.repositories.JobRiskRepository;
import com.rpa.rpaui.utils.RpaConstant;
import com.rpa.rpaui.utils.RpaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
  protected static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

  @Value("${file.upload-dir}")
  private String uploadDir;

  @Value("${file.upload-size}")
  private Integer maxSize;

  @Autowired
  JobRelatedRepository jobRelatedRepository;

  @Autowired
  JobRiskRepository jobRiskRepository;

  private static String getFileExtension(String fileName) {
    if (fileName == null) {
      throw new IllegalArgumentException("fileName must not be null!");
    }

    String extension = "";

    int index = fileName.lastIndexOf('.');
    if (index > 0) {
      extension = fileName.substring(index + 1);
    }

    return extension;
  }

  public List<String> store(MultipartFile[] files, String folder) throws Exception {
    if (files.length == 0) {
      return null;
    }
    List<String> urls = new ArrayList<>();
    for (MultipartFile file : files) {
      String url = upload(file, folder);
      urls.add(url);
    }
    return urls;
  }

  public String upload(MultipartFile file, String folder) {
    if (folder == null || !RpaConstant.FOLDERS.contains(folder)) {
      throw new ExceptionRpa(HttpStatus.BAD_REQUEST, RpaMessage.FOLDER_NOT_EXISTS);
    }
    double fileSize = Objects.isNull(file) ? 0 : (file.getSize() / 1024 / 1024);
    if (fileSize > maxSize) {
      throw new ExceptionRpa(HttpStatus.BAD_REQUEST, RpaMessage.UPLOAD_MAX_SIZE, maxSize + "MB");
    }
    String fileName =
        StringUtils.cleanPath(file.getOriginalFilename());
    LOG.info(String.format("Upload file name: %s", fileName));

    try {
      if (fileName.contains("..")) {
        throw new ExceptionRpa(HttpStatus.BAD_REQUEST, RpaMessage.UPLOAD_FILE_EXISTS, fileName);
      }
      String randomFolder = UUID.randomUUID().toString();
      String uploadFolder = uploadDir + "/" + folder + "/" + randomFolder + "/";

      File f = new File(uploadFolder + fileName);
      if (f.exists()) {
        throw new ExceptionRpa(HttpStatus.BAD_REQUEST, RpaMessage.UPLOAD_FILE_EXISTS, fileName);
      }

      Path fileDir = Paths.get(uploadFolder).toAbsolutePath().normalize();
      Files.createDirectories(fileDir);
      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = fileDir.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      LOG.info("Upload file successful");
      return randomFolder + "/" + fileName;
    } catch (IOException ex) {
      throw new ExceptionRpa(HttpStatus.BAD_REQUEST, RpaMessage.UPLOAD_FILE_EXISTS, fileName);
    }
  }

  public byte[] getFile(String filePath) throws NoSuchFileException {
    try {
      Path path = Paths.get(uploadDir + "/" + filePath);

      return Files.readAllBytes(path);
    } catch (Exception e) {
      throw new NoSuchFileException("No such file");
    }
  }

  public boolean deleteFile(String folder, String subFolder, String fileName, Integer updateDB) {
    try {
      String filePath = String.format("%s/%s/%s/%s", uploadDir, folder, subFolder, fileName);
      Path fileDir = Paths.get(filePath).toAbsolutePath().normalize();
      Files.deleteIfExists(fileDir);

      JobRelated jobRelated = null;
      JobRisk jobRisks = null;
      switch (updateDB) {
        case 1:
          jobRelated = jobRelatedRepository.findByAttachment1(fileName);
          if(jobRelated != null) {
              jobRelated.setAttachment1(null);
              jobRelatedRepository.save(jobRelated);
          }
          break;
        case 2:
          jobRelated = jobRelatedRepository.findByAttachment2(fileName);
            if(jobRelated != null) {
                jobRelated.setAttachment2(null);
                jobRelatedRepository.save(jobRelated);
            }
          break;
        case 3:
          jobRisks = jobRiskRepository.findByAttachment1(fileName);
          if (jobRisks != null) {
            jobRisks.setAttachment1(null);
            jobRiskRepository.save(jobRisks);
          }
          break;
        case 4:
          jobRisks = jobRiskRepository.findByAttachment2(fileName);
          if (jobRisks != null) {
            jobRisks.setAttachment2(null);
            jobRiskRepository.save(jobRisks);
          }
          break;
      }

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void downloadFile(HttpServletResponse response, Integer id, Integer type) throws ServletException, IOException {
    JobRelated jobRelated = jobRelatedRepository.findByJobId(id);
    JobRisk jobRisks = jobRiskRepository.findByJobId(id);
    String fileName = "";
    if(jobRelated != null) {
      if (type == 1) {
        fileName = jobRelated.getAttachment1();
      }
      if (type == 2) {
        fileName = jobRelated.getAttachment2();
      }
    }
    if(jobRisks != null){
      if (type == 3) {
        fileName = jobRisks.getAttachment1();
      }
      if (type == 4) {
        fileName = jobRisks.getAttachment2();
      }
    }

    String dir = uploadDir + File.separator + RpaConstant.RPA_SPEC_FOLDER + File.separator + fileName;
    File file = new File(dir);
    if(!file.exists()){
      throw new ServletException("File doesn't exists on server.");
    }
    LOG.info(String.format("File location on server: %s", file.getAbsolutePath()));

    String downloadName = fileName.substring(fileName.lastIndexOf('/') + 1);

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setContentLength((int) file.length());
    response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(downloadName, StandardCharsets.UTF_8) + "\"");
    response.setHeader("charset", "iso-8859-1");
    response.setStatus(HttpServletResponse.SC_OK);

    FileInputStream fis = new FileInputStream(file);
    OutputStream os = response.getOutputStream();
    byte[] bufferData = new byte[1024];
    int read=0;
    while((read = fis.read(bufferData)) > 0){
      os.write(bufferData, 0, read);
    }
    os.flush();
    os.close();
    fis.close();
    LOG.info(String.format("Download file completed: %s", fileName));
  }
}
