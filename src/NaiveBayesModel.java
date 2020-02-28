import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;

public class NaiveBayesModel {

	private static Instances trainingDataSet; // Sastoji se od training dataseta bez zadnjeg stupca + bitstringa generiranog genetskim algoritmom
	private static Instances testingDataSet;
	private NaiveBayes naiveBayes;
	private String resultText = "";
	
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
	}
	
	public void process() throws Exception {
		
	    NaiveBayes naiveBayes = new NaiveBayes();
	    FilteredClassifier filteredClassifier = new FilteredClassifier();
	    filteredClassifier.setClassifier(naiveBayes);
	    // train and make predictions
	    filteredClassifier.buildClassifier(trainingDataSet);
	    
	    // Testing the model
	    for (int i = 0; i < testingDataSet.numInstances(); i++) {
	    	
		    double predicted = filteredClassifier.classifyInstance(testingDataSet.instance(i));
		    double actual = testingDataSet.instance(i).classValue();
		    predictions[i] = predicted;
		    
		    // if a value is 0, we consider it positive (not-faulty), if it is 1, we consider it negative (faulty)
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
	    
	    accuracy = (truePositive + trueNegative) / testingDataSet.numInstances();
	    recall = truePositive / (truePositive + falseNegative);
	    precision = truePositive / (truePositive + falsePositive);
	    fmeasure = (2 * precision * recall) / (precision + recall);
	    truePositiveRate = truePositive / (truePositive + falseNegative);
	    trueNegativeRate = trueNegative / (trueNegative + falsePositive);
	    geometricMean = Math.sqrt(truePositiveRate * trueNegativeRate);
	    
	    resultText += /*"Correctly classified instances: " + */(truePositive + trueNegative) + "\n";
	    resultText += /*"Incorrectly classified instances: " + */(falsePositive + falseNegative) + "\n";
	    resultText += /*"Geometric mean: " + */geometricMean + "\n";
	    resultText += /*"F measure: " + */fmeasure + "\n";
	    resultText += /*"Precision: " + */precision + "\n";
	    resultText += /*"Recall: " + */recall + "\n";
	    resultText += /*"Accuracy: " + */accuracy + "\n";
	    //resultText += "Confusion matrix: \n" + "TP: " + truePositive + 
	    //		"\tFN: " + falseNegative + "\n" + "FP: " + falsePositive + "\tTN: " + trueNegative + "\n";
	    resultText += 
	    		truePositive + "\n" + 
	    		falseNegative + "\n" + 
	    		falsePositive + "\n" + 
	    		trueNegative + "\n";
	    
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
