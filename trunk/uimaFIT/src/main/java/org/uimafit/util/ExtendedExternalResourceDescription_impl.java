/*
 Copyright 2009-2010
 Ubiquitous Knowledge Processing (UKP) Lab
 Technische Universitaet Darmstadt
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
package org.uimafit.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.impl.ExternalResourceDescription_impl;
import org.apache.uima.resource.metadata.ExternalResourceBinding;

/**
 * Extended {@link ExternalResourceDescription_impl} which can carry
 * {@link ExternalResourceDescription}s.
 * 
 * @author Richard Eckart de Castilho
 */
public class ExtendedExternalResourceDescription_impl extends ExternalResourceDescription_impl {
	private static final long serialVersionUID = 4901306350609836452L;
	private List<ExternalResourceBinding> externalResourceDependencies = new ArrayList<ExternalResourceBinding>();
	private List<ExternalResourceDescription> externalResourceDescriptions = new ArrayList<ExternalResourceDescription>();

	public List<ExternalResourceBinding> getExternalResourceBindings() {
		return externalResourceDependencies;
	}

	public void setExternalResourceBindings(
			List<ExternalResourceBinding> aExternalResourceBindings) {
		externalResourceDependencies = aExternalResourceBindings;
	}

	public List<ExternalResourceDescription> getExternalResourceDescriptions() {
		return externalResourceDescriptions;
	}

	public void setExternalResourceDescriptions(
			List<ExternalResourceDescription> aExternalResourceDescriptions) {
		externalResourceDescriptions = aExternalResourceDescriptions;
	}
}
