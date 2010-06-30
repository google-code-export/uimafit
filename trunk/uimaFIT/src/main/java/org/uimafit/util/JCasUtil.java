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
	 * @param aJCas a JCas.
	 * @param aContainer the containing annotation.
	 * @param aType the type.
	 * @return An iterable.
	 * @see AnnotationIndex#iterator()
	 */
	public static <T extends AnnotationFS> Iterable<T> iterate(final JCas aJCas,
			final Class<T> aType)
	{
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return getAnnotationIterator(aJCas, aType);
			}
		};
	}

	/**
	 * Convenience method to iterator over all annotations of a given type
	 * occurring within the scope of a provided annotation.
	 *
	 * @param <T> the iteration type.
	 * @param aJCas a JCas.
	 * @param aContainer the containing annotation.
	 * @param aType the type.
	 * @return A iterable.
	 * @see {@link #getCoveredAnnotations(Class, AnnotationFS)}
	 */
	public static <T extends Annotation> Iterable<T> iterate(final Class<T> aType, final Annotation aContainer)
	{
		return getCoveredAnnotations(aType, aContainer);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type
	 * occurring within the scope of a provided annotation.
	 *
	 * @param <T> the iteration type.
	 * @param aJCas a JCas.
	 * @param aContainer the containing annotation.
	 * @param aType the type.
	 * @return A iterable.
	 */
	public static <T extends Annotation> Iterable<T> iterate(final JCas aJCas,
			final Class<T> aType, final Annotation aContainer)
	{
		return getCoveredAnnotations(aJCas, aType, aContainer);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type
	 * occurring within the scope of a provided annotation (sub-iteration).
	 *
	 * @param <T> the iteration type.
	 * @param aJCas a JCas.
	 * @param aContainer the containing annotation.
	 * @param aType the type.
     * @param aAmbiguous If set to <code>false</code>, resulting iterator will be unambiguous.
     * @param aStrict Controls if annotations that overlap to the right are considered in or out.
	 * @return A sub-iterator iterable.
	 * @see AnnotationIndex#subiterator(AnnotationFS, boolean, boolean)
	 */
	public static <T extends Annotation> Iterable<T> subiterate(final JCas aJCas,
			final Class<T> aType, final Annotation aContainer, final boolean aAmbiguous,
			final boolean aStrict)
	{
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return getAnnotationIterator(aContainer, aType, aAmbiguous, aStrict);
			}
		};
	}

	/**
	 * Get an iterator over the given annotation type.
	 *
	 * @param <T> the JCas type.
	 * @param aJCas a JCas.
	 * @param aType a type.
	 * @return a return value.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AnnotationFS> Iterator<T> getAnnotationIterator(JCas aJCas,
			Class<T> aType)
	{
		return ((AnnotationIndex<T>) aJCas.getAnnotationIndex(getType(aJCas, aType))).iterator();
	}

	/**
	 * Convenience method to get a sub-iterator for the specified type.
	 *
	 * @param <T> the iteration type.
	 * @param aContainer the containing annotation.
	 * @param aType the type.
     * @param aAmbiguous If set to <code>false</code>, resulting iterator will be unambiguous.
     * @param aStrict Controls if annotations that overlap to the right are considered in or out.
	 * @return A sub-iterator.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AnnotationFS> Iterator<T> getAnnotationIterator(
			Annotation aContainer, Class<T> aType, boolean aAmbigous, boolean aStrict)
	{
		CAS cas = aContainer.getCAS();
		return ((AnnotationIndex<T>) cas.getAnnotationIndex(CasUtil.getType(cas, aType))).subiterator(
				aContainer, aAmbigous, aStrict);
	}

	public static Type getType(JCas aJCas, Class<?> aType)
	{
		return CasUtil.getType(aJCas.getCas(), aType);
	}

	/**
	 * Get a list of annotations of the given annotation type constraint by a
	 * certain annotation. Iterates over all annotations of the given type to
	 * find the covered annotations. Does not use subiterators.
	 *
	 * @param <T> the JCas type.
	 * @param aType a UIMA type.
	 * @return a return value.
	 * @see {@link Subiterator}
	 */
	public static <T extends AnnotationFS> List<T> getCoveredAnnotations(
			Class<T> aType, AnnotationFS annotation)
	{
		CAS cas = annotation.getCAS();
		return CasUtil.getCoveredAnnotations(cas, CasUtil.getType(cas, aType), annotation);
	}

	/**
	 * Get a list of annotations of the given annotation type constraint by a
	 * certain annotation. Iterates over all annotations of the given type to
	 * find the covered annotations. Does not use subiterators.
	 *
	 * @param <T> the JCas type.
	 * @param aCas a JCas containing the annotation.
	 * @param aType a UIMA type.
	 * @return a return value.
	 * @see {@link Subiterator}
	 */
	public static <T extends Annotation> List<T> getCoveredAnnotations(
			JCas aJCas, final Class<T> aType, Annotation aContainer)
	{
		return CasUtil.getCoveredAnnotations(aJCas.getCas(), getType(aJCas, aType), aContainer);
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
	public static <T extends Annotation> T get(JCas jCas, Class<T> cls, int index) {
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
