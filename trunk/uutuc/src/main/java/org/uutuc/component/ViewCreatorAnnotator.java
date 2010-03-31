/* 
  Copyright 2010 Regents of the University of Colorado.  
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
package org.uutuc.component;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.descriptor.ConfigurationParameter;
import org.uutuc.factory.ConfigurationParameterFactory;
import org.uutuc.util.InitializeUtil;

/**
 * This annotator can be placed at/near the beginning of a pipeline to ensure
 * that a particular view is created before it is used further downstream. It
 * will create a view for the view name specified by the configuration parameter
 * PARAM_VIEW_NAME if it doesn't exist. One place this is useful is if you are
 * using an annotator that uses the default view and you have mapped the default
 * view into a different view via a sofa mapping. The default view is created
 * automatically - but if you have mapped the default view to some other view,
 * then the view provided to your annotator (when it asks for the default view)
 * will not be created unless you have explicitly created it.
 * 
 * @author Philip Ogren
 * 
 */
public class ViewCreatorAnnotator extends JCasAnnotator_ImplBase {

	public static final String VIEW_NAME = "viewName";

	public static String PARAM_VIEW_NAME = ConfigurationParameterFactory.createConfigurationParameterName(
			ViewCreatorAnnotator.class, "viewName");

	@ConfigurationParameter
	private String viewName = VIEW_NAME;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		InitializeUtil.initialize(this, context);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			try {
				jCas.getView(viewName);
			}
			catch (CASRuntimeException ce) {
				jCas.createView(viewName);
			}
		}
		catch (CASException ce) {
		}
	}

}
