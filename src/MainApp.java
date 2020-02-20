import java.io.IOException;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import weka.core.Instances;
/** Class containing main method.
 * */
public class MainApp {
	
	public static final int MAXIMUM_NUMBER_OF_GENERATIONS = 1000;
	
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
		shlApp.setSize(1500, 698);
		shlApp.setText("Software Defect Prediction");
		
		Input input = new Input();
		
		Button btnUcitaj = new Button(shlApp, SWT.NONE);
		btnUcitaj.setBounds(0, 9, 134, 28);
		btnUcitaj.setText("Dodaj datoteku");

		Button btnDatotekaSpremna = new Button(shlApp, SWT.CHECK);
		btnDatotekaSpremna.setBounds(10, 43, 124, 18);
		btnDatotekaSpremna.setText("Datoteka spremna");
		btnDatotekaSpremna.setEnabled(false);
		
		btnUcitaj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FileDialog fileDialog = new FileDialog(shlApp, SWT.MULTI);
					@SuppressWarnings("unused")
					String firstFile = fileDialog.open(); // if SupressWarnings is moved, this is "unused", but fileDialog wont open without it.
					String fileName = fileDialog.getFileName();
					String filePath = fileDialog.getFilterPath();
					input.readFile(fileName, filePath);
					btnDatotekaSpremna.setSelection(true);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Button btnIzracunaj = new Button(shlApp, SWT.NONE);
		btnIzracunaj.setBounds(0, 67, 134, 28);
		btnIzracunaj.setText("Izracunaj");
		
		btnIzracunaj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
								
				GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(input.getData());
				Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
				String textOutput = "";

				System.out.println("--------------------------------------------");
				textCalculation.append("--------------------------------------------\n");
				System.out.println("Generation # 0" + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
				textCalculation.append("Generation # 0" + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness() + "\n");
				textOutput = printPopulation(population, "Target Chromosome: " + Arrays.toString(GeneticAlgorithm.TARGET_CHROMOSOME));
				textCalculation.append(textOutput);
				int generationNumber = 0;
				
				while((population.getChromosomes()[0].getFitness() < GeneticAlgorithm.TARGET_CHROMOSOME.length) && generationNumber < MAXIMUM_NUMBER_OF_GENERATIONS) {
					generationNumber++;
					System.out.println("\n--------------------------------------------");
					textCalculation.append("--------------------------------------------\n");
					population = geneticAlgorithm.evolve(population);
					population.sortChromosomesByFitness();
					//TODO implementirati naive bayes za svaki kromosom te populacije. Dobiti ću listu naive bayes modela, nakon x iteracija imati ćemo model
					// bayesa sa najboljom predikcijom, s kojim ćemo onda testirati 3.1 dataset.
					Instances dataWithoutLastColumn = FileHandler.getDataWithoutLastColumn(input.getData());
					Instances mergedData = FileHandler.mergeDataWithLastColumn(dataWithoutLastColumn, population.getChromosomes()[0]);
					// Bayes ide sada, trening sa datasetom koji se nalazi u mergedData.
					//
					System.out.println("Generation # " + generationNumber + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
					textCalculation.append("Generation # " + generationNumber + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness() + "\n");
					textOutput = printPopulation(population, "Target Chromosome: " + Arrays.toString(GeneticAlgorithm.TARGET_CHROMOSOME));
					textCalculation.append(textOutput);
				}
			}
		});
		
		btnClose = new Button(shlApp, SWT.NONE);
		btnClose.setBounds(0, 135, 134, 28);
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
		textCalculation.setBounds(177, 10, 500, 540);
		
		textScoring = new Text(shlApp, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textScoring.setBounds(677, 10, 900, 820);
	}
	
	public static String printPopulation(Population population, String heading) { 
		
		String outputText = "";
		
		System.out.println(heading);
		outputText = heading + "\n";
		System.out.println("--------------------------------------------");
		outputText += "--------------------------------------------\n";
		for(int x = 0; x < population.getChromosomes().length; x++) {
			System.out.println("Chromosome # " + x + " : " + Arrays.toString(population.getChromosomes()[x].getGenes()) + 
					" | Fitness: " + population.getChromosomes()[x].getFitness());
			outputText += "Chromosome # " + x + " : " + Arrays.toString(population.getChromosomes()[x].getGenes()) + 
					" | Fitness: " + population.getChromosomes()[x].getFitness() + "\n";
		}
		outputText += "\n";
		
		return outputText;
	}
}
