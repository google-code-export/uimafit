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

package org.uutuc.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;
import org.uutuc.factory.JCasFactory;
import org.uutuc.factory.TokenFactory;
import org.uutuc.type.Sentence;
import org.uutuc.type.Token;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class TokenizerTest {

	JCas jCas;
	
	@Before
	public void setUp() throws Exception {
		jCas = Util.JCAS.get();
		jCas.reset();
	}

	
	@Test
	public void test1() throws UIMAException {
		String text = "What if we built a rocket ship made of cheese?" + "We could fly it to the moon for repairs.";
		TokenFactory.createTokens(jCas, text, Token.class, Sentence.class,
				"What if we built a rocket ship made of cheese ? \n We could fly it to the moon for repairs .",
				"A B C D E F G H I J K L M N O P Q R S T U", null, "org.uutuc.type.Token:pos", null);

		FSIndex sentenceIndex = jCas.getAnnotationIndex(Sentence.type);
		assertEquals(2, sentenceIndex.size());
		FSIterator sentences = sentenceIndex.iterator();
		Sentence sentence = (Sentence) sentences.next();
		assertEquals("What if we built a rocket ship made of cheese?", sentence.getCoveredText());
		sentence = (Sentence) sentences.next();
		assertEquals("We could fly it to the moon for repairs.", sentence.getCoveredText());

		FSIndex tokenIndex = jCas.getAnnotationIndex(Token.type);
		assertEquals(21, tokenIndex.size());
		Token token = AnnotationRetrieval.get(jCas, Token.class, 0);
		testToken(token, "What", 0, 4, "A", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 1);
		testToken(token, "if", 5, 7, "B", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 9);
		testToken(token, "cheese", 39, 45, "J", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 10);
		testToken(token, "?", 45, 46, "K", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 11);
		testToken(token, "We", 46, 48, "L", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 12);
		testToken(token, "could", 49, 54, "M", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 19);
		testToken(token, "repairs", 78, 85, "T", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 20);
		testToken(token, ".", 85, 86, "U", null);
	}
	
	@Test
	public void test2() throws UIMAException {
		String text = "What if we built a rocket ship made of cheese? \n" + "We could fly it to the moon for repairs.";
		TokenFactory.createTokens(jCas, text, Token.class, Sentence.class,
				"What if we built a rocket ship made of cheese ? \n We could fly it to the moon for repairs .",
				"A B C D E F G H I J K L M N O P Q R S T U", null, "org.uutuc.type.Token:pos", null);

		Token token = AnnotationRetrieval.get(jCas, Token.class, 10);
		testToken(token, "?", 45, 46, "K", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 11);
		testToken(token, "We", 48, 50, "L", null);

		jCas.reset();
		text = "What if we built a rocket ship made of cheese? \n" + "We could fly it to the moon for repairs.";
		TokenFactory.createTokens(jCas, text, Token.class, Sentence.class,
				"What if we built a rocket ship made of cheese ?\nWe could fly it to the moon for repairs .",
				"A B C D E F G H I J K L M N O P Q R S T U", null, "org.uutuc.type.Token:pos", null);

		token = AnnotationRetrieval.get(jCas, Token.class, 10);
		testToken(token, "?", 45, 46, "K", null);
		token = AnnotationRetrieval.get(jCas, Token.class, 11);
		testToken(token, "We", 48, 50, "L", null);
	}
	
	@Test
	public void test3() throws UIMAException {
		String text = "If you like line writer, then you should really check out line rider.";
		TokenFactory.createTokens(jCas, text, Token.class, Sentence.class);

		FSIndex tokenIndex = jCas.getAnnotationIndex(Token.type);
		assertEquals(13, tokenIndex.size());
		Token token = AnnotationRetrieval.get(jCas, Token.class, 0);
		testToken(token, "If", 0, 2, null, null);
		token = AnnotationRetrieval.get(jCas, Token.class, 12);
		testToken(token, "rider.", 63, 69, null, null);
		FSIndex sentenceIndex = jCas.getAnnotationIndex(Sentence.type);
		assertEquals(1, sentenceIndex.size());
		Sentence sentence = AnnotationRetrieval.get(jCas, Sentence.class, 0);
		assertEquals(text, sentence.getCoveredText());
	}

	private void testToken(Token token, String coveredText, int begin, int end, String partOfSpeech, String stem) {
		assertEquals(coveredText, token.getCoveredText());
		assertEquals(begin, token.getBegin());
		assertEquals(end, token.getEnd());
		assertEquals(partOfSpeech, token.getPos());
		assertEquals(stem, token.getStem());
	}

	@Test
	public void testSpaceSplit() {
		String[] splits = " asdf ".split(" ");
		assertEquals(2, splits.length);
	}

	@Test
	public void testBadInput() throws UIMAException {
		String text = "If you like line writer, then you should really check out line rider.";
		IllegalArgumentException iae = null;
		try {
			TokenFactory.createTokens(jCas, text, Token.class, Sentence.class, "If you like line rider, then you really don't need line writer");
		}catch (IllegalArgumentException e) {
			iae = e;
		}
		assertNotNull(iae);
	}
	
	@Test
	public void testStems() throws UIMAException {
		JCas jCas = JCasFactory.createJCas(Token.class, Sentence.class);
		String text = "Me and all my friends are non-conformists.";
		TokenFactory.createTokens(jCas, text, Token.class, Sentence.class,
				"Me and all my friends are non - conformists .",
				"M A A M F A N - C .",
				"me and all my friend are non - conformist .",
				"org.uutuc.type.Token:pos", "org.uutuc.type.Token:stem");

		assertEquals("Me and all my friends are non-conformists.", jCas.getDocumentText());
		Token friendToken = AnnotationRetrieval.get(jCas, Token.class, 4);
		assertEquals("friends", friendToken.getCoveredText());
		assertEquals("F", friendToken.getPos());
		assertEquals("friend", friendToken.getStem());
	}
}
