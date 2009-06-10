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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.Constants;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AggregateAnalysisEngine_impl;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.SofaMapping;
import org.apache.uima.analysis_engine.metadata.impl.FixedFlow_impl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.util.FileUtils;

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
	 * @param parameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The AnalysisEngine created from the XML descriptor and the
	 *         configuration parameters.
	 * @throws IOException
	 * @throws UIMAException
	 */
	public static AnalysisEngine createAnalysisEngine(String descriptorName, Object... parameters)
			throws UIMAException, IOException {
		Import_impl imprt = new Import_impl();
		imprt.setName(descriptorName);
		URL url = imprt.findAbsoluteUrl(UIMAFramework.newDefaultResourceManager());
		ResourceSpecifier specifier = ResourceCreationSpecifierFactory.createResourceCreationSpecifier(url, parameters);
		return UIMAFramework.produceAnalysisEngine(specifier);
	}

	/**
	 * Get an AnalysisEngine from an XML descriptor file and a set of
	 * configuration parameters.
	 * 
	 * @param descriptorPath
	 *            The path to the XML descriptor file.
	 * @param parameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The AnalysisEngine created from the XML descriptor and the
	 *         configuration parameters.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngine createAnalysisEngineFromPath(String descriptorPath, Object... parameters)
			throws UIMAException, IOException {
		ResourceSpecifier specifier;
		specifier = ResourceCreationSpecifierFactory.createResourceCreationSpecifier(descriptorPath, parameters);
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
	 * @param configurationParameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The AnalysisEngine created from the AnalysisComponent class and
	 *         initialized with the type system and the configuration
	 *         parameters.
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine createPrimitive(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, Object... configurationParameters) throws ResourceInitializationException {
		return createPrimitive(componentClass, typeSystem, (TypePriorities) null, configurationParameters);
	}

	public static AnalysisEngine createPrimitive(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, String[] prioritizedTypeNames, Object... configurationParameters) throws ResourceInitializationException {
		TypePriorities typePriorities = TypePrioritiesFactory.createTypePriorities(prioritizedTypeNames);
		return createPrimitive(componentClass, typeSystem, typePriorities, configurationParameters);

	}

	public static AnalysisEngineDescription createPrimitiveDescription(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, Object... configurationParameters) throws ResourceInitializationException {
		 return createPrimitiveDescription(componentClass, typeSystem, (TypePriorities)null, configurationParameters);
	}

	public static AnalysisEngineDescription createPrimitiveDescription(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, TypePriorities typePriorities, Object... configurationParameters) throws ResourceInitializationException {

		// create the descriptor and set configuration parameters
		AnalysisEngineDescription desc = new AnalysisEngineDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setPrimitive(true);
		desc.setAnnotatorImplementationName(componentClass.getName());
		
		if(configurationParameters != null)
			ResourceCreationSpecifierFactory.setConfigurationParameters(desc, configurationParameters);

		// set the type system
		if (typeSystem != null) {
			desc.getAnalysisEngineMetaData().setTypeSystem(typeSystem);
		}
		
		if(typePriorities != null)
			desc.getAnalysisEngineMetaData().setTypePriorities(typePriorities);

		return desc;
	}

	public static AnalysisEngine createPrimitive(Class<? extends AnalysisComponent> componentClass,
			TypeSystemDescription typeSystem, TypePriorities typePriorities, Object... configurationParameters) throws ResourceInitializationException {

		AnalysisEngineDescription desc = createPrimitiveDescription(componentClass, typeSystem, typePriorities, configurationParameters);

		// create the AnalysisEngine, initialize it and return it
		return createPrimitive(desc);
	}

	public static AnalysisEngine createPrimitive( AnalysisEngineDescription desc) throws ResourceInitializationException {
		AnalysisEngine engine = new PrimitiveAnalysisEngine_impl();
		engine.initialize(desc, null);
		return engine;
	}
	
	public static AnalysisEngine createAggregate(List<Class<? extends AnalysisComponent>> componentClasses, 
			TypeSystemDescription typeSystem, TypePriorities typePriorities, SofaMapping[] sofaMappings, Object... configurationParameters) throws ResourceInitializationException {
		AnalysisEngineDescription desc = createAggregateDescription(componentClasses, typeSystem, typePriorities, sofaMappings, configurationParameters);
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

	public static AnalysisEngineDescription createAggregateDescription(List<Class<? extends AnalysisComponent>> componentClasses, 
			TypeSystemDescription typeSystem, TypePriorities typePriorities, SofaMapping[] sofaMappings, Object... configurationParameters) throws ResourceInitializationException {

		List<AnalysisEngineDescription> primitiveEngineDescriptions = new ArrayList<AnalysisEngineDescription>();
		List<String> componentNames = new ArrayList<String>();
		
		for(Class<? extends AnalysisComponent> componentClass : componentClasses) {
			AnalysisEngineDescription primitiveDescription = createPrimitiveDescription(componentClass, typeSystem, typePriorities, configurationParameters);
			primitiveEngineDescriptions.add(primitiveDescription);
			componentNames.add(componentClass.getName());
		}
		return createAggregateDescription(primitiveEngineDescriptions, componentNames, typeSystem, typePriorities, sofaMappings);
	}

	public static AnalysisEngine createAggregate(List<AnalysisEngineDescription> analysisEngineDescriptions, List<String> componentNames,
			TypeSystemDescription typeSystem, TypePriorities typePriorities, SofaMapping[] sofaMappings) throws ResourceInitializationException {

		AnalysisEngineDescription desc = createAggregateDescription(analysisEngineDescriptions, componentNames, typeSystem, typePriorities, sofaMappings);
		// create the AnalysisEngine, initialize it and return it
		AnalysisEngine engine = new AggregateAnalysisEngine_impl();
		engine.initialize(desc, null);
		return engine;

	}
	
	@SuppressWarnings("unchecked")
	public static AnalysisEngineDescription createAggregateDescription(List<AnalysisEngineDescription> analysisEngineDescriptions, List<String> componentNames,
			TypeSystemDescription typeSystem, TypePriorities typePriorities, SofaMapping[] sofaMappings) throws ResourceInitializationException {

		// create the descriptor and set configuration parameters
		AnalysisEngineDescription desc = new AnalysisEngineDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setPrimitive(false);
		
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

		if(typePriorities != null)
			desc.getAnalysisEngineMetaData().setTypePriorities(typePriorities);

	    if(sofaMappings != null)
	    	desc.setSofaMappings(sofaMappings);
		
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
