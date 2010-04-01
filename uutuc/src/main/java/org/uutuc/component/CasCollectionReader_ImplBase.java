package org.uutuc.component;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.factory.ExternalResourceConfigurator;
import org.uutuc.util.InitializeUtil;

public abstract class CasCollectionReader_ImplBase
	extends CollectionReader_ImplBase
{
	@Override
	// This method should not be overwritten. Overwrite initialize(UimaContext) instead.
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

	public void close()
		throws IOException
	{
		// Nothing by default
	}
}
