package org.uimafit.examples.getstarted;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

public class GetStartedQuickPipeline {

	public static void main(String[] args) throws UIMAException {
		//normally you would pass in a list of classes corresponding to type system types here or provide the name of your type system (e.g. "org.uimafit.examples.TypeSystem.xml") 
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(new Class[0]);
		//uimaFIT doesn't provide any collection readers - so we will just instantiate a JCas and run it through our AE
		JCas jCas = JCasFactory.createJCas(typeSystemDescription);
		//here we will instantiate the analysis engine using the value "uimaFIT" for the parameter "stringParamName".
		AnalysisEngine analysisEngine = AnalysisEngineFactory.createPrimitive(GetStartedQuickAE.class, typeSystemDescription, "stringParamName", "uimaFIT");
		//run the analysis engine and look for a special greeting in your console.
		analysisEngine.process(jCas);
	}
}
