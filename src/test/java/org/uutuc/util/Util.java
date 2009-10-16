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
package org.uutuc.util;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uutuc.factory.AnalysisEngineFactory;
import org.uutuc.factory.JCasFactory;
import org.uutuc.factory.TypeSystemDescriptionFactory;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class Util {

	public static TypeSystemDescription TYPE_SYSTEM_DESCRIPTION =
		TypeSystemDescriptionFactory.createTypeSystemDescription("org.uutuc.type.TypeSystem");
	
	public static TypePriorities TYPE_PRIORITIES = null;

	public static ThreadLocal<JCas> JCAS = new ThreadLocal<JCas>();
	static {
		try {
			JCAS.set(JCasFactory.createJCas("org.uutuc.type.TypeSystem"));
		}
		catch (UIMAException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static AnalysisEngineDescription createPrimitiveDescription(Class<? extends AnalysisComponent> componentClass, Object... configurationData) throws ResourceInitializationException {
		return AnalysisEngineFactory.reflectPrimitiveDescription(componentClass, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES, configurationData);
	}

}
