/*
 Copyright 2009-2010
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.Subiterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Utility methods for convenient access to the {@link CAS}.
 *
 * @author Richard Eckart de Castilho
 * @author Niklas Jakob
 */
public class CasUtil {
	/**
	 * Package name of JCas wrapper classes built into UIMA.
	 */
	public static final String UIMA_BUILTIN_JCAS_PREFIX = "org.apache.uima.jcas.";

	/**
	 * Convenience method to iterator over all feature structures of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            a CAS.
	 * @param type
	 *            the type.
	 * @return An iterable.
	 * @see AnnotationIndex#iterator()
	 * @deprecated use {@link #selectFS}
	 */
	@Deprecated
	public static <T extends FeatureStructure> Iterable<T> iterateFS(final CAS cas, final Type type) {
		return selectFS(cas, type);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            a CAS.
	 * @param type
	 *            the type.
	 * @return An iterable.
	 * @see AnnotationIndex#iterator()
	 * @deprecated use {@link #select}
	 */
	@Deprecated
	public static <T extends AnnotationFS> Iterable<T> iterate(final CAS cas, final Type type) {
		return select(cas, type);
	}

	/**
	 * Get an iterator over the given feature structures type.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param type
	 *            a type.
	 * @return a return value.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends FeatureStructure> Iterator<T> iteratorFS(CAS cas, Type type) {
		return ((FSIterator<T>) cas.getIndexRepository().getAllIndexedFS(type));
	}

	/**
	 * Get an iterator over the given annotation type.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param type
	 *            a type.
	 * @return a return value.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AnnotationFS> Iterator<T> iterator(CAS cas, Type type) {
		return ((AnnotationIndex<T>) cas.getAnnotationIndex(type)).iterator();
	}

	/**
	 * Get the CAS type for the given JCas wrapper class.
	 *
	 * @param cas
	 *            the CAS hosting the type system.
	 * @param type
	 *            the JCas wrapper class.
	 * @return the CAS type.
	 */
	public static Type getType(CAS cas, Class<?> type) {
		return getType(cas, type.getName());
	}

	/**
	 * Get the CAS type for the given name.
	 *
	 * @param cas
	 *            the CAS hosting the type system.
	 * @param typeName
	 *            the fully qualified type name.
	 * @return the CAS type.
	 */
	public static Type getType(CAS cas,String typeName) {
		if (typeName.startsWith(UIMA_BUILTIN_JCAS_PREFIX)) {
			typeName = "uima." + typeName.substring(UIMA_BUILTIN_JCAS_PREFIX.length());
		}
		Type t = cas.getTypeSystem().getType(typeName);
		if (t == null) {
			throw new IllegalArgumentException("Undeclared type [" + typeName + "]");
		}
		return t;
	}

	/**
	 * Get the CAS type for the given JCas wrapper class  making sure it is or inherits from
	 * {@link Annotation}.
	 *
	 * @param cas
	 *            the CAS hosting the type system.
	 * @param type
	 *            the JCas wrapper class.
	 * @return the CAS type.
	 */
	public static Type getAnnotationType(CAS cas, Class<?> type) {
		Type t = getType(cas, type);
		if (!cas.getTypeSystem().subsumes(cas.getAnnotationType(), t)) {
			throw new IllegalArgumentException("Type ["+type.getName()+"] is not an annotation type");
		}
		return t;
	}

	/**
	 * Get the CAS type for the given name making sure it is or inherits from Annotation.
	 *
	 * @param cas
	 *            the CAS hosting the type system.
	 * @param typeName
	 *            the fully qualified type name.
	 * @return the CAS type.
	 */
	public static Type getAnnotationType(CAS cas,String typeName) {
		Type t = getType(cas, typeName);
		if (!cas.getTypeSystem().subsumes(cas.getAnnotationType(), t)) {
			throw new IllegalArgumentException("Type ["+typeName+"] is not an annotation type");
		}
		return t;
	}

	/**
	 * Convenience method to iterator over all feature structures of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param array
	 *            features structure array.
	 * @param type
	 *            the type.
	 * @return A collection of the selected type.
	 */
	public static <T extends FeatureStructure> Collection<T> selectFS(ArrayFS array, Type type) {
		return FSCollectionFactory.create(array, type);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param array
	 *            features structure array.
	 * @param type
	 *            the type.
	 * @return A collection of the selected type.
	 */
	public static <T extends AnnotationFS> Collection<T> select(ArrayFS array, Type type) {
		CAS cas = array.getCAS();
		if (!cas.getTypeSystem().subsumes(cas.getAnnotationType(), type)) {
			throw new IllegalArgumentException("Type ["+type.getName()+"] is not an annotation type");
		}
		return FSCollectionFactory.create(cas, type);
	}

	/**
	 * Convenience method to iterator over all feature structures of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param array
	 *            features structure array.
	 * @param typeName
	 *            the fully qualified type name.
	 * @return A collection of the selected type.
	 */
	public static <T extends FeatureStructure> Collection<T> selectFS(ArrayFS array, String typeName) {
		return selectFS(array, getType(array.getCAS(), typeName));
	}

	/**
	 * Convenience method to iterator over all annotations of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param array
	 *            features structure array.
	 * @param typeName
	 *            the fully qualified type name.
	 * @return A collection of the selected type.
	 */
	public static <T extends AnnotationFS> Collection<T> select(ArrayFS array, String typeName) {
		return select(array, getAnnotationType(array.getCAS(), typeName));
	}

	/**
	 * Convenience method to iterator over all feature structures of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            the CAS containing the type system.
	 * @param type
	 *            the type.
	 * @return A collection of the selected type.
	 */
	public static <T extends FeatureStructure> Collection<T> selectFS(final CAS cas, final Type type) {
		return FSCollectionFactory.create(cas, type);
	}

	/**
	 * Convenience method to iterator over all annotations of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            the CAS containing the type system.
	 * @param type
	 *            the type.
	 * @return A collection of the selected type.
	 */
	public static <T extends AnnotationFS> Collection<T> select(final CAS cas, final Type type) {
		if (!cas.getTypeSystem().subsumes(cas.getAnnotationType(), type)) {
			throw new IllegalArgumentException("Type ["+type.getName()+"] is not an annotation type");
		}
		return FSCollectionFactory.create(cas, type);
	}

	/**
	 * Convenience method to iterator over all feature structures of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            the CAS containing the type system.
	 * @param typeName
	 *            the fully qualified type name.
	 * @return A collection of the selected type.
	 */
	public static <T extends FeatureStructure> Collection<T> selectFS(final CAS cas, final String typeName) {
		return selectFS(cas, getType(cas, typeName));
	}

	/**
	 * Convenience method to iterator over all annotations of a given type.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            the CAS containing the type system.
	 * @param typeName
	 *            the fully qualified type name.
	 * @return A collection of the selected type.
	 */
	public static <T extends AnnotationFS> Collection<T> select(final CAS cas, final String typeName) {
		return select(cas, getAnnotationType(cas, typeName));
	}

	/**
	 * Get a list of annotations of the given annotation type constraint by a certain annotation.
	 * Iterates over all annotations of the given type to find the covered annotations. Does not use
	 * subiterators and does not respect type prioritites. Was adapted from {@link Subiterator}.
	 * Uses the same approach except that type priorities are ignored.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param type
	 *            a UIMA type.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @return a return value.
	 * @see Subiterator
	 */
	public static <T extends AnnotationFS> List<T> selectCovered(CAS cas, Type type,
			AnnotationFS coveringAnnotation) {
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

			assert !(a.getBegin() < coveringAnnotation.getBegin()) : "Illegal begin "
					+ a.getBegin() + " in [" + coveringAnnotation.getBegin() + ".."
					+ coveringAnnotation.getEnd() + "]";

			assert !(a.getEnd() < coveringAnnotation.getBegin()) : "Illegal end " + a.getEnd()
					+ " in [" + coveringAnnotation.getBegin() + ".." + coveringAnnotation.getEnd()
					+ "]";

			if (!a.equals(coveringAnnotation)) {
				list.add(a);
			}
		}

		return list;
	}

	/**
	 * This method exists simply as a convenience method for unit testing. It is not very efficient
	 * and should not, in general be used outside the context of unit testing.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            a CAS containing the feature structure.
	 * @param typeName
	 *            the fully qualified type name.
	 * @param index
	 *            this can be either positive (0 corresponds to the first annotation of a type) or
	 *            negative (-1 corresponds to the last annotation of a type.)
	 * @return an annotation of the given type
	 */
	public static <T extends FeatureStructure> T selectFSByIndex(CAS cas, String typeName, int index) {
		return selectFSByIndex(cas, getType(cas, typeName), index);
	}

	/**
	 * This method exists simply as a convenience method for unit testing. It is not very efficient
	 * and should not, in general be used outside the context of unit testing.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            a CAS containing the annotation.
	 * @param typeName
	 *            the fully qualified type name.
	 * @param index
	 *            this can be either positive (0 corresponds to the first annotation of a type) or
	 *            negative (-1 corresponds to the last annotation of a type.)
	 * @return an annotation of the given type
	 */
	public static <T extends AnnotationFS> T selectByIndex(CAS cas, String typeName, int index) {
		return selectByIndex(cas, getAnnotationType(cas, typeName), index);
	}

	/**
	 * Get a list of annotations of the given annotation type constraint by a certain annotation.
	 * Iterates over all annotations of the given type to find the covered annotations. Does not use
	 * subiterators and does not respect type prioritites. Was adapted from {@link Subiterator}.
	 * Uses the same approach except that type priorities are ignored.
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param typeName
	 *            the fully qualified type name.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @return a return value.
	 * @see Subiterator
	 */
	public static <T extends AnnotationFS> List<T> selectCovered(CAS cas, String typeName,
			AnnotationFS coveringAnnotation) {
		return selectCovered(cas, getAnnotationType(cas, typeName), coveringAnnotation);
	}

	/**
	 * This method exists simply as a convenience method for unit testing. It is not very efficient
	 * and should not, in general be used outside the context of unit testing.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            a CAS containing the feature structure.
	 * @param type
	 *            a UIMA type.
	 * @param index
	 *            this can be either positive (0 corresponds to the first annotation of a type) or
	 *            negative (-1 corresponds to the last annotation of a type.)
	 * @return an annotation of the given type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends FeatureStructure> T selectFSByIndex(CAS cas, Type type, int index) {
		FSIterator<FeatureStructure> i = cas.getIndexRepository().getAllIndexedFS(type);
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

	/**
	 * This method exists simply as a convenience method for unit testing. It is not very efficient
	 * and should not, in general be used outside the context of unit testing.
	 *
	 * @param <T>
	 *            the iteration type.
	 * @param cas
	 *            a CAS containing the annotation.
	 * @param type
	 *            a UIMA type.
	 * @param index
	 *            this can be either positive (0 corresponds to the first annotation of a type) or
	 *            negative (-1 corresponds to the last annotation of a type.)
	 * @return an annotation of the given type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AnnotationFS> T selectByIndex(CAS cas, Type type, int index) {
		if (!cas.getTypeSystem().subsumes(cas.getAnnotationType(), type)) {
			throw new IllegalArgumentException("Type ["+type.getName()+"] is not an annotation type");
		}
		FSIterator<AnnotationFS> i = cas.getAnnotationIndex(type).iterator();
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

	/**
	 * Get the single instance of the specified type from the JCas.
	 *
	 * @param <T>
	 *            JCas wrapper type.
	 * @param cas
	 *            a JCas containing the annotation.
	 * @param type
	 *            a UIMA type.
	 * @return the single instance of the given type. throws IllegalArgumentException if not exactly
	 *         one instance if the given type is present.
	 */
	public static <T extends FeatureStructure> T selectSingle(CAS cas, Type type) {
		FSIterator<FeatureStructure> iterator = cas.getIndexRepository().getAllIndexedFS(type);

		if (!iterator.hasNext()) {
			throw new IllegalArgumentException("CAS does not contain any [" + type.getName()+"]");
		}

		@SuppressWarnings("unchecked")
		T result = (T) iterator.next();

		if (iterator.hasNext()) {
			throw new IllegalArgumentException("CAS contains more than one [" + type.getName()+"]");
		}

		return result;
	}

	/**
	 * Get the single instance of the specified type from the JCas.
	 *
	 * @param <T>
	 *            JCas wrapper type.
	 * @param cas
	 *            a JCas containing the feature structure.
	 * @param typeName
	 *            the fully qualified type name.
	 * @return the single instance of the given type. throws IllegalArgumentException if not exactly
	 *         one instance if the given type is present.
	 */
	public static <T extends FeatureStructure> T selectSingle(CAS cas, String typeName) {
		return selectSingle(cas, getType(cas, typeName));
	}

	/**
	 * Returns the n annotations preceding the given annotation
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param type
	 *            a UIMA type.
	 * @param annotation
	 *            anchor annotation
	 * @param count
	 *            number of annotations to collect
	 * @return List of aType annotations preceding anchor annotation
	 */
	public static <T extends AnnotationFS> List<T> selectPreceding(CAS cas, Type type,
			Annotation annotation, int count) {
		if (!cas.getTypeSystem().subsumes(cas.getAnnotationType(), type)) {
			throw new IllegalArgumentException("Type ["+type.getName()+"] is not an annotation type");
		}
		List<T> precedingAnnotations = new LinkedList<T>();

		// move to first previous annotation
		FSIterator<AnnotationFS> itr = cas.getAnnotationIndex(type).iterator();
		itr.moveTo(annotation);

		int currentAnnotation = 0;

		itr.moveToPrevious();

		while (currentAnnotation < count && itr.isValid()) {
			@SuppressWarnings("unchecked")
			T buf = (T) itr.get();
			precedingAnnotations.add(buf);

			currentAnnotation++;
			itr.moveToPrevious();
		}

		// return in correct order
		Collections.reverse(precedingAnnotations);

		return precedingAnnotations;
	}

	/**
	 * Returns the n annotations preceding the given annotation
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param typeName
	 *            the fully qualified type name.
	 * @param annotation
	 *            anchor annotation
	 * @param count
	 *            number of annotations to collect
	 * @return List of aType annotations preceding anchor annotation
	 */
	public static <T extends AnnotationFS> List<T> selectPreceding(CAS cas, String typeName,
			Annotation annotation, int count) {
		return selectPreceding(cas, getAnnotationType(cas, typeName), annotation, count);
	}

	/**
	 * Returns the n annotations following the given annotation
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param type
	 *            a UIMA type.
	 * @param annotation
	 *            anchor annotation
	 * @param count
	 *            number of annotations to collect
	 * @return List of aType annotations following anchor annotation
	 */
	public static <T extends AnnotationFS> List<T> selectFollowing(CAS cas, Type type,
			Annotation annotation, int count) {
		if (!cas.getTypeSystem().subsumes(cas.getAnnotationType(), type)) {
			throw new IllegalArgumentException("Type ["+type.getName()+"] is not an annotation type");
		}
		List<T> followingAnnotations = new LinkedList<T>();

		// move to first previous annotation
		FSIterator<AnnotationFS> itr = cas.getAnnotationIndex(type).iterator();
		itr.moveTo(annotation);

		int currentAnnotation = 0;

		itr.moveToNext();

		while (currentAnnotation < count && itr.isValid()) {
			@SuppressWarnings("unchecked")
			T buf = (T) itr.get();
			followingAnnotations.add(buf);

			currentAnnotation++;
			itr.moveToNext();
		}

		return followingAnnotations;
	}

	/**
	 * Returns the n annotations following the given annotation
	 *
	 * @param <T>
	 *            the JCas type.
	 * @param cas
	 *            a CAS.
	 * @param typeName
	 *            the fully qualified type name.
	 * @param annotation
	 *            anchor annotation
	 * @param count
	 *            number of annotations to collect
	 * @return List of aType annotations following anchor annotation
	 */
	public static <T extends AnnotationFS> List<T> selectFollowing(CAS cas, String typeName,
			Annotation annotation, int count) {
		return selectFollowing(cas, getAnnotationType(cas, typeName), annotation, count);
	}

	/**
	 * Convenience method to get the specified view or a default view if the requested view does not
	 * exist. The default can also be {@code null}.
	 *
	 * @param cas
	 *            a CAS
	 * @param viewName
	 *            the requested view.
	 * @param fallback
	 *            the default view if the requested view does not exist.
	 * @return the requested view or the default if the requested view does not exist.
	 */
	public static CAS getView(CAS cas, String viewName, CAS fallback) {
		CAS view;
		try {
			view = cas.getView(viewName);
		}
		catch (CASRuntimeException e) {
			// use fall-back view instead
			view = fallback;
		}
		return view;
	}

	/**
	 * Convenience method to get the specified view or create a new view if the requested view does
	 * not exist.
	 *
	 * @param cas
	 *            a CAS
	 * @param viewName
	 *            the requested view.
	 * @param create
	 *            the view is created if it does not exist.
	 * @return the requested view
	 * @throws IllegalArgumentException
	 *             if the view does not exist and is not to be created.
	 */
	public static CAS getView(CAS cas, String viewName, boolean create) {
		CAS view = null;
		try {
			view = cas.getView(viewName);
		}
		catch (CASRuntimeException e) {
			// View does not exist
		}

		if (view == null && create) {
			view = cas.createView(viewName);
		}

		if (view == null) {
			throw new IllegalArgumentException("No view with name [" + viewName + "]");
		}

		return view;
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
		return toText(iterable.iterator());
	}

	/**
	 * Fetch the text covered by the specified annotations and return it as a list of strings.
	 *
	 * @param <T>
	 *            UIMA JCas type.
	 * @param iterator
	 *            annotation iterator.
	 * @return list of covered strings.
	 */
	public static <T extends AnnotationFS> List<String> toText(Iterator<T> iterator) {
		List<String> text = new ArrayList<String>();
		while (iterator.hasNext()) {
			AnnotationFS a = iterator.next();
			text.add(a.getCoveredText());
		}
		return text;
	}
}
