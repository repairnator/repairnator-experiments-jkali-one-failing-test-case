package name.mjw.jquante.math.qm;

import java.util.ArrayList;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import name.mjw.jquante.config.impl.AtomInfo;
import name.mjw.jquante.math.qm.basis.ContractedGaussian;
import name.mjw.jquante.molecule.Molecule;
import name.mjw.jquante.parallel.AbstractSimpleParallelTask;
import name.mjw.jquante.parallel.SimpleParallelTask;
import name.mjw.jquante.parallel.SimpleParallelTaskExecuter;

/**
 * The 1E integral (overlap S matrix) and 1E hCore matrices driver.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class OneElectronIntegrals {

	private static final Logger LOG = LogManager.getLogger(OneElectronIntegrals.class);

	/**
	 * The overlap S matrix.
	 */
	private Overlap overlap;

	/**
	 * The core Hamiltonian matrix contains integrals that represent the kinetic
	 * energy of an electron (T) and electron-nuclear potential energy (V).
	 */
	private HCore hCore;

	private BasisFunctions basisFunctions;
	private Molecule molecule;

	/**
	 * Creates a new instance of OneElectronIntegrals
	 * 
	 * @param basisFunctions
	 *            the basis functions to be used
	 * @param mol
	 *            the Molecule object, of whose 1E integrals are to be evaluated
	 */
	public OneElectronIntegrals(BasisFunctions basisFunctions, Molecule mol) {
		this.basisFunctions = basisFunctions;
		this.molecule = mol;

		// compute the 1E integrals, form S matrix and hCore
		compute1E();
	}

	/**
	 * Get BasisFunctions associated with this 1E evaluation
	 * 
	 * @return instance of basisFunctions
	 */
	public BasisFunctions getBasisFunctions() {
		return basisFunctions;
	}

	/**
	 * compute the 1E integrals, form S matrix and hCore
	 */
	protected void compute1E() {
		ArrayList<ContractedGaussian> bfs = basisFunctions.getBasisFunctions();
		int noOfBasisFunctions = bfs.size();
		int i;

		// allocate memory
		this.overlap = new Overlap(noOfBasisFunctions);
		this.hCore = new HCore(noOfBasisFunctions);

		// read in the atomic numbers
		int[] atomicNumbers = new int[molecule.getNumberOfAtoms()];
		AtomInfo ai = AtomInfo.getInstance();

		for (i = 0; i < atomicNumbers.length; i++) {
			atomicNumbers[i] = ai.getAtomicNumber(molecule.getAtom(i).getSymbol());
		}

		SimpleParallelTaskExecuter pTaskExecuter = new SimpleParallelTaskExecuter();

		OneElectronIntegralEvaluaterThread tThread = new OneElectronIntegralEvaluaterThread(atomicNumbers);
		tThread.setTaskName("OneElectronIntegralEvaluater Thread");
		tThread.setTotalItems(noOfBasisFunctions);

		pTaskExecuter.execute(tThread);
	}

	/**
	 * Getter for property overlap.
	 * 
	 * @return Value of property overlap.
	 */
	public Overlap getOverlap() {
		return this.overlap;
	}

	/**
	 * Getter for property hCore.
	 * 
	 * @return Value of property hCore.
	 */
	public HCore getHCore() {
		return this.hCore;
	}

	/**
	 * Class encapsulating the way to compute 1E electrons in a way useful for
	 * utilising multicore (processor) systems.
	 */
	protected class OneElectronIntegralEvaluaterThread extends AbstractSimpleParallelTask {

		private int startBasisFunction;
		private int endBasisFunction;
		private ArrayList<ContractedGaussian> bfs;
		private RealMatrix overlap;
		private RealMatrix hCore;
		private int[] atomicNumbers;

		public OneElectronIntegralEvaluaterThread(int[] atomicNumbers) {
			this.atomicNumbers = atomicNumbers;
		}

		public OneElectronIntegralEvaluaterThread(int startBasisFunction, int endBasisFunction, RealMatrix overlap,
				RealMatrix hCore, int[] atomicNumbers, ArrayList<ContractedGaussian> bfs) {
			this.startBasisFunction = startBasisFunction;
			this.endBasisFunction = endBasisFunction;

			this.overlap = overlap;
			this.hCore = hCore;
			this.atomicNumbers = atomicNumbers;

			this.bfs = bfs;

			setTaskName("OneElectronIntegralEvaluater Thread");
		}

		@Override
		public void run() {
			compute1E(startBasisFunction, endBasisFunction, overlap, hCore, atomicNumbers, bfs);
		}

		@Override
		public SimpleParallelTask init(int startItem, int endItem) {
			return new OneElectronIntegralEvaluaterThread(startItem, endItem,
					OneElectronIntegrals.this.overlap, OneElectronIntegrals.this.hCore,
					this.atomicNumbers, basisFunctions.getBasisFunctions());
		}

		/**
		 * method to actually compute 1E integrals
		 */
		private void compute1E(int startBasisFunction, int endBasisFunction, RealMatrix overlap, RealMatrix hCore,
				int[] atomicNumbers, ArrayList<ContractedGaussian> bfs) {

			LOG.debug("startBasisFunction: " + startBasisFunction + " endBasisFunction: " + endBasisFunction);

			int noOfBasisFunctions = bfs.size();

			ContractedGaussian bfi;
			ContractedGaussian bfj;

			// set up the S matrix and the hCore h
			for (int i = startBasisFunction; i < endBasisFunction; i++) {
				bfi = bfs.get(i);

				for (int j = 0; j < noOfBasisFunctions; j++) {
					bfj = bfs.get(j);

					overlap.setEntry(i, j, bfi.overlap(bfj)); // the overlap matrix
					hCore.setEntry(i, j, bfi.kinetic(bfj));// KE matrix elements

					for (int k = 0; k < atomicNumbers.length; k++) {
						hCore.setEntry(i, j, (hCore.getEntry(i, j)
								+ atomicNumbers[k] * bfi.nuclear(bfj, molecule.getAtom(k).getAtomCenterInAU())));
					}
				}
			}
		}
	}

}
