package com.system.karyawan.payload.request;

import jakarta.validation.constraints.NotBlank;

public class RegisterValidate {
  @NotBlank
  private String otp;

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
