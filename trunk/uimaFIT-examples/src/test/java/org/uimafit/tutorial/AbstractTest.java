package org.uimafit.tutorial;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Before;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypePrioritiesFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

public class AbstractTest {

	private static ThreadLocal<JCas> JCAS = new ThreadLocal<JCas>();
	private static ThreadLocal<TypeSystemDescription> TYPE_SYSTEM_DESCRIPTION = new ThreadLocal<TypeSystemDescription>();
	private static ThreadLocal<TypePriorities> TYPE_PRIORITIES = new ThreadLocal<TypePriorities>();

	static {
		try {
			TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription("org.uimafit.tutorial.type.TypeSystem");
			TYPE_SYSTEM_DESCRIPTION.set(tsd);

			TypePriorities tp = TypePrioritiesFactory.createTypePriorities(new String[] {
					"org.uimafit.tutorial.type.DateAnnotaton", 
				    "org.uimafit.tutorial.type.DateTimeAnnotation",
				    "org.uimafit.tutorial.type.Meeting",
				    "org.uimafit.tutorial.type.RoomNumber",
				    "org.uimafit.tutorial.type.TimeAnnotation",
				    "org.uimafit.tutorial.type.UimaAcronym",
				    "org.uimafit.tutorial.type.UimaMeeting"});
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
