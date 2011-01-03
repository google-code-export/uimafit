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

import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypePriorityList;
import org.apache.uima.resource.metadata.impl.TypePriorities_impl;

/**
 * @author Philip Ogren
 */

public final class TypePrioritiesFactory {
	private TypePrioritiesFactory() {
		// This class is not meant to be instantiated
	}

	/**
	 * Create a TypePriorities given a sequence of ordered type classes
	 * 
	 * @param prioritizedTypes
	 *            a sequence of ordered type classes
	 * @return
	 */
	public static TypePriorities createTypePriorities(Class<?>... prioritizedTypes) {
		String[] typeNames = new String[prioritizedTypes.length];
		for (int i = 0; i < prioritizedTypes.length; i++) {
			typeNames[i] = prioritizedTypes[i].getName();
		}
		return createTypePriorities(typeNames);
	}

	/**
	 * Create a TypePriorities given a sequence of ordered type names
	 * 
	 * @param prioritizedTypeNames
	 *            a sequence of ordered type names
	 * @return
	 */
	public static TypePriorities createTypePriorities(String... prioritizedTypeNames) {
		TypePriorities typePriorities = new TypePriorities_impl();
		TypePriorityList typePriorityList = typePriorities.addPriorityList();
		for (String typeName : prioritizedTypeNames) {
			typePriorityList.addType(typeName);
		}
		return typePriorities;
	}
}
