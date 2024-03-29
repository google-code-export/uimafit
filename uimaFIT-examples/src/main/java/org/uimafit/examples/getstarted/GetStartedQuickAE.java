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

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

/**
 * @author Philip Ogren
 */
public class GetStartedQuickAE extends JCasAnnotator_ImplBase {

	public static final String PARAM_STRING = "stringParam";
	@ConfigurationParameter(name = PARAM_STRING)
	private String stringParam;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		System.out.println("Hello world!  Say 'hi' to " + stringParam);
	}
}
