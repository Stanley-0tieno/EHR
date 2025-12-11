package com.ehr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ehr.models.User;
import com.ehr.models.Staff;
import com.ehr.repository.UserRepository;
import com.ehr.repository.StaffRepository;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find as User (Patient) first
        Optional<User> user = userRepository.findByEmailOrPhoneNumber(identifier);
        if (user.isPresent()) {
            User foundUser = user.get();
            String username = !Objects.equals(foundUser.getEmail(), "") ? foundUser.getEmail() : foundUser.getPhoneNumber();
            return new org.springframework.security.core.userdetails.User(
                    username,  // username
                    foundUser.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + foundUser.getRole()))
            );
        }

        // Try to find as Staff (by workId or email)
        Optional<Staff> staff = staffRepository.findByWorkId(identifier);

        if (staff.isPresent()) {
            Staff foundStaff = staff.get();
            return new org.springframework.security.core.userdetails.User(
                    foundStaff.getWorkId(),  // username
                    foundStaff.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + foundStaff.getRole()))
            );
        }

        throw new UsernameNotFoundException("User not found with identifier: " + identifier);
    }

    // Helper method to determine user type
    public String getUserType(String identifier) {
        if (userRepository.findByEmailOrPhoneNumber(identifier).isPresent()) {
            return "USER";
        }
        if (staffRepository.findByWorkId(identifier).isPresent()) {
            return "STAFF";
        }
        return "UNKNOWN";
    }
}