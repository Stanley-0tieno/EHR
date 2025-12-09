package main.java.com.ehr.models;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Data;

@Data
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String work_id;
    private String created_at;
    private String updated_at;
    private String first_name;
    private String last_name;
    private String password;
    private  enum role{doctor, receiptionist, pharmacist, labtech};
}
