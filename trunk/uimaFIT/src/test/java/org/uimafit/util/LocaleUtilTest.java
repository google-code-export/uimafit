/* 
 Copyright 2010	Regents of the University of Colorado.  
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
package org.uimafit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.uimafit.util.LocaleUtil.createLocale;
import static org.uimafit.util.LocaleUtil.getLocaleConstant;

import java.util.Locale;

import org.junit.Test;

/**
 * 
 * @author Philip Ogren
 * 
 */
public class LocaleUtilTest
{

	@Test
	public void testGetLocaleConstant() throws Exception
	{
		assertEquals(Locale.US, getLocaleConstant("US"));
		assertNull(getLocaleConstant("UN"));
		assertEquals(Locale.ENGLISH, getLocaleConstant("ENGLISH"));
		assertEquals(Locale.CHINA, getLocaleConstant("CHINA"));
		assertNull(getLocaleConstant(""));
		assertNull(getLocaleConstant(null));
	}
	
	@Test
	public void testCreateLocaleConstant() throws Exception
	{
		assertEquals(new Locale("en", "US"), createLocale("en-US"));
		assertEquals(new Locale("es"), createLocale("es"));
		assertEquals(new Locale("ko", "KR"), createLocale("ko-KR"));
		assertEquals(new Locale("es", "ES", "Traditional_WIN"), createLocale("es-ES-Traditional_WIN"));
		assertEquals(new Locale("en", "US", "Colorado"), createLocale("en-US-Colorado"));
		assertEquals(new Locale("en", "US", "Colorado"), createLocale("en-US-Colorado"));
	}

	@Test(expected=RuntimeException.class)
	public void testBad() throws Exception
	{
		createLocale("en-US-Colorado-Boulder");
	}

}
