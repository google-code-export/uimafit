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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.Constants;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AggregateAnalysisEngine_impl;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.SofaMapping;
import org.apache.uima.analysis_engine.metadata.impl.FixedFlow_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDependency;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.Capability;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.OperationalProperties;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.util.FileUtils;
import org.uimafit.factory.ConfigurationParameterFactory.ConfigurationData;
import org.uimafit.util.ReflectionUtil;

/**
 * 
 * @author Steven Bethard, Philip Ogren
 * 
 */
public class AnalysisEngineFactory {

	/**
	 * Get an AnalysisEngine from the name (Java-style, dotted) of an XML
	 * descriptor file, and a set of configuration parameters.
	 * 
	 * @param descriptorName
	 *            The fully qualified, Java-style, dotted name of the XML
	 *            descriptor file.
	 * @param configurationData
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The AnalysisEngine created from the XML descriptor and the
	 *         configuration parameters.
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static AnalysisEngine createAnalysisEngine(String descriptorName, Object... configurationData)
			throws UIMAException, IOException {
		AnalysisEngineDescription aed = createAnalysisEngineDescription(descriptorName, configurationData);
		return UIMAFramework.produceAnalysisEngine(aed);
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(String descriptorName,
			Object... configurationData) throws UIMAException, IOException {
		Import_impl imprt = new Import_impl();
		imprt.setName(descriptorName);
		URL url = imprt.findAbsoluteUrl(UIMAFramework.newDefaultResourceManager());
		ResourceSpecifier specifier = ResourceCreationSpecifierFactory.createResourceCreationSpecifier(url,
				configurationData);
		return (AnalysisEngineDescription) specifier;
	}

	/**
	 * This method provides a convenient way to instantiate an AnalysisEngine
	 * where the default view is mapped to the view name passed into the method.
	 * 
	 * @param analysisEngineDescription
	 * @param viewName
	 *            the view name to map the default view to
	 * @return an aggregate analysis engine consisting of a single component
	 *         whose default view is mapped to the the view named by viewName.
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine createAnalysisEngine(AnalysisEngineDescription analysisEngineDescription,
			String viewName) throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(analysisEngineDescription, CAS.NAME_DEFAULT_SOFA, viewName);
		return builder.createAggregate();
	}

	/**
	 * Get an AnalysisEngine from an XML descriptor file and a set of
	 * configuration parameters.
	 * 
	 * @param descriptorPath
	 *            The path to the XML descriptor file.
	 * @param configurationData
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The AnalysisEngine created from the XML descriptor and the
	 *         configuration parameters.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngine createAnalysisEngineFromPath(String descriptorPath, Object... configurationData)
			throws UIMAException, IOException {
		ResourceSpecifier specifier;
		specifier = ResourceCreationSpecifierFactory.createResourceCreationSpecifier(descriptorPath, configurationData);
		return UIMAFramework.produceAnalysisEngine(specifier);
	}

	/**
	 * Get an AnalysisEngine from an AnalysisComponent class, a type system and
	 * a set of configuration parameters.
	 * 
	 * @param componentClass
	 *            The class of the AnalysisComponent to be created as an
	 *            AnalysisEngine.
	 * @param typeSystem
	 *            A description of the types used by the AnalysisComponent (may
	 *            be null).
	 * @param configurationData
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The AnalysisEngine created from the AnalysisComponent class and
	 *         initialized with the type system and the configuration
	 *         parameters.
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine createPrimitive(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, Object... configurationData) throws ResourceInitializationException {
		return createPrimitive(componentClass, typeSystem, (TypePriorities) null, configurationData);
	}

	public static AnalysisEngine createPrimitive(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, String[] prioritizedTypeNames, Object... configurationData)
			throws ResourceInitializationException {
		TypePriorities typePriorities = TypePrioritiesFactory.createTypePriorities(prioritizedTypeNames);
		return createPrimitive(componentClass, typeSystem, typePriorities, configurationData);

	}

	public static AnalysisEngineDescription createPrimitiveDescription(
			Class<? extends AnalysisComponent> componentClass, TypeSystemDescription typeSystem,
			Object... configurationData) throws ResourceInitializationException {
		return createPrimitiveDescription(componentClass, typeSystem, (TypePriorities) null, configurationData);
	}

	public static AnalysisEngineDescription createPrimitiveDescription(
			Class<? extends AnalysisComponent> componentClass, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, Object... configurationData) throws ResourceInitializationException {
		return createPrimitiveDescription(componentClass, typeSystem, typePriorities, (Capability[]) null,
				configurationData);
	}

	public static AnalysisEngineDescription createPrimitiveDescription(
			Class<? extends AnalysisComponent> componentClass, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, Capability[] capabilities, Object... configurationData)
			throws ResourceInitializationException {
		ConfigurationData cdata = ConfigurationParameterFactory.createConfigurationData(configurationData);
		return createPrimitiveDescription(componentClass, typeSystem, typePriorities, capabilities,
				cdata.configurationParameters, cdata.configurationValues);
	}

	public static AnalysisEngineDescription createPrimitiveDescription(
			Class<? extends AnalysisComponent> componentClass, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, Capability[] capabilities, ConfigurationParameter[] configurationParameters,
			Object[] configurationValues) throws ResourceInitializationException {

		// create the descriptor and set configuration parameters
		AnalysisEngineDescription desc = new AnalysisEngineDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setPrimitive(true);
		desc.setAnnotatorImplementationName(componentClass.getName());
		org.uimafit.descriptor.AnalysisComponent componentAnno = ReflectionUtil.getInheritableAnnotation(
				org.uimafit.descriptor.AnalysisComponent.class, componentClass);
		if (componentAnno != null) {
			OperationalProperties op = desc.getAnalysisEngineMetaData().getOperationalProperties();
			op.setMultipleDeploymentAllowed(componentAnno.multipleDeploymentAllowed());
			op.setModifiesCas(componentAnno.modifiesCas());
			op.setOutputsNewCASes(componentAnno.outputsNewCases());
		}
		else {
			OperationalProperties op = desc.getAnalysisEngineMetaData().getOperationalProperties();
			op.setMultipleDeploymentAllowed(org.uimafit.descriptor.AnalysisComponent.MULTIPLE_DEPLOYMENT_ALLOWED_DEFAULT);
			op.setModifiesCas(org.uimafit.descriptor.AnalysisComponent.MODIFIES_CAS_DEFAULT);
			op.setOutputsNewCASes(org.uimafit.descriptor.AnalysisComponent.OUTPUTS_NEW_CASES_DEFAULT);
		}

		AnalysisEngineMetaData meta = desc.getAnalysisEngineMetaData();
		meta.setName(componentClass.getName());
		meta.setVendor(componentClass.getPackage().getName());
		meta.setDescription("Descriptor automatically generated by UUTUC");
		meta.setVersion("unknown");

		// Extract external resource dependencies
		Collection<ExternalResourceDependency> deps = ExternalResourceConfigurator.getResourceDeclarations(
				componentClass).values();
		desc.setExternalResourceDependencies(deps.toArray(new ExternalResourceDependency[deps.size()]));

		ConfigurationData reflectedConfigurationData = ConfigurationParameterFactory
				.createConfigurationData(componentClass);
		ResourceCreationSpecifierFactory.setConfigurationParameters(desc,
				reflectedConfigurationData.configurationParameters, reflectedConfigurationData.configurationValues);
		if (configurationParameters != null) {
			ResourceCreationSpecifierFactory.setConfigurationParameters(desc, configurationParameters,
					configurationValues);
		}

		// set the type system
		if (typeSystem != null) {
			desc.getAnalysisEngineMetaData().setTypeSystem(typeSystem);
		}

		if (typePriorities != null) {
			desc.getAnalysisEngineMetaData().setTypePriorities(typePriorities);
		}

		if (capabilities == null) {
			Capability capability = CapabilityFactory.createCapability(componentClass);
			if (capability != null) {
				capabilities = new Capability[] {capability};
			}
		}
		if (capabilities != null) {
			desc.getAnalysisEngineMetaData().setCapabilities(capabilities);
		}
		return desc;
	}

	public static void setConfigurationParameters(AnalysisEngineDescription analysisEngineDescription,
			Object... configurationData) throws ResourceInitializationException {
		ConfigurationData cdata = ConfigurationParameterFactory.createConfigurationData(configurationData);
		ResourceCreationSpecifierFactory.setConfigurationParameters(analysisEngineDescription,
				cdata.configurationParameters, cdata.configurationValues);

	}

	public static AnalysisEngine createPrimitive(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, TypePriorities typePriorities, Object... configurationParameters)
			throws ResourceInitializationException {

		AnalysisEngineDescription desc = createPrimitiveDescription(componentClass, typeSystem, typePriorities,
				configurationParameters);

		// create the AnalysisEngine, initialize it and return it
		return createPrimitive(desc);
	}

	@SuppressWarnings("unchecked")
	public static AnalysisEngine createPrimitive(AnalysisEngineDescription desc, Object... configurationData)
			throws ResourceInitializationException {
		AnalysisEngine engine = new PrimitiveAnalysisEngine_impl();

		if (configurationData != null) {
			ConfigurationData cdata = ConfigurationParameterFactory.createConfigurationData(configurationData);
			ConfigurationParameter[] configurationParameters = cdata.configurationParameters;
			Object[] configurationValues = cdata.configurationValues;
			ResourceCreationSpecifierFactory.setConfigurationParameters(desc, configurationParameters,
					configurationValues);
			ResourceMetaData metaData = desc.getMetaData();
			ConfigurationParameterSettings paramSettings = metaData.getConfigurationParameterSettings();
			Map additionalParameters = new HashMap();
			additionalParameters.put(AnalysisEngine.PARAM_CONFIG_PARAM_SETTINGS, paramSettings);
			engine.initialize(desc, additionalParameters);
		}
		else {
			engine.initialize(desc, null);
		}
		return engine;
	}

	public static AnalysisEngine createAggregate(List<Class<? extends AnalysisComponent>> componentClasses,
			TypeSystemDescription typeSystem, TypePriorities typePriorities, SofaMapping[] sofaMappings,
			Object... configurationParameters) throws ResourceInitializationException {
		AnalysisEngineDescription desc = createAggregateDescription(componentClasses, typeSystem, typePriorities,
				sofaMappings, configurationParameters);
		// create the AnalysisEngine, initialize it and return it
		AnalysisEngine engine = new AggregateAnalysisEngine_impl();
		engine.initialize(desc, null);
		return engine;
	}

	public static AnalysisEngine createAggregate(AnalysisEngineDescription desc) throws ResourceInitializationException {
		// create the AnalysisEngine, initialize it and return it
		AnalysisEngine engine = new AggregateAnalysisEngine_impl();
		engine.initialize(desc, null);
		return engine;
	}

	public static AnalysisEngineDescription createAggregateDescription(
			List<Class<? extends AnalysisComponent>> componentClasses, TypeSystemDescription typeSystem,
			TypePriorities typePriorities, SofaMapping[] sofaMappings, Object... configurationParameters)
			throws ResourceInitializationException {

		List<AnalysisEngineDescription> primitiveEngineDescriptions = new ArrayList<AnalysisEngineDescription>();
		List<String> componentNames = new ArrayList<String>();

		for (Class<? extends AnalysisComponent> componentClass : componentClasses) {
			AnalysisEngineDescription primitiveDescription = createPrimitiveDescription(componentClass, typeSystem,
					typePriorities, configurationParameters);
			primitiveEngineDescriptions.add(primitiveDescription);
			componentNames.add(componentClass.getName());
		}
		return createAggregateDescription(primitiveEngineDescriptions, componentNames, typeSystem, typePriorities,
				sofaMappings);
	}

	public static AnalysisEngine createAggregate(List<AnalysisEngineDescription> analysisEngineDescriptions,
			List<String> componentNames, TypeSystemDescription typeSystem, TypePriorities typePriorities,
			SofaMapping[] sofaMappings) throws ResourceInitializationException {

		AnalysisEngineDescription desc = createAggregateDescription(analysisEngineDescriptions, componentNames,
				typeSystem, typePriorities, sofaMappings);
		// create the AnalysisEngine, initialize it and return it
		AnalysisEngine engine = new AggregateAnalysisEngine_impl();
		engine.initialize(desc, null);
		return engine;

	}

	public static AnalysisEngineDescription createAggregateDescription(
			AnalysisEngineDescription... analysisEngineDescriptions) throws ResourceInitializationException {
		String[] names = new String[analysisEngineDescriptions.length];
		int i = 0;
		for (AnalysisEngineDescription aed : analysisEngineDescriptions) {
			names[i] = aed.getImplementationName() + "-" + i;
			i++;
		}

		return createAggregateDescription(asList(analysisEngineDescriptions), asList(names), null, null, null);
	}

	@SuppressWarnings("unchecked")
	public static AnalysisEngineDescription createAggregateDescription(
			List<AnalysisEngineDescription> analysisEngineDescriptions, List<String> componentNames,
			TypeSystemDescription typeSystem, TypePriorities typePriorities, SofaMapping[] sofaMappings)
			throws ResourceInitializationException {

		// create the descriptor and set configuration parameters
		AnalysisEngineDescription desc = new AnalysisEngineDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setPrimitive(false);

		// if any of the aggregated analysis engines does not allow multiple
		// deployment, then the
		// aggregate engine may also not be multiply deployed
		boolean allowMultipleDeploy = true;
		for (AnalysisEngineDescription d : analysisEngineDescriptions) {
			allowMultipleDeploy &= d.getAnalysisEngineMetaData().getOperationalProperties()
					.isMultipleDeploymentAllowed();
		}
		desc.getAnalysisEngineMetaData().getOperationalProperties().setMultipleDeploymentAllowed(allowMultipleDeploy);

		List<String> flowNames = new ArrayList<String>();

		for (int i = 0; i < analysisEngineDescriptions.size(); i++) {
			AnalysisEngineDescription aed = analysisEngineDescriptions.get(i);
			String componentName = componentNames.get(i);
			desc.getDelegateAnalysisEngineSpecifiersWithImports().put(componentName, aed);
			flowNames.add(componentName);
		}

		FixedFlow fixedFlow = new FixedFlow_impl();
		fixedFlow.setFixedFlow(flowNames.toArray(new String[flowNames.size()]));
		desc.getAnalysisEngineMetaData().setFlowConstraints(fixedFlow);

		if (typePriorities != null) {
			desc.getAnalysisEngineMetaData().setTypePriorities(typePriorities);
		}

		if (sofaMappings != null) {
			desc.setSofaMappings(sofaMappings);
		}

		return desc;
	}

	/**
	 * Creates an AnalysisEngine from the given descriptor, and uses the engine
	 * to process the file or text.
	 * 
	 * @param descriptorFileName
	 *            The fully qualified, Java-style, dotted name of the XML
	 *            descriptor file.
	 * @param fileNameOrText
	 *            Either the path of a file to be loaded, or a string to use as
	 *            the text. If the string given is not a valid path in the file
	 *            system, it will be assumed to be text.
	 * @return A JCas object containing the processed document.
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static JCas process(String descriptorFileName, String fileNameOrText) throws IOException, UIMAException {
		AnalysisEngine engine = createAnalysisEngine(descriptorFileName);
		JCas jCas = process(engine, fileNameOrText);
		engine.collectionProcessComplete();
		return jCas;
	}

	/**
	 * Processes the file or text with the given AnalysisEngine.
	 * 
	 * @param analysisEngine
	 *            The AnalysisEngine object to process the text.
	 * @param fileNameOrText
	 *            Either the path of a file to be loaded, or a string to use as
	 *            the text. If the string given is not a valid path in the file
	 *            system, it will be assumed to be text.
	 * @return A JCas object containing the processed document.
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static JCas process(AnalysisEngine analysisEngine, String fileNameOrText) throws IOException, UIMAException {
		File textFile = new File(fileNameOrText);
		String text;
		if (textFile.exists()) {
			text = FileUtils.file2String(textFile);
		}
		else {
			text = fileNameOrText;
		}

		JCas jCas = analysisEngine.newJCas();
		jCas.setDocumentText(text);
		analysisEngine.process(jCas);
		return jCas;
	}

}
