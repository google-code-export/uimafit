/*
 Copyright 2011
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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import static java.util.Arrays.asList;

import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.TOP;

/**
 * Bridge between Java {@link Collection Collections} from different representations of collections
 * of UIMA {@link FeatureStructure FeatureStructures}.
 *
 * @author Richard Eckart de Castilho
 *
 * @param <T> data type.
 */
public abstract class FSCollectionFactory<T extends FeatureStructure> extends AbstractCollection<T> {

	@SuppressWarnings("unchecked")
	public static <T extends FeatureStructure> Collection<T> create(CAS cas, Type type)
	{
		// If the type is an annotation type, we can use the annotation index, which directly
		// provides us with its size. If not, we have to use getAllIndexedFS() which we have to
		// scan from beginning to end in order to determine its size.
		TypeSystem ts = cas.getTypeSystem();
		if (ts.subsumes(cas.getAnnotationType(), type)) {
			return (FSCollectionFactory<T>) create(cas.getAnnotationIndex(type));
		}
		else {
			return create((FSIterator<T>) cas.getIndexRepository().getAllIndexedFS(type));
		}
	}

	public static <T extends FeatureStructure> Collection<T> create(FSIterator<T> aIterator)
	{
		return new FSIteratorAdapter<T>(aIterator);
	}

	public static <T extends AnnotationFS> Collection<T> create(AnnotationIndex<T> aIndex)
	{
		return new AnnotationIndexAdapter<T>(aIndex);
	}

	public static <T extends FeatureStructure> Collection<T> create(ArrayFS aArray)
	{
		return create(aArray, null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends FeatureStructure> Collection<T> create(ArrayFS aArray, Type type)
	{
		TypeSystem ts = aArray.getCAS().getTypeSystem();
		List<FeatureStructure> data = new ArrayList<FeatureStructure>(aArray.size());
		for (int i = 0; i < aArray.size(); i++) {
			FeatureStructure value = aArray.get(i);
			if (value != null && (type == null || ts.subsumes(type, value.getType()))) {
				data.add(value);
			}
		}
		return (Collection<T>) asList(data.toArray(new FeatureStructure[data.size()]));
	}

	// Using TOP here because FSList is only available in the JCas.
	public static <T extends TOP> Collection<T> create(FSList aList)
	{
		return create(aList, null);
	}

	// Using TOP here because FSList is only available in the JCas.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends TOP> Collection<T> create(FSList aList, Type type)
	{
		TypeSystem ts = aList.getCAS().getTypeSystem();
		List<FeatureStructure> data = new ArrayList<FeatureStructure>();
		FSList i = aList;
		while (i instanceof NonEmptyFSList) {
			NonEmptyFSList l = (NonEmptyFSList) i;
			TOP value = l.getHead();
			if (value != null && (type == null || ts.subsumes(type, value.getType()))) {
				data.add(l.getHead());
			}
			i = l.getTail();
		}

		return (Collection) asList(data.toArray(new FeatureStructure[data.size()]));
	}

	private static class FSIteratorAdapter<T extends FeatureStructure> extends FSCollectionFactory<T>
	{
		private volatile int sizeCache = -1;
		private final FSIterator<T> index;

		public FSIteratorAdapter(FSIterator<T> aIterator)
		{
			index = aIterator.copy();
			index.moveToFirst();
		}

		@Override
		public Iterator<T> iterator() {
			return index.copy();
		}

		@Override
		public int size() {
			// Unfortunately FSIterator does not expose the sizes of its internal collection,
			// neither the current position although FSIteratorAggregate has a private field
			// with that information.
			if (sizeCache == -1) {
				synchronized (this) {
					if (sizeCache == -1) {
						FSIterator<T> clone = index.copy();
						clone.moveToFirst();
						sizeCache = 0;
						while (clone.isValid()) {
							sizeCache++;
							clone.moveToNext();
						}
					}
				}
			}

			return sizeCache;
		}
	}

	private static class AnnotationIndexAdapter<T extends AnnotationFS> extends FSCollectionFactory<T>
	{
		private final AnnotationIndex<T> index;

		public AnnotationIndexAdapter(AnnotationIndex<T> aIndex)
		{
			index = aIndex;
		}

		@Override
		public Iterator<T> iterator() {
			return index.iterator();
		}

		@Override
		public int size() {
			return index.size();
		}
	}
}
