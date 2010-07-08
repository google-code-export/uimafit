/* 
 Copyright 2009-2010	Regents of the University of Colorado.  
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

package org.uimafit.testing.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * This class provides a way to hide system output which is sometimes desirable when
 * running a suite of unit tests which call methods that might contain sysout calls.
 * This class is a hack and there may be better ways of doing this.  Please advise if you
 * know better!
 * 
 * @author Steven Bethard, Philip Ogren
 */
public class HideOutput extends OutputStream {
	protected PrintStream out;

	protected PrintStream err;

	public HideOutput() {
		this.out = System.out;
		this.err = System.err;
		System.setOut(new PrintStream(this));
		System.setErr(new PrintStream(this));
	}

	public void restoreOutput() {
		System.setOut(this.out);
		System.setErr(this.err);
	}

	@Override
	public void write(int b) throws IOException {
	}
	
}
