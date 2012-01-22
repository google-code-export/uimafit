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

package org.uimafit.examples.resource;

import static org.junit.Assert.assertTrue;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregate;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.junit.Test;
import org.uimafit.descriptor.ExternalResource;

/**
 * Example for the use of external resources with uimaFIT.
 *
 * @author Richard Eckart de Castilho
 */
public class ExternalResourceExample {
	/**
	 * Simple model that only stores the URI it was loaded from. Normally data would be loaded from
	 * the URI instead and made accessible through methods in this class. This simple example only
	 * allows to access the URI.
	 */
	public static final class SharedModel implements SharedResourceObject {
		private String uri;

		public void load(DataResource aData) throws ResourceInitializationException {
			uri = aData.getUri().toString();
		}

		public String getUri() {
			return uri;
		}
	}

	/**
	 * Example annotator that uses the SharedModel. In the process() we only test if the model was
	 * properly initialized by uimaFIT
	 */
	public static class Annotator extends org.uimafit.component.JCasAnnotator_ImplBase {
		final static String MODEL_KEY = "Model";
		@ExternalResource(key = MODEL_KEY)
		private SharedModel model;

		@Override
		public void process(JCas aJCas) throws AnalysisEngineProcessException {
			assertTrue(model.getUri().endsWith("somemodel.bin"));
			// Prints the instance ID to the console - this proves the same instance
			// of the SharedModel is used in both Annotator instances.
			System.out.println(model);
		}
	}

	/**
	 * JUnit test that illustrates how to configure the Annotator with the SharedModel
	 */
	@Test
	public void configureAggregatedExample() throws Exception {
		AnalysisEngineDescription aed1 = createPrimitiveDescription(Annotator.class);
		AnalysisEngineDescription aed2 = createPrimitiveDescription(Annotator.class);

		// Bind external resource to the aggregate
		AnalysisEngineDescription aaed = createAggregateDescription(aed1, aed2);
		bindResource(aaed, Annotator.MODEL_KEY, SharedModel.class, new File("somemodel.bin")
				.toURI().toURL().toString());

		// Check the external resource was injected
		AnalysisEngine ae = createAggregate(aaed);
		ae.process(ae.newJCas());
	}

	/**
	 * JUnit test that illustrates how to configure the Annotator with the SharedModel
	 */
	@Test
	public void configureAnnotatorsIndividuallyExample() throws Exception {
		AnalysisEngineDescription aed1 = createPrimitiveDescription(Annotator.class);
		AnalysisEngineDescription aed2 = createPrimitiveDescription(Annotator.class);

		ExternalResourceDescription extDesc = createExternalResourceDescription("sharedModel",
				SharedModel.class, new File("somemodel.bin").toURI().toURL().toString());

		// Binding external resource to each Annotator individually
		bindResource(aed1, Annotator.MODEL_KEY, extDesc);
		bindResource(aed2, Annotator.MODEL_KEY, extDesc);

		// Check the external resource was injected
		AnalysisEngineDescription aaed = createAggregateDescription(aed1, aed2);
		AnalysisEngine ae = createAggregate(aaed);
		ae.process(ae.newJCas());
	}
}