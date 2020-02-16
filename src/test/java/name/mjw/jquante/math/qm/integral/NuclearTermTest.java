package name.mjw.jquante.math.qm.integral;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

import name.mjw.jquante.math.qm.basis.Power;

public class NuclearTermTest {

	private final double delta = 0.000001;

	NuclearTerm nuclearTerm = new NuclearTerm();

	@Test
	public void constructATerm000000001() {

		assertEquals(1.0, nuclearTerm.constructATerm(0, 0, 0, 0, 0, 0, 0, 0, 1), delta);

	}

	@Test
	public void constructATerm000011111() {

		assertEquals(1.0, nuclearTerm.constructATerm(0, 0, 0, 0, 1, 1, 1, 1, 1), delta);

	}

	@Test
	public void constructATerm100011111() {

		assertEquals(-1.0, nuclearTerm.constructATerm(1, 0, 0, 0, 1, 1, 1, 1, 1), delta);

	}

	@Test
	public void constructATerm000111111() {

		assertEquals(1.0, nuclearTerm.constructATerm(0, 0, 0, 1, 1, 1, 1, 1, 1), delta);

	}

	@Test
	public void constructATerm100111111() {

		assertEquals(-2.0, nuclearTerm.constructATerm(1, 0, 0, 1, 1, 1, 1, 1, 1), delta);

	}

	@Test
	public void constructATerm200111111() {

		assertEquals(1.0, nuclearTerm.constructATerm(2, 0, 0, 1, 1, 1, 1, 1, 1), delta);

	}

	@Test
	public void constructATerm201111111() {

		assertEquals(-0.5, nuclearTerm.constructATerm(2, 0, 1, 1, 1, 1, 1, 1, 1), delta);

	}

	@Test
	public void constructATerm210111111() {

		assertEquals(0.5, nuclearTerm.constructATerm(2, 1, 0, 1, 1, 1, 1, 1, 1), delta);

	}

	@Test
	public void constructAArray000001() {

		double[] expected = new double[] { 1.0 };

		assertArrayEquals(expected, nuclearTerm.constructAArray(0, 0, 0.0, 0.0, 0.0, 1.0), delta);

	}

	@Test
	public void constructAArray011111() {

		double[] expected = new double[] { 1.0, -1.0 };

		assertArrayEquals(expected, nuclearTerm.constructAArray(0, 1, 1.0, 1.0, 1.0, 1.0), delta);

	}

	@Test
	public void constructAArray111111() {

		double[] expected = new double[] { 1.5, -2.5, 1.0 };

		assertArrayEquals(expected, nuclearTerm.constructAArray(1, 1, 1.0, 1.0, 1.0, 1.0), delta);

	}

	@Test
	public void nuclearAttraction() {
		assertEquals(-3.141593, nuclearTerm.nuclearAttraction(new Vector3D(0, 0, 0), 1.0, new Power(0, 0, 0), 1.0,
				new Vector3D(0, 0, 0), 1.0, new Power(0, 0, 0), 1.0, new Vector3D(0, 0, 0)), delta);

	}

}
