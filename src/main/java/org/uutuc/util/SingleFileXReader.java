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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XCASDeserializer;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.xml.sax.SAXException;

/**
 * <br>
 * 
 * This collection reader allows one to read in a single XMI or XCAS file. It's
 * primary purpose is to help out a couple JCasFactory create methods.  However,
 * it is also used for this project unit tests as an example collection reader.  
 * 
 * @author Steven Bethard, Philip Ogren
 */
public class SingleFileXReader extends CollectionReader_ImplBase {

	/**
	 * "FileName" is a single, required, string parameter that takes either the
	 * name of a single file or the root directory containing all the files to
	 * be processed.
	 */
	public static final String PARAM_FILE_NAME = "org.uutuc.util.SingleFileXReader.PARAM_FILE_NAME";

	/**
	 * "XMLScheme" is a single, optional, string parameter that specifies the
	 * UIMA XML serialization scheme that should be used. Valid values for this
	 * parameter are "XMI" (default) and "XCAS".
	 * 
	 * @see XmiCasSerializer
	 * @see XCASSerializer
	 */
	public static final String PARAM_XML_SCHEME = "org.uutuc.util.SingleFileXReader.PARAM_XML_SCHEME";

	public static final String XMI = "XMI";

	public static final String XCAS = "XCAS";

	private boolean useXMI = true;

	private boolean hasNext = true;

	private File file;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();

		// get the input directory
		String fileName = (String) this.getUimaContext().getConfigParameterValue(PARAM_FILE_NAME);

		if (fileName == null) {
			throw new ResourceInitializationException(ResourceInitializationException.CONFIG_SETTING_ABSENT,
					new Object[] { PARAM_FILE_NAME });
		}
		file = new File(fileName);

		String xmlScheme = (String) this.getUimaContext().getConfigParameterValue(PARAM_XML_SCHEME);

		if (xmlScheme.equals(XMI)) useXMI = true;
		else if (xmlScheme.equals(XCAS)) useXMI = false;
		else throw new ResourceInitializationException(String.format(
				"parameter '%1$s' must be either '%2$s' or '%3$s' or left empty.", PARAM_XML_SCHEME, XMI, XCAS), null);

	}

	public void getNext(CAS cas) throws IOException, CollectionException {
		FileInputStream inputStream = new FileInputStream(file);

		try {
			if (useXMI) XmiCasDeserializer.deserialize(inputStream, cas);
			else XCASDeserializer.deserialize(inputStream, cas);
		}
		catch (SAXException e) {
			throw new CollectionException(e);
		}
		finally {
			inputStream.close();
		}

		inputStream.close();
		hasNext = false;
	}

	public void close() throws IOException {

	}

	public Progress[] getProgress() {
		if (hasNext) {
			return new Progress[] { new ProgressImpl(0, 1, ProgressImpl.ENTITIES) };
		}
		return new Progress[] { new ProgressImpl(1, 1, ProgressImpl.ENTITIES) };
	}

	public boolean hasNext() throws IOException, CollectionException {
		return hasNext;
	}

}
