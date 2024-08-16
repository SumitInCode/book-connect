package com.ssuamkiett.bookconnect.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

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
        return saveFileToPath(sourceFile, bookTargetPath);
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
        return saveFileToPath(sourceFile, userTargetPath);
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

    public void ensureDirExists(Path dirPath) throws IOException {
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    private String saveFileToPath(MultipartFile sourceFile, Path targetPath) {
        try {
            Files.write(targetPath, sourceFile.getBytes());
            return targetPath.toString();
        } catch (IOException e) {
            logger.warn("Could not save file to path: {}", targetPath, e);
            return null;
        }
    }
}
