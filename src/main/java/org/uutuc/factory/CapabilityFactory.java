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

package org.uutuc.factory;

import org.apache.uima.resource.metadata.Capability;
import org.apache.uima.resource.metadata.impl.Capability_impl;
import org.uutuc.descriptor.SofaCapability;

/**
 * @author Philip Ogren
 */

public class CapabilityFactory {
	
	
	public static Capability[] createCapability(Class<?> componentClass) {
		if(componentClass.isAnnotationPresent(SofaCapability.class)) {
			SofaCapability annotation = componentClass.getAnnotation(SofaCapability.class);
			String[] inputSofas = annotation.inputSofas();
			if(inputSofas.length == 1 && inputSofas[0].equals(SofaCapability.NO_DEFAULT_VALUE)) {
				inputSofas = new String[0];
			}
			String[] outputSofas = annotation.outputSofas();
			if(outputSofas.length == 1 && outputSofas[0].equals(SofaCapability.NO_DEFAULT_VALUE)) {
				outputSofas = new String[0];
			}
			return new Capability[] { CapabilityFactory.createCapability(inputSofas, outputSofas)};
		}
		return (Capability[]) null;
	}
	
	public static Capability createCapability(String[] inputSofas, String[] outputSofas) {
		Capability capability = new Capability_impl();
		capability.setInputSofas(inputSofas);
		capability.setOutputSofas(outputSofas);
		return capability;
	}
	
}
