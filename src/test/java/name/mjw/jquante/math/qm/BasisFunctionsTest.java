package name.mjw.jquante.math.qm;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import name.mjw.jquante.molecule.Atom;
import name.mjw.jquante.molecule.Molecule;
import name.mjw.jquante.molecule.impl.MoleculeImpl;

import org.junit.Test;

public class BasisFunctionsTest {

	double diff = 0.00001;

	@Test
	public void one() {

		BasisFunctions bf = null;

		Atom H1 = new Atom("H", 1.0, new Vector3D(0.752510, -0.454585, 0.000000));

		Molecule hydrogen = new MoleculeImpl("hydrogen");
		hydrogen.addAtom(H1);

		try {
			bf = new BasisFunctions(hydrogen, "sto-3g");

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(1, bf.getBasisFunctions().size());

		assertEquals(3.425251, bf.getBasisFunctions().get(0).getExponents()
				.get(0), diff);

		assertEquals(0.154329, bf.getBasisFunctions().get(0).getCoefficients()
				.get(0), diff);

	}

	@Test
	public void two() {

		Atom H1 = new Atom("H", 1.0, new Vector3D(0.752510, -0.454585, 0.000000));
		Atom O = new Atom("O", 6.0, new Vector3D(0.000000, 0.113671, 0.000000));
		Atom H2 = new Atom("H", 1.0,
				new Vector3D(-0.752510, -0.454585, 0.000000));

		Molecule water = new MoleculeImpl("water");
		water.addAtom(H1);
		water.addAtom(O);
		water.addAtom(H2);

		BasisFunctions bf = null;

		try {
			bf = new BasisFunctions(water, "sto-3g");

		} catch (Exception e) {

			e.printStackTrace();
		}
		assertEquals(7, bf.getBasisFunctions().size());

	}

}
