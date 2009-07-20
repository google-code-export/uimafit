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
package org.uutuc.descriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks an analysis component member variable as a
 * configuration parameter. The types of member variables that can use this
 * annotation are:
 * <ul>
 * <li>String</li>
 * <li>String[]</li>
 * <li>Boolean</li>
 * <li>boolean</li>
 * <li>Boolean[]</li>
 * <li>boolean[]</li>
 * <li>Integer</li>
 * <li>int</li>
 * <li>Integer[]</li>
 * <li>int[]</li>
 * <li>Float</li>
 * <li>float</li>
 * <li>Float[]</li>
 * <li>float[]</li>
 * </ul>
 * 
 * Fields marked with this annotation should be declared public or have a setter
 * method. The setter method should be named according to setter naming
 * convention - e.g. a field named "myConfigurationParameter" should have a
 * corresponding setter named "setMyConfigurationParameter" with a single
 * parameter whose type is the same as the type of the field (i.e. you can not
 * mix and match the object or primitive types here).
 * 
 * @author Philip Ogren
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigurationParameter {

	String name();

	String description() default "";

	boolean mandatory() default false;

	/**
	 * What can be the value should correspond with the type of the field that
	 * is annotated. If for example, the field is a String, then the default
	 * value can be any string - e.g. "asdf". If the field is a boolean, then
	 * the default value can be "true" for true or any other string for false.
	 * If the field is an integer, then the default value can be any string that
	 * Integer.parseInt() can successfully parse. Remember that just because the
	 * default value is a string here that you should give an actual integer
	 * (not an integer parseable string) value when setting the parameter via
	 * e.g. AnalysisEngineFactory.createPrimitiveDescription(). If the field is
	 * an float, then the default value can be any string that
	 * Float.parseFloat() can successfully parse. Remember that just because the
	 * default value is a string here that you should give an actual float value
	 * (not a float parseable string) when setting the parameter via e.g.
	 * AnalysisEngineFactory.createPrimitiveDescription().
	 * <p>
	 * If the field is multiValued, then the value should look something like
	 * this '{"value1", "value2"}' 
	 * <p>
	 * If you want a field to be initialized with a null value, then do not specify a default value or specify the value given by the field {@link #NO_DEFAULT_VALUE}
	 */
	String[] defaultValue() default NO_DEFAULT_VALUE;

	public static final String TYPE_STRING = org.apache.uima.resource.metadata.ConfigurationParameter.TYPE_STRING;

	public static final String TYPE_BOOLEAN = org.apache.uima.resource.metadata.ConfigurationParameter.TYPE_BOOLEAN;

	public static final String TYPE_INTEGER = org.apache.uima.resource.metadata.ConfigurationParameter.TYPE_INTEGER;

	public static final String TYPE_FLOAT = org.apache.uima.resource.metadata.ConfigurationParameter.TYPE_FLOAT;

	public static final String NO_DEFAULT_VALUE = "org.uutuc.descriptor.ConfigurationParameter.NO_DEFAULT_VALUE";

}
