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
package org.uimafit.util;

import org.junit.Test;
import org.uimafit.testing.util.HideOutput;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class HideOutputTest {

	@Test
	public void testHideOutput() {
		HideOutput ho = new HideOutput();
		System.out.println("you should not see this!  Please consider this a test failure!  Message brought to you by org.uimafit.util.HideOutputTest.testHideOutput()");
		ho.restoreOutput();
		System.out.println("you should see this.  Message brought to you by org.uimafit.util.HideOutputTest.testHideOutput()");
		
		
	}
}
