package com.system.karyawan.controllers.rest;

import com.system.karyawan.models.User;
import com.system.karyawan.service.TransactionalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transactional-test")
public class TransactionalController {

    @Autowired
    private TransactionalService transactionalService;

    private Logger logger = LoggerFactory.getLogger(TransactionalController.class);

    @GetMapping("/success")
    public ResponseEntity<?> transactionalSuccess() {
        try {

            User user1 = new User("adekenrian11", "adekenrian11@gmail.com", "12345678", "REGISTER");
            User user2 = new User("adekenrian22", "adekenrian22@gmail.com", "12345678", "REGISTER");

            List<User> users = new ArrayList<User>();
            users.add(user1);
            users.add(user2);

            List<User> deleteList = transactionalService.deleteListUser(users);
            List<User> response = transactionalService.saveListUser(users);
            logger.info("save list SUCCESSS");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/failed")
    public ResponseEntity<?> transactionalFailed() {
        try {
            User user1 = new User("adekenrian99", "adekenrian99@gmail.com", "12345678", "REGISTER");
            User user2 = new User("adekenrian98", "adekenrian98@gmail.com", null, "REGISTER");

            List<User> users = new ArrayList<User>();
            users.add(user1);//success
            users.add(user2);// failed because password null

            List<User> response = transactionalService.saveListUser(users);
            logger.info("save list SUCCESSS");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
