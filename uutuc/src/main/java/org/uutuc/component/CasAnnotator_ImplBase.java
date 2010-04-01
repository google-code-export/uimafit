package org.uutuc.component;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.factory.ExternalResourceConfigurator;
import org.uutuc.util.InitializeUtil;

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;

public abstract class CasAnnotator_ImplBase
	extends org.apache.uima.analysis_component.CasAnnotator_ImplBase
{
	@Override
	@OverrideMustInvoke
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);
		InitializeUtil.initialize(this, aContext);
		ExternalResourceConfigurator.configure(aContext, this);
	}
}
