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
package org.uutuc.factory.testAes;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.descriptor.ConfigurationParameter;
import org.uutuc.descriptor.SofaCapability;
import org.uutuc.util.InitializeUtil;

/**
 * @author Philip Ogren
 */

@SofaCapability(inputSofas= {CAS.NAME_DEFAULT_SOFA, "MyInputSofa"}, outputSofas="MyOutputSofa")
public class ParameterizedAE extends JCasAnnotator_ImplBase {

	public static final String PARAM_STRING_1 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_STRING_1";
	@ConfigurationParameter(name = PARAM_STRING_1, mandatory = true, defaultValue = "pineapple")
	private String string1;

	public static final String PARAM_STRING_2 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_STRING_2";
	@ConfigurationParameter(name = PARAM_STRING_2, mandatory = false, defaultValue = { "coconut", "mango" })
	private String[] string2;

	public static final String PARAM_STRING_3 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_STRING_3";
	@ConfigurationParameter(name = PARAM_STRING_3)
	private String string3;

	public static final String PARAM_STRING_4 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_STRING_4";
	@ConfigurationParameter(name = PARAM_STRING_4, mandatory = true, defaultValue = "apple")
	private String[] string4;

	public static final String PARAM_STRING_5 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_STRING_5";
	@ConfigurationParameter(name = PARAM_STRING_5, mandatory = false, defaultValue="")
	private String[] string5;

	public static final String PARAM_BOOLEAN_1 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_BOOLEAN_1";
	@ConfigurationParameter(name = PARAM_BOOLEAN_1, mandatory = true, defaultValue = "false")
	private boolean boolean1;

	public static final String PARAM_BOOLEAN_2 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_BOOLEAN_2";
	@ConfigurationParameter(name = PARAM_BOOLEAN_2)
	private Boolean boolean2;

	public static final String PARAM_BOOLEAN_3 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_BOOLEAN_3";
	@ConfigurationParameter(name = PARAM_BOOLEAN_3, mandatory = true, defaultValue = { "true", "true", "false" })
	private Boolean[] boolean3;

	public static final String PARAM_BOOLEAN_4 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_BOOLEAN_4";
	@ConfigurationParameter(name = PARAM_BOOLEAN_4, mandatory = true, defaultValue = { "true", "false", "true" })
	public boolean[] boolean4;

	public static final String PARAM_BOOLEAN_5 = "org.uutuc.factory.testAes.ParameterizedAE.PARAM_BOOLEAN_5";
	@ConfigurationParameter(name = PARAM_BOOLEAN_5, mandatory = true, defaultValue="false")
	private boolean[] boolean5;

	public static final String PARAM_INT_1= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_INT_1";
	@ConfigurationParameter(name = PARAM_INT_1, mandatory = true, defaultValue="0")
	private int int1;
	
	public static final String PARAM_INT_2= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_INT_2";
	@ConfigurationParameter(name = PARAM_INT_2, defaultValue="42")
	private int int2;
	
	public static final String PARAM_INT_3= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_INT_3";
	@ConfigurationParameter(name = PARAM_INT_3, defaultValue= {"42","111"})
	private int[] int3;

	public static final String PARAM_INT_4= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_INT_4";
	@ConfigurationParameter(name = PARAM_INT_4, defaultValue= "2", mandatory = true)
	private Integer[] int4;


	public static final String PARAM_FLOAT_1= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_FLOAT_1";
	@ConfigurationParameter(name = PARAM_FLOAT_1, mandatory = true, defaultValue="0.0f")
	private float float1;

	public static final String PARAM_FLOAT_2= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_FLOAT_2";
	@ConfigurationParameter(name = PARAM_FLOAT_2, mandatory = false, defaultValue="3.1415f")
	private float float2;

	public static final String PARAM_FLOAT_3= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_FLOAT_3";
	@ConfigurationParameter(name = PARAM_FLOAT_3, mandatory = true)
	private float float3;

	public static final String PARAM_FLOAT_4= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_FLOAT_4";
	@ConfigurationParameter(name = PARAM_FLOAT_4, mandatory = false)
	private float[] float4;

	public static final String PARAM_FLOAT_5= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_FLOAT_5";
	@ConfigurationParameter(name = PARAM_FLOAT_5, mandatory = false, defaultValue= {"0.0f", "3.1415f", "2.7182818f"})
	private float[] float5;

	public static final String PARAM_FLOAT_6= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_FLOAT_6";
		@ConfigurationParameter(name = PARAM_FLOAT_6, mandatory = true)
	private Float[] float6;

	public static final String PARAM_FLOAT_7= "org.uutuc.factory.testAes.ParameterizedAE.PARAM_FLOAT_7";
	@ConfigurationParameter(name = PARAM_FLOAT_7, mandatory = true, defaultValue= {"1.1111f", "2.2222f", "3.333f"})
	private Float[] float7;

	public float[] getFloat4() {
		return float4;
	}

	public void setFloat4(float[] float4) {
		this.float4 = float4;
	}

	public float[] getFloat5() {
		return float5;
	}

	public void setFloat5(float[] float5) {
		this.float5 = float5;
	}

	public Float[] getFloat6() {
		return float6;
	}

	public void setFloat6(Float[] float6) {
		this.float6 = float6;
	}

	public Float[] getFloat7() {
		return float7;
	}

	public void setFloat7(Float[] float7) {
		this.float7 = float7;
	}


	public Integer[] getInt4() {
		return int4;
	}

	public float getFloat1() {
		return float1;
	}

	public void setFloat1(float float1) {
		this.float1 = float1;
	}

	public float getFloat2() {
		return float2;
	}

	public void setFloat2(float float2) {
		this.float2 = float2;
	}

	public float getFloat3() {
		return float3;
	}

	public void setFloat3(float float3) {
		this.float3 = float3;
	}

	public void setInt4(Integer[] int4) {
		this.int4 = int4;
	}

	public int[] getInt3() {
		return int3;
	}

	public void setInt3(int[] int3) {
		this.int3 = int3;
	}

	public int getInt2() {
		return int2;
	}

	public void setInt2(int int2) {
		this.int2 = int2;
	}

	public int getInt1() {
		return int1;
	}

	public void setInt1(int int1) {
		this.int1 = int1;
	}

	public boolean[] getBoolean5() {
		return boolean5;
	}

	public void setBoolean5(boolean[] boolean5) {
		this.boolean5 = boolean5;
	}

	public Boolean[] getBoolean3() {
		return boolean3;
	}

	public void setBoolean3(Boolean[] boolean3) {
		this.boolean3 = boolean3;
	}

	public boolean isBoolean2() {
		return boolean2;
	}

	public void setBoolean2(Boolean boolean2) {
		this.boolean2 = boolean2;
	}

	public boolean isBoolean1() {
		return boolean1;
	}

	public void setBoolean1(boolean boolean1) {
		this.boolean1 = boolean1;
	}

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		InitializeUtil.initialize(this, context);
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub

	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}

	public String[] getString2() {
		return string2;
	}

	public void setString2(String[] string2) {
		this.string2 = string2;
	}

	public String getString3() {
		return string3;
	}

	public void setString3(String string3) {
		this.string3 = string3;
	}

	public String[] getString4() {
		return string4;
	}

	public void setString4(String[] string4) {
		this.string4 = string4;
	}

	public String[] getString5() {
		return string5;
	}

	public void setString5(String[] string5) {
		this.string5 = string5;
	}

}
