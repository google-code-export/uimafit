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
package org.uutuc.factory;

import static org.junit.Assert.assertNotNull;

import java.awt.Point;

import org.apache.uima.UIMA_IllegalArgumentException;
import org.junit.Test;
import org.uutuc.descriptor.ConfigurationParameter;

/**
 * @author Philip Ogren
 */

public class ConfigurationParameterFactoryTest {

	public static final String PARAM_DOUBLE_1 = "org.uutuc.factory.ConfigurationParameterFactoryTest.PARAM_STRING_1";
	@ConfigurationParameter(name = PARAM_DOUBLE_1, mandatory = true, defaultValue="3.1415")
	private Double double1;

	public static final String PARAM_DOUBLE_2 = "org.uutuc.factory.ConfigurationParameterFactoryTest.PARAM_STRING_1";
	@ConfigurationParameter(name = PARAM_DOUBLE_2, mandatory = true, defaultValue="3.3333")
	private Double[] double2;
	private Double[] double3;
	
	
	public Double[] getDouble2() {
		return double2;
	}



	public void setDouble2(Double[] double2) {
		this.double2 = double2;
	}



	public Double[] getDouble3() {
		return double3;
	}



	public void setDouble3(Double[] double3) {
		this.double3 = double3;
	}



	public Double getDouble1() {
		return double1;
	}



	public void setDouble1(Double double1) {
		this.double1 = double1;
	}



	@Test
	public void test1() throws SecurityException, NoSuchFieldException {
		UIMA_IllegalArgumentException uiae = null;
		try {
		ConfigurationParameterFactory.getDefaultValue(ConfigurationParameterFactoryTest.class.getDeclaredField("double1"));
		}catch(UIMA_IllegalArgumentException e) {
			uiae = e;
		}
		assertNotNull(uiae);

		uiae = null;
		try {
		ConfigurationParameterFactory.getDefaultValue(ConfigurationParameterFactoryTest.class.getDeclaredField("double2"));
		}catch(UIMA_IllegalArgumentException e) {
			uiae = e;
		}
		assertNotNull(uiae);

		IllegalArgumentException iae = null;
		try {
		ConfigurationParameterFactory.getDefaultValue(ConfigurationParameterFactoryTest.class.getDeclaredField("double3"));
		}catch(IllegalArgumentException e) {
			iae = e;
		}
		assertNotNull(iae);

	}
	
	@Test
	public void test2() throws SecurityException, NoSuchFieldException {
		IllegalArgumentException iae = null;
		try {
			ConfigurationParameterFactory.createPrimitiveParameter(ConfigurationParameterFactoryTest.class.getDeclaredField("double3"));
		} catch(IllegalArgumentException e) {
			iae = e;
		}
		assertNotNull(iae);
		
		UIMA_IllegalArgumentException uiae = null;
		try {
		ConfigurationParameterFactory.createPrimitiveParameter("point", Point.class, "", true);
		} catch(UIMA_IllegalArgumentException e) {
			uiae = e;
		}
		assertNotNull(uiae);
		
	}
}
