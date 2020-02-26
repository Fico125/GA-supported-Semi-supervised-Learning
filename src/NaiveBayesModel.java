import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;

public class NaiveBayesModel {

	private static Instances trainingDataSet; // Sastoji se od training dataseta bez zadnjeg stupca + bitstringa generiranog genetskim algoritmom
	private static Instances testingDataSet;
	private NaiveBayes naiveBayes;
	private String resultText = "";
	private Evaluation evaluation;
	
	private double truePositive = 0.0;
	private double trueNegative = 0.0;
	private double falsePositive = 0.0;
	private double falseNegative = 0.0;
	private double truePositiveRate = 0.0;
	private double trueNegativeRate = 0.0;
	private double geometricMean = 0.0;
	private double accuracy = 0.0;
	private double precision = 0.0;
	private double recall = 0.0;
	private double fmeasure = 0.0;

	private static double[] predictions = new double[GeneticAlgorithm.TARGET_CHROMOSOME.length];

	public NaiveBayesModel(Instances trainingDataSet, Instances testingDataSet) throws Exception {
		
		super();
		NaiveBayesModel.trainingDataSet = trainingDataSet;
		NaiveBayesModel.testingDataSet = testingDataSet;
		
		naiveBayes = new NaiveBayes();
		evaluation = new Evaluation(NaiveBayesModel.trainingDataSet);
	}
	
	public void process() throws Exception {
		/*		
		naiveBayes.buildClassifier(trainingDataSet);
		evaluation.evaluateModel(naiveBayes, testingDataSet);
		//evaluation.crossValidateModel(naiveBayes, trainingDataSet, 5, new Random(1));
		resultText += evaluation.toSummaryString();
		*/

	    NaiveBayes nb = new NaiveBayes();
	    FilteredClassifier fc = new FilteredClassifier();
	    fc.setClassifier(nb);
	    // train and make predictions
	    fc.buildClassifier(trainingDataSet);
	    
	    // Testing the model
	    for (int i = 0; i < testingDataSet.numInstances(); i++) {
	    	
		    double pred = fc.classifyInstance(testingDataSet.instance(i));
		    double actual = Double.parseDouble(testingDataSet.classAttribute().value((int) testingDataSet.instance(i).classValue()));
		    double predicted = Double.parseDouble(testingDataSet.classAttribute().value((int) pred));
		    predictions[i] = predicted;

		    if (actual == predicted) {
		    	accuracy++;
		    }
		    
		    // ako je vrijednost 0, smatramo ga pozitivnim (not-faulty), ako je 1, smatramo ga negativnim(faulty)
		    if(actual == 0.0 && predicted == 0.0) { 
		    	truePositive++;
		    }
		    else if(actual == 0.0 && predicted == 1.0) {
		    	falseNegative++;
		    }
		    else if(actual == 1.0 && predicted == 1.0) {
		    	trueNegative++;
		    }
		    else { // actual == 1.0 && predicted == 0.0
		    	falsePositive++;
		    }
	    }
	    recall = truePositive / (truePositive + falseNegative);
	    precision = truePositive / (truePositive + falsePositive);
	    fmeasure = (2 * precision * recall) / (precision + recall);
	    truePositiveRate = truePositive / (truePositive + falseNegative);
	    trueNegativeRate = trueNegative / (trueNegative + falsePositive);
	    geometricMean = Math.sqrt(truePositiveRate * trueNegativeRate);
	    
	    resultText += "Correctly classified instances: " + (truePositive + trueNegative) + "\n";
	    resultText += "Incorrectly classified instances: " + (falsePositive + falseNegative) + "\n";
	    resultText += "Geometric mean: " + geometricMean + "\n";
	    resultText += "F measure: " + fmeasure + "\n";
	    resultText += "Precision: " + precision + "\n";
	    resultText += "Recall: " + recall + "\n";
	    resultText += "Accuracy: " + accuracy + "\n";
	    resultText += "Confusion matrix: \n" + "TP: " + truePositive + 
	    		"\tFN: " + falseNegative + "\n" + "FP: " + falsePositive + "\tTN: " + trueNegative + "\n";
	    
	    System.out.println("Correctly classified instances: " + (truePositive + trueNegative));
	    System.out.println("Incorrectly classified instances: " + (falsePositive + falseNegative));
	    System.out.println("Geometric mean: " + geometricMean);
	    System.out.println("F measure: " + fmeasure);
	    System.out.println("Precision: " + precision);
	    System.out.println("Recall: " + recall);
	    System.out.println("Accuracy: " + accuracy);
	    System.out.println("Confusion matrix: \n" + "TP: " + truePositive + 
	    		"\tFN: " + falseNegative + "\n" + "FP: " + falsePositive + "\tTN: " + trueNegative);
	    
	}
	
	public String getResultText() {

		return resultText.toString();
	}

	public NaiveBayes getNaiveBayes() {
		
		return naiveBayes;
	}

	public static double[] getPredictions() {
		
		return predictions;
	}

	public Evaluation getEvaluation() {
		
		return evaluation;
	}
	
	public double getAccuracy() {
		
		return accuracy;
	}
	
	public double getGeometricMean() {
		
		return geometricMean;
	}
	
	public double getPrecision() {
		
		return precision;
	}
	
	public double getRecall() {
		
		return recall;
	}
	
	public double getFmeasure() {
		
		return fmeasure;
	}
}
