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

package org.uimafit.descriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Philip Ogren
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TypeCapability {

	/**
	 * inputs can be type names or feature names. A feature name typically looks
	 * like a type name followed by a colon (':') followed by the feature name.  A valid feature name from
	 * the uimaFIT test type system is "org.uimafit.type.Token:pos"
	 * 
	 */
	String[] inputs() default NO_DEFAULT_VALUE;

	String[] outputs() default NO_DEFAULT_VALUE;

	public static final String NO_DEFAULT_VALUE = "org.uimafit.descriptor.TypeCapability.NO_DEFAULT_VALUE";

}
