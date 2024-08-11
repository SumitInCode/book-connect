package com.ssuamkiett.BookConnect.file;

import com.ssuamkiett.BookConnect.book.Book;
import com.ssuamkiett.BookConnect.book.BookRepository;
import com.ssuamkiett.BookConnect.exception.OperationNotPermittedException;
import com.ssuamkiett.BookConnect.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final FileRepository fileRepository;
    private final BookRepository bookRepository;
    @Value("${springdoc.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@NonNull MultipartFile sourceFile, @NonNull Integer userId) {
        final String fileUploadSubPath = "users" + java.io.File.separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile, @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + java.io.File.separator + fileUploadSubPath;
        java.io.File targetFile = new java.io.File(finalUploadPath);
        if (!targetFile.exists()) {
            boolean dirCreated = targetFile.mkdirs();
            if (!dirCreated) {
                log.warn("Could not create directory for uploading file");
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = fileUploadPath + java.io.File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            logger.info("File saved to {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            logger.error("Unable to save file", e);
        }
        return null;
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

    @Transactional
    public String uploadFileInDB(MultipartFile sourceFile, Integer bookId, Authentication connectedUser) throws IOException {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with the ID : " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Operation not permitted for this user");
        }
        File lFile = File.builder()
                .id(bookId)
                .name(book.getTitle())
                .type(sourceFile.getContentType())
                .data(sourceFile.getBytes())
                .build();
        fileRepository.save(lFile);
        bookRepository.save(book);
        return "File Uploaded Successfully";
    }

    public byte[] getFileFromDB(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with the ID : " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if(book.isArchived() || !book.isShareable()) {
            if(!Objects.equals(book.getOwner().getId(), user.getId())) {
                throw new OperationNotPermittedException("Operation not permitted");
            }
        }
        Optional<File> file = fileRepository.findById(bookId);
        return file.map(File::getData).orElseThrow(() -> new EntityNotFoundException("No file found with the ID : " + bookId));
    }
}
