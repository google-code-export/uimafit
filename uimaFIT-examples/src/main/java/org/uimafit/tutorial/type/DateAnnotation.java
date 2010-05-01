

/* First created by JCasGen Fri Apr 02 09:48:30 MDT 2010 */
package org.uimafit.tutorial.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sat May 01 14:19:53 MDT 2010
 * XML source: C:/Users/Philip/Documents/Academic/workspace/uimaFIT-examples/src/main/resources/org/uimafit/tutorial/type/TypeSystem.xml
 * @generated */
public class DateAnnotation extends DateTimeAnnotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(DateAnnotation.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected DateAnnotation() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public DateAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public DateAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public DateAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
}

    