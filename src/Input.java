import java.io.File;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Input {

	private Instances data;
	private Instances new_data;
	
	public void readFile(String filename, String filepath) throws Exception {
		
		String fullFilePath = filepath + "/" + filename;
		
		try {
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(fullFilePath));
			data = loader.getDataSet();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	  	int lastColumnIndex = data.numAttributes() - 1;
		
	  	// Converting the number of bugs into 0/1 values
	  	  for ( int i = 0; i < data.numInstances(); i++) {
	  		  
	  		  Instance currentInstance = data.instance(i);
	  		  int brojBugova = Integer.parseInt( currentInstance.toString(lastColumnIndex) );
	  		  if ( brojBugova != 0 ) {
	  			  currentInstance.setValue(lastColumnIndex, 1.0);
	  		  }
	  	  }
	  	  	
	  	  // setting class attribute
	  	  data.setClassIndex(data.numAttributes() - 1);
	  	  	
	  	  new_data = FileHandler.numericToNominal(data);
	}
	
	public Instances getData() {
		
		return new_data;
	}
}
