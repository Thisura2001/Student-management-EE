package com.example.stmanagementsystem.Controller;

import Utill.UtilProcess;
import com.example.stmanagementsystem.DTO.StudentDto;
import com.example.stmanagementsystem.Dao.DaoImpl.StudentDaoImpl;
import com.example.stmanagementsystem.Dao.StudentDao;
import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
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

@WebServlet(value = "/Student"
//@initParams = { //servlet config
//        @WebInitParam(name = "driver-class",value = "com.mysql.cj.jdbc.Driver"),
//        @WebInitParam(name = "dbURL",value = "jdbc:mysql://localhost:3306/student"),
//        @WebInitParam(name = "dbUserName",value = "root"),
//        @WebInitParam(name = "dbPassword",value = "1234")
//    }
)
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
            student.setId(UtilProcess.generateID());
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
        var studentId = req.getParameter("id");
        var getStudent = new StudentDaoImpl();
        try (var writer = resp.getWriter()){ //Try-With-Resources Statement(writer eka auto close wenwa mema dmmama dmme natat awlk na e unt dna eka hody)
            var student = getStudent.getStudent(studentId, connection);
            System.out.println(student);
            resp.setContentType("application/json");
            var jsonb = JsonbBuilder.create();
            jsonb.toJson(student,writer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsonb jsonb = JsonbBuilder.create();
        StudentDto studentDto = jsonb.fromJson(req.getReader(), StudentDto.class);
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        StudentDaoImpl studentDaoImpl = new StudentDaoImpl();
        var UpdateStudent = studentDaoImpl.updateStudent(studentDto.getId(),studentDto,connection);

        if (UpdateStudent){
            writer.println(studentDto+" "+ "Update Successful !!");
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }else {
            writer.println("Student not updated Try again");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StudentDto studentDto = new StudentDto();
        String id = req.getParameter("id");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        StudentDaoImpl studentDaoImpl = new StudentDaoImpl();
        var deleteStudent =studentDaoImpl.deleteStudent(id,connection);
        if (deleteStudent){
            resp.setStatus(HttpServletResponse.SC_OK);
            writer.println(studentDto+" "+"Delete Successful !!");
        }else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.println("Student not Deleted Try again !!");
        }
    }
}
