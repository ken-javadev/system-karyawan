package com.system.karyawan.controllers.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/role-access-test")
public class RoleAccessController {

  @GetMapping("/user")
  @SecurityRequirement(name = "Bearer Authentication")
  @PreAuthorize("hasRole('USER') or hasRole('API') or hasRole('ADMIN')")
  public String userAccess() {
    return "THIS IS ROLE USER or API or ADMIN";
  }


  @GetMapping("/api")
  @SecurityRequirement(name = "Bearer Authentication")
  @PreAuthorize("hasRole('API')")
  public String apiAccess() {
    return "THIS IS ROLE API";
  }

  @GetMapping("/admin")
  @SecurityRequirement(name = "Bearer Authentication")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "THIS IS ROLE ADMIN";
  }
}
