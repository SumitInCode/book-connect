package com.ssuamkiett.bookconnect.file;

import io.micrometer.common.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class StorageService {
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    @Value("${springdoc.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@NonNull MultipartFile sourceFile, @NonNull Integer userId, @NonNull Integer bookId, FileType fileType)  {
        String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        Path bookSubPath = Paths.get(fileUploadPath, userId.toString(), "books", bookId.toString());
        try {
            ensureDirExists(bookSubPath);
        }
        catch (IOException exception) {
            logger.warn("Error creating dir {}", bookSubPath, exception);
        }
        String fileName = (fileType == FileType.BOOK_COVER_PHOTO) ? "cover_photo" : "bookPDF";
        Path bookTargetPath = bookSubPath.resolve(fileName + "." + fileExtension);
        return writeFileToPath(sourceFile, bookTargetPath);
    }

    public String saveFile(@NonNull MultipartFile sourceFile, @NonNull Integer userId, @NonNull String userName) {
        String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        Path userSubPath = Paths.get(fileUploadPath, userId.toString(), "profile");
        try {
            ensureDirExists(userSubPath);
        }
        catch (IOException exception) {
            logger.warn("Error creating dir {}", userSubPath, exception);
        }
        Path userTargetPath = userSubPath.resolve(userName.toLowerCase() + "." + fileExtension);
        return writeFileToPath(sourceFile, userTargetPath);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    private void ensureDirExists(Path dirPath) throws IOException {
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    private String writeFileToPath(MultipartFile sourceFile, Path targetPath) {
        try {
            Files.write(targetPath, sourceFile.getBytes());
            return targetPath.toString();
        } catch (IOException e) {
            logger.warn("Could not save file to path: {}", targetPath, e);
            return null;
        }
    }

    public byte[] readFileFromLocation(String filePathString) {
        if (StringUtils.isBlank(filePathString)) {
            logger.warn("File path is blank or null.");
            return null;
        }
        Path filePath = Paths.get(filePathString);
        try {
            return Files.readAllBytes(filePath);
        }
        catch (NoSuchFileException e) {
            logger.warn("File not found: {}", filePathString, e);
        }
        catch (IOException e) {
            logger.warn("Failed to read file: {}", filePathString, e);
        }
        return null;
    }

    public String getFullFilePath(String relativePath) {
        if(relativePath == null) {
            return null;
        }
        return Paths.get(relativePath).toAbsolutePath().normalize().toString();
    }
}
