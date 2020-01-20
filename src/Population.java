import java.util.Arrays;

public class Population {
	
	private Chromosome[] chromosomes;

	/** Population represents a population of chromosomes.
	 * @param length size of the population. */
	public Population(int length) {
		
		chromosomes = new Chromosome[length];
	}
	
	/** Method that initializes an array of chromosomes. */
	public Population initializePopulation() {
		
		for(int x = 0; x < chromosomes.length; x++) {
			chromosomes[x] = new Chromosome(GeneticAlgorithm.TARGET_CHROMOSOME.length).initializeChromosome();
		}
		sortChromosomesByFitness();
		return this;
	}
	

	public Chromosome[] getChromosomes() {
		
		return chromosomes;
	}
	
	public void sortChromosomesByFitness() {
		
		Arrays.sort(chromosomes, (chromosome1, chromosome2) -> {
			int flag = 0;
			if(chromosome1.getFitness() > chromosome2.getFitness()) flag = -1;
			else if(chromosome1.getFitness() < chromosome2.getFitness()) flag = 1;
			return flag;
		});
	}
}
