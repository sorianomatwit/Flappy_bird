package game;

import java.util.ArrayList;

import processing.core.PApplet;
import Toy_Neural_Network.NeuralNetwork;
import game.Flappy_Bird.Bird;

public class Flappy_Bird extends PApplet {
	public static void main(String[] args) {
		PApplet.main("game.Flappy_Bird");
	}

	public void settings() {
		size(480, 540);
	}

	ArrayList<Pipe> pipes;
	ArrayList<Bird> birds;
	ArrayList<Bird> savedBirds;
	Gen flock;
	int cycles = 1;
	boolean play = true;
	public static final int Pop_Total = 350;

	// identical use to setup in Processing IDE except for size()
	public void setup() {
		pipes = new ArrayList<Pipe>();
		birds = new ArrayList<Bird>();
		savedBirds = new ArrayList<Bird>();
		flock = new Gen();
		for (int i = 0; i < 3; i++) {
			pipes.add(new Pipe(i));
		}
		for (int i = 0; i < Pop_Total; i++) {
			birds.add(new Bird());
		}
	}

	public void keyPressed() {
		if (keyCode == RIGHT) {
			if(cycles != 100) cycles++;
		}
		if (keyCode == LEFT) {
			if(cycles != 1) cycles--;
		}
	}

	public void draw() {
		background(0);
		// drawing stuff
		for (Pipe p : pipes) {
			if (play)
				p.show();
		}
		for (Bird flappy : birds) {
			flappy.show();
		}
		for (int k = 0; k < cycles; k++) {
			// game logic
			for (Pipe p : pipes) {
				p.move();
			}
			for (int i = 0; i < birds.size(); i++) {
				Bird flappy = birds.get(i);

				for (Pipe p : pipes) {

					if (!flappy.isDead()) {
						flappy.addScore(p);
					}

					if (flappy.die(p)) {
						flappy.setDead(true);
					}
				}

				textSize(30);
				textAlign(LEFT);
				fill(255);
				text("Score: " + flappy.getScore(), 30, 60);
				// player actions

				flappy.think(pipes);
				if (play)
					flappy.fall();
				if (flappy.isDead()) {
					savedBirds.add(flappy);
					birds.remove(i);
				}
			}
			if (birds.size() == 0) {
				birds = flock.nextGenertion(savedBirds, Pop_Total);
				resetGame();
			}
			textSize(30);
			textAlign(LEFT);
			fill(255);
			text("Death: " + savedBirds.size(), 30, 30);
			textSize(30);
			textAlign(LEFT);
			fill(255);
			text("Gen: " + flock.getGenNum(), 30, 90);
		}
		textSize(30);
		textAlign(LEFT);
		fill(255);
		text("Cycles: " + cycles, 30, 120);

	}

	public void resetGame() {
		for (int i = 0; i < pipes.size(); i++) {
			Pipe p = pipes.get(i);
			p.setX(width + (p.getThreshold() * i));
			p.setNewHeights();
		}
	}
	// -----------------------------------------------------------------------

//	public void keyPressed() {
//		if(!play) {
//			birds.get(0).setDead(false);
//			birds.get(0).setScore(0);
//			for (int i = 0;i < pipes.size();i++) {
//				Pipe p = pipes.get(i);
//				p.setX(width+(p.getThreshold()*i));
//				p.setNewHeights();
//			}
//		}
//		play = true;
//		if (key == 32 && !birds.get(0).isDead()) {
//			birds.get(0).jump();
//		}		
//	}

	// ---------------------------------------------------------
	// PIPE CLASS
	public class Pipe {

		private double x, y;
		private float r= 0,b= 0,g = 255;
		
		private boolean closs = false;
		
		private double[] heights = new double[2];
		private double w = 50;
		private double playermass = 30;
		private int label;
		private double threshold = 200;
		private double space = 5;

		public Pipe(int index) {
			double maxHeight = height - (space * playermass);
			double percent = random(0, 1);
			double rpercent = 1 - percent;
			heights[0] = maxHeight * percent;
			heights[1] = maxHeight * rpercent;
			x = (width + threshold * index);
			label = index;
			y = 0;
		}

		// getters
		public void setX(double a) {
			x = a;
		}

		public double getThreshold() {
			return threshold;
		}

		public int getLabel() {
			return label;
		}

		public double getX() {
			return x;
		}

		public double getY_T() {
			return y;
		}

		public double getY_B() {
			return heights[0] + (space * playermass);
		}

		public double getHeight_T() {
			return heights[0];
		}

		public double getHeight_B() {
			return heights[1];
		}

		public double getWidth() {
			return w;
		}

		// --------------
		public void isClose() {
			closs = true;
		}
		public void show() {
			strokeWeight(2);
			stroke(127);
			if(closs) {
				r = 255;
				g = 0;
			} else {
				r = 0;
				g = 255;
			}
			fill(r, g, b);
			closs = false;
			rect((float) x, 0, (float) w, (float) heights[0]);

			rect((float) x, (float) (heights[0] + (space * playermass)), (float) w, (float) heights[1]);

		}

		public void move() {
			x -= 1.3;
			if (x + w < -threshold / 2) {
				setNewHeights();
				x = width;
			}
		}

		private void setNewHeights() {
			double maxHeight = height - (space * playermass);
			double percent = random(0, 1);
			double rpercent = 1 - percent;
			heights[0] = maxHeight * percent;
			heights[1] = maxHeight * rpercent;
		}
	}

	// -------------------------------------------------------------------------------------------------------
	// Gen functions
	public class Gen {

		private int genNum = 1;

		public int getGenNum() {
			return genNum;
		}

