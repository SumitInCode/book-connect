package com.ssuamkiett.bookconnect.user;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/profile-pic", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser) throws Exception {
        userService.uploadProfilePicture(file, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/profile-pic")
    public ResponseEntity<?> getProfilePic(Authentication connectedUser) {
        byte[] file = userService.getProfilePicture(connectedUser);
        return ResponseEntity.status(HttpStatus.OK).
                contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
