package com.example.stmanagementsystem.Controller;

import Utill.UtilProcess;
import com.example.stmanagementsystem.DTO.StudentDto;
import com.example.stmanagementsystem.Dao.DaoImpl.StudentDaoImpl;
import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(value = "/student")
public class StudentController extends HttpServlet {
    Connection connection;
    @Override
    public void init() throws ServletException {
//      try {
//          var initialContext = new InitialContext();
//          DataSource pool = (DataSource) initialContext.lookup("java:comp/env/jdbc/studentRegistration");
//         this.connection =  pool.getConnection();
//
//      } catch (SQLException | NamingException e) {
//          throw new RuntimeException(e);
//      }
//    }
        try {
            var Driver = getServletContext().getInitParameter("driver-class");
            var url = getServletContext().getInitParameter("dbURL");
            var userName = getServletContext().getInitParameter("dbUserName");
            var password = getServletContext().getInitParameter("dbPassword");
            Class.forName(Driver);
            this.connection =  DriverManager.getConnection(url,userName,password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**/
        //Todo: SAVE STUDENT;
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType()==null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }  /*Chek Json type*/
        /*process*/
        /*BufferedReader reader=req.getReader();
        StringBuilder sb =new StringBuilder();
        var writer= resp.getWriter();
        reader.lines().forEach(line->sb.append(line+"\n"));
        System.out.println(sb);
        writer.write(sb.toString());
        writer.close();*/

        /*jason manipulate with parson*/
        /*JsonReader reader1= Json.createReader(req.getReader());*/
        /*JsonObject jsonObject= reader1.readObject();
        System.out.println(jsonObject.getString("email"));*/

        /*----------*/
        /*JsonReader reader1= Json.createReader(req.getReader());
        JsonArray jArray = reader1.readArray();
        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jsonObject =jArray.getJsonObject(i);
            System.out.println(jsonObject.getString("name"));
        }*/


        /*---Organize code ADD DAO----*/

       /* String id = UUID.randomUUID().toString();
        Jsonb jsonb= JsonbBuilder.create();
        StudentDTO studentDTO= jsonb.fromJson(req.getReader(),StudentDTO.class);
        studentDTO.setId(id);
        System.out.println(studentDTO);*/

        /*----add dto ------*/
        /*Jsonb jsonb= JsonbBuilder.create();
        List<StudentDTO>studentDTOList = jsonb.fromJson(req.getReader(),new ArrayList<StudentDTO>(){
        }.getClass().getGenericSuperclass());
        studentDTOList.forEach(System.out::println);*/


        /*Origanize code ADD DAO*/

        /*try {
            var ps = connection.prepareStatement(SAVE_STUDENT);
            ps.setString(1, studentDTO.getId());
            ps.setString(2, studentDTO.getName());
            ps.setString(3, studentDTO.getCity());
            ps.setString(4, studentDTO.getEmail());
            if(ps.executeUpdate() != 0 ){
                resp.getWriter().write("Student Saved");
            } else {
                resp.getWriter().write("Student Not Saved");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/

        try (var writer = resp.getWriter()){
            String id = UUID.randomUUID().toString();
            Jsonb jsonb= JsonbBuilder.create();
            StudentDto studentDTO= jsonb.fromJson(req.getReader(),StudentDto.class); /*getreader eken json eka read kara frmJson eken ema josn eka bind karai.*/
            studentDTO.setId(id);
            System.out.println(studentDTO);

            var saveData = new StudentDaoImpl();
            /*writer.write(saveData.saveStudent(studentDTO, connection));*/
            if (saveData.saveStudent(studentDTO, connection)) {
                writer.write("Student Save Successfully");
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }else {
                writer.write("Student Save Failed");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (JsonException e) {
            throw new RuntimeException(e);
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
