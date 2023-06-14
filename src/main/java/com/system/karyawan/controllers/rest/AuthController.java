package com.system.karyawan.controllers.rest;

import com.system.karyawan.controllers.scheduller.SchedullerJob;
import com.system.karyawan.models.ERole;
import com.system.karyawan.models.Role;
import com.system.karyawan.models.User;
import com.system.karyawan.payload.request.*;
import com.system.karyawan.payload.response.JwtResponse;
import com.system.karyawan.payload.response.MessageResponse;
import com.system.karyawan.repository.RoleRepository;
import com.system.karyawan.repository.UserRepository;
import com.system.karyawan.security.jwt.JwtUtils;
import com.system.karyawan.service.impl.UserDetailsImpl;
import com.system.karyawan.service.EmailService;
import com.system.karyawan.variable.AppConstant;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private EmailService emailService;
  @Autowired
  private PasswordEncoder passwordEncoder;

  private Logger logger = LoggerFactory.getLogger(AuthController.class);


  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      logger.error("Error: Username is already taken!");
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      logger.error("Error: Email is already in use!");
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),encoder.encode(signUpRequest.getPassword()),"Register");

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "api":
          Role modRole = roleRepository.findByName(ERole.ROLE_API).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    Integer otp = generateOTP();
    emailService.sendMail(user.getEmail(),"OTP REGISTER","Your OTP : "+otp.toString());


    user.setRoles(roles);
    user.setStatus(AppConstant.StatusUser.REGISTER.toString());
    user.setOtp(otp.toString());
    userRepository.save(user);
    logger.info("User registered successfully, Please check your email for activate your account!");
    return ResponseEntity.ok(new MessageResponse("User registered successfully, Please check your email for activate your account!"));
  }

  private static Integer generateOTP() {
    Integer otp = 100000 + new Random().nextInt(900000);
    return otp;
  }

  @PostMapping("/register/validate-otp")
  public ResponseEntity<?> registerOtp(@Valid @RequestBody RegisterValidate registerValidate) {
    Optional<User> user = userRepository.findByOtpAndStatus(registerValidate.getOtp(), AppConstant.StatusUser.REGISTER.toString());
    if(user.isPresent()) {
      User userUpdateStatus = user.get();
      userUpdateStatus.setStatus(AppConstant.StatusUser.ACTIVE.toString());
      userRepository.save(userUpdateStatus);
      logger.info("User registered successfully");
      return ResponseEntity.ok("User registered successfully");
    }else{
      logger.error("OTP not match!");
      return ResponseEntity.badRequest().body(new MessageResponse("OTP not match!"));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
            .collect(Collectors.toList());
    logger.info("login successfully");
    return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPassword forgotPassword) {
    Optional<User> user = userRepository.findByEmail(forgotPassword.getEmail());
    if(user.isPresent()) {
      User userUpdateStatus = user.get();

      Integer otp = generateOTP();
      emailService.sendMail(userUpdateStatus.getEmail(),"OTP FORGOT PASSWORD","Your OTP : "+otp.toString());

      userUpdateStatus.setOtp(otp.toString());
      userUpdateStatus.setStatus(AppConstant.StatusUser.RESET.toString());
      userRepository.save(userUpdateStatus);
      logger.info("Please check otp in your email for re-activate your account");
      return ResponseEntity.ok("Please check otp in your email for re-activate your account");
    }else{
      logger.error("email not found in our system!");
      return ResponseEntity.badRequest().body(new MessageResponse("email not found in our system!"));
    }
  }

  @PostMapping("/forgot-password/validate-otp")
  public ResponseEntity<?> validateOTP(@Valid @RequestBody ForgotPasswordValidate forgotPasswordValidate) {
    Optional<User> user = userRepository.findByOtpAndStatus(forgotPasswordValidate.getOtp(), AppConstant.StatusUser.RESET.toString());
    if (user.isPresent()) {
      User userUpdateStatus = user.get();
      userUpdateStatus.setStatus(AppConstant.StatusUser.ACTIVE.toString());
      userUpdateStatus.setPassword(passwordEncoder.encode(forgotPasswordValidate.getNewPassword()));
      userRepository.save(userUpdateStatus);
      logger.info("Reset password successfully");
      return ResponseEntity.ok("Reset password successfully");
    } else {
      logger.error("OTP not match!");
      return ResponseEntity.badRequest().body(new MessageResponse("OTP not match!"));
    }
  }
}
