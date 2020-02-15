package app.models.repository;

import app.models.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
    Student findFirstByOrderById();
    Student findByStudentNumber(String number);
    boolean existsByStudentNumber(String number);
}
