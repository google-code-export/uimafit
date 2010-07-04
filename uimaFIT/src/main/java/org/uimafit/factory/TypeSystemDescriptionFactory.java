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

import static org.apache.uima.UIMAFramework.getXMLParser;
import static org.apache.uima.util.CasCreationUtils.mergeTypeSystems;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.resource.metadata.impl.TypeSystemDescription_impl;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author Steven Bethard, Philip Ogren
 * @author Richard Eckart de Castilho
 */
public final class TypeSystemDescriptionFactory {
	private TypeSystemDescriptionFactory()
	{
		// This class is not meant to be instantiated
	}

	/**
	 * System property indicating which locations to scan for type descriptions. A list of
	 * locations may be given separated by ";".
	 */
	public static final String TYPE_IMPORT_PATTERN = "org.uimafit.type.import_pattern";

	/**
	 * Type manifest location.
	 */
	public static final String TYPE_MANIFEST_PATTERN = "classpath*:META-INF/org.uimafit/types.txt";

	private static String[] typeDescriptorLocations;


	/**
	 * Creates a TypeSystemDescription from a list of classes belonging to a type system - i.e. classes generated by JCasGen.
	 *
	 * @param typeSystemClasses
	 *            The type system class objects.
	 * @return A TypeSystemDescription that includes all of the specified
	 *         Annotation types.
	 */
	public static TypeSystemDescription createTypeSystemDescription(Class<?>... typeSystemClasses) {
		TypeSystemDescription typeSystem = new TypeSystemDescription_impl();
		List<Import> imports = new ArrayList<Import>();
		for (Class<?> typeSystemClass : typeSystemClasses) {
			Import imprt = new Import_impl();
			imprt.setName(typeSystemClass.getName());
			imports.add(imprt);
		}
		Import[] importArray = new Import[imports.size()];
		typeSystem.setImports(imports.toArray(importArray));
		return typeSystem;
	}

	/**
	 * Creates a TypeSystemDescription from descriptor names.
	 *
	 * @param descriptorNames
	 *            The fully qualified, Java-style, dotted descriptor names.
	 * @return A TypeSystemDescription that includes the types from all of the
	 *         specified files.
	 */
	public static TypeSystemDescription createTypeSystemDescription(String... descriptorNames) {
		TypeSystemDescription typeSystem = new TypeSystemDescription_impl();
		List<Import> imports = new ArrayList<Import>();
		for (String descriptorName : descriptorNames) {
			Import imp = new Import_impl();
			imp.setName(descriptorName);
			imports.add(imp);
		}
		Import[] importArray = new Import[imports.size()];
		typeSystem.setImports(imports.toArray(importArray));
		return typeSystem;
	}

	/**
	 * Creates a TypeSystemDescription from a descriptor file
	 *
	 * @param descriptorURIs The descriptor file paths.
	 * @return A TypeSystemDescription that includes the types from all of the specified files.
	 */
	public static TypeSystemDescription createTypeSystemDescriptionFromPath(String... descriptorURIs) {
		TypeSystemDescription typeSystem = new TypeSystemDescription_impl();
		List<Import> imports = new ArrayList<Import>();
		for (String descriptorURI : descriptorURIs) {
			Import imp = new Import_impl();
			imp.setLocation(descriptorURI);
			imports.add(imp);
		}
		Import[] importArray = new Import[imports.size()];
		typeSystem.setImports(imports.toArray(importArray));
		return typeSystem;
	}

	/**
	 * Creates a {@link TypeSystemDescription} from all type descriptions that can be found via
	 * the {@link #TYPE_IMPORT_PATTERN} or via the {@code META-INF/org.uimafit/types.txt} files
	 * in the classpath.
	 *
	 * @return the auto-scanned type system.
	 * @throws ResourceInitializationException
	 */
	public static TypeSystemDescription createTypeSystemDescription()
		throws ResourceInitializationException
	{
		List<TypeSystemDescription> tsdList = new ArrayList<TypeSystemDescription>();
		for (String location : scanTypeDescriptors()) {
			try {
				XMLInputSource xmlInputType1 = new XMLInputSource(location);
				tsdList.add(getXMLParser().parseTypeSystemDescription(xmlInputType1));
				LogFactory.getLog(TypeSystemDescription.class).debug(
						"Detected type system at [" + location + "]");
			}
			catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
			catch (InvalidXMLException e) {
				LogFactory.getLog(TypeSystemDescription.class).warn(
						"[" + location + "] is not a type file. Ignoring.", e);
			}
		}

		return mergeTypeSystems(tsdList);
	}

	/**
	 * Get all currently accessible type system descriptor locations. A scan is actually only
	 * performed on the first call and the locations are cached. To force a re-scan use
	 * {@link #forceTypeDescriptorsScan()}.
	 *
	 * @return an array of locations.
	 * @throws ResourceInitializationException if the locations could not be resolved.
	 */
	public static String[] scanTypeDescriptors()
		throws ResourceInitializationException
	{
		if (typeDescriptorLocations == null) {
			ArrayList<String> patterns = new ArrayList<String>();

			// Scan auto-import locations
			patterns.addAll(Arrays.asList(System.getProperty(TYPE_IMPORT_PATTERN, "").split(";")));

			// Scan manifest
			for (String mfUrl : resolve(TYPE_MANIFEST_PATTERN)) {
				InputStream is = null;
				try {
					is = new URL(mfUrl).openStream();
					patterns.addAll(IOUtils.readLines(is));
				}
				catch (IOException e) {
					throw new ResourceInitializationException(e);
				}
				finally {
					IOUtils.closeQuietly(is);
				}
			}

			String[] patternsArray = patterns.toArray(new String[patterns.size()]);
			typeDescriptorLocations = resolve(patternsArray);
		}
		return typeDescriptorLocations;
	}

	/**
	 * Force rescan of type descriptors. The next call to {@link #scanTypeDescriptors()} will
	 * rescan all auto-import locations.
	 */
	public static void forceTypeDescriptorsScan()
	{
		typeDescriptorLocations = null;
	}

	/**
	 * Resolve a list of patterns to a set of URLs.
	 *
	 * @param patterns
	 * @return an array of locations.
	 * @throws ResourceInitializationException if the locations could not be resolved.
	 */
	public static String[] resolve(String... patterns)
		throws ResourceInitializationException
	{
		Set<String> locations = new HashSet<String>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			// Scan auto-import locations. Using a set to avoid scanning a pattern twice.
			for (String pattern : new TreeSet<String>(Arrays.asList(patterns))) {
				for (Resource r : resolver.getResources(pattern)) {
					locations.add(r.getURL().toString());
				}
			}
			return locations.toArray(new String[locations.size()]);
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}
}
