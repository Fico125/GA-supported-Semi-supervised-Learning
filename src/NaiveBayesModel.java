import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

public class NaiveBayesModel {

	private static Instances trainingDataSet; // Sastoji se od training dataseta bez zadnjeg stupca + bitstringa generiranog genetskim algoritmom
	private static Instances testingDataSet; // TODO Provjeriti bi li se trebao sastojati od cijelog drugog dataseta (test dataset), ili od čega?
	private static Instances predictionDataSet; // TODO Provjeriti da li bi ovo trebao biti CIJELI training dataset, odnosno podaci od atributa + zadnji stupac?
	
	public NaiveBayesModel(Instances trainingDataSet, Instances testingDataSet, Instances predictionDataSet) {
		super();
		NaiveBayesModel.trainingDataSet = trainingDataSet;
		NaiveBayesModel.testingDataSet = testingDataSet;
		NaiveBayesModel.predictionDataSet = predictionDataSet;
	}
	
	public void process() throws Exception {
		
		// TODO Provjeriti s profesorom bi li trebalo umjesto običnog Naive Bayesa koristiti NaiveBayesMultinomial
		NaiveBayes naiveBayes = new NaiveBayes();
		naiveBayes.buildClassifier(trainingDataSet);
		
		Evaluation eval = new Evaluation(trainingDataSet);
		// Razlika između ovog koda i onog od ranije da se ovdje koristi metoda evaluateModel sa testingDataSet, a ranije se koristila
		// metoda crossValidateModel (naiveBayes, TrainData, 5, new Random(1)) pa ne znam što je točno i što bi trebalo koristiti
		eval.evaluateModel(naiveBayes, testingDataSet);
		
		/** Print the algorithm summary */
		System.out.println("** Naive Bayes Evaluation with Datasets **");
		System.out.println(eval.toSummaryString());
		System.out.println("\nF-measure: " + eval.fMeasure(1) + "\nPrecision: " + eval.precision(1) + "\nRecall: " + eval.recall(1));
		// TODO Ovdje se koristi predictionDataSet, ali nije mi jasno u koju svrhu pa je zakomentiran kod.
		/*
		for (int i = 0; i < predictionDataSet.numInstances(); i++) {
			System.out.println("Prediction instance: " + predictionDataSet.instance(i));
			double index = naiveBayes.classifyInstance(predictionDataSet.instance(i));
			String className = trainingDataSet.attribute(0).value((int) index);
			System.out.println("Training instance: " + className);
		}
		*/
	}
	
}
