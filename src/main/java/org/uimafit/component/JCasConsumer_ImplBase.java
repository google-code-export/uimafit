/*
 Copyright 2010
 Ubiquitous Knowledge Processing (UKP) Lab
 Technische Universitaet Darmstadt
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
package org.uimafit.component;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.AnalysisComponent;
import org.uimafit.factory.ExternalResourceConfigurator;
import org.uimafit.util.InitializeUtil;

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;

/**
 * Base class for JCas consumers (actually a {@link org.apache.uima.analysis_component.JCasAnnotator_ImplBase})
 * which initializes itself based on annotations.
 *
 * @author Richard Eckart de Castilho
 */
@AnalysisComponent(multipleDeploymentAllowed=false)
public abstract class JCasConsumer_ImplBase
	extends org.apache.uima.analysis_component.JCasAnnotator_ImplBase
{
	@Override
	@OverrideMustInvoke
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);
		InitializeUtil.initialize(this, aContext);
		ExternalResourceConfigurator.configure(aContext, this);
	}
}
