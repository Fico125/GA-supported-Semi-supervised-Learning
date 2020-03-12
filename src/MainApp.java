import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
/** Class containing main method.
 * */
@SuppressWarnings("unused")
public class MainApp {
	
	public static final int MAXIMUM_NUMBER_OF_GENERATIONS = 500;
	protected Shell shlApp;

	public static void main(String[] args) throws IOException{
		
		try {
			
			MainApp window = new MainApp();
			window.openWindow();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void openWindow() {
		
		Display display = Display.getDefault();
		createContents();
		shlApp.open();
		shlApp.layout();
		
		while(!shlApp.isDisposed()) {
			
			if(display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void createContents() {
		
		HashMap<Integer, Double> fitnessFunctionEvolution = new HashMap<Integer, Double>();
		
		shlApp = new Shell();
		shlApp.setMinimumSize(90, 22);
		shlApp.setMaximized(true);
		shlApp.setText("Genetic Algorithm supported Semi-supervised Learning");
		
		Text textCalculation = new Text(shlApp, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textCalculation.setBounds(177, 10, 700, 820);
		
		Text textScoring = new Text(shlApp, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textScoring.setBounds(877, 10, 700, 820);
		
		Input inputTrain = new Input();
		Input inputTest = new Input();
				
		Button btnClose = new Button(shlApp, SWT.NONE);
		btnClose.setBounds(0, 170, 134, 28);
		btnClose.setText("Close");
		
		Button btnUcitajTrain = new Button(shlApp, SWT.NONE);
		btnUcitajTrain.setBounds(0, 9, 134, 28);
		btnUcitajTrain.setText("Add file (Train)");

		Button btnDatotekaSpremna = new Button(shlApp, SWT.CHECK);
		btnDatotekaSpremna.setBounds(10, 43, 124, 18);
		btnDatotekaSpremna.setText("File ready");
		btnDatotekaSpremna.setEnabled(false);
		
		Button btnUcitajTest = new Button(shlApp, SWT.NONE);
		btnUcitajTest.setBounds(0, 67, 134, 28);
		btnUcitajTest.setText("Add file (Test)");

		Button btnDatotekaTestSpremna = new Button(shlApp, SWT.CHECK);
		btnDatotekaTestSpremna.setBounds(10, 101, 124, 18);
		btnDatotekaTestSpremna.setText("File ready");
		btnDatotekaTestSpremna.setEnabled(false);
		
		Button btnIzracunaj = new Button(shlApp, SWT.NONE);
		btnIzracunaj.setBounds(0, 135, 134, 28);
		btnIzracunaj.setText("Start");
		
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					System.exit(0);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		btnUcitajTrain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
					FileDialog fileDialog = new FileDialog(shlApp, SWT.MULTI);
					String firstFile = fileDialog.open(); // if SupressWarnings is moved, this is "unused", but fileDialog wont open without it.
					String fileName = fileDialog.getFileName();
					String filePath = fileDialog.getFilterPath();
					inputTrain.readFile(fileName, filePath);
					btnDatotekaSpremna.setSelection(true);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		btnUcitajTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
					FileDialog fileDialog = new FileDialog(shlApp, SWT.MULTI);
					String firstFile = fileDialog.open(); // if SupressWarnings is moved, this is "unused", but fileDialog wont open without it.
					String fileName = fileDialog.getFileName();
					String filePath = fileDialog.getFilterPath();
					inputTest.readFile(fileName, filePath);
					btnDatotekaTestSpremna.setSelection(true);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		btnIzracunaj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(inputTrain.getData());
				int generationNumber = 0;

				textCalculation.append("Dataset length: " + GeneticAlgorithm.TARGET_CHROMOSOME.length + "\n");
				textCalculation.append("Number of generations: " + MAXIMUM_NUMBER_OF_GENERATIONS + "\n");
				textCalculation.append("Population size: " + GeneticAlgorithm.POPULATION_SIZE + "\n");
				textCalculation.append("Mutation rate: " + GeneticAlgorithm.MUTATION_RATE + "\n");
				textCalculation.append("Number of elite chromosomes: " + GeneticAlgorithm.NUMB_OF_ELITE_CHROMOSOMES + "\n");
				textCalculation.append("Tournament selection size: " + GeneticAlgorithm.TOURNAMENT_SELECTION_SIZE + "\n");
				
				Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
				Instances dataWithoutLastColumn = FileHandler.getDataWithoutLastColumn(inputTrain.getData());
				Instances trainData = dataWithoutLastColumn; // trainData spajamo sa posljednjim stupcem (GA outputom) prilikom izračunavanja fitnessa u metodi computeFitness
				textCalculation.append("Number of attributes in a dataset: " + trainData.numAttributes() + "\n");
				
				//Instances testData = inputTest.getData();
				//testData = FileHandler.numericToNominal(testData);
				// We comment upper 2 lines for testData and uncomment these bottom 2 lines if we want to used
				// reduced dataset for testing purposes.
			    Instances testData = FileHandler.reduceDatasetByGivenPercent(inputTest.getData(), 98.0);
			    testData = FileHandler.numericToNominal(testData);
				
				Instances predictionData = inputTrain.getData(); // ovo je cijeli test dataset, podaci i zadnji stupac
				predictionData = FileHandler.numericToNominal(predictionData);
				
				population.computeFitness(trainData, testData);
				
				while(generationNumber < MAXIMUM_NUMBER_OF_GENERATIONS) {	
					
					generationNumber++;
					
					System.out.println("-------------------------------------------------------------------------------------------------------");
					System.out.println("Gen. num.: " + generationNumber);
					textCalculation.append("-------------------------------------------------------------------------------------------------------\n");
					textCalculation.append("-------------------------------------------------------------------------------------------------------");
					textCalculation.append("\nGeneration number: " + generationNumber + "\n");
					
					population = geneticAlgorithm.evolve(population, trainData, testData);
					population.sortChromosomesByFitness();
					
					String ChromosomeBayesOutput = Chromosome.getOutputTextChromosome();
					textCalculation.append(ChromosomeBayesOutput);
					
					double fitnessOfTheFittestChromosomeInGenerataion = geneticAlgorithm.getFitnessOfTheFittestChromosomeFromGeneration();
					System.out.println("\n\nFittness of the fittest chromosome in this population: " + String.valueOf(fitnessOfTheFittestChromosomeInGenerataion) + "\n");
					textCalculation.append("\n\nFittness of the fittest chromosome in this population: " + String.valueOf(fitnessOfTheFittestChromosomeInGenerataion) + "\n");
					
					fitnessFunctionEvolution.put(generationNumber, fitnessOfTheFittestChromosomeInGenerataion); // Storing generation number and fitness of the fittest chromosome
					
					if(fitnessOfTheFittestChromosomeInGenerataion == 1.0) {
						break;
					}
				}
				
				Chromosome[] lastGenChromosomes = new Chromosome[population.getChromosomes().length];
				double[] lastGenChromosomesFitness = new double[population.getChromosomes().length];
				System.out.println("\nChromosomes and their fitness from last generation:\n");
				//textCalculation.append("\nChromosomes and their fitness from last generation:\n");
				for (int i = 0; i < GeneticAlgorithm.POPULATION_SIZE; i++  ){
					
					System.out.println(population.getChromosomes()[i]  + " | Fitness: " + 
							String.valueOf(population.getChromosomes()[i].getFitness()) + "\n");
					lastGenChromosomes[i] = population.getChromosomes()[i];
					lastGenChromosomesFitness[i] = population.getChromosomes()[i].getFitness();
					//textCalculation.append("Chromosome #" + i + ": " + population.getChromosomes()[i] + " | Fitness: " + 
					//		String.valueOf(population.getChromosomes()[i].getFitness()) + "\n");
				}
				System.out.println("------------------------------------------------------------------------\n");
				textCalculation.append("------------------------------------------------------------------------\n");
				System.out.println("Evolution of the best fitness function through the generations: ");
				textCalculation.append("Evolution of the best fitness function through the generations: \n");
				@SuppressWarnings("rawtypes")
				Set set = fitnessFunctionEvolution.entrySet();
			    @SuppressWarnings("rawtypes")
				Iterator iterator = set.iterator();
			    while(iterator.hasNext()) {
			    	@SuppressWarnings("rawtypes")
			    	Map.Entry mentry = (Map.Entry)iterator.next();
			   		System.out.print("Gen: "+ mentry.getKey() + " FF: ");
			   		textCalculation.append("Gen: "+ mentry.getKey() + " FF: ");
			   		System.out.println(mentry.getValue());
			   		textCalculation.append(mentry.getValue() + "\n");
			    }
			    
			    //Y1'_BEST EVALUATION START
//			    double[] predictionsofLastModel = NaiveBayesModel.getPredictions();
//			    int[] trainDataLastColumn = FileHandler.getLastColumnValues(inputTrain.getData());
//				double truePositive = 0.0;
//				double trueNegative = 0.0;
//				double falsePositive = 0.0;
//				double falseNegative = 0.0;
//				double truePositiveRate = 0.0;
//				double trueNegativeRate = 0.0;
//				double geometricMean = 0.0;
//				double precision = 0.0;
//			    double accuracy = 0.0;
//				double recall = 0.0;
//				double fmeasure = 0.0;
//				
//				System.out.println("\nPredictions for \"Y1'_BEST\": ");
//				//textCalculation.append("\nPredictions: \n");
//			    for(int i = 0; i < trainDataLastColumn.length; i++) {
//			    	
//			    	int actual = trainDataLastColumn[i];
//			    	int predicted = (int) predictionsofLastModel[i];
//			    	System.out.println(/*"Attribute #" + i + */"Actual: " + actual + ", predicted: " + predicted);
//			    	//textCalculation.append("Attribute #" + i + ", actual: " + actual + ", predicted: " + predicted + "\n");
//
//				    // if a value is 0, we consider it positive (faulty), if it is 1, we consider it negative (not-faulty) 
//				    if(actual == 0.0 && predicted == 0.0) { 
//				    	trueNegative++;
//				    }
//				    else if(actual == 0.0 && predicted == 1.0) {
//				    	falsePositive++;
//				    }
//				    else if(actual == 1.0 && predicted == 1.0) {
//				    	truePositive++;
//				    }
//				    else { // actual == 1.0 && predicted == 0.0
//				    	falseNegative++;
//				    }
//			    }
//			    
//			    accuracy = (truePositive + trueNegative) / trainDataLastColumn.length;
//			    recall = truePositive / (truePositive + falseNegative);
//			    precision = truePositive / (truePositive + falsePositive);
//			    fmeasure = (2 * precision * recall) / (precision + recall);
//			    truePositiveRate = truePositive / (truePositive + falseNegative);
//			    trueNegativeRate = trueNegative / (trueNegative + falsePositive);
//			    geometricMean = Math.sqrt(truePositiveRate * trueNegativeRate);
//			    
//			    System.out.println("\"Y1'_Best\" model statistics: ");
//			    textScoring.append("\"Y1'_Best\" model statistics: \n");
//			    
//			    //System.out.println("Total number of instances: " + trainDataLastColumn.length);
//			    //textScoring.append("Total number of instances: " + trainDataLastColumn.length + "\n");
//			    
//			    System.out.println("Correctly classified instances: " + (truePositive + trueNegative));
//			    textScoring.append(/*"Correctly classified instances: " + */(truePositive + trueNegative) + "\n");
//			    
//			    System.out.println("Incorrectly classified instances: " + (falsePositive + falseNegative));
//			    textScoring.append(/*"Incorrectly classified instances: " + */(falsePositive + falseNegative) + "\n");
//			    
//			    System.out.println("Geometric mean: " + geometricMean);
//			    textScoring.append(/*"Geometric mean: " + */geometricMean + "\n");
//			    
//			    System.out.println("F measure: " + fmeasure);
//			    textScoring.append(/*"F measure: " + */fmeasure + "\n");
//			    
//			    System.out.println("Precision: " + precision);
//			    textScoring.append(/*"Precision: " + */precision + "\n");
//			    
//			    System.out.println("Recall: " + recall);
//			    textScoring.append(/*"Recall: " + */recall + "\n");
//
//			    System.out.println("Accuracy: " + accuracy);
//			    textScoring.append(/*"Accuracy: " + */accuracy + "\n");
//			    
//			    //System.out.println("Confusion matrix: \n" + "TP: " + truePositive + 
//			    //		"\tFN: " + falseNegative + "\n" + "FP: " + falsePositive + "\tTN: " + trueNegative);
//			    //textScoring.append("Confusion matrix: \n" + "TP: " + truePositive + 
//			    //		"\tFN: " + falseNegative + "\n" + "FP: " + falsePositive + "\tTN: " + trueNegative + "\n");
//			    System.out.println(
//			    		"TP: " + truePositive + "\n" + 
//			    		"FN: " + falseNegative + "\n" + 
//			    		"FP: " + falsePositive + "\n" + 
//			    		"TN: " + trueNegative + "\n");
//			    
//			    textScoring.append(
//			    		truePositive + "\n" + 
//			    		falseNegative + "\n" + 
//			    		falsePositive + "\n" + 
//			    		trueNegative + "\n");
			    //Y1'_BEST EVALUATION END
			    
			    // ENSEMBLE OF BEST MODELS EVALUATION START
			    NaiveBayesModel[] bestModels = new NaiveBayesModel[population.getChromosomes().length];
			    FilteredClassifier[] classifiersOfBestModels = new FilteredClassifier[population.getChromosomes().length];
			    for(int i = 0; i < population.getChromosomes().length; i++) {
			    	bestModels[i] = population.getChromosomes()[i].getNaiveBayesOfSelectedChromosome();
			    	classifiersOfBestModels[i] = bestModels[i].getFilteredClassifier();
			    }
			    
			    int numberOfModelsForEnsemble = 5;
			    
			    String ensembleOfModelsOutput = NaiveBayesModel.evaluateEnsembleModel(classifiersOfBestModels, predictionData, numberOfModelsForEnsemble);
				System.out.println("------------------------------------------------------------------------\n");
				textScoring.append("------------------------------------------------------------------------\n");
			    System.out.println("\n\"Ensemble of best models\" statistics: \n"  + ensembleOfModelsOutput);
			    textScoring.append("\"Ensemble of best models\" statistics: \n" + ensembleOfModelsOutput);
			    // ENSEMBLE OF BEST MODELS EVALUATION END

			
			    // BEST MODEL EVALUATION START
				NaiveBayesModel bestModel = population.getChromosomes()[0].getNaiveBayesOfSelectedChromosome();
				String bestModelOutput = bestModel.evaluateLastModel(bestModel.getFilteredClassifier(), predictionData);
				System.out.println("------------------------------------------------------------------------\n");
				textScoring.append("------------------------------------------------------------------------\n");
			    System.out.println("\n\"Best model\" statistics: \n"  + bestModelOutput);
			    textScoring.append("\"Best model\" statistics: \n" + bestModelOutput);
			    // BEST MODEL EVALUATION END
			    
				// SUPERVISED LEARNING "CALLING METHOD" VERSION START
			    String standardSupervisedLearningOutput = standardSupervisedLearning(inputTrain.getData(), testData);
			    textScoring.append(standardSupervisedLearningOutput);
				// SUPERVISED LEARNING "CALLING METHOD" VERSION END
			    
				// SUPERVISED LEARNING "within Izracunaj button" VERSION START
//			    try {
//
//			    	Instances standardTestData = testData;
//					Instances standardTrainData = inputTrain.getData();
//					standardTrainData = FileHandler.numericToNominal(standardTrainData);
//			    	
//					// Standard supervised learning using our class NaiveBayesModel and our evaluation without genetic algorithm
//					NaiveBayesModel standardNaiveBayes = new NaiveBayesModel(standardTestData, standardTrainData);
//					standardNaiveBayes.process();
//					String results = standardNaiveBayes.getResultText();
//					textScoring.append("------------------------------------------------------------------------\n");
//					textScoring.append("Standard Supervised learning statistics: \n" + results + "\n");
//	
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
				// SUPERVISED LEARNING "within Izracunaj button" VERSION END

			    String testDatasetReductionOutput = FileHandler.getReductionOfDatasetOutput();
			    textScoring.append(testDatasetReductionOutput);
				
			    System.out.println("------------------------------------------------------------------------\n");
			    textCalculation.append("------------------------------------------------------------------------\n");
			    String hammingDistanceOutput = calculateHammingDistanceBetweenChromosomes(lastGenChromosomes, lastGenChromosomesFitness);
			    textCalculation.append(hammingDistanceOutput);
			}
		});		
	}
	
	public String standardSupervisedLearning(Instances train, Instances test) {
		
		String resultText = "";
		
	    try {

	    	Instances standardTestData = test;
			Instances standardTrainData = train;
			standardTrainData = FileHandler.numericToNominal(standardTrainData);
	    	
			// Standard supervised learning using our class NaiveBayesModel and our evaluation without genetic algorithm
			NaiveBayesModel standardNaiveBayes = new NaiveBayesModel(standardTestData, standardTrainData);
			standardNaiveBayes.process();
			System.out.println("------------------------------------------------------------------------");
			resultText += "------------------------------------------------------------------------\n";
			resultText += "Standard Supervised learning statistics: \n";
			resultText += standardNaiveBayes.getResultText() + "\n";

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return resultText;
	}
	
	public String calculateHammingDistanceBetweenChromosomes(Chromosome[] lastGenChromosomes, double[] lastGenChromosomesFitness) {
		
		String resultText = "";
		int[] fittestChromosome = lastGenChromosomes[0].getGenes();
		int chromosomeLength = lastGenChromosomes[0].getGenes().length;
		double[] hammingPercentage = new double[chromosomeLength-1];
		double[] hammingDistance = new double[chromosomeLength-1];

		System.out.println("Calculating Hamming distance between fittest chromosome and all the rest");
		resultText += "Calculating Hamming distance between fittest chromosome and all the rest\n";
		System.out.println("Chromosome length (number of genes): " + chromosomeLength);
		resultText += "Chromosome length (number of genes): " + chromosomeLength + "\n";
		
		// Loop that passes through all of the rest chromosomes
		for(int i = 1; i < lastGenChromosomes.length; i++) {
			
			hammingDistance[i] = 0;
			int [] chromosomeGenes = lastGenChromosomes[i].getGenes();
			
			// Loop that passes through all genes of the i-th chromosome
			for(int j = 0; j < chromosomeLength; j++) {
				
				if(fittestChromosome[j] != chromosomeGenes[j])
					hammingDistance[i]++;
			}
			
			hammingPercentage[i] = ((double)hammingDistance[i] / chromosomeLength) * 100;
			
			//System.out.println("Distance between 1. and " + (i+1) + ". chromosome: " + hammingDistance[i] + " | Percentage: " + String.format("%.3g", hammingPercentage[i]) + " | FF: " + lastGenChromosomesFitness[i]);
			//resultText += "Distance between 1. and " + (i+1) + ". chromosome: " + hammingDistance[i] + " | Percentage: " + String.format("%.3g", hammingPercentage[i]) + " | FF: " + lastGenChromosomesFitness[i] + "\n";
			resultText += "Distance between 1. and " + (i+1) + "\n";
		}
		
		for(int i = 1; i < lastGenChromosomes.length; i++) {
			resultText += hammingDistance[i] + "\n";
		}
		
		resultText += "Percentage:\n";
		for(int i = 1; i < lastGenChromosomes.length; i++) {
			resultText += String.format("%.3g", hammingPercentage[i]) + "\n";
		}
		
		resultText += "Fitness function:\n";
		for(int i = 1; i < lastGenChromosomes.length; i++) {
			resultText += lastGenChromosomesFitness[i] + "\n";
		}
				
		return resultText;
	}
}
