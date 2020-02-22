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
public class MainApp {
	
	public static final int MAXIMUM_NUMBER_OF_GENERATIONS = 100;
	
	protected Shell shlApp;
	private Text textCalculation;
	private Text textScoring;
	private Button btnClose;
	

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
		
		Input inputTrain = new Input();
		Input inputTest = new Input();

		
		Button btnUcitajTrain = new Button(shlApp, SWT.NONE);
		btnUcitajTrain.setBounds(0, 9, 134, 28);
		btnUcitajTrain.setText("Dodaj datoteku (Train)");

		Button btnDatotekaSpremna = new Button(shlApp, SWT.CHECK);
		btnDatotekaSpremna.setBounds(10, 43, 124, 18);
		btnDatotekaSpremna.setText("Datoteka spremna");
		btnDatotekaSpremna.setEnabled(false);
		
		btnUcitajTrain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FileDialog fileDialog = new FileDialog(shlApp, SWT.MULTI);
					@SuppressWarnings("unused")
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
		btnUcitajTest.setText("Dodaj datoteku (Test)");

		Button btnDatotekaTestSpremna = new Button(shlApp, SWT.CHECK);
		btnDatotekaTestSpremna.setBounds(10, 101, 124, 18);
		btnDatotekaTestSpremna.setText("Datoteka spremna");
		btnDatotekaTestSpremna.setEnabled(false);
		
		btnUcitajTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FileDialog fileDialog = new FileDialog(shlApp, SWT.MULTI);
					@SuppressWarnings("unused")
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
		btnIzracunaj.setText("Izracunaj");
		
		btnIzracunaj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//System.out.println("INICIJALIZACIJA GENETSKOG ALGORITMA");				
				GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(inputTrain.getData());
				
				//System.out.println("INICIJALIZACIJA POPULACIJE");
				Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
				Instances dataWithoutLastColumn = FileHandler.getDataWithoutLastColumn(inputTrain.getData());
				//Instances trainData = FileHandler.mergeDataWithLastColumn(dataWithoutLastColumn, population.getChromosomes()[0]);
				Instances trainData = dataWithoutLastColumn; // trainData spajamo sa posljednjim stupcem (GA outputom) prilikom izračunavanja fitnessa u metodi computeFitness
				trainData.setClassIndex(trainData.numAttributes() - 1);
				
				Instances testData = inputTest.getData();
				Instances predictionData = inputTrain.getData(); // ovo je cijeli test dataset, podaci i zadnji stupac
				population.computeFitness(trainData, testData);
				String textOutput = "";

				/*
				System.out.println("-------------------------------------------------------------------------------------------------------");
				textCalculation.append("-------------------------------------------------------------------------------------------------------\n");
				System.out.println("Generation # 0" + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
				textCalculation.append("Generation # 0" + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness() + "\n");
				textOutput = printPopulation(population, "Target Chromosome: " + Arrays.toString(GeneticAlgorithm.TARGET_CHROMOSOME));
				textCalculation.append(textOutput);
				*/
				
				int generationNumber = 0;
				
				//System.out.println("PRINT PRIJE WHILE-a");
				//while((population.getChromosomes()[0].getFitness() < GeneticAlgorithm.TARGET_CHROMOSOME.length) && generationNumber < MAXIMUM_NUMBER_OF_GENERATIONS) {
				while(generationNumber < MAXIMUM_NUMBER_OF_GENERATIONS) {	
					generationNumber++;
					System.out.println("-------------------------------------------------------------------------------------------------------");
					System.out.println("Gen. num.: " + generationNumber);
					
					//System.out.println("Evolving population START");
					population = geneticAlgorithm.evolve(population, trainData, testData);
					
					//System.out.println("Sorting population by fitness");
					population.sortChromosomesByFitness();
					/*
					System.out.println("Generation # " + generationNumber + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
					textCalculation.append("Generation # " + generationNumber + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness() + "\n");
					textOutput = printPopulation(population, "Target Chromosome: " + Arrays.toString(GeneticAlgorithm.TARGET_CHROMOSOME));
					textCalculation.append(textOutput);
					*/
				}
				System.out.println("\nChromosome fitness from last generation:");
				for (int i = 0; i < GeneticAlgorithm.POPULATION_SIZE; i++  ){
					
					System.out.println(population.getChromosomes()[i].getFitness());
				}
				
				//System.out.println("Testing the best model after genetic selection");
				NaiveBayesModel bestModel = population.getChromosomes()[0].getNaiveB();
				Evaluation evaluation;
				
				try {
					//System.out.println("Evaluation of the best model after genetic selection");
					evaluation = new Evaluation(predictionData);
					evaluation.evaluateModel(bestModel.getNaiveBayes(), predictionData);
					System.out.println("\nBest Model Statistics: " + evaluation.toSummaryString());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		btnClose = new Button(shlApp, SWT.NONE);
		btnClose.setBounds(0, 170, 134, 28);
		btnClose.setText("Zatvori");
		
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
		
		textCalculation = new Text(shlApp, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textCalculation.setBounds(177, 10, 700, 820);
		
		textScoring = new Text(shlApp, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textScoring.setBounds(877, 10, 700, 820);
	}
	/*
	public static String printPopulation(Population population, String heading) { 
		
		String outputText = "";
		
		System.out.println(heading);
		outputText = heading + "\n";
		//System.out.println("--------------------------------------------");
		//outputText += "--------------------------------------------\n";
		for(int x = 0; x < population.getChromosomes().length; x++) {
			System.out.println("Chromosome # " + x + " : " + Arrays.toString(population.getChromosomes()[x].getGenes()) + 
					" | Fitness: " + population.getChromosomes()[x].getFitness());
			outputText += "Chromosome # " + x + " : " + Arrays.toString(population.getChromosomes()[x].getGenes()) + 
					" | Fitness: " + population.getChromosomes()[x].getFitness() + "\n";
		}
		System.out.println("-------------------------------------------------------------------------------------------------------");
		outputText += "-------------------------------------------------------------------------------------------------------\n";
		outputText += "\n";
		
		return outputText;
	}
	*/
}
