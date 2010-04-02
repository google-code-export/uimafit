/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.uutuc.tutorial.ex1;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uutuc.descriptor.TypeCapability;
import org.uutuc.factory.AnalysisEngineFactory;
import org.uutuc.factory.TypeSystemDescriptionFactory;
import org.uutuc.tutorial.type.RoomNumber;

/**
 * Example annotator that detects room numbers using Java 1.4 regular
 * expressions.
 */
@TypeCapability(outputs= {"org.apache.uima.tutorial.RoomNumber", "org.apache.uima.tutorial.RoomNumber:building"})
public class RoomNumberAnnotator extends JCasAnnotator_ImplBase {
	private Pattern mYorktownPattern = Pattern.compile("\\b[0-4]\\d-[0-2]\\d\\d\\b");

	private Pattern mHawthornePattern = Pattern.compile("\\b[G1-4][NS]-[A-Z]\\d\\d\\b");

	/**
	 * @see JCasAnnotator_ImplBase#process(JCas)
	 */
	public void process(JCas aJCas) {
		// get document text
		String docText = aJCas.getDocumentText();
		// search for Yorktown room numbers
		Matcher matcher = mYorktownPattern.matcher(docText);
		while (matcher.find()) {
			// found one - create annotation
			RoomNumber annotation = new RoomNumber(aJCas);
			annotation.setBegin(matcher.start());
			annotation.setEnd(matcher.end());
			annotation.setBuilding("Yorktown");
			annotation.addToIndexes();
		}
		// search for Hawthorne room numbers
		matcher = mHawthornePattern.matcher(docText);
		while (matcher.find()) {
			// found one - create annotation
			RoomNumber annotation = new RoomNumber(aJCas);
			annotation.setBegin(matcher.start());
			annotation.setEnd(matcher.end());
			annotation.setBuilding("Hawthorne");
			annotation.addToIndexes();
		}
	}

	public static void main(String[] args) throws Exception {
	
		File outputDirectory = new File("src/main/resources/org/uutuc/tutorial/ex1/");
		outputDirectory.mkdirs();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(RoomNumber.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createPrimitiveDescription(RoomNumberAnnotator.class, tsd);
		
		aed.toXML(new FileOutputStream(new File(outputDirectory, "RoomNumberAnnotator.xml")));

	}
}
