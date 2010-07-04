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

package org.uimafit.factory;

import java.io.IOException;
import java.net.URL;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterDeclarations;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;

/**
 * @author Steven Bethard, Philip Ogren
 */
public final class ResourceCreationSpecifierFactory {
	private ResourceCreationSpecifierFactory()
	{
		// This class is not meant to be instantiated
	}

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
	 * Parse a ResourceCreationSpecifier from XML descriptor file input, setting
	 * additional configuration parameters as necessary.
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
			String message = "a value must be specified for each parameter name: an odd number of values passed in ("
					+ parameters.length + ")";
			throw new IllegalArgumentException(message);
		}

		ResourceCreationSpecifier specifier;
		XMLParser parser = UIMAFramework.getXMLParser();
		specifier = (ResourceCreationSpecifier) parser.parseResourceSpecifier(xmlInput);
		setConfigurationParameters(specifier, parameters);


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
	 * @param configurationData
	 *            The configuration parameters to be set. These should be
	 *            supplied as (name, value) pairs, so there should always be an
	 *            even number of parameters.
	 */
	public static void setConfigurationParameters(ResourceCreationSpecifier specifier,
			Object... configurationData) {
		if (configurationData.length % 2 != 0) {
			String message = "a value must be specified for each parameter name: an odd number of values passed in ("
					+ configurationData.length + ")";
			throw new IllegalArgumentException(message);
		}

		ConfigurationParameter[] configurationParameters = new ConfigurationParameter[configurationData.length / 2];
		Object[] configurationValues = new Object[configurationData.length / 2];

		for (int i = 0; i < configurationValues.length; i++) {
			String name = (String) configurationData[i * 2];
			Object value = configurationData[i * 2 + 1];
			ConfigurationParameter param = ConfigurationParameterFactory.createPrimitiveParameter(name, value
					.getClass(), null, false);
			configurationParameters[i] = param;
			configurationValues[i] = value;
		}
		setConfigurationParameters(specifier, configurationParameters, configurationValues);
	}

	public static void setConfigurationParameters(ResourceCreationSpecifier specifier,
			ConfigurationParameter[] configurationParameters, Object[] configurationValues) {
		setConfigurationParameters(specifier.getMetaData(), configurationParameters, configurationValues);
	}

	public static void setConfigurationParameters(ResourceMetaData metaData,
			ConfigurationParameter[] configurationParameters, Object[] configurationValues) {
		ConfigurationParameterDeclarations paramDecls = metaData.getConfigurationParameterDeclarations();
		ConfigurationParameterSettings paramSettings = metaData.getConfigurationParameterSettings();
		for (int i = 0; i < configurationParameters.length; i++) {
			if (paramDecls != null &&  paramDecls.getConfigurationParameter(null, configurationParameters[i].getName()) == null) {
				paramDecls.addConfigurationParameter(configurationParameters[i]);
			}
			paramSettings.setParameterValue(configurationParameters[i].getName(), configurationValues[i]);
		}
	}

}
