package Toy_Neural_Network;
import Toy_Neural_Network.Matrix_instance;
import Toy_Neural_Network.func;
import processing.core.PApplet;

public class Matrix {
	static public Matrix_instance Product(Matrix_instance m, Matrix_instance n) {
		Matrix_instance temp = new Matrix_instance(m.getRows(), n.getCols());
		if (m.getCols() == n.getRows()) {
			for (int i = 0; i < temp.getRows(); i++) {
				for (int j = 0; j < temp.getCols(); j++) {
					double sum = 0;
					for (int k = 0; k < m.getCols(); k++) {
						sum += m.getValue(i, k) * n.getValue(k, j);
					}
					temp.setValue(i, j, sum);
				}
			}
		} else {
			System.out.printf("%s%n", InncorrectDimensions());
			return null;
		}
		return temp;
	}

	// -----------------------------------------------
	static public Matrix_instance Product(Matrix_instance m, double[][] n) {
		Matrix_instance temp = new Matrix_instance(m.getRows(), n[0].length);
		if (m.getCols() == n.length) {
			for (int i = 0; i < temp.getRows(); i++) {
				for (int j = 0; j < temp.getCols(); j++) {
					double sum = 0;
					for (int k = 0; k < m.getCols(); k++) {
						sum += m.getValue(i, k) * n[k][j];
					}
					temp.setValue(i, j, sum);
				}
			}
		} else {
			System.out.printf("%s%n", InncorrectDimensions());
			return null;
		}
		return temp;
	}

	// ---------------------------------------------------------
	// Transpose matrix
	static public Matrix_instance transpose(Matrix_instance m) {
		Matrix_instance results = new Matrix_instance(m.getCols(), m.getRows());
		for (int i = 0; i < results.getRows(); i++) {
			for (int j = 0; j < results.getCols(); j++) {
				results.setValue(i, j, m.getValue(j, i));
			}
		}
		return results;
	}

	// ---------------------------------------------------------
	static public Matrix_instance applyFunction(func<Double> action, Matrix_instance m) {
		Matrix_instance temporary = m;
		for (int i = 0; i < m.getRows(); i++) {
			for (int j = 0; j < m.getCols(); j++) {
				double temp = temporary.getValue(i, j);
				temporary.setValue(i,j,action.accept(temp));
			}
		}
		return temporary;
	}

	// -----------------------------------------------
	// return new matrix element wise
	static public Matrix_instance subtract(Matrix_instance m, Matrix_instance n) {
		if (m.getRows() != n.getRows() || m.getCols() != n.getCols()) {
			System.out.printf("%s%n", InncorrectDimensions());
			return null;
		}
		Matrix_instance temp = new Matrix_instance(m.getRows(), m.getCols());
		for (int i = 0; i < temp.getRows(); i++) {
			for (int j = 0; j < temp.getCols(); j++) {
				double data = (m.getValue(i, j) - n.getValue(i, j));
				temp.setValue(i, j, data);
			}
		}

		return temp;
	}

	// -----------------------------------------------
	static public Matrix_instance Product(double[][] m, double[][] n) {
		Matrix_instance temp = new Matrix_instance(m.length, n[0].length);
		if (m.length == n[0].length) {
			for (int i = 0; i < temp.getRows(); i++) {
				for (int j = 0; j < temp.getCols(); j++) {
					double sum = 0;
					for (int k = 0; k < m[0].length; k++) {
						sum += m[i][k] * n[k][j];
					}
					temp.setValue(i, j, sum);
				}
			}
		} else {
			System.out.printf("%s%n", InncorrectDimensions());
			return null;
		}
		return temp;
	}

	static public String InncorrectDimensions() {
		return "Matrix Dimension Are Not Valid";
	}

	public static void push(double[] a, double b, int count) {
		for (int i = 0; i < a.length - count; i++) {
			a[i] = b;
		}
	}
}
