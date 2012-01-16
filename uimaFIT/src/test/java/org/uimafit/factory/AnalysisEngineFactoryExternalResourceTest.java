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
import static org.junit.Assert.assertNotSame;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.component.NoOpAnnotator;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.testRes.TestExternalResource;

/**
 * @author Shuo Yang
 * @author Richard Eckart de Castilho
 */
public class AnalysisEngineFactoryExternalResourceTest {
	/**
	 * Test simple injection.
	 */
	@Test
	public void testInjection() throws Exception {
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(
				TestAnalysisEngine.class,
				TestAnalysisEngine.PARAM_RESOURCE,
				createExternalResourceDescription(TestExternalResource.class,
						TestExternalResource.PARAM_VALUE, TestExternalResource.EXPECTED_VALUE));
		
		ae.process(ae.newCAS());
	}

	/**
	 * Test simple nesting.
	 */
	@Test
	public void testSimpleNesting() throws Exception {
		AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(
				TestAnalysisEngine.class,
				TestAnalysisEngine.PARAM_RESOURCE,
				createExternalResourceDescription(TestExternalResource2.class,
						TestExternalResource.PARAM_VALUE, TestExternalResource.EXPECTED_VALUE,
						TestExternalResource2.PARAM_RESOURCE, createExternalResourceDescription(
								TestExternalResource.class,
								TestExternalResource.PARAM_VALUE, TestExternalResource.EXPECTED_VALUE)));

		ae.process(ae.newCAS());
	}

	/**
	 * Test deeper nesting level.
	 */
	@Test
	public void testDeeperNesting() throws Exception {
		ExternalResourceDescription resDesc2 = createExternalResourceDescription(
				TestExternalResource.class,
				TestExternalResource.PARAM_VALUE, TestExternalResource.EXPECTED_VALUE);

		ExternalResourceDescription resDesc = createExternalResourceDescription(
				TestExternalResource2.class,
				TestExternalResource2.PARAM_RESOURCE, resDesc2,
				TestExternalResource.PARAM_VALUE, TestExternalResource.EXPECTED_VALUE);

		AnalysisEngineDescription aeDesc = AnalysisEngineFactory.createPrimitiveDescription(
				TestAnalysisEngine.class,
				TestAnalysisEngine.PARAM_RESOURCE,
				createExternalResourceDescription(TestExternalResource2.class,
						TestExternalResource.PARAM_VALUE, TestExternalResource.EXPECTED_VALUE,
						TestExternalResource2.PARAM_RESOURCE, resDesc));
		
		AnalysisEngine ae = createPrimitive(aeDesc);
		ae.process(ae.newCAS());
	}

	public static class TestExternalResource2 extends TestExternalResource {
		public final static String PARAM_RESOURCE = "resource2";
		@ExternalResource(key = PARAM_RESOURCE)
		private TestExternalResource resource;

		@Override
		public void afterResourcesInitialized() {
			// Ensure the External Resource is binded
			assertNotNull(resource);
			assertNotSame(this, resource);
			resource.assertConfiguredOk();
			assertConfiguredOk();
		}
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

	public static class TestAnalysisEngine2 extends NoOpAnnotator {

		public final static String PARAM_RESOURCE = "resource";
		@ExternalResource(key = PARAM_RESOURCE)
		private TestExternalResource2 resource;

		@Override
		public void process(CAS aCAS) throws AnalysisEngineProcessException {
			assertNotNull(resource);
			resource.assertConfiguredOk();
		}
	}
}
