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
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.junit.Test;
import org.uutuc.factory.AnalysisEngineFactory;
import org.uutuc.factory.CollectionReaderFactory;
import org.uutuc.factory.testAes.Annotator1;
import org.uutuc.factory.testAes.Annotator2;
import org.uutuc.factory.testAes.Annotator3;

/**
 * @author Philip Ogren
 */

public class SimplePipelineTest {

	@Test
	public void test1() throws UIMAException, IOException {
		CollectionReader cr = CollectionReaderFactory.createCollectionReader(SingleFileXReader.class, 
				Util.TYPE_SYSTEM_DESCRIPTION, SingleFileXReader.PARAM_FILE_NAME, "test/data/docs/test.xmi",
				SingleFileXReader.PARAM_XML_SCHEME, SingleFileXReader.XMI);
		AnalysisEngineDescription aed1 = AnalysisEngineFactory.createPrimitiveDescription(Annotator1.class, Util.TYPE_SYSTEM_DESCRIPTION);
		AnalysisEngineDescription aed2 = AnalysisEngineFactory.createPrimitiveDescription(Annotator2.class, Util.TYPE_SYSTEM_DESCRIPTION);
		AnalysisEngineDescription aed3 = AnalysisEngineFactory.createPrimitiveDescription(Annotator3.class, Util.TYPE_SYSTEM_DESCRIPTION);
		SimplePipeline.runPipeline(cr, aed1, aed2, aed3);
		
	}
}
