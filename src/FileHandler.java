import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class FileHandler {

	// Converting numeric to nominal values
    protected static Instances numericToNominal(Instances instance)
    {
        Instances newInstance = null;
        NumericToNominal convert = new NumericToNominal();
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "last";

        try
        {
            convert.setOptions(options);
            convert.setInputFormat(instance);
            newInstance = Filter.useFilter(instance, convert);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return newInstance;
    }
    
    protected static int[] getLastColumnValues(Instances data) {
    	
    	int lastColumnIndex = data.numAttributes() - 1;
    	int[] lastColumnValues = new int[data.numInstances()];
    	
    	for(int i = 0; i < data.numInstances(); i++) {
	  		Instance currentInstance = data.instance(i);
	  		lastColumnValues[i] = Integer.parseInt(currentInstance.toString(lastColumnIndex));
    	}
    	
    	return lastColumnValues;
    }
}
