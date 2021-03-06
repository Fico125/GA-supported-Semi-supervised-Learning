import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;

@SuppressWarnings("unused")
public class FileHandler {
	
	private static String reductionOfDatasetOutput = "";

	// Converting numeric to nominal values
    protected static Instances numericToNominal(Instances instance) {
    	
        Instances newInstance = null;
        NumericToNominal convert = new NumericToNominal();
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "last";

        try {
        	
            convert.setOptions(options);
            convert.setInputFormat(instance);
            newInstance = Filter.useFilter(instance, convert);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newInstance;
    }
    
    /** Method that returns just the last column values from a given dataset. */
    protected static int[] getLastColumnValues(Instances data) {
    	
    	int lastColumnIndex = data.numAttributes() - 1;
    	int[] lastColumnValues = new int[data.numInstances()];
    	
    	for(int i = 0; i < data.numInstances(); i++) {
    		
	  		Instance currentInstance = data.instance(i);
	  		lastColumnValues[i] = Integer.parseInt(currentInstance.toString(lastColumnIndex));
    	}
    	
    	return lastColumnValues;
    }
    
    /** Method that returns dataset without the last column (class index). */
    protected static Instances getDataWithoutLastColumn(Instances data) {
    	
    	Instances new_data = null;
    	int[] lastColumnIndex = {data.numAttributes() - 1};
    	Remove removeFilter = new Remove();
    	
    	removeFilter.setAttributeIndicesArray(lastColumnIndex);
    	
    	try {
    		
			removeFilter.setInputFormat(data);
	    	new_data = Filter.useFilter(data, removeFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return new_data;
    }
    
    /** Method that accepts data from dataset(without last column values), and data that contains
     *  last column values generated by GA, and combining them into one dataset.*/
    protected static Instances mergeDataWithLastColumn(Instances data, Chromosome lastColumn) {
    	
    	Instances new_data = null;
    	int[] lastColumnValues;
    	
    	new_data = new Instances(data);
    	lastColumnValues = lastColumn.getGenes();

        try {
        	
        	Add filter;
        	filter = new Add();
        	filter.setAttributeIndex("last");
            filter.setAttributeName("faulty_GA_predicted");
			filter.setInputFormat(new_data);
	        new_data = Filter.useFilter(new_data, filter);
		} catch (Exception e) {
			//e.printStackTrace();
		}
        
        int brojZadnjegStupca = new_data.numAttributes() - 1;
        for(int i = 0; i < new_data.numInstances(); i++) {
        	
	  		  Instance currentInstance = new_data.instance(i);
	  		  currentInstance.setValue(brojZadnjegStupca, lastColumnValues[i]);
        }
        
        new_data.setClassIndex(new_data.numAttributes() - 1);
	  	new_data = FileHandler.numericToNominal(new_data);
        
    	return new_data;
    }
    
    public static Instances reduceDatasetByGivenPercent(Instances data, double percent) {
    	
    	reductionOfDatasetOutput = "";
    	
    	if(percent == 0.0) {
    		return data;
    	}
    	
    	Instances new_data = new Instances(data, 0);
    	int newSize = (int) Math.round(data.numInstances() - (data.numInstances() * percent/100));
    	int numZeros = 0;
    	int numOnes = 0;
    	int randomNumber;
    	Random random = new Random();
    	Set<Integer> listOfIndexes = new HashSet<Integer>(); // Using Set to block duplicate index values

    	// Counting how many zeros and ones we have in a dataset
    	for(int i = 0; i < data.numInstances(); i++) {
    		
    		int temp = (int) data.instance(i).classValue();
    		if(temp == 0) numZeros++;
    		else numOnes++;
    	}
    	
    	System.out.println("Dataset before reduction: " + "\nNumber of instances: " + data.numInstances() + 
    			"\nNumber of zeros: " + numZeros + "\nNumber of ones: " + numOnes + "\n");
    	reductionOfDatasetOutput += "Dataset before reduction: " + "\nNumber of instances: " + data.numInstances() + 
    			"\nNumber of zeros: " + numZeros + "\nNumber of ones: " + numOnes + "\n";

    	// Variables storing the amount of new ones and zeros we need to have in a reducted dataset.
    	int newNumberOfZeros = (int) ((numZeros - (numZeros * (percent/100))));
    	int newNumberOfOnes = (int) ((numOnes - (numOnes * (percent/100))));
    	
    	if((newNumberOfZeros + newNumberOfOnes) == 3) {
    		if(Math.random() < 0.5) 
    		{
    			newNumberOfZeros = 2;
    			newNumberOfOnes = 1;
    		} else {
    			newNumberOfZeros = 1;
    			newNumberOfOnes = 2;
    		}
    	}
    	
    	System.out.println("\nDataset after reduction: " + "\nNumber of instances: " + (newNumberOfZeros + newNumberOfOnes) + 
    			"\nNumber of zeros: " + newNumberOfZeros + "\nNumber of ones: " + newNumberOfOnes + "\n");
    	reductionOfDatasetOutput += "\nDataset after reduction: " + "\nNumber of instances: " + (newNumberOfZeros + newNumberOfOnes) + 
    			"\nNumber of zeros: " + newNumberOfZeros + "\nNumber of ones: " + newNumberOfOnes + "\n";
    	
    	// Adding indexes to a list until we reach the size of reducted dataset (that is sum of new number of zeros and ones)
    	while(listOfIndexes.size() < (newNumberOfZeros + newNumberOfOnes)) {
    		
    		int i = 0;
    		
    		while(i < newNumberOfZeros) {
    			
        		randomNumber = random.nextInt(data.numInstances());
    			if(data.instance(randomNumber).classValue() == 0.0 && !(listOfIndexes.contains(randomNumber)) && randomNumber != 0) {
    				listOfIndexes.add(randomNumber);
    				i++;
    			}
    		}
    		
    		i = 0;
    		while(i < newNumberOfOnes) {
    			
        		randomNumber = random.nextInt(data.numInstances());
    			if(data.instance(randomNumber).classValue() == 1.0 && !(listOfIndexes.contains(randomNumber)) && randomNumber != 0) {
    				listOfIndexes.add(randomNumber);
    				i++;
    			}
    		}
    	}
    	
    	Iterator<Integer> iterator = listOfIndexes.iterator();
    	while(iterator.hasNext()){
    		
    		new_data.add(data.instance(iterator.next()));
    	}
    	
    	return new_data;
    }
    
	public static String getReductionOfDatasetOutput() {
		
		return reductionOfDatasetOutput;
	}
}
