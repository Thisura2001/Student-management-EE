package com.example.stmanagementsystem.Controller;

import com.example.stmanagementsystem.DTO.StudentDto;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/Student")
public class StudentController extends HttpServlet {
    Connection connection;
    static String save_statement = "INSERT INTO student VALUES (?,?,?,?,?)";
    static String getStudent_statement = "SELECT * FROM student WHERE id=?";
    static String deleteStudent = "DELETE FROM student WHERE id=?";
    static String updateStudent = "UPDATE student SET name = ?, city = ?, email = ?, level = ? WHERE id = ?";
    @Override
    public void init() throws ServletException {
      try {
          System.out.println("Start");
          var driverClass =getServletContext().getInitParameter("driver-class");
          var dbUrl =getServletContext().getInitParameter("dbURL");
          var dbUserName =getServletContext().getInitParameter("dbUserName");
          var dbPassword =getServletContext().getInitParameter("dbPassword");
          System.out.println("after Var");
          System.out.println(driverClass);
          Class.forName(driverClass);
          System.out.println("after forName");

          // Establish a connection to the database
          this.connection = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
          System.out.println("end");
          // Perform database operations here (e.g., store the connection as a servlet context attribute)

      }catch (ClassNotFoundException | SQLException e) {
          throw new RuntimeException(e);
      }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //save student

        if (!req.getContentType().toLowerCase().startsWith("application/json") || req.getContentType() == null) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
/*
        BufferedReader reader=req.getReader();
        PrintWriter writer = resp.getWriter();
        StringBuilder stringBuilder = new StringBuilder();
        reader.lines().forEach(line->stringBuilder.append(line+"\n"));
        System.out.println(stringBuilder.toString());
        writer.write(stringBuilder.toString());
        writer.close();*/

        //json manipulation with Parsan

       /* JsonReader reader = Json.createReader(req.getReader());
        //JsonObject jsonObject = reader.readObject();
        JsonArray jsonValues = reader.readArray();
        for (int i=0; i<jsonValues.size();i++){
            System.out.println(jsonValues.getJsonObject(i).getString("age"));
        }*/


        //String id = UUID.randomUUID().toString();  generate ids

        Jsonb jsonb = JsonbBuilder.create();// mulinma jsonBuilder eken jsonb type obeject ekk create krnw( JsonBuilder is a tool or helper that makes it easier to create JSON objects)
        List<StudentDto> studentList = jsonb.fromJson(req.getReader(),
                new ArrayList<StudentDto>() {}.getClass().getGenericSuperclass());// req ewana json ek apita oni type ek deela ek bind krnw

        //studentDTO.setId(id);// anith ithuru id kyn property ekt me dan dena value ek dagannw

        for (StudentDto student : studentList) {
//            String id = UUID.randomUUID().toString(); // to auto generate id
//            student.setId(id);
            System.out.println(student);


            try {
                PreparedStatement preparedStatement = connection.prepareStatement(save_statement);
                preparedStatement.setString(1,student.getId());
                preparedStatement.setString(2,student.getName());
                preparedStatement.setString(3,student.getCity());
                preparedStatement.setString(4,student.getEmail());
                preparedStatement.setString(5,student.getLevel());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StudentDto studentDto = new StudentDto();
        String id = req.getParameter("id");
        PrintWriter writer = resp.getWriter();
        try (writer) {
            PreparedStatement preparedStatement = connection.prepareStatement(getStudent_statement);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                studentDto.setId(resultSet.getString(1));
                studentDto.setName(resultSet.getString(2));
                studentDto.setCity(resultSet.getString(3));
                studentDto.setEmail(resultSet.getString(4));
                studentDto.setLevel(resultSet.getString(5));
            }
            resp.setContentType("application/json");// json type response ekk enw kyl kynnn onima ne eth dana eka hodai
            System.out.println(studentDto);
            Jsonb jsonb = JsonbBuilder.create();//create json object
            jsonb.toJson(studentDto,resp.getWriter());//convert to json type (object , response eke writer)

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsonb jsonb = JsonbBuilder.create();
        StudentDto studentDto = jsonb.fromJson(req.getReader(), StudentDto.class);
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        if (studentDto.getId() == null||studentDto.getId().isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.println("Id missing or empty");
            jsonb.toJson(studentDto,writer);
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateStudent);
            preparedStatement.setString(1,studentDto.getName());
            preparedStatement.setString(2,studentDto.getCity());
            preparedStatement.setString(3,studentDto.getEmail());
            preparedStatement.setString(4,studentDto.getLevel());
            preparedStatement.setString(5,studentDto.getId());
            int affectRow = preparedStatement.executeUpdate();
            if (affectRow>0){
                writer.println(studentDto+" "+ "Update Successful !!");
            }else {
                writer.println("Student not updated Try again");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        Jsonb jsonb = JsonbBuilder.create();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        if (id == null||id.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.println("Id missing or empty");
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteStudent);
            preparedStatement.setString(1,id);
            int affectRow = preparedStatement.executeUpdate();
            if (affectRow>0){
                writer.println(id+" "+ "Delete Successful !!");
            }else {
                writer.println("Student not deleted Try again");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
