import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

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
import weka.core.Instances;
/** Class containing main method.
 * */
@SuppressWarnings("unused")
public class MainApp {
	
	public static final int MAXIMUM_NUMBER_OF_GENERATIONS = 50;
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
		
		Button btnUcitajTrain = new Button(shlApp, SWT.NONE);
		btnUcitajTrain.setBounds(0, 9, 134, 28);
		btnUcitajTrain.setText("Add file (Train)");

		Button btnDatotekaSpremna = new Button(shlApp, SWT.CHECK);
		btnDatotekaSpremna.setBounds(10, 43, 124, 18);
		btnDatotekaSpremna.setText("File ready");
		btnDatotekaSpremna.setEnabled(false);
		
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
		
		Button btnUcitajTest = new Button(shlApp, SWT.NONE);
		btnUcitajTest.setBounds(0, 67, 134, 28);
		btnUcitajTest.setText("Add file (Test)");

		Button btnDatotekaTestSpremna = new Button(shlApp, SWT.CHECK);
		btnDatotekaTestSpremna.setBounds(10, 101, 124, 18);
		btnDatotekaTestSpremna.setText("File ready");
		btnDatotekaTestSpremna.setEnabled(false);
		
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
		
		Button btnIzracunaj = new Button(shlApp, SWT.NONE);
		btnIzracunaj.setBounds(0, 135, 134, 28);
		btnIzracunaj.setText("Start");
		
		btnIzracunaj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(inputTrain.getData());

				textCalculation.append("Dataset length: " + GeneticAlgorithm.TARGET_CHROMOSOME.length + "\n");
				textCalculation.append("Number of generations: " + MAXIMUM_NUMBER_OF_GENERATIONS + "\n");
				textCalculation.append("Population size: " + GeneticAlgorithm.POPULATION_SIZE + "\n");
				textCalculation.append("Mutation rate: " + GeneticAlgorithm.MUTATION_RATE + "\n");
				textCalculation.append("Number of elite chromosomes: " + GeneticAlgorithm.NUMB_OF_ELITE_CHROMOSOMES + "\n");
				textCalculation.append("Tournament selection size: " + GeneticAlgorithm.TOURNAMENT_SELECTION_SIZE + "\n");
				
				Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
				Instances dataWithoutLastColumn = FileHandler.getDataWithoutLastColumn(inputTrain.getData());
				Instances trainData = dataWithoutLastColumn; // trainData spajamo sa posljednjim stupcem (GA outputom) prilikom izračunavanja fitnessa u metodi computeFitness
				//trainData.setClassIndex(trainData.numAttributes() - 1);
				textCalculation.append("Number of attributes in a dataset: " + trainData.numAttributes() + "\n");
				
				Instances testData = inputTest.getData();
				Instances predictionData = inputTrain.getData(); // ovo je cijeli test dataset, podaci i zadnji stupac
				population.computeFitness(trainData, testData);
				
				int generationNumber = 0;
				
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
					
					double fittestChromosome = geneticAlgorithm.getFitnessOfTheFittestChromosomeFromGeneration();
					System.out.println("\n\nFittness of the fittest chromosome in this population: " + String.valueOf(fittestChromosome) + "\n");
					textCalculation.append("\n\nFittness of the fittest chromosome in this population: " + String.valueOf(fittestChromosome) + "\n");
					
					if(fittestChromosome == 1.0) {
						break;
					}
				}
				
				System.out.println("\nChromosome fitness from last generation:");
				textCalculation.append("\nChromosome fitness from last generation:\n");
				
				for (int i = 0; i < GeneticAlgorithm.POPULATION_SIZE; i++  ){
					
					System.out.println(population.getChromosomes()[i].getFitness());
					textCalculation.append("Chromosome #" + i + ": " + String.valueOf(population.getChromosomes()[i].getFitness()) + "\n");
				}
				
//				System.out.println("\nChromosomes from last generation:\n");
//				textCalculation.append("\nChromosomes from last generation:\n");
//				for (int i = 0; i < GeneticAlgorithm.POPULATION_SIZE; i++  ){
//					
//					System.out.println(population.getChromosomes()[i]);
//					textCalculation.append("Chromosome #" + i + ": " + population.getChromosomes()[i] + "\n");
//				}
				
				NaiveBayesModel bestModel = population.getChromosomes()[0].getNaiveBayesOfSelectedChromosome();
				Evaluation evaluation;
				
				try {
					
					evaluation = new Evaluation(predictionData);
					evaluation.evaluateModel(bestModel.getNaiveBayes(), predictionData);
					System.out.println("\nBest Model Statistics: " + evaluation.toSummaryString());
					textScoring.append("Best Model Statistics: \n" + evaluation.toSummaryString());
					textScoring.append("Area under ROC \t\t\t" + evaluation.areaUnderROC(1) + "\n");
					textScoring.append("Error rate \t\t\t" + evaluation.errorRate() + "\n");
					textScoring.append("F-measure \t\t\t" + evaluation.fMeasure(1) + "\n");
					textScoring.append("Precision \t\t\t" + evaluation.precision(1) + "\n");
					textScoring.append("Recall \t\t\t" + evaluation.recall(1) + "\n");
					textScoring.append("Matthews Correlation Coefficient \t\t\t" + evaluation.matthewsCorrelationCoefficient(1) + "\n");
					textScoring.append("Confusion Matrix \n" + 
					"TP: " + evaluation.confusionMatrix()[0][0] + "\t" + 
					"   FN: " + evaluation.confusionMatrix()[0][1] + "\n" + 
					"FP: " + evaluation.confusionMatrix()[1][0] + "\t" + 
					"   TN: " + evaluation.confusionMatrix()[1][1] + "\n" + "\n");

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});		
	}
}
