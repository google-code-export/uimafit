/* 
 Copyright 2009 Regents of the University of Colorado.  
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypePriorityList;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.uutuc.type.Sentence;
import org.uutuc.type.Token;
import org.uutuc.util.AnnotationRetrieval;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class AnalysisEngineFactoryTest {

	@Test
	public void testCreateAnalysisEngineFromPath() throws UIMAException, IOException {
		AnalysisEngine engine = AnalysisEngineFactory.createAnalysisEngineFromPath("src/main/java/org/uutuc/util/JCasAnnotatorAdapter.xml");
		assertNotNull(engine);
	}
	
	@Test
	public void testProcess1() throws UIMAException, IOException {
		JCas jCas = AnalysisEngineFactory.process("org.uutuc.util.JCasAnnotatorAdapter", "There is no excuse!");
		
		assertEquals("There is no excuse!", jCas.getDocumentText());
	}
	
	@Test
	public void testProcess2() throws UIMAException, IOException {
		JCas jCas = AnalysisEngineFactory.process("org.uutuc.util.JCasAnnotatorAdapter", "test/data/docs/A.txt");
		
		assertEquals("Aaa Bbbb Cc Dddd eeee ff .", jCas.getDocumentText());
	}
	
	@Test
	public void testCreateAnalysisEngineWithPrioritizedTypes() throws UIMAException, IOException {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription("org.uutuc.type.TypeSystem");
		String[] prioritizedTypeNames = new String[] { "org.uutuc.type.Token", "org.uutuc.type.Sentence"};
		AnalysisEngine engine = AnalysisEngineFactory.createAnalysisEngine(org.uutuc.util.JCasAnnotatorAdapter.class, typeSystemDescription, prioritizedTypeNames, (Object[])null);
		
		TypePriorities typePriorities = engine.getAnalysisEngineMetaData().getTypePriorities();
		assertEquals(1, typePriorities.getPriorityLists().length);
		TypePriorityList typePriorityList = typePriorities.getPriorityLists()[0];
		assertEquals(2, typePriorityList.getTypes().length);
		assertEquals("org.uutuc.type.Token", typePriorityList.getTypes()[0]);
		assertEquals("org.uutuc.type.Sentence", typePriorityList.getTypes()[1]);
		
		JCas jCas = engine.newJCas();
		TokenFactory.createTokens(jCas, "word", Token.class, Sentence.class);
		FSIterator tokensInSentence = jCas.getAnnotationIndex().subiterator(AnnotationRetrieval.get(jCas, Sentence.class, 0));
		assertFalse(tokensInSentence.hasNext());
		

		prioritizedTypeNames = new String[] { "org.uutuc.type.Sentence", "org.uutuc.type.Token"};
		engine = AnalysisEngineFactory.createAnalysisEngine(org.uutuc.util.JCasAnnotatorAdapter.class, typeSystemDescription, prioritizedTypeNames, (Object[])null);
		jCas = engine.newJCas();
		TokenFactory.createTokens(jCas, "word", Token.class, Sentence.class);
		tokensInSentence = jCas.getAnnotationIndex().subiterator(AnnotationRetrieval.get(jCas, Sentence.class, 0));
		assertTrue(tokensInSentence.hasNext());

		
	}
	
}
