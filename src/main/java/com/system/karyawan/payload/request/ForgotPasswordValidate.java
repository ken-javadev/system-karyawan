package com.system.karyawan.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ForgotPasswordValidate {

  @NotBlank
  private String otp;

  @NotBlank
  private String newPassword;

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
