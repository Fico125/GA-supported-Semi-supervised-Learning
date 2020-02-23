import java.util.Arrays;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Chromosome {

	private double fitness = 0.0; // Representing the fitness of a chromosome.
	private int[] genes;
	private NaiveBayesModel naiveB;

	/** Chromosome represents a candidate soludion made of N genes. */
	public Chromosome(int length) {
		
		genes = new int[length];
	}
	
	/** Method for initializing chromosome genes with 0-s and 1-s on random. */
	public Chromosome initializeChromosome() {
		
		for(int x = 0; x < genes.length; x++) {
			
			if(Math.random() >= 0.5) genes[x] = 1;
			else genes[x] = 0;
		}
		
		return this;
	}
	
	/** Method for calculating fitness of a chromosome by comparing it with the target chromosome. */
	private double recalculateFitness(Instances trainData, Instances testData) {
		
		double chromosomeFitness = 0.0;
		
		try {
			
			naiveB = new NaiveBayesModel(trainData, testData);
			naiveB.process();
			
			Evaluation evaluation = naiveB.getEvaluation();
			chromosomeFitness = evaluation.precision(1);
			System.out.println("Chromosome fitness: (precision)" + chromosomeFitness);
			
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		return chromosomeFitness;
	}

	public void setFitness(Instances trainData, Instances testData) {
		
		fitness = recalculateFitness(trainData, testData);
	}
	
	public NaiveBayesModel getNaiveB() {
		
		return naiveB;
	}
	
	public int[] getGenes() {
		
		return genes;
	}
	
	public double getFitness() {	
		
		return fitness;
	}
	
	public String toString() {
		
		return Arrays.toString(this.genes);
	}
}
