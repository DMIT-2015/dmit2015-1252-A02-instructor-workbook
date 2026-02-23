package dmit2015.service;

import dmit2015.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student createStudent(Student student);

    Optional<Student> getStudentById(Integer id);

    List<Student> getAllStudents();

    Student updateStudent(Student student);

    void deleteStudentById(Integer id);

    long count();
}