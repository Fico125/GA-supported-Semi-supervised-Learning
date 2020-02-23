import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

public class NaiveBayesModel {

	private static Instances trainingDataSet; // Sastoji se od training dataseta bez zadnjeg stupca + bitstringa generiranog genetskim algoritmom
	private static Instances testingDataSet;
	private NaiveBayes naiveBayes;
	private String resultText = "";
	private Evaluation evaluation;

	public NaiveBayesModel(Instances trainingDataSet, Instances testingDataSet) throws Exception {
		
		super();
		NaiveBayesModel.trainingDataSet = trainingDataSet;
		NaiveBayesModel.testingDataSet = testingDataSet;
		
		naiveBayes = new NaiveBayes();
		evaluation = new Evaluation(NaiveBayesModel.trainingDataSet);
	}
	
	public void process() throws Exception {
				
		naiveBayes.buildClassifier(trainingDataSet);
		
		//evaluation.crossValidateModel(naiveBayes, trainingDataSet, 5, new Random(1));
		evaluation.evaluateModel(naiveBayes, testingDataSet);
		resultText += evaluation.toSummaryString();
	}
	
	public String getResultText() {

		return resultText.toString();
	}

	public NaiveBayes getNaiveBayes() {
		
		return naiveBayes;
	}


	public Evaluation getEvaluation() {
		
		return evaluation;
	}	
}
