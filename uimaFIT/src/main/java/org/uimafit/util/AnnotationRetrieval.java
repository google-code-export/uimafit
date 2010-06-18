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

package org.uimafit.util;

import java.util.Iterator;

import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class AnnotationRetrieval {

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
	public static <T extends Annotation> T get(JCas jCas, Class<T> cls, int index) {
		int type;
		try {
			type = (Integer) cls.getField("type").get(null);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException();
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException();
		}

		// TODO we should probably iterate from the end rather than
		// iterating forward from the beginning.
		FSIndex<Annotation> fsIndex = jCas.getAnnotationIndex(type);
		if (index < 0) index = fsIndex.size() + index;

		if (index < 0 || index >= fsIndex.size()) return null;
		FSIterator<Annotation> iterator = fsIndex.iterator();
		Object returnValue = iterator.next();
		for (int i = 0; i < index; i++) {
			returnValue = iterator.next();
		}
		return cls.cast(returnValue);
	}

	
	public static <T> Iterator<T> get(JCas jCas, Class<T> annotationClass) {
		return new AnnotationIterator<T>(jCas, annotationClass);
	}
	
	public static class AnnotationIterator<T> implements Iterator<T>{
		
		FSIterator<Annotation> fsIterator;
		Class<T> annotationClass;
		
		public AnnotationIterator(JCas jCas, Class<T> annotationClass) {
			int type;
			try {
				type = annotationClass.getField("type").getInt(null);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			AnnotationIndex<Annotation> index = jCas.getAnnotationIndex(type);
			this.fsIterator = index.iterator();
			this.annotationClass = annotationClass;
		}
		
		public boolean hasNext() {
			return fsIterator.hasNext();
		}

		public T next() {
			Object next = fsIterator.next();
			return annotationClass.cast(next);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
