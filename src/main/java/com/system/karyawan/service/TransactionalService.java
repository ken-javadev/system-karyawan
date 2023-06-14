package com.system.karyawan.service;

import com.system.karyawan.models.User;
import com.system.karyawan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionalService {
    @Autowired
    UserRepository userRepository;
    @Transactional
    public List<User> saveListUser(List<User> list) {
        List<User> userResponse = new ArrayList<User>();
        for (User item : list) {
            User save = userRepository.save(item);
            userResponse.add(save);
        }
        return userResponse;
    }

    @Transactional
    public List<User> deleteListUser(List<User> list) {
        List<User> userResponse = new ArrayList<User>();
        for (User item : list) {
            User save = userRepository.save(item);
            userResponse.add(save);
        }
        return userResponse;
    }

}
