package org.uimafit.examples.experiment.pos;

import static org.uimafit.examples.experiment.pos.ViewNames.VIEW1;
import static org.uimafit.examples.experiment.pos.ViewNames.VIEW2;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.SofaCapability;
import org.uimafit.examples.type.Sentence;
import org.uimafit.examples.type.Token;
import org.uimafit.util.JCasUtil;

/**
 * This simple AE copies tokens and sentences from one view to another.  
 * @author Philip
 *
 */

@SofaCapability(inputSofas= {VIEW1, VIEW2})
public class SentenceAndTokenCopier extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
		JCas view1 = jCas.getView(VIEW1);
		JCas view2 = jCas.getView(VIEW2);
		
		for(Token token1 : JCasUtil.iterate(view1, Token.class)) {
			new Token(view2, token1.getBegin(), token1.getEnd()).addToIndexes();
		}

		for(Sentence sentence1 : JCasUtil.iterate(view1, Sentence.class)) {
			new Sentence(view2, sentence1.getBegin(), sentence1.getEnd()).addToIndexes();
		}
		}
		catch (CASException ce) {
			throw new AnalysisEngineProcessException(ce);
		}

	}

}
