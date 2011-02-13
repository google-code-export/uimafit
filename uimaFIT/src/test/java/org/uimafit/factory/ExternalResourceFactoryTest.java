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

package org.uimafit.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContextAdmin;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ConfigurationManager;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.Resource_ImplBase;
import org.apache.uima.resource.SharedResourceObject;
import org.apache.uima.resource.metadata.ResourceManagerConfiguration;
import org.junit.Test;
import org.uimafit.ComponentTestBase;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.component.initialize.ConfigurationParameterInitializer;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;

/**
 * Test case for {@link ExternalResource} annotations.
 *
 * @author Richard Eckart de Castilho
 */
public class ExternalResourceFactoryTest extends ComponentTestBase {
	private static final String EX_URI = "http://dum.my";
	private static final String EX_FILE_1 = "src/test/resources/data/html/1.html";
	private static final String EX_FILE_3 = "src/test/resources/data/html/3.html";

	@Test
	public void testScanBind() throws Exception {
		AnalysisEngineDescription desc = createPrimitiveDescription(DummyAE.class,
				typeSystemDescription);

		bindResource(desc, DummyResource.class);
		bindResource(desc, DummyAE.RES_KEY_1, ConfigurableResource.class,
				ConfigurableResource.PARAM_VALUE, "1");
		bindResource(desc, DummyAE.RES_KEY_2, ConfigurableResource.class,
				ConfigurableResource.PARAM_VALUE, "2");
		bindResource(desc, DummySharedResourceObject.class, EX_URI);
		// An undefined URL may be used if the specified file/remote URL does not exist or if
		// the network is down.
		bindResource(desc, DummyAE.RES_SOME_URL, new File(EX_FILE_1).toURI().toURL());
		bindResource(desc, DummyAE.RES_SOME_OTHER_URL, new File(EX_FILE_3).toURI().toURL());
		bindResource(desc, DummyAE.RES_SOME_FILE, new File(EX_FILE_1));

		ResourceManagerConfiguration resCfg = desc.getResourceManagerConfiguration();
		assertEquals(7, resCfg.getExternalResourceBindings().length);

		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(desc);
		assertNotNull(ae);

		ae.process(ae.newJCas());
	}

	public static final class DummyAE extends JCasAnnotator_ImplBase {
		@ExternalResource
		DummyResource r;

		static final String RES_KEY_1 = "Key1";
		@ExternalResource(key = RES_KEY_1)
		ConfigurableResource configRes1;

		static final String RES_KEY_2 = "Key2";
		@ExternalResource(key = RES_KEY_2)
		ConfigurableResource configRes2;

		@ExternalResource
		DummySharedResourceObject sharedObject;

		static final String RES_SOME_URL = "SomeUrl";
		@ExternalResource(key = RES_SOME_URL)
		DataResource someUrl;

		static final String RES_SOME_OTHER_URL = "SomeOtherUrl";
		@ExternalResource(key = RES_SOME_OTHER_URL)
		DataResource someOtherUrl;

		static final String RES_SOME_FILE = "SomeFile";
		@ExternalResource(key = RES_SOME_FILE)
		DataResource someFile;

		@Override
		public void process(JCas aJCas) throws AnalysisEngineProcessException {
			assertNotNull(r);
			assertNotNull(configRes1);
			assertEquals("1", configRes1.getValue());
			assertNotNull(configRes2);
			assertEquals("2", configRes2.getValue());
			assertNotNull(sharedObject);
			assertEquals(EX_URI, sharedObject.getUrl().toString());
			assertNotNull(someUrl);
			assertEquals(new File(EX_FILE_1).toURI().toString(), someUrl.getUri().toString());
			assertNotNull(someOtherUrl);
			assertEquals(new File(EX_FILE_3).toURI().toString(), someOtherUrl.getUri().toString());
			assertTrue(someFile.getUrl().toString().startsWith("file:"));
			assertTrue("URL [" + someFile.getUrl() + "] should end in [" + EX_FILE_1 + "]", someFile
					.getUrl().toString().endsWith(EX_FILE_1));
		}
	}

	public static final class DummyResource extends Resource_ImplBase {
		// Nothing
	}

	public static final class ConfigurableResource extends Resource_ImplBase {
		public static final String PARAM_VALUE = "Value";
		@ConfigurationParameter(name = PARAM_VALUE, mandatory = true)
		private String value;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException
		{
			if (!super.initialize(aSpecifier, aAdditionalParams)) {
				return false;
			}

			// Create synthetic context to be able to use InitializeUtil.
			UimaContextAdmin context = UIMAFramework.newUimaContext(UIMAFramework.getLogger(),
					UIMAFramework.newDefaultResourceManager(), UIMAFramework.newConfigurationManager());
			ConfigurationManager cfgMgr = context.getConfigurationManager();
			cfgMgr.setSession(context.getSession());
			CustomResourceSpecifier spec = (CustomResourceSpecifier) aSpecifier;
			for (Parameter p : spec.getParameters()) {
				cfgMgr.setConfigParameterValue(context.getQualifiedContextName() + p.getName(), p
						.getValue());
			}
			ConfigurationParameterInitializer.initialize(this, context);

			return true;
		}

		public String getValue() {
			return value;
		}
	}

	public static final class DummySharedResourceObject implements SharedResourceObject {
		private URI uri;

		public void load(DataResource aData) throws ResourceInitializationException {
			assertEquals(EX_URI, aData.getUri().toString());
			uri = aData.getUri();
		}

		public URI getUrl() {
			return uri;
		}
	}

}
