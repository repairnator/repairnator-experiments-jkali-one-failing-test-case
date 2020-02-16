package dev.repositories.concours;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import dev.entites.Concours;

@Repository
public class ConcoursRepositoryMemoire implements ConcoursRepository {

	private List<Concours> lesconcours = new ArrayList<>();

	/**
	 * Initialiser la liste des entites
	 */
	@PostConstruct
	public void initialiser() {
		// créer un contexte avec la classe ClassPathXmlApplicationContext
		// Récupérer les données via la méthode context.getBeansOfType(...)
		// fermer le contexte
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("jdd/jdd-concours.xml")) {
			this.lesconcours = new ArrayList<>(context.getBeansOfType(Concours.class).values());

		}

	}

	@Override
	public List<Concours> findAll() {
		return this.lesconcours;
	}

	@Override
	public void save(Concours entite) {
		this.lesconcours.add(entite);

	}

	@Override
	public void update(Concours entiteAvecId) {
		Long id = entiteAvecId.getId();
		for (Concours c : lesconcours) {
			if (id == entiteAvecId.getId()) {
				c.setTitre(entiteAvecId.getTitre());
				c.setParticipants(entiteAvecId.getParticipants());
				c.setQuizzes(entiteAvecId.getQuizzes());
			}
		}

	}

	@Override
	public void delete(Concours entite) {
		this.lesconcours.remove(entite);

	}

	@Override
	public Optional<Concours> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
