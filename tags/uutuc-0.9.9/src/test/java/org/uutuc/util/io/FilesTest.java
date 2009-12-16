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

package org.uutuc.util.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uutuc.util.TearDownUtil;

/**
 * @author Philip Ogren
 */

public class FilesTest {

	@Test
	public void testSingleFile() {
		Iterable<File> files = Files.getFiles("src/test/resources/data/html/1.html");
		Iterator<File> filesIterator = files.iterator();
		assertTrue(filesIterator.hasNext());
		File file = filesIterator.next();
		assertEquals("1.html", file.getName());
	}
	
	@Test
	public void testNames() {
		Set<String> fileNames = new HashSet<String>();
		fileNames.add("2.1.html");
		fileNames.add("4.1.1.html");
		fileNames.add("X.html");
		
		Set<String> retrievedFileNames = new HashSet<String>();

		Iterable<File> files = Files.getFiles(new File("src/test/resources/data/html"), fileNames);
		for(File file : files) {
			retrievedFileNames.add(file.getName());
		}
		
		assertEquals(2, retrievedFileNames.size());
		assertTrue(retrievedFileNames.contains("2.1.html"));
		assertTrue(retrievedFileNames.contains("4.1.1.html"));
	}
	
	@Test
	public void testPatternFilter() {
		String[] patterns = {"[.]txt", "^abc[.]def$"};
		Set<String> expected = new HashSet<String>();
		expected.add("abc.def");
		expected.add("abc.txt");
		expected.add("abc.txt.def");
		
		
		FileFilter filter = Files.createPatternFilter(patterns);
		Set<String> actual = new HashSet<String>();
		for (File file: Files.getFiles(OUTPUT_DIR, filter)) {
			actual.add(file.getName());
		}
		assertEquals(expected, actual);
	}
	
	@Before
	public void setUp() throws IOException {
		if (!OUTPUT_DIR.exists()) {
			OUTPUT_DIR.mkdirs();
		}
		new File(OUTPUT_DIR, "txt").createNewFile();
		new File(OUTPUT_DIR, "abc.def").createNewFile();
		new File(OUTPUT_DIR, "abc.txt").createNewFile();
		new File(OUTPUT_DIR, "abc.def.ghi").createNewFile();
		new File(OUTPUT_DIR, "abc.txt.def").createNewFile();
	}
	
	@After
	public void tearDown() {
		TearDownUtil.removeDirectory(OUTPUT_DIR);
	}
	
	protected static final File OUTPUT_DIR = new File("src/test/resources/data/html/files");
}
