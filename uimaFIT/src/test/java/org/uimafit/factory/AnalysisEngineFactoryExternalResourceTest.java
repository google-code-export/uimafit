/*
 Copyright 2011
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

package org.uimafit.factory;

import static org.junit.Assert.assertNotNull;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.junit.Test;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.testRes.TestExternalResource;

/**
 * @author Shuo Yang
 * @author Richard Eckart de Castilho
 */
public class AnalysisEngineFactoryExternalResourceTest {
	@Test
	public void testAutoExternalResourceBinding() throws Exception {
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(
				TestAnalysisEngine.class,
				TestAnalysisEngine.PARAM_RESOURCE,
				createExternalResourceDescription(TestExternalResource.class,
						TestExternalResource.PARAM_VALUE, TestExternalResource.EXPECTED_VALUE));
		
		ae.process(ae.newCAS());
	}

	public static class TestAnalysisEngine extends CasAnnotator_ImplBase {

		public final static String PARAM_RESOURCE = "resource";
		@ExternalResource(key = PARAM_RESOURCE)
		private TestExternalResource resource;

		@Override
		public void process(CAS aCAS) throws AnalysisEngineProcessException {
			assertNotNull(resource);
			resource.assertConfiguredOk();
		}
	}
}
