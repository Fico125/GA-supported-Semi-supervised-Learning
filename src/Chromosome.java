import java.util.Arrays;

import weka.core.Instances;

public class Chromosome {

	private double fitness = 0.0; // Representing the fitness of a chromosome.
	private int[] genes;
	private NaiveBayesModel naiveB;
	private static String outputTextChromosome = "";

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
	
	/** Method for calculating fitness of a chromosome by comparing it with the target chromosome. 
	 * Chromosome fitness is actually precision (evaluation metric).It is set after training, testing
	 * and evaluating the Naive Bayes model.*/
	private double recalculateFitness(Instances trainData, Instances testData) {
		
		double chromosomeFitness = 0.0;
		try {
			
			naiveB = new NaiveBayesModel(trainData, testData);
			naiveB.process();
			chromosomeFitness = naiveB.getGeometricMean();
			outputTextChromosome += "\n************************************************************\n";
			outputTextChromosome += "Model summary for chromosome: \n" + naiveB.getResultText();
			/*
			Evaluation evaluation = naiveB.getEvaluation();
			
			outputTextChromosome += "\n************************************************************";
			System.out.println("Model summary for chromosome: " + evaluation.toSummaryString());
			outputTextChromosome += "\nModel summary for chromosome: " + naiveB.getResultText();
			//FF-a je Geometric mean => korijen od(TPR * TNR)
			//double TPR = evaluation.truePositiveRate(1);
			//double TNR = evaluation.trueNegativeRate(1);
			//double geometricMean = Math.sqrt(TPR * TNR);
			//chromosomeFitness = geometricMean;
			chromosomeFitness = evaluation.precision(1);
			System.out.println("Chromosome fitness (precision): " + chromosomeFitness);
			outputTextChromosome += "Chromosome fitness (precision): " + chromosomeFitness;
			*/
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		return chromosomeFitness;
	}

	public void setFitness(Instances trainData, Instances testData) {
		
		fitness = recalculateFitness(trainData, testData);
	}
	
	public NaiveBayesModel getNaiveBayesOfSelectedChromosome() {
		
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
	
	public static String getOutputTextChromosome() {
		
		return outputTextChromosome;
	}
	
	public static void setOutputTextChromosome(String outputTextChromosome) {
		
		Chromosome.outputTextChromosome = outputTextChromosome;
	}
}
