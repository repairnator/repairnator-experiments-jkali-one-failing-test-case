package name.mjw.jquante.math.qm;

/**
 * An external guess for Density Matrix (DM) should implement this interface.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface DensityGuesser {

	/**
	 * Guess the DM, using some intelligent techniques ... (like Molecular
	 * Tailoring Approach - MTA)
	 * 
	 * @param scfMethod
	 *            the SCF method for which the guess is to be done
	 * @return the density matrix (DM) of the molecule
	 */
	public Density guessDM(SCFMethod scfMethod);

}