package Toy_Neural_Network;
import processing.core.PApplet;

public class NeuralNetwork {
	private int input_nodes;
	private int hidden_nodes;
	private int output_nodes;

	private Matrix_instance weights_ih;
	private Matrix_instance weights_ho;
	private Matrix_instance bias_h;
	private Matrix_instance bias_o;

	private double alpha = 0.1;

	public NeuralNetwork(int input_nodes, int hidden_nodes, int output_nodes) {
		this.input_nodes = input_nodes;
		this.hidden_nodes = hidden_nodes;
		this.output_nodes = output_nodes;

		weights_ih = new Matrix_instance(this.hidden_nodes, this.input_nodes);
		weights_ho = new Matrix_instance(this.output_nodes, this.hidden_nodes);

		this.weights_ih.randomise();
		this.weights_ho.randomise();

		bias_h = new Matrix_instance(this.hidden_nodes, 1);
		bias_o = new Matrix_instance(this.output_nodes, 1);
		
		this.bias_h.randomise();
		this.bias_o.randomise();
	}

	public NeuralNetwork(NeuralNetwork n) {
		this(n.getInputNodes(),n.getHiddenNodes(),n.getOutputNodes());
//		this.bias_o = n.getBias_O();
//		this.bias_h = n.getBias_H();
		this.weights_ho = n.getWeights_HO();
		this.weights_ih = n.getWeights_IH();
		this.bias_o.randomise();
		this.bias_h.randomise();
	}
	
	
	public double[] predict(double[] input_array) {
		// generating the hidden outputs
		Matrix_instance input = new Matrix_instance(input_array);
		Matrix_instance hidden = Matrix.Product(this.weights_ih, input);
		hidden.adder(this.bias_h);

		// activation function
		hidden.applyFunction(i -> sigmoid(i));
		Matrix_instance output = Matrix.Product(this.weights_ho, hidden);
		output.adder(this.bias_o);
		output.applyFunction(i -> sigmoid(i));
		return output.toArray();
	}
	public NeuralNetwork copy() {
		return new NeuralNetwork(this);
	}
	// ----------------------------------------------------------------------------------------
	// Activation functions
	public double sigmoid(double x) {
		return (double) (1 / (1 + Math.exp(-x)));
	}

	public double dsigmoid(double y) {
		return y * (1 - y);
	}

	public double tangent(double x) {
		return Math.tanh(x);
	}

	public double dtangent(double y) {
		return 1 - (y * y);
	}
	
	public double mutation(double val, double rate) {
		double rand = (Math.random() * 2 - 1);
		if(rand < rate) {
			return rand;
		} 
		return val;
	}
	public void mutate(double val){
		this.weights_ho.applyFunction(i -> mutation(i,val));
		this.weights_ih.applyFunction(i -> mutation(i,val));
		this.bias_h.applyFunction(i -> mutation(i,val));
		this.bias_o.applyFunction(i -> mutation(i,val));
	}
	public void mutate(func<Double> action){
		this.weights_ho.applyFunction(action);
		this.weights_ih.applyFunction(action);
		this.bias_h.applyFunction(action);
		this.bias_o.applyFunction(action);
	}

	// ----------------------------------------------------------------------------------------
	public void setLearningRate(double x) {
		alpha = x;
	}

	public void train(double[] input_array, double[] target_array) {
		Matrix_instance inputs = new Matrix_instance(input_array);
		Matrix_instance targets = new Matrix_instance(target_array);

		// hidden
		Matrix_instance hidden = Matrix.Product(this.weights_ih, inputs);
		hidden.adder(this.bias_h);
		hidden.applyFunction(i -> sigmoid(i));

		// output
		Matrix_instance output = Matrix.Product(this.weights_ho, hidden);
		output.adder(this.bias_o);
		output.applyFunction(i -> sigmoid(i));

		// Caluculate error
		// ERROR = TARGETS - OUTPUTS
		Matrix_instance output_error = Matrix.subtract(targets, output);
		Matrix_instance who_t = Matrix.transpose(this.weights_ho);
		Matrix_instance hidden_error = Matrix.Product(who_t, output_error);

		// calculate hidden_error
		// calc gradient
		Matrix_instance gradient = Matrix.applyFunction(i -> dsigmoid(i), output);
		gradient.multiply(output_error);
		gradient.multiply(alpha);

		// calc deltas
		Matrix_instance hidden_t = Matrix.transpose(hidden);
		Matrix_instance weights_ho_deltas = Matrix.Product(gradient, hidden_t);

		// adjust the weights by deltas
		this.weights_ho.adder(weights_ho_deltas);
		// adjust bias by deltas(gradients);
		this.bias_o.adder(gradient);

		// calc hidden gradient
		Matrix_instance hidden_gradient = Matrix.applyFunction(i -> dsigmoid(i), hidden);
		hidden_gradient.multiply(hidden_error);
		hidden_gradient.multiply(alpha);

		// calc input -> hidden deltas
		Matrix_instance inputs_T = Matrix.transpose(inputs);
		Matrix_instance weights_ih_deltas = Matrix.Product(hidden_gradient, inputs_T);

		// adjust the weights by deltas
		this.weights_ih.adder(weights_ih_deltas);
		// adjust bias by deltas(gradients);
		this.bias_h.adder(hidden_gradient);
	}
	// ----------------------------------------------------------------------------------------

	// getters

	public int getInputNodes() {
		return this.input_nodes;
	}

	public int getHiddenNodes() {
		return this.hidden_nodes;
	}

	public int getOutputNodes() {
		return this.output_nodes;
	}
	public double getLearningRate() {
		return alpha;
	}
	
	public Matrix_instance getBias_O() {
		return bias_o;
	}
	public Matrix_instance getBias_H() {
		return bias_h;
	}
	public Matrix_instance getWeights_IH() {
		return weights_ih;
	}
	public Matrix_instance getWeights_HO() {
		return weights_ho;
	}
}
