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

import static org.junit.Assert.assertNotNull;

import java.awt.Point;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.junit.Test;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class ResourceCreationSpecifierFactoryTest {


	@Test
	public void test2() throws UIMAException, IOException {
		IllegalArgumentException iae = null;
		try {
			ResourceCreationSpecifierFactory.createResourceCreationSpecifier("src/main/java/org/uutuc/util/JCasAnnotatorAdapter.xml", new String[] {"test"});
		} catch(IllegalArgumentException e) {
			iae = e;
		}
		assertNotNull(iae);
		
		iae = null;
		try {
			UimaContextFactory.createUimaContext("test");
		}catch(IllegalArgumentException e) {
			iae = e;
		}
		assertNotNull(iae);
		
		iae = null;
		try {
			ResourceCreationSpecifierFactory.createResourceCreationSpecifier("src/main/java/org/uutuc/util/JCasAnnotatorAdapter.xml", new Object[] { "test", new Point(0,5)});
		}catch(IllegalArgumentException e) {
			iae = e;
		}
		assertNotNull(iae);
		
	}
}
