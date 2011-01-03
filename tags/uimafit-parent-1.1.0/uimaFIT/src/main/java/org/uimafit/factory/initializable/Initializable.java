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

package org.uimafit.factory.initializable;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * <br>
 * Copyright (c) 2010, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * @author Philip Ogren
 * 
 *         This interface provides a way of initializing a class with a
 *         UimaContext. The standard use case of this interface involves
 *         situations in which a member variable is declared with an interface
 *         type and the actual class that is used is decided at runtime. When
 *         the member variable is instantiated, it is useful to provide it a
 *         UimaContext so that it can initialize itself based on configuration
 *         parameters.
 *         <p>
 *         As an example, consider the component
 *         org.uimafit.component.xwriter.XWriter which has a member variable of
 *         type XWriterFileNamer which is an interface that provides a way for
 *         XWriter to come up with a name each file that is generated given a
 *         JCas. The default XWriterFileNamer that is provided,
 *         IntegerFileNamer, provides a single configuration parameter for
 *         specifying a prefix to each file name that is generated. This
 *         parameter is initialized in the IntegerFileNamer.initialize() method.
 *         This initialize method is called because XWriter instantiates its
 *         XWriterFileNamer member variable using a InitializableFactory.create
 *         method. Therefore, when XWriter is instantiated it should be given a
 *         value for the configuration parameter IntegerFileNamer.PARAM_PREFIX.
 *         See the unit tests for XWriter for a complete code example. Note that
 *         the implementation of XWriterFileNamer does not have to implement
 *         Initializable if it has no need for the initialize method.
 */

public interface Initializable {

	/**
	 * This method will be called automatically if the implementing class is instantiated with InitializableFactory.
	 * @param context
	 * @throws ResourceInitializationException
	 */
	public void initialize(UimaContext context) throws ResourceInitializationException;

}
