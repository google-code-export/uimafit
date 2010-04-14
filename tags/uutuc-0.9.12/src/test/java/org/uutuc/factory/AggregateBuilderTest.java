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

package org.uutuc.factory;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.uutuc.factory.testAes.Annotator1;
import org.uutuc.factory.testAes.Annotator2;
import org.uutuc.factory.testAes.Annotator3;
import org.uutuc.factory.testAes.ViewNames;
import org.uutuc.type.Sentence;
import org.uutuc.type.Token;
import org.uutuc.util.JCasAnnotatorAdapter;
import org.uutuc.util.SimplePipeline;
import org.uutuc.util.SingleFileXReader;
import org.uutuc.util.Util;

/**
 * 
 * @author Philip Ogren
 *
 */
public class AggregateBuilderTest {

	@Test
	public void testAggregateBuilder() throws UIMAException, IOException {
		JCas jCas = Util.JCAS.get();
		jCas.reset();
		
		TokenFactory.createTokens(jCas, "Anyone up for a game of Foosball?", Token.class, Sentence.class);

		TypeSystemDescription typeSystem = TypeSystemDescriptionFactory.createTypeSystemDescription(Sentence.class, Token.class);
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem), ViewNames.PARENTHESES_VIEW, "A");
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator2.class, typeSystem), ViewNames.SORTED_VIEW, "B", ViewNames.SORTED_PARENTHESES_VIEW, "C", ViewNames.PARENTHESES_VIEW, "A");
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator3.class, typeSystem), ViewNames.INITIAL_VIEW, "B");
		AnalysisEngine aggregateEngine = builder.createAggregate();

		aggregateEngine.process(jCas);

		assertEquals("Anyone up for a game of Foosball?", jCas.getDocumentText());
		assertEquals("Any(o)n(e) (u)p f(o)r (a) g(a)m(e) (o)f F(oo)sb(a)ll?", jCas.getView("A").getDocumentText());
		assertEquals("?AFaaabeeffgllmnnoooooprsuy", jCas.getView("B").getDocumentText());
		assertEquals("(((((((((())))))))))?AFaaabeeffgllmnnoooooprsuy", jCas.getView("C").getDocumentText());
		assertEquals("yusrpooooonnmllgffeebaaaFA?", jCas.getView(ViewNames.REVERSE_VIEW).getDocumentText());

		CollectionReader cr = CollectionReaderFactory.createCollectionReader(SingleFileXReader.class,
				Util.TYPE_SYSTEM_DESCRIPTION, SingleFileXReader.PARAM_FILE_NAME, "src/test/resources/data/docs/test.xmi",
				SingleFileXReader.PARAM_XML_SCHEME, SingleFileXReader.XMI);
		AnalysisEngine ae1 = AnalysisEngineFactory.createPrimitive(JCasAnnotatorAdapter.class, Util.TYPE_SYSTEM_DESCRIPTION);

		SimplePipeline.runPipeline(cr, ae1, aggregateEngine);

		
		AnalysisEngineDescription aggregateDescription = builder.createAggregateDescription();
		builder = new AggregateBuilder();
		builder.add(aggregateDescription);
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem), ViewNames.PARENTHESES_VIEW, "PARENS");
		aggregateEngine = builder.createAggregate();

		jCas.reset();
		
		TokenFactory.createTokens(jCas, "Anyone up for a game of Foosball?", Token.class, Sentence.class);

		aggregateEngine.process(jCas);

		assertEquals("Anyone up for a game of Foosball?", jCas.getDocumentText());
		assertEquals("Any(o)n(e) (u)p f(o)r (a) g(a)m(e) (o)f F(oo)sb(a)ll?", jCas.getView("A").getDocumentText());
		assertEquals("?AFaaabeeffgllmnnoooooprsuy", jCas.getView("B").getDocumentText());
		assertEquals("(((((((((())))))))))?AFaaabeeffgllmnnoooooprsuy", jCas.getView("C").getDocumentText());
		assertEquals("yusrpooooonnmllgffeebaaaFA?", jCas.getView(ViewNames.REVERSE_VIEW).getDocumentText());
		assertEquals("Any(o)n(e) (u)p f(o)r (a) g(a)m(e) (o)f F(oo)sb(a)ll?", jCas.getView("PARENS").getDocumentText());


	}

	@Test
	public void testAggregateBuilder2() throws UIMAException, IOException {
		JCas jCas = Util.JCAS.get();
		jCas.reset();
		
		TokenFactory.createTokens(jCas, "'Verb' is a noun!?", Token.class, Sentence.class);

		TypeSystemDescription typeSystem = TypeSystemDescriptionFactory.createTypeSystemDescription(Sentence.class, Token.class);
		AggregateBuilder builder = new AggregateBuilder();
		String componentName1 = builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem));
		String componentName2 = builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem));
		String componentName3 = builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem));

		assertEquals("org.uutuc.factory.testAes.Annotator1", componentName1);
		assertEquals("org.uutuc.factory.testAes.Annotator1.2", componentName2);
		assertEquals("org.uutuc.factory.testAes.Annotator1.3", componentName3);
		
		builder.addSofaMapping(componentName1, ViewNames.PARENTHESES_VIEW, "A");
		builder.addSofaMapping(componentName2, ViewNames.PARENTHESES_VIEW, "B");
		builder.addSofaMapping(componentName3, ViewNames.PARENTHESES_VIEW, "C");
		AnalysisEngineDescription aggregateEngineDescription = builder.createAggregateDescription();
		AnalysisEngine aggregateEngine = AnalysisEngineFactory.createAggregate(aggregateEngineDescription);
		
		aggregateEngine.process(jCas);

		assertEquals("'Verb' is a noun!?", jCas.getDocumentText());
		assertEquals("'V(e)rb' (i)s (a) n(ou)n!?", jCas.getView("A").getDocumentText());
		assertEquals("'V(e)rb' (i)s (a) n(ou)n!?", jCas.getView("B").getDocumentText());
		assertEquals("'V(e)rb' (i)s (a) n(ou)n!?", jCas.getView("C").getDocumentText());
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testOddNumberOfViewNames() throws ResourceInitializationException {
		TypeSystemDescription typeSystem = TypeSystemDescriptionFactory.createTypeSystemDescription(Sentence.class, Token.class);
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem), ViewNames.PARENTHESES_VIEW);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testDuplicateComponentNames() throws ResourceInitializationException {
		TypeSystemDescription typeSystem = TypeSystemDescriptionFactory.createTypeSystemDescription(Sentence.class, Token.class);
		AggregateBuilder builder = new AggregateBuilder();
		builder.add("name", AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem));
		builder.add("name", AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, typeSystem));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadSofaMapping() throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		builder.addSofaMapping("name", ViewNames.PARENTHESES_VIEW, "A");
	}

}
