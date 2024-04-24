package com.dmm.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dmm.task.service.AccountUserDetailsService;

public class RoleCheckController {

    @Autowired
    private AccountUserDetailsService userDetailsService;

    @GetMapping("/get-user-role")
    public String getUserRole(@RequestParam String userName) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            return userDetails.getAuthorities().toString();
        } catch (UsernameNotFoundException e) {
            return "User not found: " + userName;
        }
    }
}
