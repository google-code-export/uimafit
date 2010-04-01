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
