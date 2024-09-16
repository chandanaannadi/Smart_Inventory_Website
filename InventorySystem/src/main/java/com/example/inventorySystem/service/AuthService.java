package com.example.inventorySystem.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.example.inventorySystem.dto.UserDto;
import com.example.inventorySystem.dto.forms.LoginForm;
import com.example.inventorySystem.dto.forms.SignUpForm;
import com.example.inventorySystem.dto.forms.UserProfileForm;
import com.example.inventorySystem.entity.User;
import com.example.inventorySystem.liveChat.user.Status;
import com.example.inventorySystem.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserDto authenticateUser(HttpServletRequest request, HttpSession httpSession) {

        String sessionToken = httpSession.getAttribute("session-token") != null
                ? httpSession.getAttribute("session-token").toString()
                : null;

        if (sessionToken != null) {
            User user = userRepository.findBySessionToken(sessionToken);
            if (user != null)
                return mapUserDto(user);
            return null;
        }

        Cookie[] cookies = request.getCookies();
        String rememberMeToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me-cookie".equals(cookie.getName())) {
                    rememberMeToken = cookie.getValue();
                    break;
                }
            }
        }

        if (rememberMeToken != null) {
            User user = userRepository.findByRememberMeToken(rememberMeToken);
            if (user != null) {
                sessionToken = UUID.randomUUID().toString();
                // we are creating random session and setting it to the user
                user.setSessionToken(sessionToken);
                userRepository.save(user);
                return mapUserDto(user);
            }
        }
        return null;
    }

    @SuppressWarnings("static-access")
    public User getUserByRememberMeToken(String rememberMeToken) {
        return new User().builder()
                .id(1l)
                .userName("asjid")
                .password("password")
                .rememberMeToken("remembertoken")
                .sessionToken("sessiontoken")
                .phone("03044115263")
                .name("User name")
                .build();
    }

    @SneakyThrows
    public UserDto login(LoginForm loginForm) {
        User user = userRepository.findByUserName(loginForm.getUsername());
        if (user == null || !user.getPassword().equals(loginForm.getPassword()))
            throw new AccessDeniedException("Invalid Credentials");

        user.setSessionToken(UUID.randomUUID().toString());
        if (loginForm.getRememberMe())
            user.setRememberMeToken(UUID.randomUUID().toString());

        userRepository.save(user);
        return mapUserDto(user);
    }

    @SneakyThrows
    public UserDto updateUserProfile(UserProfileForm userProfileForm, MultipartFile file) {
        User user = userRepository.findById(userProfileForm.getId())
                .orElseThrow(() -> new Exception("Invalid user"));

        if (!StringUtils.isEmpty(userProfileForm.getNewPassword())
                || !StringUtils.isEmpty(userProfileForm.getConfirmPassword())
                || !StringUtils.isEmpty(userProfileForm.getCurrentPassword())) {
            if (!userProfileForm.getCurrentPassword().equals(user.getPassword()))
                throw new Exception("Invalid current password");

            if (!userProfileForm.getConfirmPassword().equals(userProfileForm.getNewPassword()))
                throw new Exception("New password and confirm password must be same");
            user.setPassword(userProfileForm.getNewPassword());
        }


        user.setName(userProfileForm.getName());
        user.setPhone(userProfileForm.getPhoneNumber());

        if (!file.isEmpty())
            user.setImage(file.getBytes());

        userRepository.save(user);

        return mapUserDto(user);
    }

    @SneakyThrows
    public void signUp(SignUpForm signUpForm) {
        User user = userRepository.findByUserName(signUpForm.getUserName());
        if (user != null)
            throw new Exception("User already exists");
        if (StringUtils.isEmpty(signUpForm.getPassword())
                || StringUtils.isEmpty(signUpForm.getPassword())
                || !signUpForm.getPassword().equals(signUpForm.getConfirmPassword()))
            throw new Exception("Please provide valid password and confirm password");
        userRepository.save(User.builder()
                .userName(signUpForm.getUserName())
                .password(signUpForm.getPassword())
                .role("CUSTOMER")
                .status(Status.ONLINE)
                .build());
    }

    private UserDto mapUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .name(user.getName())
                .phoneNumber(user.getPhone())
                .sessionToken(user.getSessionToken())
                .rememberMeToken(user.getRememberMeToken())
                .image(user.getImage())
                .role(user.getRole())
                .status(user.getStatus())
                .warehouseId(user.getWarehouseId())
                .warehouseName(user.getWarehouseName())
                .build();
    }

    @SneakyThrows
    public void changePassword(UserProfileForm userProfileForm) {
        User user = userRepository.findByUserName(userProfileForm.getUserName());
        if (user == null)
            throw new Exception("User does not exists");
        if (!userProfileForm.getCode().equalsIgnoreCase("1234"))
            throw new Exception("Invalid code");
        if (!userProfileForm.getNewPassword().equals(userProfileForm.getConfirmPassword()))
            throw new Exception("Password and confirm password are not same");

        user.setPassword(userProfileForm.getNewPassword());
        userRepository.save(user);
    }

    @SneakyThrows
    public List<UserDto> getAllUsers(){
    	List<UserDto> usersList =  new ArrayList<UserDto>();
    	List<User> users = userRepository.findByRole("CUSTOMER");
    	if(!users.isEmpty()) {
    		for(User user: users) {
    			usersList.add(mapUserDto(user));
    		}
    	}
    	return usersList;
    }
}
