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
package org.uimafit.factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XCASDeserializer;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.JCasAnnotatorAdapter;
import org.xml.sax.SAXException;
/**
 * @author Steven Bethard, Philip Ogren
 */
public class JCasFactory {


	public static JCas createJCas(String typeSystemDescriptorName) throws UIMAException {
		AnalysisEngine engine = AnalysisEngineFactory.createPrimitive(JCasAnnotatorAdapter.class, TypeSystemDescriptionFactory
				.createTypeSystemDescription(typeSystemDescriptorName));
		return engine.newJCas();

	}

	public static JCas createJCasFromPath(String typeSystemDescriptorPath) throws UIMAException {
		return createJCas(TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath(typeSystemDescriptorPath));
	}

	public static JCas createJCas(Class<?>... annotationClasses) throws UIMAException {
		return createJCas(TypeSystemDescriptionFactory.createTypeSystemDescription(annotationClasses));
	}


	public static JCas createJCas(TypeSystemDescription typeSystemDescription) throws UIMAException {
		AnalysisEngine engine = AnalysisEngineFactory.createPrimitive(JCasAnnotatorAdapter.class, typeSystemDescription);
		return engine.newJCas();

	}

	public static JCas createJCas(String xmiFileName, TypeSystemDescription typeSystemDescription) throws UIMAException,
			IOException {
		return createJCas(xmiFileName, typeSystemDescription, true);
	}

	public static JCas createJCas(String xmiFileName, TypeSystemDescription typeSystemDescription, boolean isXmi)
			throws UIMAException, IOException {
		JCas jCas = createJCas(typeSystemDescription);
		loadJCas(jCas, xmiFileName, isXmi);
		return jCas;
	}

	public static void loadJCas(JCas jCas, String xmiFileName) throws IOException {
		loadJCas(jCas, xmiFileName, true);
	}
	public static void loadJCas(JCas jCas, String xmlFileName, boolean isXmi) throws IOException {
		FileInputStream inputStream = new FileInputStream(xmlFileName);
		loadJCas(jCas, inputStream, isXmi);
	}

	public static void loadJCas(JCas jCas, InputStream xmiInputStream) throws IOException {
		loadJCas(jCas, xmiInputStream, true);
	}
	
	public static void loadJCas(JCas jCas, InputStream xmlInputStream, boolean isXmi) throws IOException {
		jCas.reset();
		try {
			CAS cas = jCas.getCas();
			if (isXmi) {
				XmiCasDeserializer.deserialize(xmlInputStream, cas);
			}
			else {
				XCASDeserializer.deserialize(xmlInputStream, cas);
			}
		}
		catch (SAXException e) {
			throw new IOException(e);
		}
		finally {
			xmlInputStream.close();
		}
	}

}
