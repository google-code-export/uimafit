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

import static org.junit.Assert.*;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.junit.Test;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.component.NoOpAnnotator;
import org.uimafit.component.Resource_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.pipeline.SimplePipeline;

/**
 * @author Shuo Yang
 */
public class AnalysisEngineFactoryExternalResourceTest {

	private static String USER_NAME = "Steve";
	private static String EXPECTED_GREETING = "Hi Steve!";

	@Test
	public void testAutoExternalResourceBinding() throws UIMAException, IOException {
		JCas jcas = JCasFactory.createJCas();

		AnalysisEngineDescription descriptor = AnalysisEngineFactory.createPrimitiveDescription(
				TestAnalysisEngine.class,
				TestAnalysisEngine.PARAM_RESOURCE,
				createExternalResourceDescription(TestExternalResource.class,
						TestExternalResource.PARAM_USER_NAME, USER_NAME,
						TestExternalResource.PARAM_EXPECTED_GREETING, EXPECTED_GREETING),
				TestAnalysisEngine.PARAM_EXPECTED_GREETING, EXPECTED_GREETING);

		SimplePipeline.runPipeline(jcas, descriptor);
	}

	@Test
	public void testNestedAutoExternalResourceBinding() throws UIMAException, IOException {
		JCas jcas = JCasFactory.createJCas();

		AnalysisEngineDescription descriptor = AnalysisEngineFactory.createPrimitiveDescription(
				TestAnalysisEngine2.class,
				TestAnalysisEngine2.PARAM_RESOURCE,
				createExternalResourceDescription(TestExternalResource2.class,
						TestExternalResource2.PARAM_USER_NAME, USER_NAME,
						TestExternalResource2.PARAM_EXPECTED_GREETING, EXPECTED_GREETING,
						TestExternalResource2.PARAM_RESOURCE, createExternalResourceDescription(TestExternalResource.class,
								TestExternalResource.PARAM_USER_NAME, USER_NAME,
								TestExternalResource.PARAM_EXPECTED_GREETING, EXPECTED_GREETING)),
				TestAnalysisEngine.PARAM_EXPECTED_GREETING, EXPECTED_GREETING);

		SimplePipeline.runPipeline(jcas, descriptor);
	}

	@Test
	public void testSharedExternalResource() throws UIMAException, IOException {
		JCas jcas = JCasFactory.createJCas();

		ExternalResourceDescription sharedExternalResource = createExternalResourceDescription(
				TestExternalResource.class, TestExternalResource.PARAM_USER_NAME, USER_NAME,
				TestExternalResource.PARAM_EXPECTED_GREETING, EXPECTED_GREETING);

		AnalysisEngineDescription descriptor1 = createPrimitiveDescription(
				TestAnalysisEngine.class, 
				TestAnalysisEngine.PARAM_RESOURCE, sharedExternalResource, 
				TestAnalysisEngine.PARAM_EXPECTED_GREETING, EXPECTED_GREETING);

		AnalysisEngineDescription descriptor2 = createPrimitiveDescription(
				TestAnalysisEngine.class, 
				TestAnalysisEngine.PARAM_RESOURCE, sharedExternalResource, 
				TestAnalysisEngine.PARAM_EXPECTED_GREETING, EXPECTED_GREETING);

		SimplePipeline.runPipeline(jcas, descriptor1, descriptor2);
	}

	public static class TestExternalResource extends Resource_ImplBase {
		public final static String PARAM_USER_NAME = "userName";
		@ConfigurationParameter(name = PARAM_USER_NAME)
		private String userName;

		public final static String PARAM_EXPECTED_GREETING = "expectedGreeting";
		@ConfigurationParameter(name = PARAM_EXPECTED_GREETING)
		private String expectedGreeting;

		public String greetUser() {
			String greeting = "Hi " + userName + "!";
			// Ensure normal parameters get passed to External Resource
			assertEquals(expectedGreeting, greeting);
			return greeting;
		}
	}

	public static class TestExternalResource2 extends Resource_ImplBase {
		public final static String PARAM_USER_NAME = "userName";
		@ConfigurationParameter(name = PARAM_USER_NAME)
		private String userName;

		public final static String PARAM_EXPECTED_GREETING = "expectedGreeting";
		@ConfigurationParameter(name = PARAM_EXPECTED_GREETING)
		private String expectedGreeting;
		
		public final static String PARAM_RESOURCE = "resource2";
		@ExternalResource(key = PARAM_RESOURCE)
		private TestExternalResource resource;

		@Override
		public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException
		{
			if (!super.initialize(aSpecifier, aAdditionalParams)) {
				return false;
			}

			return true;
		}
		
		public String greetUser() {
			String greeting = "Hi " + userName + "!";
			// Ensure normal parameters get passed to External Resource
			assertEquals(expectedGreeting, greeting);
			return greeting;
		}
		
		@Override
		public void afterResourcesInitialized() {
			// Ensure the External Resource is binded
			assertNotNull(resource);
			assertNotSame(this, resource);
		}
	}

	public static class TestAnalysisEngine extends CasAnnotator_ImplBase {

		public final static String PARAM_RESOURCE = "resource";
		@ExternalResource(key = PARAM_RESOURCE)
		private TestExternalResource resource;

		public final static String PARAM_EXPECTED_GREETING = "expectedGreeting";
		@ConfigurationParameter(name = PARAM_EXPECTED_GREETING)
		private String expectedGreeting;

		@Override
		public void initialize(UimaContext context) throws ResourceInitializationException {
			super.initialize(context);
			// Ensure the External Resource is binded
			assertNotNull(resource);
		}

		@Override
		public void process(CAS aCAS) throws AnalysisEngineProcessException {
			String greeting = resource.greetUser();
			// Ensure normal parameters get passed to Analysis Engine
			assertEquals(expectedGreeting, greeting);
		}
	}

	public static class TestAnalysisEngine2 extends NoOpAnnotator {

		public final static String PARAM_RESOURCE = "resource";
		@ExternalResource(key = PARAM_RESOURCE)
		private TestExternalResource2 resource;

		@Override
		public void initialize(UimaContext context) throws ResourceInitializationException {
			super.initialize(context);
			// Ensure the External Resource is binded
			assertNotNull(resource);
		}
	}
}
