package com.example.stmanagementsystem.Dao.DaoImpl;

import com.example.stmanagementsystem.DTO.StudentDto;
import com.example.stmanagementsystem.Dao.StudentDao;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class  StudentDaoImpl implements StudentDao {
    static String SAVE_STUDENT = "INSERT INTO student (id,name,city,email,level) VALUES (?,?,?,?,?)";
    static String GET_STUDENT = "SELECT * FROM student WHERE id=?";
    static String UPDATE_STUDENT = "UPDATE student SET name=?,city=?,email=?,level=? WHERE id=?";
    static String DELETE_STUDENT = "DELETE FROM student WHERE id=?";

    @Override
    public StudentDto getStudent(String studentId, Connection connection) throws SQLException {
        var studentDTO = new StudentDto();
        try {
            var ps = connection.prepareStatement(GET_STUDENT);
            ps.setString(1, studentId);
            var resultSet = ps.executeQuery();
            while (resultSet.next()) {
                studentDTO.setId(resultSet.getString("id"));
                studentDTO.setName(resultSet.getString("name"));
                studentDTO.setCity(resultSet.getString("city"));
                studentDTO.setEmail(resultSet.getString("email"));
                studentDTO.setLevel(resultSet.getString("level"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentDTO;
    }

    @Override
    public boolean saveStudent(StudentDto studentDTO, Connection connection) {
        return false;
    }

    @Override
    public boolean deleteStudent(String studentId, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT);
            preparedStatement.setString(1,studentId);
            return preparedStatement.executeUpdate()>0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateStudent(String studentId,StudentDto studentDto,Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT);
            preparedStatement.setString(1,studentDto.getName());
            preparedStatement.setString(2,studentDto.getCity());
            preparedStatement.setString(3,studentDto.getEmail());
            preparedStatement.setString(4,studentDto.getLevel());
            preparedStatement.setString(5,studentDto.getId());
            return preparedStatement.executeUpdate()>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
