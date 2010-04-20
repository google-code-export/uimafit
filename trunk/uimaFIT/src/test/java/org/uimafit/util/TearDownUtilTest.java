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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.uimafit.util.TearDownUtil;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class TearDownUtilTest {

	File removeDir;
	File emptyDir;
	
	@Before
	public void setUp() {
		removeDir = new File("test/data/teardown/remove");
		emptyDir = new File("test/data/teardown/empty");
		emptyDir.mkdirs();
	}
	
	@Test
	public void testRemoveDirectory() throws FileNotFoundException {
		File subDir = new File(removeDir, "subdir");
		subDir.mkdirs();
		PrintStream out = new PrintStream(new File(removeDir, "test.txt"));
		out.println("some text goes here");
		out.close();
		
		out = new PrintStream(new File(subDir, "test2.txt"));
		out.println("2 two too to tu tutu.");
		out.close();
	
		TearDownUtil.removeDirectory(removeDir);
		assertFalse(subDir.exists());
		assertFalse(removeDir.exists());
		
		TearDownUtil.removeDirectory(new File("test/data"));
	}
	
	@Test
	public void testEmptyDirectory() throws FileNotFoundException{
		File subDir = new File(emptyDir, "subdir");
		subDir.mkdir();
		PrintStream out = new PrintStream(new File(emptyDir, "test3.txt"));
		out.println("three 3 thwee free flee!");
		out.close();
		
		out = new PrintStream(new File(subDir, "test4.txt"));
		out.println("4 for four fore foar.");
		out.close();
	
		TearDownUtil.emptyDirectory(emptyDir);
		assertFalse(subDir.exists());
		assertTrue(emptyDir.exists());
		assertEquals(0, emptyDir.list().length);
		
		TearDownUtil.removeDirectory(emptyDir);
		assertFalse(emptyDir.exists());

		TearDownUtil.removeDirectory(new File("test/data"));

	}
}
