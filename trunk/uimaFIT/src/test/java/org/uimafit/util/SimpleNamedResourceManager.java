package org.uimafit.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.impl.ResourceManager_impl;
import org.apache.uima.resource.metadata.ResourceManagerConfiguration;
import org.apache.uima.util.Level;

/**
 * <b>EXPERIMENTAL CODE</b>
 * <p>
 * Simple {@link ResourceManager} allowing for direct injection of Java objects into UIMA
 * components as external resources.
 * <p>
 * This implementation uses a simple map to look up an Java object by key. If any component
 * using this resource manager declares an external resource by the given key, the Java object
 * will be bound to that external resource.
 * <p>
 * Example:
 * <p>
 * <blockquote><pre>
 * class MyComponent extends JCasAnnotator_ImplBase {
 *   static final String RES_INJECTED_POJO = "InjectedPojo";
 *   {@code @ExternalResource(key = RES_INJECTED_POJO)}
 *   private String injectedString;
 *
 *   public void process(JCas aJCas) throws AnalysisEngineProcessException {
 *     ...
 *   }
 * }
 *
 * Map<String, Object> context = new HashMap<String, Object>();
 * context(MyComponent.RES_INJECTED_POJO, "Just an injected POJO");
 *
 * SimpleNamedResourceManager resMgr = new SimpleNamedResourceManager();
 * resMgr.setExternalContext(externalContext);

 * AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(desc, resMgr, null);
 * </pre></blockquote>
 *
 * @author Richard Eckart de Castilho
 */
public class SimpleNamedResourceManager extends ResourceManager_impl {
	private Map<String, Object> externalContext;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initializeExternalResources(ResourceManagerConfiguration aConfiguration,
			String aQualifiedContextName, java.util.Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {

		for (Entry<String, Object> e : externalContext.entrySet()) {
			Object registration = mInternalResourceRegistrationMap.get(e.getKey());

			if (registration == null) {
				try {
					// Register resource
					// ResourceRegistration unfortunately is package private
					Object reg = newInstance(
							"org.apache.uima.resource.impl.ResourceManager_impl$ResourceRegistration",
							Object.class, e.getValue(),
							ExternalResourceDescription.class, null,
							String.class, aQualifiedContextName);
					((Map) mInternalResourceRegistrationMap).put(e.getKey(), reg);

					// Perform binding
					mResourceMap.put(aQualifiedContextName + e.getKey(), e.getValue());
				}
				catch (Exception e1) {
					throw new ResourceInitializationException(e1);
				}
			}
			else {
				try {
					Object desc = getFieldValue(registration, "description");

					if (desc != null) {
						String definingContext = getFieldValue(registration, "definingContext");

						if (aQualifiedContextName.startsWith(definingContext)) {
							UIMAFramework.getLogger().logrb(
									Level.CONFIG,
									ResourceManager_impl.class.getName(),
									"initializeExternalResources",
									LOG_RESOURCE_BUNDLE,
									"UIMA_overridden_resource__CONFIG",
									new Object[] { e.getKey(), aQualifiedContextName,
											definingContext });
						}
						else {
							UIMAFramework.getLogger().logrb(
									Level.WARNING,
									ResourceManager_impl.class.getName(),
									"initializeExternalResources",
									LOG_RESOURCE_BUNDLE,
									"UIMA_duplicate_resource_name__WARNING",
									new Object[] { e.getKey(), definingContext,
											aQualifiedContextName });
						}
					}
				}
				catch (Exception e1) {
					throw new ResourceInitializationException(e1);
				}
			}
		}

		super.initializeExternalResources(aConfiguration, aQualifiedContextName, aAdditionalParams);
	}

	public void setExternalContext(Map<String, Object> aExternalContext) {
		externalContext = aExternalContext;
	}

	/**
	 * Instantiate a non-visible class.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T newInstance(String aClassName, Object... aArgs) throws ResourceInitializationException {
		Constructor constr = null;
		try {
			Class<?> cl = Class.forName(aClassName);

			List<Class> types = new ArrayList<Class>();
			List<Object> values = new ArrayList<Object>();
			for (int i = 0; i < aArgs.length; i += 2) {
				types.add((Class) aArgs[i]);
				values.add(aArgs[i+1]);
			}

			constr = cl.getDeclaredConstructor(types.toArray(new Class[types.size()]));
			constr.setAccessible(true);
			return (T) constr.newInstance(values.toArray(new Object[values.size()]));
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		finally {
			if (constr != null) {
				constr.setAccessible(false);
			}
		}
	}

	/**
	 * Get a field value from a non-visible field.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getFieldValue(Object aObject, String aFieldName) throws ResourceInitializationException
	{
		Field f = null;
		try {
			f = aObject.getClass().getField(aFieldName);
			f.setAccessible(true);
			return (T) f.get(aObject);
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		finally {
			if (f != null) {
				f.setAccessible(false);
			}
		}
	}
}