		public ArrayList<Bird> nextGenertion(ArrayList<Bird> sb, int pop) {
			ArrayList<Bird> b = new ArrayList<Bird>();
			this.calculateFitness(sb);

			for (int i = 0; i < pop; i++) {
				b.add(pickOne(sb, sb.get(i).getFitness()));
			}
			sb.clear();
			genNum++;
			return b;
		}

		public Bird pickOne(ArrayList<Bird> b, double prob) {
			int index = 0;
			double r = Math.random();
			while (r > 0) {
				r -= b.get(index).getFitness();
				index++;
			}
			index--;
			Bird s = b.get(index);
			Bird child = new Bird(s.brain);
			child.MyMutate();
			return child;

		}

		public void calculateFitness(ArrayList<Bird> b) {
			double sum = 0.0;
			// ArrayList<Double> copyArrayList = new ArrayList<Double>();
			for (Bird f : b) {
				//sum += f.getScore();
				sum+=f.getLifespan();
			}
			for (Bird f : b) {
				//f.setFitness(f.getScore() / sum);
				f.setFitness(f.getLifespan() / sum);
				// System.out.printf("Bird %d fitness: %f%n",i,sum,f.getFitness());
				// i++;
			}

		}
	}

	// ---------------------------------------------------------
	// BIRD CLASS
	public class Bird {
		private double x, y;
		private double vsp = 0;
		private double grv = (double) 0.3;
		private double mass;

		private boolean dead = false;
		private int Score = 0;
		private int savedLabel = -1;

		private double fitness = 0;
		private int lifespan = 0;
		public NeuralNetwork brain;

		public Bird() {
			brain = new NeuralNetwork(4, 8, 2);
			brain.setLearningRate(0.5);
			y = height / 2;
			x = width / 3;
			mass = 30;
		}

		public Bird(NeuralNetwork brain) {
			this.brain = brain.copy();
			y = height / 2;
			x = width / 3;
			mass = 30;
		}

		public void MyMutate() {
			this.brain.mutate(i -> specialMut(i,0.05));
		}
		
		public double specialMut(double x, double rate) {
			double rand = randomGaussian()*0.1;
			if(Math.random() < rate) {
				return x+ rand;
			} 
			return x;
		}

		public void think(ArrayList<Pipe> p) {
			Pipe closepipe;
			double[] closeX = new double[p.size()];
			for (int i = 0; i < p.size(); i++) {
				Pipe a = p.get(i);
				closeX[i] = (a.getX() + a.getWidth() - x);
				// System.out.printf("CloseX[%d]: %f%n",i,closeX[i]);
			}
			int lowindex = 0;
			for (int j = 0; j < closeX.length; j++) {
				if (closeX[lowindex] > closeX[j]) {
					lowindex = j;
				}
				if (closeX[lowindex] < 0) {
					lowindex++;
					if (lowindex > closeX.length - 1) {
						lowindex = 0;
					}
				}
			}
			
			closepipe = p.get(lowindex);
			closepipe.isClose();
			// System.out.printf("Pipe: %d%n",closepipe.getLabel());

			double[] inputs = new double[4];
			inputs[0] = y / height;
			inputs[1] = closepipe.getHeight_T() / height;
			inputs[2] = closepipe.getY_B() / height;
			inputs[3] = closepipe.getX() / width;

			double[] output = brain.predict(inputs);
			if (output[0] > output[1]) {
				jump();
			}
		}

		public void jump() {
			if (y - mass / 2 > 0) {
				vsp = -5;
			}
		}

		public void addScore(Pipe p) {
			if (this.x > p.getX() + p.getWidth() && savedLabel != p.getLabel()
					&& p.getX() > this.x - p.getThreshold()) {
				savedLabel = p.getLabel();
				Score++;
				// System.out.println(Score);
			}

		}

		public void setY(double a) {
			this.y = a;
		}

		public double getY_b() {
			return y + mass / 2 + 1;
		}

		public int getScore() {
			return Score;
		}

		public void setScore(int i) {
			Score = i;
		}

		public double getFitness() {
			return fitness;
		}

		public void setFitness(double x) {
			fitness = x;
		}

		public double getLifespan() {
			return lifespan;
		}

		public void show() {

			if (!dead) {
				fill(255, (float) 0.5);
			} else {
				fill(255, 0, 0);
			}
			ellipse((float) x, (float) y, (float) mass, (float) mass);
			
		}

		private int sign(double i) {
			if (i > 0) {
				return 1;
			} else if (i < 0) {
				return -1;
			}
			return 0;
		}

		public void fall() {
			if (y - mass / 2 < 0) {
				y = mass / 2;
				dead = true;
			}
			vsp += grv;
			while (((y + mass / 2) + vsp > height)) {
				while (!((y + mass / 2) + sign(vsp) > height)) {
					y += sign(vsp);
				}
				vsp = 0;
			}
			y += vsp;
			lifespan++;
		}

		public void setVsp(double x) {
			vsp = x;
		}

		public boolean die(Pipe p) {
			if (collision(p) || y+mass/2+1 > height) {
				// flappy.setY(-100);
				return true;
			}
			return false;
		}

		public boolean isDead() {
			return dead;
		}

		public void setDead(boolean x) {
			dead = x;
		}

		public boolean collision(Pipe p) {
			// B right side P left side
			if ((((this.x + this.mass / 2) > p.getX()) && this.x - mass / 2 < p.getX() + p.getWidth()
					&& (this.y - this.mass / 2 < p.getHeight_T() || this.y + this.mass / 2 > p.getY_B()))) {
				return true;
			}
			return false;
		}
	}

}
