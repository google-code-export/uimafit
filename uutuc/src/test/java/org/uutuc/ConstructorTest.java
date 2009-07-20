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
package org.uutuc;

import org.junit.Test;
import org.uutuc.factory.AnalysisEngineFactory;
import org.uutuc.factory.AnnotationFactory;
import org.uutuc.factory.CapabilityFactory;
import org.uutuc.factory.CollectionReaderFactory;
import org.uutuc.factory.ConfigurationParameterFactory;
import org.uutuc.factory.JCasFactory;
import org.uutuc.factory.ResourceCreationSpecifierFactory;
import org.uutuc.factory.SofaMappingFactory;
import org.uutuc.factory.TokenFactory;
import org.uutuc.factory.TypePrioritiesFactory;
import org.uutuc.factory.TypeSystemDescriptionFactory;
import org.uutuc.factory.UimaContextFactory;
import org.uutuc.util.AnnotationRetrieval;
import org.uutuc.util.DisableLogging;
import org.uutuc.util.SimplePipeline;
import org.uutuc.util.SingleFileXReader;
import org.uutuc.util.TearDownUtil;
import org.uutuc.util.Util;
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
