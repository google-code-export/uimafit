/*
 Copyright 2009
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

package org.uutuc.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.uutuc.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uutuc.factory.ExternalResourceFactory.bindResource;
import static org.uutuc.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource_ImplBase;
import org.apache.uima.resource.metadata.ResourceManagerConfiguration;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.uutuc.descriptor.ExternalResource;

/**
 * Test case for {@link ExternalResource} annotations.
 * 
 * @author Richard Eckart de Castilho
 */
public class ExternalResourceFactoryTest
{
	@Test
	public void testScanBind()
		throws Exception
	{
		TypeSystemDescription tsd = createTypeSystemDescription(new Class<?>[0]);
		AnalysisEngineDescription desc = createPrimitiveDescription(
				DummyAE.class, tsd);
		bindResource(desc, DummyResource.class);

		ResourceManagerConfiguration resCfg = desc.getResourceManagerConfiguration();
		assertEquals(1, resCfg.getExternalResourceBindings().length);
		
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(desc);
		assertNotNull(ae);
	}

	public static final class DummyAE
		extends JCasAnnotator_ImplBase
	{
		@ExternalResource
		DummyResource r;

		@Override
		public void process(JCas aJCas)
			throws AnalysisEngineProcessException
		{
			// Do nothing
		}
	}

	public static final class DummyResource
		extends Resource_ImplBase
	{
		// Nothing
	}
}
