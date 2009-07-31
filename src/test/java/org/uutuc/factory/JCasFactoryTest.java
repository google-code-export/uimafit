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

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uutuc.type.Token;
import org.uutuc.util.AnnotationRetrieval;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class JCasFactoryTest {

	@Test
	public void testXMI() throws UIMAException, IOException {
		JCas jCas = JCasFactory.createJCas("test/data/docs/test.xmi", TypeSystemDescriptionFactory.createTypeSystemDescription("org.uutuc.type.TypeSystem"));
		assertEquals("Me and all my friends are non-conformists.", jCas.getDocumentText());

	}

	@Test
	public void testXCAS() throws UIMAException, IOException {
		JCas jCas = JCasFactory.createJCas("test/data/docs/test.xcas", TypeSystemDescriptionFactory.createTypeSystemDescription("org.uutuc.type.TypeSystem"), false);
		assertEquals("... the more knowledge advances the more it becomes possible to condense it into little books.", jCas.getDocumentText());

	}

	@Test
	public void testFromPath() throws UIMAException {
		JCas jCas = JCasFactory.createJCasFromPath("src/test/java/org/uutuc/type/TypeSystem.xml");
		jCas.setDocumentText("For great 20 minute talks, check out TED.com.");
		AnnotationFactory.createAnnotation(jCas, 0, 3, Token.class);
		assertEquals("For", AnnotationRetrieval.get(jCas, Token.class, 0).getCoveredText());
	}
	
	@Test
	public void test() throws UIMAException {
		JCas jCas = JCasFactory.createJCas("org.uutuc.type.TypeSystem");
		jCas.setDocumentText("For great 20 minute talks, check out TED.com.");
		AnnotationFactory.createAnnotation(jCas, 0, 3, Token.class);
		assertEquals("For", AnnotationRetrieval.get(jCas, Token.class, 0).getCoveredText());
	}

}
