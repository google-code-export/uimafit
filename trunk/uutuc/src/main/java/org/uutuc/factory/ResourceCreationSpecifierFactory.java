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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterDeclarations;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.impl.ConfigurationParameter_impl;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
/**
 * @author Steven Bethard, Philip Ogren
 */
public class ResourceCreationSpecifierFactory {

	
	/**
	 * Parse a ResourceCreationSpecifier from the URL of an XML descriptor file,
	 * setting additional configuration parameters as necessary.
	 * 
	 * @param descriptorURL
	 *            The URL of the XML descriptor file.
	 * @param parameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The ResourceCreationSpecifier for the XML descriptor with all the
	 *         configuration parameters set.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static ResourceCreationSpecifier createResourceCreationSpecifier(URL descriptorURL, Object[] parameters)
	throws UIMAException, IOException {
		return createResourceCreationSpecifier(new XMLInputSource(descriptorURL), parameters);
	}

	/**
	 * Parse a ResourceCreationSpecifier from XML descriptor file input,
	 * setting additional configuration parameters as necessary.
	 * 
	 * @param xmlInput
	 *            The descriptor file as an XMLInputSource.
	 * @param parameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The ResourceCreationSpecifier for the XML descriptor with all the
	 *         configuration parameters set.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static ResourceCreationSpecifier createResourceCreationSpecifier(XMLInputSource xmlInput, Object[] parameters)
	throws UIMAException, IOException {
		if (parameters.length % 2 != 0) {
			String message = "a value must be specified for each parameter name: an odd number of values passed in ("+parameters.length+")";
			throw new IllegalArgumentException(message);
		}

		ResourceCreationSpecifier specifier;
		XMLParser parser = UIMAFramework.getXMLParser();
		specifier = (ResourceCreationSpecifier) parser.parseResourceSpecifier(xmlInput);
		
		ResourceMetaData metaData = specifier.getMetaData();
		ConfigurationParameterSettings settings = metaData.getConfigurationParameterSettings();
		setConfigurationParameters(settings, null, parameters);
//		for (int i = 0; i < parameters.length; i += 2) {
//			settings.setParameterValue((String) parameters[i], parameters[i + 1]);
//		}
		return specifier;

	}

	/**
	 * Parse a ResourceCreationSpecifier from an XML descriptor file, setting
	 * additional configuration parameters as necessary.
	 * 
	 * @param descriptorPath
	 *            The path to the XML descriptor file.
	 * @param parameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The ResourceCreationSpecifier for the XML descriptor with all the
	 *         configuration parameters set.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static ResourceCreationSpecifier createResourceCreationSpecifier(String descriptorPath, Object[] parameters)
	throws UIMAException, IOException {
		return createResourceCreationSpecifier(new XMLInputSource(descriptorPath), parameters);
	}

	/**
	 * Create configuration parameter declarations and settings from a list of
	 * (name, value) pairs.
	 * 
	 * @param specifier
	 *            The ResourceCreationSpecifier whose parameters are to be set.
	 * @param configurationParameters
	 *            The configuration parameters to be set. These should be
	 *            supplied as (name, value) pairs, so there should always be an
	 *            even number of parameters.
	 */
	public static void setConfigurationParameters(ResourceCreationSpecifier specifier,
			Object... configurationParameters) {
		if (configurationParameters.length % 2 != 0) {
			String message = "a value must be specified for each parameter name: an odd number of values passed in ("+configurationParameters.length+")";
			throw new IllegalArgumentException(message);
		}
		ResourceMetaData metaData = specifier.getMetaData();
		ConfigurationParameterDeclarations paramDecls = metaData.getConfigurationParameterDeclarations();
		ConfigurationParameterSettings paramSettings = metaData.getConfigurationParameterSettings();
		setConfigurationParameters(paramSettings, paramDecls, configurationParameters);
	}
	
	public static void setConfigurationParameters(ConfigurationParameterSettings paramSettings, ConfigurationParameterDeclarations paramDecls,
			Object... configurationParameters) {
		
		for (int i = 0; i < configurationParameters.length; i += 2) {
			String name = (String) configurationParameters[i];
			Object value = configurationParameters[i + 1];

			Class<?> javaClass = value.getClass();
			String javaClassName;
			if (javaClass.isArray()) javaClassName = javaClass.getComponentType().getName();
			else javaClassName = javaClass.getName();

			String uimaType = javaUimaTypeMap.get(javaClassName);
			if (uimaType == null) {
				String message = "invalid parameter type " + javaClassName;
				throw new IllegalArgumentException(message);
			}
			ConfigurationParameter param = new ConfigurationParameter_impl();
			param.setName(name);
			param.setType(uimaType);
			param.setMultiValued(javaClass.isArray());
			if(paramDecls != null)
				paramDecls.addConfigurationParameter(param);
			paramSettings.setParameterValue(name, value);
		}

	}

	/**
	 * A mapping from Java class names to UIMA configuration parameter type
	 * names. Used by setConfigurationParameters().
	 */
	public static final Map<String, String> javaUimaTypeMap = new HashMap<String, String>();
	static {
		javaUimaTypeMap.put(Boolean.class.getName(), ConfigurationParameter.TYPE_BOOLEAN);
		javaUimaTypeMap.put(Float.class.getName(), ConfigurationParameter.TYPE_FLOAT);
		javaUimaTypeMap.put(Integer.class.getName(), ConfigurationParameter.TYPE_INTEGER);
		javaUimaTypeMap.put(String.class.getName(), ConfigurationParameter.TYPE_STRING);
	};

}
