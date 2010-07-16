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
package org.uimafit.descriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.uimafit.factory.ConfigurationParameterFactory;
 
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

	/**
	 * the default value for name if none is given.  
	 */
	public static final String USE_FIELD_NAME = "org.uimafit.descriptor.ConfigurationParameter.USE_FIELD_NAME";

	/**
	 * If you do not specify a name then the default name will be given by {@link #USE_FIELD_NAME} will be the default name.  This 
	 * tells ConfigurationParameterFactory to use the name of the annotated field as the name of the configuration parameter.  The exact name that is used is determined by the method {@link ConfigurationParameterFactory#getConfigurationParameterName(java.lang.reflect.Field)}
	 */
	String name() default USE_FIELD_NAME;

	/**
	 * A description for the configuration parameter
	 */
	String description() default "";

	/**
	 * specifies whether this configuration parameter is mandatory - i.e. the value must be provided
	 */
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
	 * a float, then the default value can be any string that
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

	/**
	 * Tells the ConfigurationParameterFactory that no default value has been provided
	 */
	public static final String NO_DEFAULT_VALUE = "org.uimafit.descriptor.ConfigurationParameter.NO_DEFAULT_VALUE";

}
