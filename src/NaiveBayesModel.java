import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

public class NaiveBayesModel {

	private static Instances trainingDataSet; // Sastoji se od training dataseta bez zadnjeg stupca + bitstringa generiranog genetskim algoritmom
	private static Instances testingDataSet;
	
	private NaiveBayes naiveBayes;// = new NaiveBayes();
	public NaiveBayes getNaiveBayes() {
		return naiveBayes;
	}

	Evaluation evaluation;// = new Evaluation(trainingDataSet);

	public Evaluation getEvaluation() {
		
		return evaluation;
	}

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
		
		//System.out.println("Model summary: " + evaluation.toSummaryString());
	}
	
}
