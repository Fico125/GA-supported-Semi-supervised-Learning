import java.util.Arrays;

public class Chromosome {

	private boolean isFitnessChanged = true; // Used to check if fitness of a chromsome has changed,and if so trigger the recalculation of fitness. 
	private int fitness = 0; // Representing the fitness of a chromosome.
	private int[] genes;
	
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
	
	public int getFitness() {
		
		if(isFitnessChanged) {
			fitness = recalculateFitness();
			isFitnessChanged = false;
		}
		return fitness;
	}
	
	/** Method for calculating fitness of a chromosome by comparing it with the target chromosome. */
	public int recalculateFitness() {
		
		int chromosomeFitness = 0;
		for(int x = 0; x < genes.length; x++) {
			//if(genes[x] == GeneticAlgorithm.TARGET_CHROMOSOME[x]) chromosomeFitness++;
			if(genes[x] == GeneticAlgorithm.TARGET_CHROMOSOME[x]) chromosomeFitness++;

		}
		return chromosomeFitness;
	}
	
	public String toString() {
		
		return Arrays.toString(this.genes);
	}
}
