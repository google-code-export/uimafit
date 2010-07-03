package org.uimafit.examples.tutorial.ex1;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.JCasFactory.createJCas;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static org.uimafit.util.JCasUtil.iterate;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.examples.tutorial.type.RoomNumber;

public class RoomNumberAnnotatorPipeline {

	public static void main(String[] args) throws UIMAException {
		String text = "The meeting was moved from Yorktown 01-144 to Hawthorne 1S-W33.";
		TypeSystemDescription tsd = createTypeSystemDescription(RoomNumber.class);
		JCas jCas = createJCas(tsd);
		jCas.setDocumentText(text);
		AnalysisEngine analysisEngine = createPrimitive(RoomNumberAnnotator.class, tsd);
		analysisEngine.process(jCas);

		for (RoomNumber roomNumber : iterate(jCas, RoomNumber.class)) {
			System.out.println(roomNumber.getCoveredText() + "\tbuilding = "+roomNumber.getBuilding());
		}
	}
}
