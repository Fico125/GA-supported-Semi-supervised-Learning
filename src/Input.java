import java.io.File;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Input {

	private Instances data;
	
	public void readFile(String filename, String filepath) throws Exception {
		
		String fullFilePath = filepath + "/" + filename;
		
		try {
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(fullFilePath));
			data = loader.getDataSet();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Instances getData() {
		return data;
	}
}
