/* 
 Copyright 2009 Regents of the University of Colorado.  
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

package org.uutuc.util;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.uutuc.factory.AnalysisEngineFactory;

/**
 * @author Steven Bethard, Philip Ogren
 *
 */
public class SimplePipeline {

	/**
	 * Run the CollectionReader and AnalysisEngines as a pipeline.
	 * 
	 * @param reader
	 *            The CollectionReader that loads the documents into the CAS.
	 * @param descs
	 *            Primitive AnalysisEngineDescriptions that process the CAS, in
	 *            order. If you have a mix of primitive and aggregate engines,
	 *            then please create the AnalysisEngines yourself and call the
	 *            other runPipeline method.
	 */
	public static void runPipeline(CollectionReader reader, AnalysisEngineDescription... descs) throws UIMAException,
			IOException {
		AnalysisEngine[] engines = new AnalysisEngine[descs.length];
		for (int i = 0; i < engines.length; ++i) {
			if (descs[i].isPrimitive()) {
				engines[i] = AnalysisEngineFactory.createPrimitive(descs[i]);
			}
			else {
				engines[i] = AnalysisEngineFactory.createAggregate(descs[i]);
			}
		}
		runPipeline(reader, engines);
	}

	public static void runPipeline(CollectionReader reader, AnalysisEngine... engines) throws UIMAException,
			IOException {
		for (JCas jCas : new JCasIterable(reader, engines)) {
			assert jCas != null;
		}
		for (AnalysisEngine engine : engines) {
			engine.collectionProcessComplete();
		}
		reader.close();
	}

}
