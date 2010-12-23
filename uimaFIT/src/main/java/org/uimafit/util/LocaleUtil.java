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

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 
 * @author Philip Ogren
 * 
 */
public class LocaleUtil
{

	/**
	 * This method returns the locale constant for the given string. For example, see
	 * {@link Locale#US} as an example locale constant. To retrieve that locale using this method,
	 * pass in the string value "US". If there is no locale constant for the passed in string, then
	 * null is returned
	 * 
	 * @param localeConstant
	 *            a string value that names a locale constant.
	 * @return the corresponding locale or null if there is no locale for the provided string.
	 */
	public static Locale getLocaleConstant(String localeConstant)
	{
		try {
			Field field = Locale.class.getField(localeConstant);
			if (field != null && field.getType().equals(Locale.class)) {
				return (Locale) field.get(null);
			}
		}
		catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * This method passes through to {@link Locale#Locale(String)} unless the provided string
	 * contains a hyphen. If it does, then the string is split on the hyphen and the resulting
	 * strings are passed into the multi-parameter constructors of Locale. The passed in string
	 * should not contain more than two hyphens as the Locale constructor with the most params is
	 * three.
	 * 
	 * @param localeString
	 * @return
	 */
	public static Locale createLocale(String localeString)
	{
		if (localeString.indexOf('-') != -1) {
			String[] localeData = localeString.split("-");
			int length = localeData.length;
			if (length == 2) {
				return new Locale(localeData[0], localeData[1]);
			}
			else if (length == 3) {
				return new Locale(localeData[0], localeData[1], localeData[2]);
			}
			else {
				throw new RuntimeException(
						"locale string must consist of 2 or 3 hyphen separated values or must not contain a hyphen.");
			}

		}
		else {
			return new Locale(localeString);
		}
	}

	/**
	 * passes through to getLocaleConstant. If this returns null, then this method passes through to
	 * createLocale.
	 * 
	 * @param localeString
	 * @return
	 */
	public static Locale getLocale(String localeString)
	{
		Locale locale = getLocaleConstant(localeString);
		if (locale != null) {
			return locale;
		}
		return createLocale(localeString);
	}
}
