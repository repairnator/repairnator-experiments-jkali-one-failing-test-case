package name.mjw.jquante.math.qm;

import java.util.ArrayList;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import name.mjw.jquante.math.MathUtil;
import name.mjw.jquante.math.qm.integral.IntegralsUtil;
import name.mjw.jquante.parallel.AbstractSimpleParallelTask;
import name.mjw.jquante.parallel.SimpleParallelTask;
import name.mjw.jquante.parallel.SimpleParallelTaskExecuter;

/**
 * Represents the G Matrix used to form the Fock matrix. Contains the
 * contribution from the two electron integrals and the density matrix.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GMatrix extends Array2DRowRealMatrix {

	private static final Logger LOG = LogManager.getLogger(GMatrix.class);

	private static final long serialVersionUID = -6555277318704252665L;
	private TwoElectronIntegrals twoEI;
	private Density density;
	private ArrayList<GMatrix> partialGMatrixList;

	/**
	 * Creates a new instance of square (NxN) Matrix
	 * 
	 * @param n
	 *            the dimension
	 */
	public GMatrix(int n) {
		super(n, n);
	}

	public GMatrix(double[][] data) {
		super(data);
	}

	/**
	 * Form the GMatrix from two electron integrals and the density matrix.
	 * 
	 * @param scfType
	 *            the SCFType
	 * @param twoEI
	 *            the 2E integrals
	 * @param density
	 *            the Density matrix
	 */
	public void compute(SCFType scfType, TwoElectronIntegrals twoEI, Density density) {
		this.twoEI = twoEI;
		this.density = density;

		LOG.debug("{}", density);

		if (scfType == SCFType.HARTREE_FOCK_DIRECT) {
			makeGMatrixDirect();
		} else {
			makeGMatrix();
		}
	}

	/**
	 * Make the G matrix <br>
	 * i.e. Form the 2J-K integrals corresponding to a density matrix
	 */
	protected void makeGMatrix() {
		// make sure if this is really the case
		// just if in case TwoElectronIntegrals class decided other wise
		if (twoEI.isOnTheFly()) {
			makeGMatrixDirect();
			return;
		}

		LOG.debug("makeGMatrix() called");
		int noOfBasisFunctions = density.getRowDimension();

		RealVector densityOneD = MathUtil.realMatrixToRealVector(density); // form 1D vector of density
		RealVector tempVector = new ArrayRealVector(noOfBasisFunctions * noOfBasisFunctions);

		double[] ints = twoEI.getTwoEIntegrals();

		int i;
		int j;
		int k;
		int l;
		int kl;
		int indexJ;
		int indexK1;
		int indexK2;
		for (i = 0; i < noOfBasisFunctions; i++) {
			for (j = 0; j < i + 1; j++) {

				kl = 0;

				for (k = 0; k < noOfBasisFunctions; k++) {
					for (l = 0; l < noOfBasisFunctions; l++) {
						indexJ = IntegralsUtil.ijkl2intindex(i, j, k, l);
						indexK1 = IntegralsUtil.ijkl2intindex(i, k, j, l);
						indexK2 = IntegralsUtil.ijkl2intindex(i, l, k, j);

						tempVector.setEntry(kl, (2.0 * ints[indexJ] - 0.5 * ints[indexK1] - 0.5 * ints[indexK2]));

						kl++;
					}
				}

				this.setEntry(i, j, tempVector.dotProduct(densityOneD));
				this.setEntry(j, i, tempVector.dotProduct(densityOneD));
			}
		}
		LOG.debug(this);
	}

	/**
	 * Make the G matrix <br>
	 * i.e. Form the 2J-K integrals corresponding to a density matrix
	 * 
	 * This computes integrals on the fly rather than read in from a pre-calculated
	 * storage.
	 */
	protected void makeGMatrixDirect() {
		LOG.debug("makeGMatrixDirect() called");
		SimpleParallelTaskExecuter pTaskExecuter = new SimpleParallelTaskExecuter();

		// allocate memory for partial GMatrices
		partialGMatrixList = new ArrayList<>();

		// start the threads
		GMatrixFormationThread tThread = new GMatrixFormationThread();
		tThread.setTaskName("GMatrixFormationThread Thread");
		tThread.setTotalItems(density.getRowDimension());

		pTaskExecuter.execute(tThread);

		// Zero this matrix... there must be a better way to do this
		for (int i = 0; i < this.getRowDimension(); i++) {
			for (int j = 0; j < this.getColumnDimension(); j++) {
				this.setEntry(i, j, 0.0);
			}
		}

		if (!partialGMatrixList.isEmpty()) {
			// collect the result and sum the partial contributions
			int n = this.getRowDimension();

			// sum up the partial results
			for (GMatrix pgMat : partialGMatrixList) {

				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						this.setEntry(i, j, (this.getEntry(i, j) + pgMat.getEntry(i, j)));
					}
				}

			}

			// half the elements
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					this.setEntry(i, j, (this.getEntry(i, j) * 0.5));

				}
			}
		}
	}

	/**
	 * Compute GMatrix partial derivative for an atom index.
	 * 
	 * @param atomIndex
	 *            the atom index with respect to which the derivative are to be
	 *            evaluated
	 * @param scfMethod
	 *            the reference to the SCFMethod
	 * @return three element array of GMatrix elements representing partial
	 *         derivatives with respect to x, y and z of atom position
	 */
	public ArrayList<GMatrix> computeDerivative(int atomIndex, SCFMethod scfMethod) {
		ArrayList<GMatrix> gDer = new ArrayList<>(3);

		scfMethod.getTwoEI().compute2EDerivatives(atomIndex, scfMethod);
		ArrayList<double[]> twoEDers = scfMethod.getTwoEI().getTwoEDer();
		double[] d2IntsDxa = twoEDers.get(0);
		double[] d2IntsDya = twoEDers.get(1);
		double[] d2IntsDza = twoEDers.get(2);

		density = scfMethod.getDensity();
		int noOfBasisFunctions = density.getRowDimension();
		RealVector densityOneD = MathUtil.realMatrixToRealVector(density); // form 1D vector of density

		GMatrix gdx = new GMatrix(noOfBasisFunctions);
		GMatrix gdy = new GMatrix(noOfBasisFunctions);
		GMatrix gdz = new GMatrix(noOfBasisFunctions);

		RealVector xvec = new ArrayRealVector(noOfBasisFunctions * noOfBasisFunctions);
		RealVector yvec = new ArrayRealVector(noOfBasisFunctions * noOfBasisFunctions);
		RealVector zvec = new ArrayRealVector(noOfBasisFunctions * noOfBasisFunctions);

		int i;
		int j;
		int k;
		int l;
		int kl;
		int indexJ;
		int indexK1;
		int indexK2;
		for (i = 0; i < noOfBasisFunctions; i++) {
			for (j = 0; j < i + 1; j++) {
				kl = 0;
				double[] xtemp = xvec.toArray();
				double[] ytemp = xvec.toArray();
				double[] ztemp = xvec.toArray();
				for (k = 0; k < noOfBasisFunctions; k++) {
					for (l = 0; l < noOfBasisFunctions; l++) {
						indexJ = IntegralsUtil.ijkl2intindex(i, j, k, l);
						indexK1 = IntegralsUtil.ijkl2intindex(i, k, j, l);
						indexK2 = IntegralsUtil.ijkl2intindex(i, l, k, j);

						xtemp[kl] = 2. * d2IntsDxa[indexJ] - 0.5 * d2IntsDxa[indexK1] - 0.5 * d2IntsDxa[indexK2];
						ytemp[kl] = 2. * d2IntsDya[indexJ] - 0.5 * d2IntsDya[indexK1] - 0.5 * d2IntsDya[indexK2];
						ztemp[kl] = 2. * d2IntsDza[indexJ] - 0.5 * d2IntsDza[indexK1] - 0.5 * d2IntsDza[indexK2];
						kl++;
					}
				}

				gdx.setEntry(i, j, xvec.dotProduct(densityOneD));
				gdx.setEntry(j, i, xvec.dotProduct(densityOneD));

				gdy.setEntry(i, j, yvec.dotProduct(densityOneD));
				gdy.setEntry(j, i, yvec.dotProduct(densityOneD));

				gdz.setEntry(i, j, zvec.dotProduct(densityOneD));
				gdz.setEntry(j, i, zvec.dotProduct(densityOneD));

			}
		}

		gDer.add(gdx);
		gDer.add(gdy);
		gDer.add(gdz);

		return gDer;
	}

	/**
	 * Class encapsulating the way for forming GMatrix in a way useful for Utilising
	 * multi core (processor) systems.
	 */
	protected class GMatrixFormationThread extends AbstractSimpleParallelTask {

		private int startBasisFunction;
		private int endBasisFunction;

		public GMatrixFormationThread() {
		}

		public GMatrixFormationThread(int startBasisFunction, int endBasisFunction) {
			this.startBasisFunction = startBasisFunction;
			this.endBasisFunction = endBasisFunction;

			setTaskName("GMatrixFormationThread Thread");
		}

		/**
		 * Overridden run()
		 */
		@Override
		public void run() {
			makeGMatrixDirect(startBasisFunction, endBasisFunction);
		}

		/** Overridden init() */
		@Override
		public SimpleParallelTask init(int startItem, int endItem) {
			return new GMatrixFormationThread(startItem, endItem);
		}

		/** function to facilitate multi-threaded direct formation of GMatrix */
		private void makeGMatrixDirect(int startBasisFunction, int endBasisFunction) {
			int noOfBasisFunctions = density.getRowDimension();
			GMatrix theGMatrix = new GMatrix(noOfBasisFunctions);

			double[][] gMatrix = theGMatrix.getData();
			double[][] dMatrix = density.getData();

			int i;
			int j;
			int k;
			int l;
			int m;
			int ij;
			int kl;
			int[] idx;
			int[] jdx;
			int[] kdx;
			int[] ldx;
			idx = new int[8];
			jdx = new int[8];
			kdx = new int[8];
			ldx = new int[8];
			boolean[] validIdx = new boolean[8];
			validIdx[0] = true;

			double twoEIntVal;
			double twoEIntVal2;
			double twoEIntValHalf;
			for (i = startBasisFunction; i < endBasisFunction; i++) {
				idx[0] = i;
				jdx[1] = i;
				jdx[2] = i;
				idx[3] = i;
				kdx[4] = i;
				ldx[5] = i;
				kdx[6] = i;
				ldx[7] = i;
				for (j = 0; j < (i + 1); j++) {
					ij = i * (i + 1) / 2 + j;
					jdx[0] = j;
					idx[1] = j;
					idx[2] = j;
					jdx[3] = j;
					ldx[4] = j;
					kdx[5] = j;
					ldx[6] = j;
					kdx[7] = j;
					for (k = 0; k < noOfBasisFunctions; k++) {
						kdx[0] = k;
						kdx[1] = k;
						ldx[2] = k;
						ldx[3] = k;
						jdx[4] = k;
						jdx[5] = k;
						idx[6] = k;
						idx[7] = k;
						for (l = 0; l < (k + 1); l++) {
							kl = k * (k + 1) / 2 + l;
							if (ij >= kl) {
								twoEIntVal = twoEI.compute2E(i, j, k, l);
								twoEIntVal2 = twoEIntVal + twoEIntVal;
								twoEIntValHalf = 0.5 * twoEIntVal;

								setGMatrixElements(gMatrix, dMatrix, i, j, k, l, twoEIntVal2, twoEIntValHalf);

								// special case
								if ((i | j | k | l) == 0)
									continue;

								// else this is symmetry unique integral, so
								// need to
								// use this value for all 8 combinations
								// (if unique)
								ldx[0] = l;
								ldx[1] = l;
								kdx[2] = l;
								kdx[3] = l;
								idx[4] = l;
								idx[5] = l;
								jdx[6] = l;
								jdx[7] = l;
								validIdx[1] = true;
								validIdx[2] = true;
								validIdx[3] = true;
								validIdx[4] = true;
								validIdx[5] = true;
								validIdx[6] = true;
								validIdx[7] = true;

								// filter unique elements
								filterUniqueElements(idx, jdx, kdx, ldx, validIdx);

								// and evaluate them
								for (m = 1; m < 8; m++) {
									if (validIdx[m]) {
										setGMatrixElements(gMatrix, dMatrix, idx[m], jdx[m], kdx[m], ldx[m],
												twoEIntVal2, twoEIntValHalf);
									}
								}
							}
						}
					}
				}
			}
			partialGMatrixList.add(new GMatrix(gMatrix));
		}

		/** Set the GMatrix value for a given combination */
		private void setGMatrixElements(double[][] gMatrix, double[][] dMatrix, int i, int j, int k, int l,
				double twoEIntVal2, double twoEIntValHalf) {
			gMatrix[i][j] += dMatrix[k][l] * twoEIntVal2;
			gMatrix[k][l] += dMatrix[i][j] * twoEIntVal2;
			gMatrix[i][k] -= dMatrix[j][l] * twoEIntValHalf;
			gMatrix[i][l] -= dMatrix[j][k] * twoEIntValHalf;
			gMatrix[j][k] -= dMatrix[i][l] * twoEIntValHalf;
			gMatrix[j][l] -= dMatrix[i][k] * twoEIntValHalf;
		}

		/** find unique elements and mark the ones that are not */
		private void filterUniqueElements(int[] idx, int[] jdx, int[] kdx, int[] ldx, boolean[] validIdx) {
			int i;
			int j;
			int k;
			int l;
			int m;
			int n;

			for (m = 0; m < 8; m++) {
				i = idx[m];
				j = jdx[m];
				k = kdx[m];
				l = ldx[m];
				for (n = m + 1; n < 8; n++) {
					if (i == idx[n] && j == jdx[n] && k == kdx[n] && l == ldx[n])
						validIdx[n] = false;
				}
			}
		}
	}
}
