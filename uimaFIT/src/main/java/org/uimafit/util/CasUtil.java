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

 getCoveredAnnotations() contains code adapted from the UIMA Subiterator class.
*/
package org.uimafit.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.Subiterator;
import org.apache.uima.cas.text.AnnotationFS;

/**
 * Utility methods for convenient access to the {@link CAS}.
 *
 * @author Richard Eckart de Castilho
 */
public class CasUtil
{
	/**
	 * Package name of JCas wrapper classes built into UIMA.
	 */
	public static final String UIMA_BUILTIN_JCAS_PREFIX = "org.apache.uima.jcas.";

	/**
	 * Get the CAS type for the given JCas wrapper class.
	 *
	 * @param cas the CAS hosting the type system.
	 * @param type the JCas wrapper class.
	 * @return the CAS type.
	 */
	public static Type getType(CAS cas, Class<?> type)
	{
		String typeName = type.getName();
		if (typeName.startsWith(UIMA_BUILTIN_JCAS_PREFIX)) {
			typeName = "uima."+typeName.substring(UIMA_BUILTIN_JCAS_PREFIX.length());
		}
		Type t = cas.getTypeSystem().getType(typeName);
		if (t == null) {
			throw new IllegalArgumentException("Undeclared type ["+typeName+"]");
		}
		return t;
	}

	/**
	 * Get a list of annotations of the given annotation type constraint by a certain annotation.
	 * Iterates over all annotations of the given type to find the covered annotations. Does not use
	 * subiterators and does not respect type prioritites. Was adapted from {@link Subiterator}.
	 * Uses the same approach except that type priorities are ignored.
	 *
	 * @param <T> the JCas type.
	 * @param cas a JCas.
	 * @param type a UIMA type.
	 * @param coveringAnnotation the covering annotation.
	 * @return a return value.
	 * @see Subiterator
	 */
	public static <T extends AnnotationFS> List<T> selectCovered(
			CAS cas, Type type, AnnotationFS coveringAnnotation)
	{
		int begin = coveringAnnotation.getBegin();
		int end = coveringAnnotation.getEnd();

		List<T> list = new ArrayList<T>();
		FSIterator<AnnotationFS> it = cas.getAnnotationIndex(type).iterator();

		// Try to seek the insertion point.
		it.moveTo(coveringAnnotation);

		// If the insertion point is beyond the index, move back to the last.
		if (!it.isValid()) {
			it.moveToLast();
			if (!it.isValid()) {
				return list;
			}
		}

		// Ignore type priorities by seeking to the first that has the same begin
		boolean moved = false;
		while (it.isValid() && (it.get()).getBegin() >= begin) {
			it.moveToPrevious();
			moved = true;
		}

		// If we moved, then we are now on one starting before the requested begin, so we have to
		// move one ahead.
		if (moved) {
			it.moveToNext();
		}

		// If we managed to move outside the index, start at first.
		if (!it.isValid()) {
			it.moveToFirst();
		}

		// Skip annotations whose start is before the start parameter.
		while (it.isValid() && (it.get()).getBegin() < begin) {
			it.moveToNext();
		}

		boolean strict = true;
		while (it.isValid()) {
			@SuppressWarnings("unchecked")
			T a = (T) it.get();
			// If the start of the current annotation is past the end parameter, we're done.
			if (a.getBegin() > end) {
				break;
			}
			it.moveToNext();
			if (strict && a.getEnd() > end) {
				continue;
			}

			if (!a.equals(coveringAnnotation)) {
				list.add(a);
			}
		}

		return list;
	}
}
