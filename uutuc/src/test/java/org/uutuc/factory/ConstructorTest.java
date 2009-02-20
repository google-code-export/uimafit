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
package org.uutuc.factory;

import org.junit.Test;
import org.uutuc.util.TearDownUtil;
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
		new CollectionReaderFactory();
		new JCasFactory();
		new ResourceCreationSpecifierFactory();
		new TypeSystemDescriptionFactory();
		new UimaContextFactory();
		new TearDownUtil();
	}
}
