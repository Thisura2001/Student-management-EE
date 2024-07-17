package com.example.stmanagementsystem.Dao.DaoImpl;
import com.example.stmanagementsystem.DTO.StudentDto;

import java.sql.Connection;
import java.sql.SQLException;

public sealed interface StudentDao permits StudentDaoImpl{
    StudentDto getStudent(String studentId, Connection connection) throws SQLException;
    boolean saveStudent(StudentDto studentDTO,Connection connection);
    boolean deleteStudent(String studentId,Connection connection);

    boolean updateStudent(String studentId, StudentDto student, Connection connection);
}
