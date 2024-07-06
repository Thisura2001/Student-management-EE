package com.example.stmanagementsystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentDto implements Serializable {
    private String id;
    private String name;
    private String email;
    private String city;
    private String level;
}
