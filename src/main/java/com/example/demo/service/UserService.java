package com.example.demo.service;

import com.example.demo.model.JwtResponse;
import com.example.demo.model.Response;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Hash;
import com.example.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Response<List<User>>> getAllUsers(String authHeader) {
        Response<List<User>> response = new Response<>();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Authorization header is missing or invalid.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authHeader.substring(7);
        if (!JwtUtils.validateJwtToken(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Invalid or expired token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<User> usersResponse = userRepository.findAll();
        if (usersResponse.isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setPayload(usersResponse);
            response.setMessage("Users data is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            response.setStatusCode(HttpStatus.OK.value());
            response.setPayload(usersResponse);
            response.setMessage("Users retrieved successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    public ResponseEntity<Response<User>> createUser(User user) {
        Response<User> response = new Response<>();

        Optional<User> checkEmail = userRepository.findByEmail(user.getEmail());
        if (checkEmail.isEmpty()) {
            user.setPassword(Hash.md5Hash(user.getPassword()));
            User savedUser = userRepository.save(user);

            response.setStatusCode(HttpStatus.CREATED.value());
            response.setPayload(savedUser);
            response.setMessage("User created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage("User cannot be created, email is duplicate");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    public ResponseEntity<Response<JwtResponse>> loginUser(User userReq) {
        Response<JwtResponse> response = new Response<>();

        if (userReq.getPassword().isEmpty() || userReq.getEmail().isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Password and Email cannot be empty!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<User> loginUserData = userRepository.findByEmail(userReq.getEmail());
        if (loginUserData.isPresent()) {
            if (loginUserData.get().getEmail().equals(userReq.getEmail()) &&
                    loginUserData.get().getPassword().equals(Hash.md5Hash(userReq.getPassword()))) {

                // Generate JWT token
                String jwtToken = JwtUtils.generateJwtToken(userReq);
                JwtResponse jwtResponse = new JwtResponse(jwtToken);

                response.setStatusCode(HttpStatus.OK.value());
                response.setPayload(jwtResponse);
                response.setMessage("Login Success");

                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                response.setMessage("Something went wrong! Wrong email and password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("User is not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
