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
package org.uimafit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TokenFactory;
import org.uimafit.type.Sentence;
import org.uimafit.type.Token;
import org.uimafit.util.AnnotationRetrieval;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class AnnotationRetrievalTest {

	@Test
	public void testGet() throws UIMAException {
		JCas jCas = JCasFactory.createJCas(Token.class, Sentence.class);
		String text = "Rot wood cheeses dew?";
		TokenFactory.createTokens(jCas, text, Token.class, Sentence.class);
		
		Token lastToken = AnnotationRetrieval.get(jCas, Token.class, -1);
		assertEquals("dew?", lastToken.getCoveredText());
		
		Token firstToken = AnnotationRetrieval.get(jCas, Token.class, 0);
		assertEquals("Rot", firstToken.getCoveredText());

		lastToken = AnnotationRetrieval.get(jCas, Token.class, 3);
		assertEquals("dew?", lastToken.getCoveredText());

		firstToken = AnnotationRetrieval.get(jCas, Token.class, -4);
		assertEquals("Rot", firstToken.getCoveredText());

		Token oobToken = AnnotationRetrieval.get(jCas, Token.class, -5);
		assertNull(oobToken);
		
		oobToken = AnnotationRetrieval.get(jCas, Token.class, 4);
		assertNull(oobToken);
		

	}
}
