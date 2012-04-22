/*
 Copyright 2009-2012
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
package org.uimafit.propertyeditors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Locale;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;

/**
 * Custom property editor for {@link Locale} that supports "-" as separator and sets the default
 * locale when {@code null} or {@code ""} is passed. This is used to be backwards-compatible with 
 * previous uimaFIT behavior.
 * 
 * @author Richard Eckart de Castilho
 */
public class GetAsTextStringEditor extends PropertyEditorSupport {
	private final PropertyEditorRegistry editorRegistry;
	private final PropertyEditorRegistrySupport editorRegistrySupport;
	
	public GetAsTextStringEditor(final PropertyEditorRegistry aEditorRegistry) {
		editorRegistry = aEditorRegistry;
		if (aEditorRegistry instanceof PropertyEditorRegistrySupport) {
			editorRegistrySupport = (PropertyEditorRegistrySupport) aEditorRegistry;
		}
		else {
			editorRegistrySupport = new PropertyEditorRegistrySupport();
		}
	}

	@Override
	public void setValue(Object value) {
		if (value == null || value instanceof String) {
			super.setValue(value);
		}
		else {
			PropertyEditor editor = editorRegistry.findCustomEditor(value.getClass(), null);
			if (editor == null) {
				editor = editorRegistrySupport.getDefaultEditor(value.getClass());
			}
			if (editor == null) {
				throw new IllegalArgumentException("Unable to convert " + value.getClass()
						+ " to String. No PropertyEditor found.");
			}
			editor.setValue(value);
			super.setValue(editor.getAsText());
		}
	}
	
	@Override
	public void setAsText(String text) {
		setValue(text);
	}

	@Override
	public String getAsText() {
		return (String) getValue();
	}
}
