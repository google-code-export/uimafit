package org.uimafit.examples.tutorial;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Before;
import org.uimafit.examples.tutorial.type.DateAnnotation;
import org.uimafit.examples.tutorial.type.DateTimeAnnotation;
import org.uimafit.examples.tutorial.type.Meeting;
import org.uimafit.examples.tutorial.type.RoomNumber;
import org.uimafit.examples.tutorial.type.TimeAnnotation;
import org.uimafit.examples.tutorial.type.UimaAcronym;
import org.uimafit.examples.tutorial.type.UimaMeeting;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypePrioritiesFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

public class ExamplesTestBase {

	private static ThreadLocal<JCas> JCAS = new ThreadLocal<JCas>();
	private static ThreadLocal<TypeSystemDescription> TYPE_SYSTEM_DESCRIPTION = new ThreadLocal<TypeSystemDescription>();
	private static ThreadLocal<TypePriorities> TYPE_PRIORITIES = new ThreadLocal<TypePriorities>();

	static {
		try {
			TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription("org.uimafit.examples.TypeSystem");
			TYPE_SYSTEM_DESCRIPTION.set(tsd);

			TypePriorities tp = TypePrioritiesFactory.createTypePriorities(
					DateAnnotation.class, DateTimeAnnotation.class, Meeting.class,
					RoomNumber.class, TimeAnnotation.class, UimaAcronym.class, UimaMeeting.class);
			TYPE_PRIORITIES.set(tp);
			
			JCas jCas = JCasFactory.createJCas(tsd);
			JCAS.set(jCas);
		}
	 catch(Exception e) {
		e.printStackTrace();
		System.exit(1);
	}
	}
	
	protected JCas jCas;
	protected TypeSystemDescription typeSystemDescription;
	protected TypePriorities typeSystemPriorities;

	/**
	 *  we do not want to create a new JCas object every time we run a test because it is expensive (~100ms on my laptop).  Instead,
	 *  we will have one JCas per thread sitting around that we will reset everytime a new test is called.  
	 */
	@Before
	public void setUp() {
		jCas = JCAS.get(); 
		jCas.reset();
		typeSystemDescription = TYPE_SYSTEM_DESCRIPTION.get();
		typeSystemPriorities = TYPE_PRIORITIES.get();
	}

}
