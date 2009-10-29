/*
 Copyright 2009
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

package org.uutuc.factory.converters;

/**
 * Converter for float values.
 * 
 * @author Richard Eckart de Castilho
 */
public class FloatConverter implements Converter
{
	public Class<?> getUimaType()
	{
		return Float.class;
	}
	
	public Class<?> getResultType()
	{
		return Float.class;
	}
	
	@SuppressWarnings("cast")
	public Object convert(Object aValue)
	{
		if (aValue instanceof String) {
			return Float.parseFloat((String) aValue);
		}
		else {
			return (Float) aValue;
		}
	}
}
