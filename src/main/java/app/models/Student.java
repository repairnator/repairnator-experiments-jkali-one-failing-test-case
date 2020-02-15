package app.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends User implements Comparable<Student> {

    private String studentNumber;
    private String program;

    @ManyToOne
    private Project project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authenticated_user_id")
    private AuthenticatedUser authenticatedUser;

    public Student(String firstName, String lastName, String email, String studentNumber, String program)
    {
        super(firstName, lastName, email);
        this.studentNumber = studentNumber;
        this.program = program;
        this.project = null;
    }

    public Student() {
        this(null, null, null, "0", null);
    }

    public void searchForProjects(String searchPhrase)
    {

    }

    /**
     * Attempt to join a project
     * @param project The project they want to join
     */
    public boolean joinProject(Project project)
    {
        if (project.addStudent(this)) {
            this.project = project;
            return true;
        }
        System.out.println("Unable to join this project"); // (for now)
        return false;
    }

    private void submitFinalReport()
    {

    }

    /**
     * Compare an unknown object to this Student object
     * @param obj Unknown object
     * @return Boolean whether or not the objects are the same
     */
    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof Student)) return false;

        Student student = (Student) obj;

        return super.equals(obj)
                && (this.studentNumber == student.studentNumber)
                && (this.program.equals(student.program))
                && ((this.project == null && student.project == null) || (this.project.equals(student.project)));
    }

    /**
     * Determine the "order" of two students based on their email addresses (alphabetical order)
     * @param compareStudent Studentyou wish to compare against
     * @return Negative (less than), 0 (equal), Positive (greater than)
     */
    public int compareTo(Student compareStudent)
    {
        return this.getEmail().compareToIgnoreCase(compareStudent.getEmail());
    }


    ///////////////
    // Get & Set //
    ///////////////

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        if(!project.getStudents().contains(this)) {
            project.addStudent(this);
        }
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }
}
