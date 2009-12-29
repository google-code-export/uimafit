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
package org.uutuc.factory.testAes;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.descriptor.SofaCapability;
import org.uutuc.util.InitializeUtil;

/**
 * @author Philip Ogren
 */

@SofaCapability(inputSofas = CAS.NAME_DEFAULT_SOFA, outputSofas = ViewNames.PARENTHESES_VIEW)
public class Annotator1 extends JCasAnnotator_ImplBase {
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		InitializeUtil.initialize(this, context);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			JCas parentheticalView = jCas.createView(ViewNames.PARENTHESES_VIEW);
			jCas = jCas.getView(CAS.NAME_DEFAULT_SOFA);
			String initialText = jCas.getDocumentText();
			String parentheticalText = initialText.replaceAll("[aeiou]+", "($0)");
			parentheticalView.setDocumentText(parentheticalText);
		}
		catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
	}

}
