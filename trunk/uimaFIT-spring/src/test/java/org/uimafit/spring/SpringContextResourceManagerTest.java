package org.uimafit.spring;

import static org.junit.Assert.assertEquals;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Test making Spring beans available to a UIMA component via the resource manager.
 *
 * @author Richard Eckart de Castilho
 */
public class SpringContextResourceManagerTest {
	// Allow UIMA components to be configured with beans from a Spring context

	// Allow the construction of UIMA components as Spring beans. This means:
	// - UIMA AnalysisComponent

	@Test
	public void test() throws Exception {
		// Acquire application context
		ApplicationContext ctx = getApplicationContext();

		// Create resource manager
		SpringContextResourceManager resMgr = new SpringContextResourceManager();
		resMgr.setApplicationContext(ctx);

		// Create component description
		AnalysisEngineDescription desc = createPrimitiveDescription(MyAnalysisEngine.class);

		// Instantiate component
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(desc, resMgr, null);

		// Test that injection works
		ae.process(ae.newJCas());
	}

	public static class MyAnalysisEngine extends JCasAnnotator_ImplBase {
		@Autowired @Qualifier("otherBean")
		private Object injectedBean;

		@Override
		public void process(JCas aJCas) throws AnalysisEngineProcessException {
			assertEquals("BEAN", injectedBean);
		}
	}

	private ApplicationContext getApplicationContext() {
		final GenericApplicationContext ctx = new GenericApplicationContext();
		AnnotationConfigUtils.registerAnnotationConfigProcessors(ctx);
		ctx.registerBeanDefinition("otherBean",
				BeanDefinitionBuilder.genericBeanDefinition(String.class)
						.addConstructorArgValue("BEAN").getBeanDefinition());
		ctx.refresh();
		return ctx;
	}
}
