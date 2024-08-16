package com.ssuamkiett.bookconnect.user;

import com.ssuamkiett.bookconnect.file.FileReadService;
import com.ssuamkiett.bookconnect.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;

    public void uploadProfilePicture(MultipartFile file, Authentication connectedUser) throws Exception {
        User user = userRepository.findByEmail(((User) connectedUser.getPrincipal()).getUsername())
                .orElseThrow(() -> new Exception("Error while updating profile picture"));
        String profilePhotoPath = fileService.saveFile(file, user.getId(), user.getFirstName());
        user.setProfilePic(profilePhotoPath);
        userRepository.save(user);
    }

    public byte[] getProfilePicture(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return FileReadService.readFileFormatLocation(user.getProfilePic());
    }
}
