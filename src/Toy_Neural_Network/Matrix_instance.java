package Toy_Neural_Network;
import java.util.function.Consumer;

import processing.core.PApplet;

public class Matrix_instance {

	private int rows;
	private int cols;
	private double[][] data;
	private boolean error = false;

	public Matrix_instance(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		data = new double[rows][cols];
		intializeValues(data);
	}

	public Matrix_instance(double[][] m) {
		this.rows = m.length;
		this.cols = m[0].length;
		data = m;
	}

	public Matrix_instance(double[] m) {
		this.rows = m.length;
		this.cols = 1;
		data = new double[rows][cols];
		for (int i = 0; i < m.length; i++) {
			data[i][0] = m[i];
		}
	}

	

	// ---------------------------------------------------------
	// Scalar Functions
	public void multiply(double n) {
		int num = 0;
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] *= n;
			}
		}
	}

	public void adder(double n) {
		int num = 0;
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] += n;
			}
		}
	}
	
	// ---------------------------------------------------------
	public void applyFunction(func<Double> action) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				double temp = this.data[i][j];
				this.data[i][j] = action.accept(temp);
			}
		}
	}

	// ---------------------------------------------------------
	// Elementwise function
	// rows = rows/ cols = cols
	public void multiply(Matrix_instance n) {
		if (this.rows == n.getRows() && this.cols == n.getCols()) {
			int num = 0;
			for (int i = 0; i < this.rows; i++) {
				for (int j = 0; j < this.cols; j++) {
					data[i][j] *= n.getValue(i, j);
				}
			}
		} else {
			System.out.printf("%s%n",Matrix.InncorrectDimensions());
			error = true;
		}
	}
	
	public void adder(Matrix_instance n) {
		int num = 0;
		if (this.rows == n.getRows() && this.cols == n.getCols()) {
			for (int i = 0; i < this.rows; i++) {
				for (int j = 0; j < this.cols; j++) {
					data[i][j] += n.getValue(i, j);
				}
			}
		} else {
			System.out.printf("%s%n",Matrix.InncorrectDimensions());
			error = true;
		}
	}
	//----------------------------------------------------------------------
	//miscellneous
	public double[] toArray() {
		int k = 0;
		double[] arr = new double[(this.cols * 2)];
			for (int i = 0; i < this.rows; i++) {
				for (int j = 0; j < this.cols; j++) {
					Matrix.push(arr,this.data[i][j],k);
					k++;
			}
		}
		return arr;
	}
	 public Matrix_instance copy() {
		 Matrix_instance m = new Matrix_instance(this.rows, this.cols);
		    for (int i = 0; i < this.rows; i++) {
		      for (int j = 0; j < this.cols; j++) {
		        m.setValue(i,j, this.data[i][j]);
		      }
		    }
		    return m;
		  }
	 public void intializeValues(double[][] m) {
			for (int i = 0; i < this.rows; i++) {
				for (int j = 0; j < this.cols; j++) {
					m[i][j] = 0;
				}
			}
		}

		public void randomise() {
			this.applyFunction(i -> Math.random()*2 -1);
		}
		@Override
		public String toString() {
			String s = Matrix.InncorrectDimensions();
			if (!error) {
				s = "";
				for (int i = 0; i < this.rows; i++) {
					for (int j = 0; j < this.cols; j++) {
						s += String.format("[%d],[%d]: %.6f%n", i, j, data[i][j]);
					}
				}
			}
			return s;
		}
	//----------------------------------------------------------------------
	// getters/setters
	public int getRows() {
		return this.rows;
	}

	public int getCols() {
		return this.cols;
	}

	public double[][] getMatrix() {
		return data;
	}

	public double getValue(int r, int c) {
		return data[r][c];
	}

	public void setValue(int r, int c, double v) {
		data[r][c] = v;
	}

	public void setMatrix(double[][] m) {
		data = m;
	}

	
}
