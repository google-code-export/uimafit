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

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

/**
 * 
 * @author Philip Ogren
 *
 */
public class GetStartedQuickPipeline {

	public static void main(String[] args) throws UIMAException {
		//normally you would pass in a list of classes corresponding to type system types here or provide the name of your type system (e.g. "org.uimafit.examples.TypeSystem.xml") 
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(new Class[0]);
		//uimaFIT doesn't provide any collection readers - so we will just instantiate a JCas and run it through our AE
		JCas jCas = JCasFactory.createJCas(typeSystemDescription);
		//here we will instantiate the analysis engine using the value "uimaFIT" for the parameter "stringParamName".
		AnalysisEngine analysisEngine = AnalysisEngineFactory.createPrimitive(GetStartedQuickAE.class, typeSystemDescription, "stringParamName", "uimaFIT");
		//run the analysis engine and look for a special greeting in your console.
		analysisEngine.process(jCas);
	}
}
