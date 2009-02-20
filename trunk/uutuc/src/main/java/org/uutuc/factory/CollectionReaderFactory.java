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

import org.apache.uima.Constants;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.impl.CollectionReaderDescription_impl;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;

/**
 * @author Steven Bethard, Philip Ogren
 */
public class CollectionReaderFactory {

	/**
	 * Get a CollectionReader from a CollectionReader class, a type system, and
	 * a set of configuration parameters.
	 * 
	 * @param readerClass
	 *            The class of the CollectionReader to be created.
	 * @param typeSystem
	 *            A description of the types used by the CollectionReader (may
	 *            be null).
	 * @param configurationParameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The CollectionReader created and initialized with the type system
	 *         and configuration parameters.
	 * @throws ResourceInitializationException
	 */
	public static CollectionReader createCollectionReader(Class<? extends CollectionReader> readerClass,
			TypeSystemDescription typeSystem, Object... configurationParameters) throws ResourceInitializationException {

		// create the descriptor and set configuration parameters
		CollectionReaderDescription desc = new CollectionReaderDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setImplementationName(readerClass.getName());
		ResourceCreationSpecifierFactory.setConfigurationParameters(desc, configurationParameters);

		// set the type system
		if (typeSystem != null) {
			desc.getCollectionReaderMetaData().setTypeSystem(typeSystem);
		}

		// create the CollectionReader
		CollectionReader reader;
		try {
			reader = readerClass.newInstance();
		}
		catch (InstantiationException e) {
			throw new ResourceInitializationException(e);
		}
		catch (IllegalAccessException e) {
			throw new ResourceInitializationException(e);
		}

		// initialize the CollectionReader and return it
		reader.initialize(desc, null);
		return reader;
	}

	/**
	 * Create a CollectionReader from an XML descriptor file and a set of
	 * configuration parameters.
	 * 
	 * @param descriptorPath
	 *            The path to the XML descriptor file.
	 * @param parameters
	 *            Any additional configuration parameters to be set. These
	 *            should be supplied as (name, value) pairs, so there should
	 *            always be an even number of parameters.
	 * @return The CollectionReader created from the XML descriptor and the
	 *         configuration parameters.
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static CollectionReader createCollectionReaderFromPath(String descriptorPath, Object... parameters)
			throws UIMAException, IOException {
		ResourceCreationSpecifier specifier = ResourceCreationSpecifierFactory.createResourceCreationSpecifier(
				descriptorPath, parameters);
		return UIMAFramework.produceCollectionReader(specifier);
	}

	/**
	 * Get a CollectionReader from the name (Java-style, dotted) of an XML
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
	 * @throws UIMAException
	 * @throws IOException
	 */

	public static CollectionReader createCollectionReader(String descriptorName, Object... parameters)
			throws UIMAException, IOException {
		Import_impl imp = new Import_impl();
		imp.setName(descriptorName);
		URL url = imp.findAbsoluteUrl(UIMAFramework.newDefaultResourceManager());
		ResourceSpecifier specifier = ResourceCreationSpecifierFactory.createResourceCreationSpecifier(url, parameters);
		return UIMAFramework.produceCollectionReader(specifier);
	}

}
