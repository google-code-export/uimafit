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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.Subiterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Utility methods for convenient access to the {@link JCas}.
 *
 * @author Richard Eckart de Castilho
 * @author Philip Ogren
 * @author Niklas Jakob
 */
public class JCasUtil {
	/**
	 * Convenience method to iterator over all features structures of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param jCas
	 *            a JCas.
	 * @param type
	 *            the type.
	 * @return An iterable.
	 * @see AnnotationIndex#iterator()
	 * @deprecated use {@link #select}
	 */
	@Deprecated
	public static <T extends TOP> Iterable<T> iterate(final JCas jCas, final Class<T> type) {
		return select(jCas, type);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type occurring within the
	 * scope of a provided annotation.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param container
	 *            the containing annotation.
	 * @param type
	 *            the type.
	 * @return A iterable.
	 * @see #selectCovered(Class, AnnotationFS)
	 * @deprecated use {@link #selectCovered}
	 */
	@Deprecated
	public static <T extends Annotation> Iterable<T> iterate(final Class<T> type,
			final Annotation container) {
		return selectCovered(type, container);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type occurring within the
	 * scope of a provided annotation.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param jCas
	 *            a JCas.
	 * @param container
	 *            the containing annotation.
	 * @param type
	 *            the type.
	 * @return A iterable.
	 * @deprecated use {@link #selectCovered}
	 */
	@Deprecated
	public static <T extends Annotation> Iterable<T> iterate(final JCas jCas, final Class<T> type,
			final Annotation container) {
		return selectCovered(jCas, type, container);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type occurring within the
	 * scope of a provided annotation (sub-iteration).
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param jCas
	 *            a JCas.
	 * @param container
	 *            the containing annotation.
	 * @param type
	 *            the type.
	 * @param ambiguous
	 *            If set to <code>false</code>, resulting iterator will be unambiguous.
	 * @param strict
	 *            Controls if annotations that overlap to the right are considered in or out.
	 * @return A sub-iterator iterable.
	 * @see AnnotationIndex#subiterator(AnnotationFS, boolean, boolean)
	 */
	public static <T extends Annotation> Iterable<T> subiterate(final JCas jCas,
			final Class<T> type, final Annotation container, final boolean ambiguous,
			final boolean strict) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return JCasUtil.iterator(container, type, ambiguous, strict);
			}
		};
	}

	/**
	 * Get an iterator over the given feature structure type.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param jCas
	 *            a JCas.
	 * @param type
	 *            a type.
	 * @return a return value.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends TOP> Iterator<T> iterator(JCas jCas, Class<T> type) {
		return (FSIterator) jCas.getIndexRepository().getAllIndexedFS(getType(jCas, type));
	}

	/**
	 * Convenience method to get a sub-iterator for the specified type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param container
	 *            the containing annotation.
	 * @param type
	 *            the type.
	 * @param ambiguous
	 *            If set to <code>false</code>, resulting iterator will be unambiguous.
	 * @param strict
	 *            Controls if annotations that overlap to the right are considered in or out.
	 * @return A sub-iterator.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AnnotationFS> Iterator<T> iterator(Annotation container,
			Class<T> type, boolean ambiguous, boolean strict) {
		CAS cas = container.getCAS();
		return ((AnnotationIndex<T>) cas.getAnnotationIndex(CasUtil.getType(cas, type)))
				.subiterator(container, ambiguous, strict);
	}

	/**
	 * Get the CAS type for the given JCas wrapper class type.
	 *
	 * @param jCas
	 *            the JCas containing the type system.
	 * @param type
	 *            the JCas wrapper class type.
	 * @return the CAS type.
	 */
	public static Type getType(JCas jCas, Class<?> type) {
		return CasUtil.getType(jCas.getCas(), type);
	}

	/**
	 * Get the CAS type for the given JCas wrapper class type making sure it inherits from
	 * {@link Annotation}.
	 *
	 * @param jCas
	 *            the JCas containing the type system.
	 * @param type
	 *            the JCas wrapper class type.
	 * @return the CAS type.
	 */
	public static Type getAnnotationType(JCas jCas, Class<?> type) {
		return CasUtil.getAnnotationType(jCas.getCas(), type);
	}

	/**
	 * Convenience method select all feature structure from the given type from an array.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param array
	 *            a feature structure array.
	 * @param type
	 *            the type.
	 * @return A collection of the selected type.
	 * @see #selectCovered(Class, AnnotationFS)
	 */
	public static <T extends TOP> Collection<T> select(final ArrayFS array, final Class<T> type) {
		return CasUtil.selectFS(array, CasUtil.getType(array.getCAS(), type.getName()));
	}

	/**
	 * Convenience method select all feature structure from the given type from a list.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param list
	 *            a feature structure list.
	 * @param type
	 *            the type.
	 * @return A collection of the selected type.
	 * @see #selectCovered(Class, AnnotationFS)
	 */
	public static <T extends TOP> Collection<T> select(final FSList list, final Class<T> type) {
		return FSCollectionFactory.create(list, CasUtil.getType(list.getCAS(), type.getName()));
	}

	/**
	 * Convenience method to iterator over all features structures of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param jCas
	 *            the JCas containing the type system.
	 * @param type
	 *            the type.
	 * @return A collection of the selected type.
	 * @see #selectCovered(Class, AnnotationFS)
	 */
	public static <T extends TOP> Collection<T> select(final JCas jCas, final Class<T> type) {
		return CasUtil.selectFS(jCas.getCas(), getType(jCas, type));
	}

	/**
	 * Get a list of annotations of the given annotation type constrained by a 'covering'
	 * annotation. Iterates over all annotations of the given type to find the covered annotations.
	 * Does not use subiterators.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param type
	 *            a UIMA type.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @return a return value.
	 * @see Subiterator
	 */
	public static <T extends AnnotationFS> List<T> selectCovered(Class<T> type,
			AnnotationFS coveringAnnotation) {
		CAS cas = coveringAnnotation.getCAS();
		return CasUtil.selectCovered(cas, CasUtil.getType(cas, type), coveringAnnotation);
	}

	/**
	 * Get a list of annotations of the given annotation type constrained by a 'covering'
	 * annotation. Iterates over all annotations of the given type to find the covered annotations.
	 * Does not use subiterators.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param jCas
	 *            a JCas containing the annotation.
	 * @param type
	 *            a UIMA type.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @return a return value.
	 * @see Subiterator
	 */
	public static <T extends Annotation> List<T> selectCovered(JCas jCas, final Class<T> type,
			Annotation coveringAnnotation) {
		return CasUtil.selectCovered(jCas.getCas(), getType(jCas, type), coveringAnnotation);
	}

	/**
	 * Get a list of annotations of the given annotation type constrained by a 'covering'
	 * annotation. Iterates over all annotations of the given type to find the covered annotations.
	 * Does not use subiterators.
	 * <p>
	 * <b>Note:</b> this is significantly slower than using {@link #selectCovered(JCas, Class, Annotation)}
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param jCas
	 *            a JCas containing the annotation.
	 * @param type
	 *            a UIMA type.
	 * @param begin
	 *            begin offset.
	 * @param end
	 *            end offset.
	 * @return a return value.
	 */
	public static <T extends Annotation> List<T> selectCovered(JCas jCas, final Class<T> type,
			int begin, int end) {
		return CasUtil.selectCovered(jCas.getCas(), getType(jCas, type), begin, end);
	}

	/**
	 * Get a list of annotations of the given annotation type constraint by a certain annotation.
	 * Iterates over all annotations to find the covering annotations.
	 *
	 * <p>
	 * <b>Note:</b> this is <b>REALLY SLOW!</b> You don't want to use this.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param jCas
	 *            a CAS.
	 * @param type
	 *            a UIMA type.
	 * @param begin
	 *            begin offset.
	 * @param end
	 *            end offset.
	 * @return a return value.
	 */
	public static <T extends Annotation> List<T> selectCovering(JCas jCas, Class<T> type, int begin,
			int end) {

		return CasUtil.selectCovering(jCas.getCas(), getType(jCas, type), begin, end);
	}

	/**
	 * Create an index for quickly lookup up the annotations covering a particular annotation.
	 * This is preferable to using {@link #selectCovering(JCas, Class, int, int)} because the
	 * overhead of scanning the CAS occurs only when the index is build. Subsequent lookups to the
	 * index are fast.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param jCas
	 *            a JCas.
	 * @param type
	 * 			  type to create the index for - this is used in lookups.
	 * @param coveringType
	 * 			  type of covering annotations.
	 * @return the index.
	 */
	public static <T extends Annotation, S extends Annotation> Map<T, Collection<S>> indexCovering(
			JCas jCas, Class<T> type, Class<S> coveringType) {
		return CasUtil.indexCovering(jCas.getCas(), getType(jCas, type),
				getType(jCas, coveringType));
	}

	/**
	 * Check if the given annotation contains any annotation of the given type.
	 *
	 * @param jCas
	 *            a JCas containing the annotation.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @param type
	 *            a UIMA type.
	 * @return if an annotation of the given type is present.
	 */
	public static boolean isCovered(JCas jCas, Annotation coveringAnnotation,
			Class<? extends Annotation> type) {
		return selectCovered(jCas, type, coveringAnnotation).size() > 0;
	}

	/**
	 * This method exists simply as a convenience method for unit testing. It is not very efficient
	 * and should not, in general be used outside the context of unit testing.
	 *
	 * @param <T>
	 *            JCas wrapper type.
	 * @param jCas
	 *            a JCas containing the annotation.
	 * @param cls
	 *            a UIMA type.
	 * @param index
	 *            this can be either positive (0 corresponds to the first annotation of a type) or
	 *            negative (-1 corresponds to the last annotation of a type.)
	 * @return an annotation of the given type
	 */
	public static <T extends TOP> T selectByIndex(JCas jCas, Class<T> cls, int index) {
		return CasUtil.<T>selectFSByIndex(jCas.getCas(), getType(jCas, cls), index);
	}

	/**
	 * Get the single instance of the specified type from the JCas.
	 *
	 * @param <T>
	 *            JCas wrapper type.
	 * @param jCas
	 *            a JCas containing the annotation.
	 * @param type
	 *            a UIMA type.
	 * @return the single instance of the given type. throws IllegalArgumentException if not exactly
	 *         one instance if the given type is present.
	 */
	public static <T extends TOP> T selectSingle(JCas jCas, Class<T> type) {
		return CasUtil.<T>selectSingle(jCas.getCas(), getType(jCas, type));
	}

	/**
	 * Returns the n annotations preceding the given annotation
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param aJCas
	 *            a JCas.
	 * @param aType
	 *            a type.
	 * @param annotation
	 *            anchor annotation
	 * @param count
	 *            number of annotations to collect
	 * @return List of aType annotations preceding anchor annotation
	 */
	public static <T extends Annotation> List<T> selectPreceding(JCas aJCas, Class<T> aType,
			Annotation annotation, int count) {
		Type t = aJCas.getTypeSystem().getType(aType.getName());
		return CasUtil.selectPreceding(aJCas.getCas(), t, annotation, count);
	}

	/**
	 * Returns the n annotations following the given annotation
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param aJCas
	 *            a JCas.
	 * @param aType
	 *            a type.
	 * @param annotation
	 *            anchor annotation
	 * @param count
	 *            number of annotations to collect
	 * @return List of aType annotations following anchor annotation
	 */
	public static <T extends Annotation> List<T> selectFollowing(JCas aJCas, Class<T> aType,
			Annotation annotation, int count) {
		Type t = aJCas.getTypeSystem().getType(aType.getName());
		return CasUtil.selectFollowing(aJCas.getCas(), t, annotation, count);
	}

	/**
	 * Test if a JCas contains an annotation of the given type.
	 *
	 * @param <T>
	 *            the annotation type.
	 * @param aJCas
	 *            a JCas.
	 * @param aType
	 *            a annotation class.
	 * @return {@code true} if there is at least one annotation of the given type in the JCas.
	 */
	public static <T extends TOP> boolean exists(JCas aJCas, Class<T> aType) {
		return JCasUtil.iterator(aJCas, aType).hasNext();
	}

	/**
	 * Convenience method to get the specified view or a default view if the requested view does not
	 * exist. The default can also be {@code null}.
	 *
	 * @param jcas
	 *            a JCas
	 * @param viewName
	 *            the requested view.
	 * @param fallback
	 *            the default view if the requested view does not exist.
	 * @return the requested view or the default if the requested view does not exist.
	 * @throws IllegalStateException
	 *             if the JCas wrapper cannot be obtained.
	 */
	public static JCas getView(JCas jcas, String viewName, JCas fallback) {
		try {
			CAS fallbackCas = fallback != null ? fallback.getCas() : null;
			CAS view = CasUtil.getView(jcas.getCas(), viewName, fallbackCas);
			return view != null ? view.getJCas() : null;
		}
		catch (CASException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Convenience method to get the specified view or create a new view if the requested view does
	 * not exist.
	 *
	 * @param jcas
	 *            a JCas
	 * @param viewName
	 *            the requested view.
	 * @param create
	 *            the view is created if it does not exist.
	 * @return the requested view
	 * @throws IllegalStateException
	 *             if the JCas wrapper cannot be obtained.
	 */
	public static JCas getView(JCas jcas, String viewName, boolean create) {
		try {
			CAS view = CasUtil.getView(jcas.getCas(), viewName, create);
			return view != null ? view.getJCas() : null;
		}
		catch (CASException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Fetch the text covered by the specified annotations and return it as a list of strings.
	 *
	 * @param <T>
	 *            UIMA JCas type.
	 * @param iterable
	 *            annotation container.
	 * @return list of covered strings.
	 */
	public static <T extends AnnotationFS> List<String> toText(Iterable<T> iterable) {
		return CasUtil.toText(iterable);
	}
}
