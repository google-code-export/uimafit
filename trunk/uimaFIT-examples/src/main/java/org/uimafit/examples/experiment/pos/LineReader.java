package org.uimafit.examples.experiment.pos;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

/**
 * This collection reader is meant for example purposes only. For a much more
 * robust and complete line reader implementation, please see
 * org.cleartk.util.linereader.LineReader.
 * 
 * This collection reader takes a single file and produces one JCas for each
 * line in the file putting the text of the line into the default view.
 * 
 * @author Philip Ogren
 * 
 */
public class LineReader extends JCasCollectionReader_ImplBase {

	public static final String PARAM_INPUT_FILE = ConfigurationParameterFactory.createConfigurationParameterName(LineReader.class, "inputFile");

	@ConfigurationParameter
	private File inputFile;

	private String[] lines;

	private int lineIndex = 0;

	@Override
	public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		try {
			lines = FileUtil.loadListOfStrings(inputFile);
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	public boolean hasNext() throws IOException, CollectionException {
		return lineIndex < lines.length;
	}

	public void getNext(JCas jCas) throws IOException, CollectionException {
		jCas.setDocumentText(lines[lineIndex]);
		lineIndex++;
	}

	public Progress[] getProgress() {
		Progress progress = new ProgressImpl(lineIndex, lines.length, Progress.ENTITIES);
		return new Progress[] { progress };
	}

}
