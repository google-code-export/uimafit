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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  @author Philip Ogren
 */
public class ReflectionUtil {

	public static List<Field> getFields(Object object) {
		Class<?> cls = object.getClass();
		return getFields(cls);
	}

	public static List<Field> getFields(Class<?> cls) {
		List<Field> fields = new ArrayList<Field>();
		while(!cls.equals(Object.class)) {
			Field[] flds = cls.getDeclaredFields();
			fields.addAll(Arrays.asList(flds));
			cls = cls.getSuperclass();
		}
		return fields;
	}

}
