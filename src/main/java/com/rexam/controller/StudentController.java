package com.rexam.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.rexam.dao.CurrentYearRepository;
import com.rexam.dao.RegistrationRepository;
import com.rexam.dao.ResultRepository;
import com.rexam.dao.StudentRepository;
import com.rexam.dao.StudentYearRepository;
import com.rexam.dao.TeachingUnitRepository;
import com.rexam.model.Component;
import com.rexam.model.CurrentYear;
import com.rexam.model.Exam;
import com.rexam.model.Registration;
import com.rexam.model.Student;
import com.rexam.model.StudentYear;
import com.rexam.model.TeachingUnit;
import com.rexam.service.AuthentificationFacade;

@Controller
@RequestMapping("/rexam")
public class StudentController {

	@Autowired
	TeachingUnitRepository tuRepository;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	StudentYearRepository studYearRepository;
	@Autowired
	ResultRepository resultRepository;
	@Autowired
	RegistrationRepository regRepository;
	@Autowired
	CurrentYearRepository yearRepository;

	@Autowired
	AuthentificationFacade authentificationFacade;

	@RequestMapping("/showTeachingUnits")
	public ModelAndView showDisciplines() {
		List<String> disciplines = tuRepository.findDisciplines();
		List<TeachingUnit> tuList = tuRepository.findAllByOrderByDisciplineAsc();
		StudentYear student = studYearRepository.findById_YearAndStudent(currentYear(), student());
		List<Registration> regs = regRepository.findByStudentYear(student);
		ModelAndView mav = new ModelAndView("teachingUnits");
		mav.addObject("disciplines", disciplines);
		for(int i = 0; i < regs.size(); i++){
			for(int j=0; j < tuList.size(); j++) {
				if(regs.get(i).getTeachingUnit().getCode().equals(tuList.get(j).getCode()) 
						&& regs.get(i).getAverageScore() == null) {
					tuList.remove(j);
				}else if(regs.get(i).getTeachingUnit().getCode().equals(tuList.get(j).getCode())
						&& regs.get(i).getAverageScore() >= 10) {
					tuList.remove(j);
				}
			}
		}
		
/*
		// Supprime l'UE si on est déja inscrit ou si l'ue a été capitalisé
		for(int i = 0; i < listStudentYear.size(); i++){
			regs = regRepository.findByStudentYear(listStudentYear.get(i));
			// si on est l'année courante, supprime les ues auxquelles on est inscrit
			if(listStudentYear.get(i).getId().getYear() == currentYear()) {
				for(int j = 0; j < regs.size(); j++){
					for(int k=0; k < tuList.size(); k++) {

						if(regs.get(j).getTeachingUnit().getCode().equals(tuList.get(k).getCode()) ) {
							tuList.remove(k);
						}
					}
				}
			}
			else {
				for(int j = 0; j < regs.size(); j++){
					for(int k=0; k < tuList.size(); k++) {
						if(regs.get(j).getTeachingUnit().getCode().equals(tuList.get(k).getCode()) 
								&& regs.get(j).getAverageScore() >= 10) {
							tuList.remove(k);
						}
					}
				}
			}
		}
*/
		mav.addObject("tuList", tuList);
		return mav;
	}

	@RequestMapping("/showExams")
	public ModelAndView showExams(@RequestParam(value = "code", required = false) String codeTU) 
	{		TeachingUnit tu = tuRepository.findOne(codeTU);
	ModelAndView mav = new ModelAndView("exams");
	mav.addObject("teachingUnit", tu);
	return mav;
	}


	@RequestMapping("/regs")
	public ModelAndView listRegistrations() {

		StudentYear student = studYearRepository.findById_YearAndStudent(currentYear(), student());
		List<Registration> regs = regRepository.findByStudentYear(student);

		if (regs == null) {
			regs = new ArrayList<Registration>();
		}

		ModelAndView mav = new ModelAndView("regslist");
		mav.addObject("regs", regs);

		return mav;
	}

	@RequestMapping("/results")
	public ModelAndView listResults() {
		StudentYear student = studYearRepository.findById_YearAndStudent(currentYear(), student());
		List<Registration> regs = regRepository.findByStudentYear(student);

		if (regs == null) {
			regs = new ArrayList<Registration>();
		}

		ModelAndView mav = new ModelAndView("reslist");
		mav.addObject("results", regs);

		return mav;
	}

	@RequestMapping("/results/{codeTU}")
	public ModelAndView detailResults(@PathVariable(value = "codeTU") String codeTU) {
		TeachingUnit tu = tuRepository.findOne(codeTU);
		StudentYear student = studYearRepository.findById_YearAndStudent(currentYear(), student());

		ModelAndView mav = new ModelAndView("detailRes");
		mav.addObject("tu", tu);
		mav.addObject("studyear", student);

		return mav;
	}

	@ModelAttribute("teachingUnits")
	Iterable<TeachingUnit> teachingUnits() {
		return tuRepository.findAll();
	}

	@ModelAttribute("currentYear")
	Integer currentYear() {
		return ((Collection<CurrentYear>) yearRepository.findAll())
				.stream().findFirst().get().getYear();
	}

	Student student() {
		return studentRepository.findByEmail(authentificationFacade.getAuthentication().getName());
	}


}