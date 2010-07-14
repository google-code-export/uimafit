package org.uimafit.examples.experiment.pos;

import static org.uimafit.examples.experiment.pos.ViewNames.GOLD_VIEW;
import static org.uimafit.examples.experiment.pos.ViewNames.SYSTEM_VIEW;

import java.text.NumberFormat;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.SofaCapability;
import org.uimafit.examples.type.Sentence;
import org.uimafit.examples.type.Token;
import org.uimafit.util.JCasUtil;

/**
 * This AE evaluates the system part-of-speech tags against the gold
 * part-of-speech tags. This is a very simple approach to evaluation of
 * part-of-speech tags that will not likely suffice in real-world scenarios for
 * a number of reasons (e.g. no confusion matrix, assumes gold-standard tokens
 * and sentences in the system view, etc.)
 * 
 * @author Philip Ogren
 * 
 */
@SofaCapability(inputSofas = { GOLD_VIEW, SYSTEM_VIEW })
public class Evaluator extends JCasAnnotator_ImplBase {

	private int totalCorrect = 0;

	private int totalWrong = 0;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			JCas goldView = jCas.getView(GOLD_VIEW);
			JCas systemView = jCas.getView(SYSTEM_VIEW);

			for (Sentence goldSentence : JCasUtil.iterate(goldView, Sentence.class)) {
				List<Token> goldTokens = JCasUtil.selectCovered(goldView, Token.class, goldSentence);
				List<Token> systemTokens = JCasUtil.selectCovered(systemView, Token.class, goldSentence);
				if (goldTokens.size() == systemTokens.size()) {
					for (int i = 0; i < goldTokens.size(); i++) {
						String goldPos = goldTokens.get(i).getPos();
						String systemPos = systemTokens.get(i).getPos();
						if (goldPos.equals(systemPos)) {
							totalCorrect++;
						}
						else {
							totalWrong++;
						}
					}
				}
				else {
					throw new RuntimeException("number of tokens in gold view differs from number of tokens in system view");
				}
			}
		}
		catch (CASException ce) {
			throw new AnalysisEngineProcessException(ce);
		}

	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		int total = totalCorrect + totalWrong;
		System.out.println("total tokens: " + total);
		System.out.println("correct: " + totalCorrect);
		System.out.println("wrong: " + totalWrong);
		float accuracy = (float) totalCorrect / total;
		System.out.println("accuracy: " + NumberFormat.getPercentInstance().format(accuracy));
	}

}
