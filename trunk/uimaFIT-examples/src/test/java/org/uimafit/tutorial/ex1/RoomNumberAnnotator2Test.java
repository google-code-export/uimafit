package org.uimafit.tutorial.ex1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.tutorial.AbstractTest;
import org.uimafit.tutorial.type.RoomNumber;
import org.uimafit.util.AnnotationRetrieval;

/**
 * This class demonstrates some simple tests using uimaFIT using the AbstractTest. 
 * These tests have the advantage that a new JCas is not created for each test.
 * @author Philip
 *
 */
public class RoomNumberAnnotator2Test extends AbstractTest{

	/**
	 * This test is nice because the super classes provides the typeSystemDescription and jCas objects.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRNA1() throws Exception {
		AnalysisEngine roomNumberAnnotatorAE = AnalysisEngineFactory.createPrimitive(RoomNumberAnnotator.class, typeSystemDescription);
		jCas.setDocumentText("The meeting is over at Yorktown 01-144");
		roomNumberAnnotatorAE.process(jCas);

		RoomNumber roomNumber = AnnotationRetrieval.get(jCas, RoomNumber.class, 0);
		assertNotNull(roomNumber);
		assertEquals("01-144", roomNumber.getCoveredText());
		assertEquals("Yorktown", roomNumber.getBuilding());
	}

}
