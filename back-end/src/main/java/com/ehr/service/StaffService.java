package com.ehr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ehr.dto.StaffCreateDto;
import com.ehr.models.Staff;
import com.ehr.repository.StaffRepository;

import java.util.Optional;

@Service
public class StaffService {
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Staff createStaff(StaffCreateDto dto) {
        if (staffRepository.existsByWorkId(dto.getWorkId())) {
            throw new IllegalArgumentException("Work ID already exists");
        }
        
        Staff staff = new Staff();
        staff.setWorkId(dto.getWorkId());
        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setRole(dto.getRole());
        staff.setPassword(passwordEncoder.encode(dto.getPassword()));

        return staffRepository.save(staff);
    }

    public Optional<Staff> findByWorkId(String workId){
        return this.staffRepository.findByWorkId(workId);
    }
}