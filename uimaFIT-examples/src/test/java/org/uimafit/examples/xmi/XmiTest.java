package org.uimafit.examples.xmi;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.component.xwriter.XWriter;
import org.uimafit.examples.tutorial.ExamplesTestBase;
import org.uimafit.examples.type.Sentence;
import org.uimafit.examples.type.Token;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.util.JCasUtil;

public class XmiTest extends ExamplesTestBase{

	/**
	 * Here we are testing Annotator3 by setting up the pipeline and running it before testing the final annotator
	 * @throws Exception
	 */
	@Test
	public void testWithoutXmi() throws Exception {
		AnalysisEngine a1 = AnalysisEngineFactory.createPrimitive(Annotator1.class, typeSystemDescription);
		AnalysisEngine a2 = AnalysisEngineFactory.createPrimitive(Annotator2.class, typeSystemDescription);
		AnalysisEngine a3 = AnalysisEngineFactory.createPrimitive(Annotator3.class, typeSystemDescription);
		jCas.setDocumentText("betgetjetletmetnetpetsetvetwetyet");
		a1.process(jCas);
		a2.process(jCas);
		a3.process(jCas);
		
		Sentence sentence = JCasUtil.selectByIndex(jCas, Sentence.class, 0);
		assertEquals("metnetpetsetvetwetyet", sentence.getCoveredText());
	}

	/**
	 * In this test we have removed the dependency on running Annotator1 and Annotator2 before running Annotator3 by introducing an 
	 * XMI file that contains the information
	 * @throws Exception
	 */
	@Test
	public void testWithXmi() throws Exception {
		jCas = JCasFactory.createJCas("src/main/resources/org/uimafit/examples/xmi/1.xmi", typeSystemDescription);
		AnalysisEngine a3 = AnalysisEngineFactory.createPrimitive(Annotator3.class, typeSystemDescription);
		a3.process(jCas);
		Sentence sentence = JCasUtil.selectByIndex(jCas, Sentence.class, 0);
		assertEquals("metnetpetsetvetwetyet", sentence.getCoveredText());
	}
	
	public static void main(String[] args) throws Exception {
		XmiTest xmiTest = new XmiTest();
		xmiTest.setUp();
		
		AnalysisEngine a1 = AnalysisEngineFactory.createPrimitive(Annotator1.class, xmiTest.typeSystemDescription);
		AnalysisEngine a2 = AnalysisEngineFactory.createPrimitive(Annotator2.class, xmiTest.typeSystemDescription);
		AnalysisEngine xWriter = AnalysisEngineFactory.createPrimitive(XWriter.class, xmiTest.typeSystemDescription, XWriter.PARAM_OUTPUT_DIRECTORY_NAME, "src/main/resources/org/uimafit/examples/xmi");
		xmiTest.jCas.setDocumentText("betgetjetletmetnetpetsetvetwetyet");
		a1.process(xmiTest.jCas);
		a2.process(xmiTest.jCas);
		xWriter.process(xmiTest.jCas);
		xWriter.collectionProcessComplete();
	}
	/**
	 * Creates a token for every three characters
	 */
	public static class Annotator1 extends JCasAnnotator_ImplBase {
		@Override
		public void process(JCas jCas) throws AnalysisEngineProcessException {
			String text = jCas.getDocumentText();
			for(int i=0; i<text.length()-3; i+=3) {
				new Token(jCas, i, i+3).addToIndexes();
			}
		}
	}

	/**
	 * sets the pos tag for each token to be the first letter of the token
	 */
	public static class Annotator2 extends JCasAnnotator_ImplBase {
		@Override
		public void process(JCas jCas) throws AnalysisEngineProcessException {
			for(Token token : JCasUtil.iterate(jCas, Token.class)) {
				token.setPos(token.getCoveredText().substring(0,1));
			}
		}
	}

	/**
	 * creates a sentence from the begining of each token whose pos tag is "m" to the end of the text.
	 */
	public static class Annotator3 extends JCasAnnotator_ImplBase {
		@Override
		public void process(JCas jCas) throws AnalysisEngineProcessException {
			for(Token token : JCasUtil.iterate(jCas, Token.class)) {
				if(token.getPos().equals("m")) {
					new Sentence(jCas, token.getBegin(), jCas.getDocumentText().length()).addToIndexes();
				}
			}
		}
	}

}
