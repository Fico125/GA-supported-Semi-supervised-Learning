import java.util.Arrays;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Chromosome {

	private boolean isFitnessChanged = true; // Used to check if fitness of a chromsome has changed,and if so trigger the recalculation of fitness. 
	private double fitness = 0.0; // Representing the fitness of a chromosome.
	private int[] genes;
	private NaiveBayesModel naiveB;
	
	public NaiveBayesModel getNaiveB() {
		return naiveB;
	}

	/** Chromosome represents a candidate soludion made of N genes.
	 * @param length size for an array of genes. */
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
	
	public int[] getGenes() {
		
		isFitnessChanged = true;
		
		return genes;
	}
	
	public double getFitness() {	
		return fitness;
	}
	
	/** Method for calculating fitness of a chromosome by comparing it with the target chromosome. */
	private double recalculateFitness(Instances trainData, Instances testData) {
		// ovdje bi trebao trenirati, testirati i evaluirati model.
		
		double chromosomeFitness = 0.0;
		try {
			//System.out.println("Naive Bayes training START");
			naiveB = new NaiveBayesModel(trainData, testData);
			naiveB.process();
			
			// TODO PROVJERITI:mislim da cijeli ovaj ostatak koda može ići u metodu process() koju pozivamo 2 linije iznad?
			Evaluation evaluation = naiveB.getEvaluation();
			chromosomeFitness = evaluation.precision(1);
			System.out.println("Chromosome fitness: (precision)" + chromosomeFitness);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		return chromosomeFitness;
	}
	
	public String toString() {
		
		return Arrays.toString(this.genes);
	}

	public void setFitness(Instances trainData, Instances testData) {
		fitness = recalculateFitness(trainData, testData);
	}
}
