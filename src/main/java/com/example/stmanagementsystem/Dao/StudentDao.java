package com.example.stmanagementsystem.Dao;

import com.example.stmanagementsystem.DTO.StudentDto;

import java.sql.Connection;
import java.sql.SQLException;

public  interface  StudentDao {
    StudentDto getStudent(String studentId, Connection connection) throws SQLException;
    boolean saveStudent(StudentDto studentDTO,Connection connection);
    boolean deleteStudent(String studentId,Connection connection);

    boolean updateStudent(String studentId, StudentDto student, Connection connection);
}
