/* 
   Copyright 2010 Regents of the University of Colorado.  
 All rights reserved. 

 Licensed under the Apache License, Version 2.0 (the "License"); 
 you may not use this file except in compliance with the License. 
 You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software 
 distributed under the License is distributed on an "AS IS" BASIS, 
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 See the License for the specific language governing permissions and 
 limitations under the License.
 */
package org.uimafit.component.xwriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.factory.testAes.Annotator1;
import org.uimafit.factory.testAes.Annotator2;
import org.uimafit.factory.testAes.Annotator3;
import org.uimafit.factory.testAes.ViewNames;
import org.uimafit.testing.factory.TokenFactory;
import org.uimafit.testing.util.TearDownUtil;
import org.uimafit.type.Sentence;
import org.uimafit.type.Token;
import org.uimafit.util.Util;

/**
 * @author Philip Ogren
 */
public class XWriterTest {

	File outputDirectory;
	
	@Before
	public void setup() {
		outputDirectory = new File("test/xmi-output");
		outputDirectory.mkdirs();
	}

	@After
	public void teardown() {
		TearDownUtil.removeDirectory(new File("test"));
	}

	@Test
	public void testXWriter() throws Exception {
		JCas jCas = Util.JCAS.get();
		jCas.reset();
		
		addDataToCas(jCas);

		TypeSystemDescription typeSystem = Util.TYPE_SYSTEM_DESCRIPTION;
		
		AnalysisEngine xWriter = AnalysisEngineFactory.createPrimitive(XWriter.class, typeSystem,
				XWriter.PARAM_OUTPUT_DIRECTORY_NAME, outputDirectory.getPath()
				);
		
		xWriter.process(jCas);
		
		File xmiFile = new File(outputDirectory, "1.xmi"); 
		assertTrue(xmiFile.exists());
		
		jCas = JCasFactory.createJCas(xmiFile.getPath(), typeSystem);
		assertEquals("Anyone up for a game of Foosball?", jCas.getDocumentText());
		assertEquals("Any(o)n(e) (u)p f(o)r (a) g(a)m(e) (o)f F(oo)sb(a)ll?", jCas.getView("A").getDocumentText());
		assertEquals("?AFaaabeeffgllmnnoooooprsuy", jCas.getView("B").getDocumentText());
		assertEquals("(((((((((())))))))))?AFaaabeeffgllmnnoooooprsuy", jCas.getView("C").getDocumentText());
		assertEquals("yusrpooooonnmllgffeebaaaFA?", jCas.getView(ViewNames.REVERSE_VIEW).getDocumentText());

	}
	
	private void addDataToCas(JCas jCas) throws UIMAException {
		TokenFactory.createTokens(jCas, "Anyone up for a game of Foosball?", Token.class, Sentence.class);

		TypeSystemDescription typeSystem = TypeSystemDescriptionFactory.createTypeSystemDescription(Sentence.class, Token.class);
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem), ViewNames.PARENTHESES_VIEW, "A");
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator2.class, typeSystem), ViewNames.SORTED_VIEW, "B",
				ViewNames.SORTED_PARENTHESES_VIEW, "C", ViewNames.PARENTHESES_VIEW, "A");
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator3.class, typeSystem), ViewNames.INITIAL_VIEW, "B");
		AnalysisEngine aggregateEngine = builder.createAggregate();

		aggregateEngine.process(jCas);
	}
	
	@Test
	public void testXmi() throws Exception {
		AnalysisEngine engine = AnalysisEngineFactory.createPrimitive(
				XWriter.class, Util.TYPE_SYSTEM_DESCRIPTION,
				XWriter.PARAM_OUTPUT_DIRECTORY_NAME, this.outputDirectory.getPath());
		JCas jCas = engine.newJCas();
		TokenFactory.createTokens(jCas,
				"I like\nspam!",
				Token.class, Sentence.class, 
				"I like spam !",
				"PRP VB NN .", null, "org.uimafit.type.Token:pos", null);
		engine.process(jCas);
		engine.collectionProcessComplete();
		
		File outputFile = new File(this.outputDirectory, "1.xmi");

		SAXBuilder builder = new SAXBuilder();
		builder.setDTDHandler(null);
		Element root = null;
		try {
			Document doc = builder.build(new StringReader(FileUtils.file2String(outputFile)));
			root = doc.getRootElement();
		} catch (JDOMException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}

		List<?> elements = root.getChildren("Sentence", root.getNamespace("type"));
		Assert.assertEquals(1, elements.size());
		elements = root.getChildren("Token", root.getNamespace("type"));
		Assert.assertEquals(4, elements.size());
		
	}

	@Test
	public void testXcas() throws Exception {
		AnalysisEngine engine = AnalysisEngineFactory.createPrimitive(
				XWriter.class, Util.TYPE_SYSTEM_DESCRIPTION,
				XWriter.PARAM_OUTPUT_DIRECTORY_NAME, this.outputDirectory.getPath(),
				XWriter.PARAM_XML_SCHEME_NAME, XWriter.XCAS);
		JCas jCas = engine.newJCas();
		TokenFactory.createTokens(jCas,
				"I like\nspam!",
				Token.class, Sentence.class, 
				"I like spam !",
				"PRP VB NN .", null, "org.uimafit.type.Token:pos", null);
		engine.process(jCas);
		engine.collectionProcessComplete();
		
		File outputFile = new File(this.outputDirectory, "1.xcas");

		SAXBuilder builder = new SAXBuilder();
		builder.setDTDHandler(null);
		Element root = null;
		try {
			Document doc = builder.build(new StringReader(FileUtils.file2String(outputFile)));
			root = doc.getRootElement();
		} catch (JDOMException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}

		List<?> elements = root.getChildren("org.uimafit.type.Sentence");
		Assert.assertEquals(1, elements.size());
		elements = root.getChildren("org.uimafit.type.Token");
		Assert.assertEquals(4, elements.size());
		
	}

	@Test (expected=ResourceInitializationException.class)
	public void testBadXmlSchemeName() throws ResourceInitializationException {
		AnalysisEngineFactory.createPrimitive(XWriter.class, Util.TYPE_SYSTEM_DESCRIPTION, XWriter.PARAM_XML_SCHEME_NAME, "xcas");
	}


}
