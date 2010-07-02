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
package org.uimafit.util;

import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.Subiterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Utility methods for convenient access to the {@link JCas}.
 *
 * @author Richard Eckart de Castilho
 */
public class JCasUtil
{
	/**
	 * Convenience method to iterator over all annotations of a given type.
	 *
	 * @param <T> the iteration type.
	 * @param jCas a JCas.
	 * @param container the containing annotation.
	 * @param type the type.
	 * @return An iterable.
	 * @see AnnotationIndex#iterator()
	 */
	public static <T extends AnnotationFS> Iterable<T> iterate(final JCas jCas,
			final Class<T> type)
	{
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return JCasUtil.iterator(jCas, type);
			}
		};
	}

	/**
	 * Convenience method to iterator over all annotations of a given type
	 * occurring within the scope of a provided annotation.
	 *
	 * @param <T> the iteration type.
	 * @param jCas a JCas.
	 * @param container the containing annotation.
	 * @param type the type.
	 * @return A iterable.
	 * @see {@link #selectCovered(Class, AnnotationFS)}
	 */
	public static <T extends Annotation> Iterable<T> iterate(final Class<T> type, final Annotation container)
	{
		return selectCovered(type, container);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type
	 * occurring within the scope of a provided annotation.
	 *
	 * @param <T> the iteration type.
	 * @param jCas a JCas.
	 * @param container the containing annotation.
	 * @param type the type.
	 * @return A iterable.
	 */
	public static <T extends Annotation> Iterable<T> iterate(final JCas jCas,
			final Class<T> type, final Annotation container)
	{
		return selectCovered(jCas, type, container);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type
	 * occurring within the scope of a provided annotation (sub-iteration).
	 *
	 * @param <T> the iteration type.
	 * @param jCas a JCas.
	 * @param container the containing annotation.
	 * @param type the type.
     * @param ambiguous If set to <code>false</code>, resulting iterator will be unambiguous.
     * @param strict Controls if annotations that overlap to the right are considered in or out.
	 * @return A sub-iterator iterable.
	 * @see AnnotationIndex#subiterator(AnnotationFS, boolean, boolean)
	 */
	public static <T extends Annotation> Iterable<T> subiterate(final JCas jCas,
			final Class<T> type, final Annotation container, final boolean ambiguous,
			final boolean strict)
	{
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return JCasUtil.iterator(container, type, ambiguous, strict);
			}
		};
	}

	/**
	 * Get an iterator over the given annotation type.
	 *
	 * @param <T> the JCas type.
	 * @param jCas a JCas.
	 * @param type a type.
	 * @return a return value.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AnnotationFS> Iterator<T> iterator(JCas jCas,
			Class<T> type)
	{
		return ((AnnotationIndex<T>) jCas.getAnnotationIndex(getType(jCas, type))).iterator();
	}

	/**
	 * Convenience method to get a sub-iterator for the specified type.
	 *
	 * @param <T> the iteration type.
	 * @param container the containing annotation.
	 * @param type the type.
     * @param ambiguous If set to <code>false</code>, resulting iterator will be unambiguous.
     * @param strict Controls if annotations that overlap to the right are considered in or out.
	 * @return A sub-iterator.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AnnotationFS> Iterator<T> iterator(
			Annotation container, Class<T> type, boolean ambiguous, boolean strict)
	{
		CAS cas = container.getCAS();
		return ((AnnotationIndex<T>) cas.getAnnotationIndex(CasUtil.getType(cas, type))).subiterator(
				container, ambiguous, strict);
	}

	public static Type getType(JCas jCas, Class<?> type)
	{
		return CasUtil.getType(jCas.getCas(), type);
	}

	/**
	 * Get a list of annotations of the given annotation type constrained by a
	 * 'covering' annotation. Iterates over all annotations of the given type to
	 * find the covered annotations. Does not use subiterators.
	 *
	 * @param <T> the JCas type.
	 * @param type a UIMA type.
	 * @return a return value.
	 * @see {@link Subiterator}
	 */
	public static <T extends AnnotationFS> List<T> selectCovered(
			Class<T> type, AnnotationFS coveringAnnotation)
	{
		CAS cas = coveringAnnotation.getCAS();
		return CasUtil.selectCovered(cas, CasUtil.getType(cas, type), coveringAnnotation);
	}

	/**
	 * Get a list of annotations of the given annotation type constrained by a
	 * 'covering' annotation. Iterates over all annotations of the given type to
	 * find the covered annotations. Does not use subiterators.
	 *
	 * @param <T> the JCas type.
	 * @param aCas a JCas containing the annotation.
	 * @param type a UIMA type.
	 * @return a return value.
	 * @see {@link Subiterator}
	 */
	public static <T extends Annotation> List<T> selectCovered(
			JCas jCas, final Class<T> type, Annotation container)
	{
		return CasUtil.selectCovered(jCas.getCas(), getType(jCas, type), container);
	}

	/**
	 * This method exists simply as a convenience method for unit testing. It is
	 * not very efficient and should not, in general be used outside the context
	 * of unit testing.
	 *
	 * @param jCas
	 * @param cls
	 * @param index
	 *            this can be either positive (0 corresponds to the first
	 *            annotation of a type) or negative (-1 corresponds to the last
	 *            annotation of a type.)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T selectByIndex(JCas jCas, Class<T> cls, int index) {
		FSIterator i = jCas.getAnnotationIndex(getType(jCas, cls)).iterator();
		int n = index;
		i.moveToFirst();
		if (n > 0) {
			while (n > 0 && i.isValid()) {
				i.moveToNext();
				n--;
			}
		}
		if (n < 0) {
			i.moveToLast();
			while (n < -1 && i.isValid()) {
				i.moveToPrevious();
				n++;
			}
		}
	
		return i.isValid() ? (T) i.get() : null;
	}
}
