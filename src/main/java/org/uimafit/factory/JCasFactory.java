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

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.util.JCasAnnotatorAdapter;
import org.uimafit.util.JCasIterable;
import org.uimafit.util.SingleFileXReader;
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
		if (isXmi) {
			CollectionReader reader = CollectionReaderFactory.createCollectionReader(SingleFileXReader.class, typeSystemDescription,
					SingleFileXReader.PARAM_XML_SCHEME, "XMI", SingleFileXReader.PARAM_FILE_NAME, xmiFileName);

			return new JCasIterable(reader).next();
		} else {
			CollectionReader reader = CollectionReaderFactory.createCollectionReader(SingleFileXReader.class, typeSystemDescription,
					SingleFileXReader.PARAM_XML_SCHEME, "XCAS", SingleFileXReader.PARAM_FILE_NAME, xmiFileName);

			return new JCasIterable(reader).next();
		}
	}


}
