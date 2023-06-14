package com.system.karyawan.service.impl;

import com.system.karyawan.models.User;
import com.system.karyawan.repository.UserRepository;
import com.system.karyawan.variable.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    if(!user.getStatus().equals(AppConstant.StatusUser.ACTIVE.toString())){
      throw new RuntimeException("User Not Active");
    }
    return UserDetailsImpl.build(user);
  }

}
