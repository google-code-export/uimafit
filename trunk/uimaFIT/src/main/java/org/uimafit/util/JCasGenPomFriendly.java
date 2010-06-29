/*
 Copyright 2010 Regents of the University of Colorado.
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

import org.apache.uima.tools.jcasgen.Jg;
import org.apache.uima.tools.jcasgen.LogThrowErrorImpl;

/**
 * This class provides a main method that is identical to the main method in
 * org.apache.uima.tools.jcasgen.Jg except that it does not call System.exit.
 * This makes it much more friendly to use with a pom.xml file. See, for
 * example, the pom.xml file in this project for an example usage.
 * 
 * @author Philip Ogren
 */

public class JCasGenPomFriendly {

	public static void main(String[] args) {
		(new Jg()).main0(args, null, null, new LogThrowErrorImpl());
	}

}
