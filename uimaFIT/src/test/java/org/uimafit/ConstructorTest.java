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
package org.uimafit;

import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.AnnotationFactory;
import org.uimafit.factory.CapabilityFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;
import org.uimafit.factory.SofaMappingFactory;
import org.uimafit.factory.TokenFactory;
import org.uimafit.factory.TypePrioritiesFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.factory.UimaContextFactory;
import org.uimafit.util.AnnotationRetrieval;
import org.uimafit.util.DisableLogging;
import org.uimafit.util.SimplePipeline;
import org.uimafit.util.SingleFileXReader;
import org.uimafit.util.TearDownUtil;
import org.uimafit.util.Util;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class ConstructorTest {

	/**
	 * This is a rather silly test added simply to improve code coverage of 
	 * the factory classes.  
	 */
	@Test
	public void testConstructors() {
		new AnalysisEngineFactory();
		new AnnotationFactory();
		new CapabilityFactory();
		new CollectionReaderFactory();
		new ConfigurationParameterFactory();
		new JCasFactory();
		new ResourceCreationSpecifierFactory();
		new TypeSystemDescriptionFactory();
		new UimaContextFactory();
		new TearDownUtil();
		new TypePrioritiesFactory();
		new TokenFactory();
		new AnnotationRetrieval();
		new DisableLogging();
		new TearDownUtil();
		new Util();
		new SimplePipeline();
		new SingleFileXReader();
		new SofaMappingFactory();
	}
}
