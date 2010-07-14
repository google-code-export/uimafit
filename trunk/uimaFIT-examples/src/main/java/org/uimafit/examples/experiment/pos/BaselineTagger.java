package org.uimafit.examples.experiment.pos;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.examples.type.Token;
import org.uimafit.util.JCasUtil;

/**
 * This "baseline" part-of-speech tagger isn't very sophisticated! Notice,
 * however, that the tagger operates on the default view. This will be mapped to
 * the "system" view when we run our experiment.
 * 
 * @author Philip Ogren
 * 
 */
public class BaselineTagger extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Token token : JCasUtil.iterate(jCas, Token.class)) {
			String word = token.getCoveredText();
			if (word.equals("a") || word.equals("the")) {
				token.setPos("DT");
			}
			else {
				token.setPos("NN");
			}
		}
	}

}
