package org.uimafit.examples.tutorial.ex1;

import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.examples.tutorial.type.RoomNumber;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.util.AnnotationRetrieval;

public class RoomNumberAnnotatorPipeline {

	public static void main(String[] args) throws UIMAException {
		String text = "The meeting was moved from Yorktown 01-144 to Hawthorne 1S-W33.";
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(RoomNumber.class);
		JCas jCas = JCasFactory.createJCas(typeSystemDescription);
		jCas.setDocumentText(text);
		AnalysisEngine analysisEngine = AnalysisEngineFactory.createPrimitive(RoomNumberAnnotator.class, typeSystemDescription);
		analysisEngine.process(jCas);
		
		Iterator<RoomNumber> roomNumbers = AnnotationRetrieval.get(jCas, RoomNumber.class);
		while(roomNumbers.hasNext()) {
			RoomNumber roomNumber = roomNumbers.next();
			System.out.println(roomNumber.getCoveredText() + "\tbuilding = "+roomNumber.getBuilding());
		}
	}
}
