package com.example.school.service;

import com.example.school.model.StudentRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import com.example.school.model.Student;
import com.example.school.repository.StudentRepository;
import java.util.*;

// Write your code here

@Service
public class StudentH2Service implements StudentRepository {
    @Autowired
    private JdbcTemplate db;

    @Override
    public ArrayList<Student> getStudents() {
        List<Student> studentList = db.query("select * from STUDENT", new StudentRowMapper());
        ArrayList<Student> students = new ArrayList<>(studentList);
        return students;
    }

    @Override
    public Student getStudentById(int studentId) {
        try {
            Student student = db.queryForObject("select * from STUDENT where studentId = ?", new StudentRowMapper(),
                    studentId);
            return student;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Student addStudent(Student student) {
        db.update("insert into STUDENT(studentName, gender, standard) values(?, ?, ?)", student.getStudentName(),
                student.getGender(), student.getStandard());
        Student savedStudent = db.queryForObject(
                "select * from STUDENT where studentName=? and gender=? and standard=?",
                new StudentRowMapper(), student.getStudentName(), student.getGender(), student.getStandard());
        return savedStudent;
    }

    @Override
    public String addManyStudents(ArrayList<Student> studentsList) {
        for (Student each : studentsList) {
            db.update("insert into STUDENT(studentName, gender, standard) values(?, ?, ?)", each.getStudentName(),
                    each.getGender(), each.getStandard());
        }
        String response = String.format("Successfully added %d students", studentsList.size());
        return response;
    }

    @Override
    public Student updateStudent(int studentId, Student student) {
        if (student.getStudentName() != null) {
            db.update("update STUDENT Set studentName=? where studentId=?", student.getStudentName(), studentId);
        }
        if (student.getGender() != null) {
            db.update("update STUDENT Set gender=? where studentId=?", student.getGender(), studentId);
        }
        if (student.getStandard() != 0) {
            db.update("update STUDENT Set standard=? where studentId=?", student.getStandard(), studentId);
        }
        return getStudentById(studentId);
    }

    @Override
    public void deleteStudent(int studentId) {
        db.update("delete from STUDENT where studentId=?", studentId);
    }

}