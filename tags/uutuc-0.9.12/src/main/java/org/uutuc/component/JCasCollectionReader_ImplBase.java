/*
 Copyright 2010
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
package org.uutuc.component;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.factory.ExternalResourceConfigurator;
import org.uutuc.util.InitializeUtil;

/**
 * Base class for JCas collection readers which initializes itself based on annotations.
 *
 * @author Richard Eckart de Castilho
 */
public abstract class JCasCollectionReader_ImplBase
	extends CollectionReader_ImplBase
{
	// This method should not be overwritten. Overwrite initialize(UimaContext) instead.
	@Override
	public final void initialize()
		throws ResourceInitializationException
	{
		InitializeUtil.initialize(this, getUimaContext());
		ExternalResourceConfigurator.configure(getUimaContext(), this);
		initialize(getUimaContext());
	}

	/**
	 * This method should be overwritten by subclasses.
	 *
	 * @param aContext
	 * @throws ResourceInitializationException
	 */
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		// Nothing by default
	}

	// This method should not be overwritten. Overwrite getNext(JCas) instead.
	public final void getNext(CAS aCAS)
		throws IOException, CollectionException
	{
		try {
			getNext(aCAS.getJCas());
		}
		catch (CASException e) {
			throw new CollectionException(e);
		}
	}

	public abstract void getNext(JCas aJCas)
		throws IOException, CollectionException;

	public void close()
		throws IOException
	{
		// Do nothing per default
	}
}
