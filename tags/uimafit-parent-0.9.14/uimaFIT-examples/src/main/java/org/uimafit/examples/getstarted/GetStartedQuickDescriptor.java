package org.uimafit.examples.getstarted;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.xml.sax.SAXException;

public class GetStartedQuickDescriptor {

	public static void main(String[] args) throws ResourceInitializationException, FileNotFoundException, SAXException, IOException {
		//normally you would pass in a list of classes corresponding to type system types here
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(new Class[0]);
		//here we will instantiate the analysis engine using the value "uimaFIT" for the parameter "stringParamName".
		AnalysisEngineDescription analysisEngineDescription = AnalysisEngineFactory.createPrimitiveDescription(GetStartedQuickAE.class, typeSystemDescription, "stringParamName", "uimaFIT");
		analysisEngineDescription.toXML(new FileOutputStream("GetStartedQuickAE.xml"));
	}
}
