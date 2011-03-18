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

package org.uimafit.factory;

import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.Constants;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.impl.CollectionReaderDescription_impl;
import org.apache.uima.resource.ExternalResourceDependency;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.Capability;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.FsIndexCollection;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.uimafit.component.initialize.ExternalResourceInitializer;
import org.uimafit.factory.ConfigurationParameterFactory.ConfigurationData;

/**
 * @author Steven Bethard, Philip Ogren
 */
public final class CollectionReaderFactory {

	private CollectionReaderFactory() {
		// This class is not meant to be instantiated
	}

	/**
	 * Create a CollectionReader from an XML descriptor file and a set of configuration parameters.
	 *
	 * @param descriptorPath
	 *            The path to the XML descriptor file.
	 * @param configurationData
	 *            Any additional configuration parameters to be set. These should be supplied as
	 *            (name, value) pairs, so there should always be an even number of parameters.
	 * @return The CollectionReader created from the XML descriptor and the configuration
	 *         parameters.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static CollectionReader createCollectionReaderFromPath(String descriptorPath,
			Object... configurationData) throws UIMAException, IOException {
		ResourceCreationSpecifier specifier = ResourceCreationSpecifierFactory
				.createResourceCreationSpecifier(descriptorPath, configurationData);
		return UIMAFramework.produceCollectionReader(specifier);
	}

	/**
	 * Get a CollectionReader from the name (Java-style, dotted) of an XML descriptor file, and a
	 * set of configuration parameters.
	 *
	 * @param descriptorName
	 *            The fully qualified, Java-style, dotted name of the XML descriptor file.
	 * @param configurationData
	 *            Any additional configuration parameters to be set. These should be supplied as
	 *            (name, value) pairs, so there should always be an even number of parameters.
	 * @return The AnalysisEngine created from the XML descriptor and the configuration parameters.
	 * @throws UIMAException
	 * @throws IOException
	 */

	public static CollectionReader createCollectionReader(String descriptorName,
			Object... configurationData) throws UIMAException, IOException {
		Import_impl imp = new Import_impl();
		imp.setName(descriptorName);
		URL url = imp.findAbsoluteUrl(UIMAFramework.newDefaultResourceManager());
		ResourceSpecifier specifier = ResourceCreationSpecifierFactory
				.createResourceCreationSpecifier(url, configurationData);
		return UIMAFramework.produceCollectionReader(specifier);
	}

