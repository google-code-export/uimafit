package org.uimafit.util;

import org.apache.uima.jcas.JCas;
import org.uimafit.type.AnalyzedText;
import org.uimafit.util.AnnotationRetrieval;

public class TypeSystemUtil {

	
	public static String getAnalyzedText(JCas jCas) {
		return _getAnalyzedText(jCas).getText();
	}

	public static void setAnalyzedText(JCas jCas, String text) {
		 _getAnalyzedText(jCas).setText(text);
	}

	private static AnalyzedText _getAnalyzedText(JCas jCas) {
		AnalyzedText analyzedText = AnnotationRetrieval.get(jCas, AnalyzedText.class, 0);
		if(analyzedText == null){
			analyzedText = new AnalyzedText(jCas);
			analyzedText.setText(jCas.getDocumentText());
			analyzedText.addToIndexes();
		}
		return analyzedText;
	}
}
