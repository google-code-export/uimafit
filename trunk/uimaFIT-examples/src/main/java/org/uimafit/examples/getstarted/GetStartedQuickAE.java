package org.uimafit.examples.getstarted;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

public class GetStartedQuickAE extends JCasAnnotator_ImplBase {

	@ConfigurationParameter(name="stringParamName")
	private String stringParam;
	
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		System.out.println("Hello world!  Say 'hi' to "+stringParam);
	}
}
