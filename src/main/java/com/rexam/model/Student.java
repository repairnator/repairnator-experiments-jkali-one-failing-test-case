package com.rexam.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "email")
public class Student extends User {

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	private List<StudentYear> studentYear;

	public List<StudentYear> getStudentYear() {
		return studentYear;
	}

	public void setStudentYear(List<StudentYear> studentYear) {
		this.studentYear = studentYear;
	}
}
