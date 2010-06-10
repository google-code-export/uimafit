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

package org.uimafit.testing.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.factory.AnnotationFactory;

/**
 * This class was written as a simple way to populate a JCas with tokens along
 * with part-of-speech and stem information and assumes you already have this
 * information at your disposal. This should not be confused with an actual
 * tokenizer (or part-of-speech tagger) which tokenizes and tags plain text.  
 * 
 * @author Steven Bethard, Philip Ogren
 */
public class TokenFactory {

	public static <TOKEN_TYPE extends Annotation, SENTENCE_TYPE extends Annotation> void createTokens(JCas jCas,
			String text, Class<TOKEN_TYPE> tokenClass, Class<SENTENCE_TYPE> sentenceClass) throws UIMAException {
		createTokens(jCas, text, tokenClass, sentenceClass, null, null, null, null, null);
	}

	public static <TOKEN_TYPE extends Annotation, SENTENCE_TYPE extends Annotation> void createTokens(JCas jCas,
			String text, Class<TOKEN_TYPE> tokenClass, Class<SENTENCE_TYPE> sentenceClass, String tokensString)
			throws UIMAException {
		createTokens(jCas, text, tokenClass, sentenceClass, tokensString, null, null, null, null);
	}

	/**
	 * Add Token and Sentence annotations to the JCas.
	 * 
	 * @param jCas
	 *            The JCas where the annotation should be added.
	 * @param text
	 *            The text of the document.
	 * @param tokensString
	 *            The text of the document with spaces separating words and
	 *            newlines separating sentences.
	 * @param posTagsString
	 *            The part of speech tags for each word, separated by spaces.
	 * @param stemsString
	 *            The stems for each word, separated by spaces.
	 * @throws UIMAException
	 */
	public static <TOKEN_TYPE extends Annotation, SENTENCE_TYPE extends Annotation> void createTokens(JCas jCas,
			String text, Class<TOKEN_TYPE> tokenClass, Class<SENTENCE_TYPE> sentenceClass, String tokensString,
			String posTagsString, String stemsString, String posFeatureName, String stemFeatureName)
			throws UIMAException {
		// set the document text and add Token annotations as indicated
		jCas.setDocumentText(text);
		int offset = 0;
		int tokenIndex = 0;

		Feature posFeature = null;
		if (posFeatureName != null) posFeature = jCas.getTypeSystem().getFeatureByFullName(posFeatureName);
		Feature stemFeature = null;
		if (stemFeatureName != null) stemFeature = jCas.getTypeSystem().getFeatureByFullName(stemFeatureName);

		String[] sentenceStrings = (tokensString != null ? tokensString : text).split("\n");
		String[] posTags = posTagsString != null ? posTagsString.split(" ") : null;
		String[] stems = stemsString != null ? stemsString.split(" ") : null;
		for (String sentenceString : sentenceStrings) {
			String[] tokenStrings = sentenceString.trim().split(" ");
			List<Annotation> tokenAnnotations = new ArrayList<Annotation>();
			for (String tokenString : tokenStrings) {
				// move the offset up to the beginning of the token
				while (!text.startsWith(tokenString, offset)) {
					offset++;
					if (offset > text.length()) {
						throw new IllegalArgumentException(String.format("unable to find string %s", tokenString));
					}
				}

				// add the Token
				int start = offset;
				offset = offset + tokenString.length();
				Annotation token = AnnotationFactory.createAnnotation(jCas, start, offset, tokenClass);
				tokenAnnotations.add(token);

				// set the stem and part of speech if present
				if (posFeatureName != null) {
					token.setStringValue(posFeature, posTags[tokenIndex]);
				}
				if (stemFeatureName != null) {
					token.setStringValue(stemFeature, stems[tokenIndex]);
				}
				tokenIndex++;
			}
			if (tokenAnnotations.size() > 0) {
				int begin = tokenAnnotations.get(0).getBegin();
				int end = tokenAnnotations.get(tokenAnnotations.size() - 1).getEnd();
				AnnotationFactory.createAnnotation(jCas, begin, end, sentenceClass);
			}
		}
	}
}
