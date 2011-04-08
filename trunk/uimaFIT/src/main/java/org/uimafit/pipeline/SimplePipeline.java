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

package org.uimafit.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.CasCreationUtils;
import org.uimafit.factory.AnalysisEngineFactory;

/**
 * @author Steven Bethard, Philip Ogren
 * 
 */
public final class SimplePipeline {
	private SimplePipeline() {
		// This class is not meant to be instantiated
	}

	/**
	 * Run the CollectionReader and AnalysisEngines as a pipeline.
	 * 
	 * @param reader
	 *            The CollectionReader that loads the documents into the CAS.
	 * @param descs
	 *            Primitive AnalysisEngineDescriptions that process the CAS, in order. If you have a
	 *            mix of primitive and aggregate engines, then please create the AnalysisEngines
	 *            yourself and call the other runPipeline method.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static void runPipeline(CollectionReader reader, AnalysisEngineDescription... descs)
			throws UIMAException, IOException {
		AnalysisEngine[] engines = createEngines(descs);
		runPipeline(reader, engines);
	}

	private static AnalysisEngine[] createEngines(AnalysisEngineDescription... descs)
			throws UIMAException {
		AnalysisEngine[] engines = new AnalysisEngine[descs.length];
		for (int i = 0; i < engines.length; ++i) {
			if (descs[i].isPrimitive()) {
				engines[i] = AnalysisEngineFactory.createPrimitive(descs[i]);
			}
			else {
				engines[i] = AnalysisEngineFactory.createAggregate(descs[i]);
			}
		}
		return engines;

	}

	/**
	 * Provides a simple way to run a pipeline for a given collection reader and sequence of
	 * analysis engines
	 * 
	 * @param reader
	 *            a collection reader
	 * @param engines
	 *            a sequence of analysis engines
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static void runPipeline(CollectionReader reader, AnalysisEngine... engines)
			throws UIMAException, IOException {
		List<ResourceMetaData> metaData = new ArrayList<ResourceMetaData>();
		metaData.add(reader.getMetaData());
		for (AnalysisEngine engine : engines) {
			metaData.add(engine.getMetaData());
		}
		CAS cas = CasCreationUtils.createCas(metaData);
		while (reader.hasNext()) {
			reader.getNext(cas);
			for (AnalysisEngine engine : engines) {
				engine.process(cas);
			}
			cas.reset();
		}
		for (AnalysisEngine engine : engines) {
			engine.collectionProcessComplete();
		}
		reader.close();
	}

	/**
	 * This method allows you to run a sequence of analysis engines over a jCas
	 * 
	 * @param jCas
	 *            the jCas to process
	 * @param descs
	 *            a sequence of analysis engines to run on the jCas
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static void runPipeline(JCas jCas, AnalysisEngineDescription... descs)
			throws UIMAException, IOException {
		AnalysisEngine[] engines = createEngines(descs);
		runPipeline(jCas, engines);
	}

	/**
	 * This method allows you to run a sequence of analysis engines over a jCas
	 * 
	 * @param jCas
	 *            the jCas to process
	 * @param engines
	 *            a sequence of analysis engines to run on the jCas
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static void runPipeline(JCas jCas, AnalysisEngine... engines) throws UIMAException,
			IOException {
		for (AnalysisEngine engine : engines) {
			engine.process(jCas);
		}

		for (AnalysisEngine engine : engines) {
			engine.collectionProcessComplete();
		}
	}

}
