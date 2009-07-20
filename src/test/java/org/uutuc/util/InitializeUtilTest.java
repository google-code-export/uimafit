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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Modifier;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;
import org.junit.Test;
import org.uutuc.factory.AnalysisEngineFactory;
import org.uutuc.factory.testAes.Annotator1;
import org.uutuc.factory.testAes.ParameterizedAE;

/**
 * @author Philip Ogren
 */

public class InitializeUtilTest {

	@Test
	public void testInitialize() throws ResourceInitializationException, SecurityException, NoSuchFieldException {
		Assert.assertTrue((ParameterizedAE.class.getDeclaredField("boolean4").getModifiers() & Modifier.PUBLIC) > 0);
		
		ResourceInitializationException rie = null;
		try {
			AnalysisEngineFactory.createPrimitive(Util.createPrimitiveDescription(ParameterizedAE.class));
		} catch(ResourceInitializationException e) {
			rie = e;
		}
		assertNotNull(rie);
		AnalysisEngine engine = AnalysisEngineFactory.createPrimitive(Util.createPrimitiveDescription(ParameterizedAE.class, ParameterizedAE.PARAM_FLOAT_3, 1.234f,
				ParameterizedAE.PARAM_FLOAT_6, new Float[] {1.234f, 0.001f}));
		
		ParameterizedAE component = new ParameterizedAE();
		component.initialize(engine.getUimaContext());
		assertEquals("pineapple", component.getString1());
		assertArrayEquals(new String[]{"coconut", "mango"}, component.getString2());
		assertEquals(null, component.getString3());
		assertArrayEquals(new String[] {"apple"}, component.getString4());
		assertArrayEquals(new String[] {""}, component.getString5());
		assertFalse(component.isBoolean1());
		
		NullPointerException npe = null;
		try {
			assertFalse(component.isBoolean2());
		} catch(NullPointerException e) {
			npe = e;
		}
		assertNotNull(npe);
		
		assertTrue(component.getBoolean3()[0]);
		assertTrue(component.getBoolean3()[1]);
		assertFalse(component.getBoolean3()[2]);
		assertTrue(component.boolean4[0]);
		assertFalse(component.boolean4[1]);
		assertTrue(component.boolean4[2]);
		assertFalse(component.getBoolean5()[0]);
		assertEquals(0, component.getInt1());
		assertEquals(42, component.getInt2());
		assertEquals(42, component.getInt3()[0]);
		assertEquals(111, component.getInt3()[1]);
		assertEquals(Integer.valueOf(2), component.getInt4()[0]);
		assertEquals(0.0f, component.getFloat1(), 0.001f);
		assertEquals(3.1415f, component.getFloat2(), 0.001f);
		assertEquals(1.234f, component.getFloat3(), 0.001f);
		assertNull(component.getFloat4());
		assertEquals(0f, component.getFloat5()[0], 0.001f);
		assertEquals(3.1415f, component.getFloat5()[1], 0.001f);
		assertEquals(2.7182818f, component.getFloat5()[2], 0.001f);
		assertEquals(1.234f, component.getFloat6()[0], 0.001f);
		assertEquals(0.001f, component.getFloat6()[1], 0.001f);
		assertEquals(1.1111f, component.getFloat7()[0], 0.001f);
		assertEquals(2.2222f, component.getFloat7()[1], 0.001f);
		assertEquals(3.3333f, component.getFloat7()[2], 0.001f);

		engine = AnalysisEngineFactory.createPrimitive(Util.createPrimitiveDescription(ParameterizedAE.class, ParameterizedAE.PARAM_FLOAT_3, 1.234f,
				ParameterizedAE.PARAM_FLOAT_6, new Float[] {1.234f, 0.001f}, ParameterizedAE.PARAM_STRING_1, "lime",
				ParameterizedAE.PARAM_STRING_2, new String[] {"banana", "strawberry"},
				ParameterizedAE.PARAM_STRING_3, "cherry",
				ParameterizedAE.PARAM_STRING_4, new String[] {"raspberry", "blueberry", "blackberry"},
				ParameterizedAE.PARAM_STRING_5, new String[2],
				ParameterizedAE.PARAM_BOOLEAN_1, true,
				ParameterizedAE.PARAM_BOOLEAN_2, true,
				ParameterizedAE.PARAM_BOOLEAN_3, new boolean[] {true, true, false},
				ParameterizedAE.PARAM_BOOLEAN_4, new Boolean[] {true, false, false},
				ParameterizedAE.PARAM_BOOLEAN_5, new Boolean[] {true},
				ParameterizedAE.PARAM_INT_1, 0,
				ParameterizedAE.PARAM_INT_2, 24,
				ParameterizedAE.PARAM_INT_3, new int[] {5}));
		component = new ParameterizedAE();
		component.initialize(engine.getUimaContext());
		assertEquals("lime", component.getString1());
		assertArrayEquals(new String[] {"banana", "strawberry"}, component.getString2());
		assertEquals("cherry", component.getString3());
		assertArrayEquals(new String[] {"raspberry", "blueberry", "blackberry"}, component.getString4());
		assertArrayEquals(new String[2], component.getString5());
		assertEquals(null, component.getString5()[0]);
		assertTrue(component.isBoolean1());
		assertTrue(component.isBoolean2());
		assertTrue(component.getBoolean3()[0]);
		assertTrue(component.getBoolean3()[1]);
		assertFalse(component.getBoolean3()[2]);
		assertTrue(component.boolean4[0]);
		assertFalse(component.boolean4[1]);
		assertFalse(component.boolean4[2]);
		assertTrue(component.getBoolean5()[0]);
		assertEquals(0, component.getInt1());
		assertEquals(24, component.getInt2());
		assertEquals(5, component.getInt3()[0]);

		engine = AnalysisEngineFactory.createPrimitive(Util.createPrimitiveDescription(ParameterizedAE.class, ParameterizedAE.PARAM_FLOAT_3, 1.234f,
				ParameterizedAE.PARAM_FLOAT_6, new Float[] {1.234f, 0.001f}, ParameterizedAE.PARAM_BOOLEAN_1, true,
				ParameterizedAE.PARAM_BOOLEAN_3, new boolean[3],
				ParameterizedAE.PARAM_FLOAT_5, new float[] {1.2f, 3.4f}));
		component = new ParameterizedAE();
		component.initialize(engine.getUimaContext());
		assertFalse(component.getBoolean3()[0]);
		assertFalse(component.getBoolean3()[1]);
		assertFalse(component.getBoolean3()[2]);
		assertEquals(component.getFloat5()[0], 1.2f, 0.001f);
		assertEquals(component.getFloat5()[1], 3.4f, 0.001f);
		
		rie = null;
		try {
		engine = AnalysisEngineFactory.createPrimitive(Util.createPrimitiveDescription(ParameterizedAE.class, ParameterizedAE.PARAM_FLOAT_3, 1.234f,
				ParameterizedAE.PARAM_FLOAT_6, new Float[] {1.234f, 0.001f}, ParameterizedAE.PARAM_STRING_1, true));
		} catch(ResourceInitializationException e) {
			rie = e;
		}
		assertNotNull(rie);
		
	}
	
	@Test
	public void testInitialize2() throws ResourceInitializationException {
		AnalysisEngine engine = AnalysisEngineFactory.createPrimitive(Util.createPrimitiveDescription(Annotator1.class));
		assertEquals(0, engine.getAnalysisEngineMetaData().getCapabilities().length);
		
	}
}
