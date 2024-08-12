package com.ssuamkiett.bookconnect.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void uploadProfilePicture(MultipartFile file, Authentication connectedUser) throws Exception {
        User user = userRepository.findByEmail(((User) connectedUser).getUsername())
                .orElseThrow(() -> new Exception("Error while updating profile picture"));
        user.setProfilePic(file.getBytes());
        userRepository.save(user);
    }

    public byte[] getProfilePicture(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return user.getProfilePic();
    }
}
