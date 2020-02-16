package name.mjw.jquante.parallel.impl;

import java.util.ArrayList;

import name.mjw.jquante.parallel.ForTask;
import name.mjw.jquante.parallel.ReduceOperation;

/**
 * Simple implementation of a ForTak returning Double
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class DoubleForTask extends ForTask<Double> {

	public DoubleForTask(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}

	@Override
	public Double reduceResult(ArrayList<Double> partResults, ReduceOperation op) {
		Double result = 0.0;

		switch (op) {
			case PLUS :
				for (Double r : partResults)
					result += r;
				break;
			case MINUS :
				for (Double r : partResults)
					result -= r;
				break;
			case PRODUCT :
				result = 1.0;
				for (Double r : partResults)
					result *= r;
				break;
			default :
				throw new UnsupportedOperationException(
						"The requested openration '" + op
								+ "' is not supported");
		}

		return result;
	}
}
