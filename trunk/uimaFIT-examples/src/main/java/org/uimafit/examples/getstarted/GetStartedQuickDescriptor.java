/* 
 Copyright 2009-2010	Regents of the University of Colorado.  
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
package org.uimafit.examples.getstarted;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.xml.sax.SAXException;

/**
 * 
 * @author Philip Ogren
 *
 */
public class GetStartedQuickDescriptor {

	public static void main(String[] args) throws ResourceInitializationException, FileNotFoundException, SAXException, IOException {
		//normally you would pass in a list of classes corresponding to type system types here
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(new Class[0]);
		//here we will instantiate the analysis engine using the value "uimaFIT" for the parameter "stringParamName".
		AnalysisEngineDescription analysisEngineDescription = AnalysisEngineFactory.createPrimitiveDescription(GetStartedQuickAE.class, typeSystemDescription, "stringParamName", "uimaFIT");
		analysisEngineDescription.toXML(new FileOutputStream("GetStartedQuickAE.xml"));
	}
}
