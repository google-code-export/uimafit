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

import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.CasCreationUtils;
import org.uimafit.factory.AnalysisEngineFactory;

/**
 * @author Steven Bethard, Philip Ogren
 * @author Richard Eckart de Castilho
 *
 */
public final class SimplePipeline {
	private SimplePipeline() {
		// This class is not meant to be instantiated
	}

	/**
	 * Run the CollectionReader and AnalysisEngines as a pipeline. After processing all CASes
	 * provided by the reader, the method calls {@link AnalysisEngine#collectionProcessComplete()
	 * collectionProcessComplete()} on the engines and {@link Resource#destroy() destroy()} on all
	 * engines.
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
		// Create the components
		AnalysisEngine[] engines = createEngines(descs);

		// Run the pipeline
		runPipeline(reader, engines);

		// Destroy the components
		destroy(engines);
	}

	/**
	 * Run the CollectionReader and AnalysisEngines as a pipeline. After processing all CASes
	 * provided by the reader, the method calls {@link AnalysisEngine#collectionProcessComplete()
	 * collectionProcessComplete()} on the engines, {@link CollectionReader#close() close()} on the
	 * reader and {@link Resource#destroy() destroy()} on the reader and all engines.
	 *
	 * @param readerDesc
	 *            The CollectionReader that loads the documents into the CAS.
	 * @param descs
	 *            Primitive AnalysisEngineDescriptions that process the CAS, in order. If you have a
	 *            mix of primitive and aggregate engines, then please create the AnalysisEngines
	 *            yourself and call the other runPipeline method.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static void runPipeline(CollectionReaderDescription readerDesc, AnalysisEngineDescription... descs)
			throws UIMAException, IOException {
		// Create the components
		AnalysisEngine[] engines = createEngines(descs);
		CollectionReader reader = createCollectionReader(readerDesc);

		// Run the pipeline
		runPipeline(reader, engines);

		// Destroy the components
		reader.close();
		destroy(reader);
		destroy(engines);
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
	 * analysis engines. After processing all CASes provided by the reader, the method calls
	 * {@link AnalysisEngine#collectionProcessComplete() collectionProcessComplete()} on the
	 * engines.
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
			runPipeline(cas, engines);
			cas.reset();
		}

		collectionProcessComplete(engines);
	}

	/**
	 * Run a sequence of {@link AnalysisEngine analysis engines} over a {@link JCas}. The result of
	 * the analysis can be read from the JCas.
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
		// Create the components
		AnalysisEngine[] engines = createEngines(descs);

		// Run the pipeline
		runPipeline(jCas, engines);

		// Destroy the components
		destroy(engines);
	}

	/**
	 * Run a sequence of {@link AnalysisEngine analysis engines} over a {@link JCas}. This method
	 * does not {@link AnalysisEngine#destroy() destroy} the engines or send them other events like
	 * {@link AnalysisEngine#collectionProcessComplete()}. This is left to the caller.
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
	}

	/**
	 * Run a sequence of {@link AnalysisEngine analysis engines} over a {@link CAS}. This method
	 * does not {@link AnalysisEngine#destroy() destroy} the engines or send them other events like
	 * {@link AnalysisEngine#collectionProcessComplete()}. This is left to the caller.
	 *
	 * @param cas
	 *            the CAS to process
	 * @param engines
	 *            a sequence of analysis engines to run on the jCas
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static void runPipeline(CAS cas, AnalysisEngine... engines) throws UIMAException,
			IOException {
		for (AnalysisEngine engine : engines) {
			engine.process(cas);
		}
	}

	/**
	 * Notify a set of {@link AnalysisEngine analysis engines} that the collection process is complete.
	 */
	private static void collectionProcessComplete(AnalysisEngine... engines)
			throws AnalysisEngineProcessException {
		for (AnalysisEngine e : engines) {
			e.collectionProcessComplete();
		}
	}

	/**
	 * Destroy a set of {@link Resource resources}.
	 */
	private static void destroy(Resource... resources)
	{
		for (Resource r : resources) {
			r.destroy();
		}
	}
}
