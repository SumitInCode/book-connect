package com.ssuamkiett.bookconnect.file;

import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReadService {
    private static final Logger logger = LoggerFactory.getLogger(FileReadService.class);

    public static byte[] readFileFormatLocation(String filePathString) {
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
}
