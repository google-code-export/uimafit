package org.uimafit.spring.util;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl;
import org.apache.uima.resource.Resource;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class ResourceInitializationUtil {
	/**
	 * Initialize an existing object as a Spring bean.
	 */
	public static <T> T initializeBean(AutowireCapableBeanFactory aBeanFactory, T aBean,
			String aName) {
		@SuppressWarnings("unchecked")
		T wrappedBean = (T) aBeanFactory.initializeBean(aBean, aName);
		aBeanFactory.autowireBean(aBean);
		return wrappedBean;
	}

	/**
	 * Handle Spring-initialization of resoures produced by the UIMA framework.
	 */
	public static Resource initResource(Resource aResource,
			ApplicationContext aApplicationContext) {
		AutowireCapableBeanFactory beanFactory = aApplicationContext
				.getAutowireCapableBeanFactory();

		if (aResource instanceof PrimitiveAnalysisEngine_impl) {
			PropertyAccessor pa = PropertyAccessorFactory.forDirectFieldAccess(aResource);

			// Access the actual AnalysisComponent and initialize it
			AnalysisComponent analysisComponent = (AnalysisComponent) pa
					.getPropertyValue("mAnalysisComponent");
			initializeBean(beanFactory, analysisComponent, aResource.getMetaData().getName());
			pa.setPropertyValue("mAnalysisComponent", analysisComponent);

			return aResource;
		}
		else {
			return (Resource) beanFactory
					.initializeBean(aResource, aResource.getMetaData().getName());
		}
	}

}
