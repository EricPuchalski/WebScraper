package com.scraper.WebScraperAuth.controller;


import com.scraper.WebScraperAuth.client.ClientApi;
import com.scraper.WebScraperAuth.client.CreateClientRequest;
import com.scraper.WebScraperAuth.dto.request.LoginRequest;
import com.scraper.WebScraperAuth.dto.request.SignupRequest;
import com.scraper.WebScraperAuth.dto.response.MessageResponse;
import com.scraper.WebScraperAuth.jwt.JwtUtils;
import com.scraper.WebScraperAuth.model.ERole;
import com.scraper.WebScraperAuth.model.Role;
import com.scraper.WebScraperAuth.model.User;
import com.scraper.WebScraperAuth.repository.RoleRepository;
import com.scraper.WebScraperAuth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ClientApi clientService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, ClientApi clientService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.clientService = clientService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(Collections.singletonMap("token", jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        if (signUpRequest.getDni() != null && userRepository.existsByDni(signUpRequest.getDni())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: dni is already in use!"));
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getDni()
        );

        String rawRole = signUpRequest.getRole();
        String reqRole = (rawRole == null || rawRole.isBlank())
                ? "client"
                : rawRole.trim().toLowerCase();

        Role role = "admin".equals(reqRole)
                ? roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."))
                : roleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        user.setRole(role);
        user = userRepository.save(user);

        log.info("New user registered: {}", user.toString());
        if (role.getName() == ERole.ROLE_CLIENT) {
            clientService.createClient(
                    CreateClientRequest.builder().
                            dni(user.getDni())
                            .userId(user.getId())
                            .name(signUpRequest.getName())
                            .lastName(signUpRequest.getLastName())
                            .build()
            );
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }




}
