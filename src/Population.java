import java.util.Arrays;

import weka.core.Instances;

public class Population {
	
	private Chromosome[] chromosomes;

	/** Population represents a population of chromosomes. */
	public Population(int length) {
		
		chromosomes = new Chromosome[length];
	}
	
	/** Method that initializes an array of chromosomes. */
	public Population initializePopulation() {
		
		for(int x = 0; x < chromosomes.length; x++) {
			
			chromosomes[x] = new Chromosome(GeneticAlgorithm.TARGET_CHROMOSOME.length).initializeChromosome();
		}
				
		return this;
	}
	
	public void computeFitness(Instances trainData, Instances testData) {
				
		for(int x = 0; x < chromosomes.length; x++) {
			
			//System.out.println(chromosomes[x]);
			trainData = FileHandler.mergeDataWithLastColumn(trainData, chromosomes[x]);
			System.out.println("Chromosome #" + x + ":\n");
			chromosomes[x].setFitness(trainData,  testData);
		}
	}
	
	public void sortChromosomesByFitness() {
		
		Arrays.sort(chromosomes, (chromosome1, chromosome2) -> {
			int flag = 0;
			if(chromosome1.getFitness() > chromosome2.getFitness()) flag = -1;
			else if(chromosome1.getFitness() < chromosome2.getFitness()) flag = 1;
			return flag;
		});
	}
	
	public Chromosome[] getChromosomes() {
		
		return chromosomes;
	}
}
