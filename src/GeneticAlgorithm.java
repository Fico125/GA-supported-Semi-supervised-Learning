import weka.core.Instances;

/** Class which manages operations of genetic algorithm.
 * */
public class GeneticAlgorithm {

	public static final int POPULATION_SIZE = 50; // Number of chromosomes in a population.
	public static int[] TARGET_CHROMOSOME; // Used to extract the length of the given dataset.
	public static final double MUTATION_RATE = 0.075; // Probability that a chromosome gene will be selected for random mutation.
	public static final int NUMB_OF_ELITE_CHROMOSOMES = 2; // Chromosomes that will not be subjected to crossover or mutation.
	public static final int TOURNAMENT_SELECTION_SIZE = 50; // Tournament population size, used for chromosome crossover selection.
	private double fitnessOfTheFittestChromosomeFromGeneration;

	public GeneticAlgorithm(Instances data) {
		
		final int[]  TARGET_CHROMOSOME_ = FileHandler.getLastColumnValues(data);
		TARGET_CHROMOSOME = TARGET_CHROMOSOME_;
	}
	
	/** Method that evolves population by calling crossover on it, and then mutating it. */
	public Population evolve(Population population, Instances trainData, Instances testData) {
		
		System.out.println("Evolve population");
		return mutatePopulation(crossoverPopulation(population, trainData, testData),trainData, testData);
	}
	
	/** Method that applies mutation to some chromosomes in a population. */
	private Population mutatePopulation(Population population,  Instances trainData, Instances testData) {
		
		System.out.println("Mutate population");
		Population mutatePopulation = new Population(population.getChromosomes().length);
		
		// Excluding elite chromosomes from mutation.
		for(int x = 0; x < NUMB_OF_ELITE_CHROMOSOMES; x++) {
			
			mutatePopulation.getChromosomes()[x] = population.getChromosomes()[x];
		}
		
		// Looping through the rest of chromosomes and calling mutate chromosome method on them.
		for(int x = NUMB_OF_ELITE_CHROMOSOMES; x < population.getChromosomes().length; x++) {
			
			mutatePopulation.getChromosomes()[x] = mutateChromosome(population.getChromosomes()[x]);
		}
		
		mutatePopulation.computeFitness(trainData, testData);
		return mutatePopulation;
	}
	
	/** Method that applies crossover to some chromosomes in a population. */
	private Population crossoverPopulation(Population population, Instances trainData, Instances testData) {
		
		System.out.println("Crossover Population");
		Population crossoverPopulation = new Population(population.getChromosomes().length);
		
		// Excluding elite chromosomes from crossover.
		for(int x = 0; x < NUMB_OF_ELITE_CHROMOSOMES; x++) {
			
			crossoverPopulation.getChromosomes()[x] = population.getChromosomes()[x];
		}
		
		// Looping through the rest of chromosomes and selecting 2 fittest chromosomes from each tournament population 
		// and doing crossover on them before returning the new population.	
		System.out.println("Calling tournament population for non-elite chromosomes");
		for(int x = NUMB_OF_ELITE_CHROMOSOMES; x < population.getChromosomes().length; x++) {
			
			Chromosome chromosome1 = selectTournamentPopulation(population, trainData, testData).getChromosomes()[0];
			Chromosome chromosome2 = selectTournamentPopulation(population, trainData, testData).getChromosomes()[0];
			crossoverPopulation.getChromosomes()[x] = crossoverChromosome(chromosome1, chromosome2);
		}
		
		fitnessOfTheFittestChromosomeFromGeneration = crossoverPopulation.getChromosomes()[0].getFitness();
		
		return crossoverPopulation;
	}
	
	/** Method in which a number of chromosomes are selected randomly and from those the one with the highest fitness is chosen for tournament. */
	private Population selectTournamentPopulation(Population population, Instances trainData, Instances testData) {
		
		Population tournamentPopulation = new Population(TOURNAMENT_SELECTION_SIZE);
		
		for(int x = 0; x < TOURNAMENT_SELECTION_SIZE; x++) {
			
			tournamentPopulation.getChromosomes()[x] = population.getChromosomes()[(int)(Math.random()*population.getChromosomes().length)];
		}
		
		tournamentPopulation.sortChromosomesByFitness();
		
		return tournamentPopulation;
	}
	
	/** Method that takes random genes from parent chromosomes and creating a new chromosome from it. */
	private Chromosome crossoverChromosome(Chromosome chromosome1, Chromosome chromosome2) {
		
		Chromosome crossoverChromosome = new Chromosome(TARGET_CHROMOSOME.length);
		
		for(int x = 0; x < chromosome1.getGenes().length; x++) {
			
			if(Math.random() < 0.5) crossoverChromosome.getGenes()[x] = chromosome1.getGenes()[x];
			else crossoverChromosome.getGenes()[x] = chromosome2.getGenes()[x];
		}
		
		return crossoverChromosome;
	}
	
	/** Method that applies randomness on parsed chromosome before returning it. */
	private Chromosome mutateChromosome(Chromosome chromosome) {
		
		Chromosome mutateChromosome = new Chromosome(TARGET_CHROMOSOME.length);
		
		for(int x = 0; x < chromosome.getGenes().length; x++) {
			
			if(Math.random() < MUTATION_RATE) {
				
				if(Math.random() < 0.5) mutateChromosome.getGenes()[x] = 1;
				else mutateChromosome.getGenes()[x] = 0;
			}
			else mutateChromosome.getGenes()[x] = chromosome.getGenes()[x];
		}
		
		return mutateChromosome;
	}
	
	public double getFitnessOfTheFittestChromosomeFromGeneration() {
		
		return fitnessOfTheFittestChromosomeFromGeneration;
	}
}
