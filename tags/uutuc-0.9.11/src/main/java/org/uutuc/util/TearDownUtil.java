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

import java.io.File;

/**
 * @author Steven Bethard, Philip Ogren
 */

public class TearDownUtil {

	/**
	 * Empty the files in the directory, and delete the directory itself.
	 * 
	 * @param dir
	 *            The directory to be deleted
	 */
	public static void removeDirectory(File dir) {
		if (dir.exists()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) removeDirectory(file);
				file.delete();
			}
			dir.delete();
		}
	}

	/**
	 * Empty the files in the directory, but do not delete the directory itself.
	 * 
	 * @param dir
	 *            The directory to be emptied
	 */
	public static void emptyDirectory(File dir) {
		if (dir.exists()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory() && !file.getName().equals(".svn")) {
					emptyDirectory(file);
				}
				file.delete();
			}
		}
	}

}