	/**
	 * Get a CollectionReader from a CollectionReader class, a type system, and a set of
	 * configuration parameters. The type system is detected automatically using
	 * {@link TypeSystemDescriptionFactory#createTypeSystemDescription()}.
	 *
	 * @param readerClass
	 *            The class of the CollectionReader to be created.
	 * @param configurationData
	 *            Any additional configuration parameters to be set. These should be supplied as
	 *            (name, value) pairs, so there should always be an even number of parameters.
	 * @return The CollectionReader created and initialized with the type system and configuration
	 *         parameters.
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(
			Class<? extends CollectionReader> readerClass, Object... configurationData)
			throws ResourceInitializationException {
		TypeSystemDescription tsd = createTypeSystemDescription();
		return createCollectionReader(readerClass, tsd, (TypePriorities) null, configurationData);
	}

	/**
	 * Get a CollectionReader from a CollectionReader class, a type system, and a set of
	 * configuration parameters.
	 *
	 * @param readerClass
	 *            The class of the CollectionReader to be created.
	 * @param typeSystem
	 *            A description of the types used by the CollectionReader (may be null).
	 * @param configurationData
	 *            Any additional configuration parameters to be set. These should be supplied as
	 *            (name, value) pairs, so there should always be an even number of parameters.
	 * @return The CollectionReader created and initialized with the type system and configuration
	 *         parameters.
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			Object... configurationData) throws ResourceInitializationException {
		return createCollectionReader(readerClass, typeSystem, (TypePriorities) null,
				configurationData);
	}

	/**
	 * @param readerClass
	 * @param typeSystem
	 * @param prioritizedTypeNames
	 * @param configurationData
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			String[] prioritizedTypeNames, Object... configurationData)
			throws ResourceInitializationException {
		TypePriorities typePriorities = TypePrioritiesFactory
				.createTypePriorities(prioritizedTypeNames);
		return createCollectionReader(readerClass, typeSystem, typePriorities, configurationData);

	}

	/**
	 * @param readerClass
	 * @param typeSystem
	 * @param typePriorities
	 * @param configurationData
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, Object... configurationData)
			throws ResourceInitializationException {
		CollectionReaderDescription desc = createDescription(readerClass, typeSystem,
				typePriorities, configurationData);
		return createCollectionReader(desc);
	}

	/**
	 * This method creates a CollectionReader from a CollectionReaderDescription adding additional
	 * configuration parameter data as desired
	 *
	 * @param desc
	 * @param configurationData
	 *            configuration parameter data as name value pairs. Will override values already set
	 *            in the description.
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(CollectionReaderDescription desc,
			Object... configurationData) throws ResourceInitializationException {
		// create the CollectionReader
		CollectionReader reader;
		try {
			reader = (CollectionReader) (Class.forName(desc.getImplementationName()).newInstance());
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}

		if (configurationData != null) {
			ConfigurationData cdata = ConfigurationParameterFactory
					.createConfigurationData(configurationData);
			ConfigurationParameter[] configurationParameters = cdata.configurationParameters;
			Object[] configurationValues = cdata.configurationValues;
			ResourceCreationSpecifierFactory.setConfigurationParameters(desc,
					configurationParameters, configurationValues);
			ResourceMetaData metaData = desc.getMetaData();
			ConfigurationParameterSettings paramSettings = metaData
					.getConfigurationParameterSettings();
			Map<String, Object> additionalParameters = new HashMap<String, Object>();
			additionalParameters.put(AnalysisEngine.PARAM_CONFIG_PARAM_SETTINGS, paramSettings);
			reader.initialize(desc, additionalParameters);
		}
		else {
			reader.initialize(desc, null);
		}
		return reader;

	}

	/**
	 * A simple factory method for creating a CollectionReaderDescription with a given class, type
	 * system description, and configuration data The type system is detected automatically using
	 * {@link TypeSystemDescriptionFactory#createTypeSystemDescription()}.
	 *
	 * @param readerClass
	 * @param configurationData
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(
			Class<? extends CollectionReader> readerClass, Object... configurationData)
			throws ResourceInitializationException {
		TypeSystemDescription tsd = createTypeSystemDescription();
		return createDescription(readerClass, tsd, (TypePriorities) null, configurationData);
	}

	/**
	 * A simple factory method for creating a CollectionReaderDescription with a given class, type
	 * system description, and configuration data
	 *
	 * @param readerClass
	 * @param typeSystem
	 * @param configurationData
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			Object... configurationData) throws ResourceInitializationException {
		return createDescription(readerClass, typeSystem, (TypePriorities) null, configurationData);
	}

	/**
	 * @param readerClass
	 * @param typeSystem
	 * @param prioritizedTypeNames
	 * @param configurationData
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			String[] prioritizedTypeNames, Object... configurationData)
			throws ResourceInitializationException {
		TypePriorities typePriorities = TypePrioritiesFactory
				.createTypePriorities(prioritizedTypeNames);
		return createDescription(readerClass, typeSystem, typePriorities, configurationData);

	}

	/**
	 * @param readerClass
	 * @param typeSystem
	 * @param typePriorities
	 * @param configurationData
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, Object... configurationData)
			throws ResourceInitializationException {
		return createDescription(readerClass, typeSystem, typePriorities, (FsIndexCollection) null,
				(Capability[]) null, configurationData);
	}

	/**
	 * @param readerClass
	 * @param typeSystem
	 * @param typePriorities
	 * @param capabilities
	 * @param configurationData
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, FsIndexCollection indexes, Capability[] capabilities,
			Object... configurationData) throws ResourceInitializationException {
		ConfigurationData cdata = ConfigurationParameterFactory
				.createConfigurationData(configurationData);
		return createDescription(readerClass, typeSystem, typePriorities, indexes, capabilities,
				cdata.configurationParameters, cdata.configurationValues);
	}

	/**
	 * The factory method for creating CollectionReaderDescription objects for a given class,
	 * TypeSystemDescription, TypePriorities, capabilities, and configuration data
	 *
	 * @param readerClass
	 * @param typeSystem
	 * @param typePriorities
	 * @param capabilities
	 * @param configurationParameters
	 * @param configurationValues
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static CollectionReaderDescription createDescription(
			Class<? extends CollectionReader> readerClass, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, FsIndexCollection indexes, Capability[] capabilities,
			ConfigurationParameter[] configurationParameters, Object[] configurationValues)
			throws ResourceInitializationException {
		// create the descriptor and set configuration parameters
		CollectionReaderDescription desc = new CollectionReaderDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setImplementationName(readerClass.getName());

		// Extract external resource dependencies
		Collection<ExternalResourceDependency> deps = ExternalResourceInitializer
				.getResourceDeclarations(readerClass).values();
		desc.setExternalResourceDependencies(deps.toArray(new ExternalResourceDependency[deps
				.size()]));

		ConfigurationData reflectedConfigurationData = ConfigurationParameterFactory
				.createConfigurationData(readerClass);
		ResourceCreationSpecifierFactory.setConfigurationParameters(desc,
				reflectedConfigurationData.configurationParameters,
				reflectedConfigurationData.configurationValues);
		if (configurationParameters != null) {
			ResourceCreationSpecifierFactory.setConfigurationParameters(desc,
					configurationParameters, configurationValues);
		}

		// set the type system
		if (typeSystem != null) {
			desc.getCollectionReaderMetaData().setTypeSystem(typeSystem);
		}

		if (typePriorities != null) {
			desc.getCollectionReaderMetaData().setTypePriorities(typePriorities);
		}

		// set indexes from the argument to this call or from the annotation present in the
		// component if the argument is null
		if (indexes != null) {
			desc.getCollectionReaderMetaData().setFsIndexCollection(indexes);
		}
		else {
			desc.getCollectionReaderMetaData().setFsIndexCollection(
					FsIndexFactory.createFsIndexCollection(readerClass));
		}

		// set capabilities from the argument to this call or from the annotation present in the
		// component if the argument is null
		if (capabilities != null) {
			desc.getCollectionReaderMetaData().setCapabilities(capabilities);
		}
		else {
			Capability capability = CapabilityFactory.createCapability(readerClass);
			if (capability != null) {
				desc.getCollectionReaderMetaData().setCapabilities(new Capability[] { capability });
			}
		}

		return desc;
	}

	/**
	 * Since the configuration parameters of a CollectionReader with the given configuration
	 * parameter data
	 *
	 * @param collectionReaderDescription
	 * @param configurationData
	 * @throws ResourceInitializationException
	 */
	public static void setConfigurationParameters(
			CollectionReaderDescription collectionReaderDescription, Object... configurationData)
			throws ResourceInitializationException {
		ConfigurationData cdata = ConfigurationParameterFactory
				.createConfigurationData(configurationData);
		ResourceCreationSpecifierFactory.setConfigurationParameters(collectionReaderDescription,
				cdata.configurationParameters, cdata.configurationValues);
	}

}
