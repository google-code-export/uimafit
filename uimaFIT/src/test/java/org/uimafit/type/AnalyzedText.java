

/* First created by JCasGen Mon Jun 28 20:43:09 MDT 2010 */
package org.uimafit.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Jun 28 20:43:09 MDT 2010
 * XML source: C:/Users/Philip/Documents/Academic/workspace/uimaFIT/src/test/resources/org/uimafit/type/TypeSystem.xml
 * @generated */
public class AnalyzedText extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(AnalyzedText.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected AnalyzedText() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public AnalyzedText(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public AnalyzedText(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public AnalyzedText(JCas jcas, int begin, int end) {
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
     
 
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (AnalyzedText_Type.featOkTst && ((AnalyzedText_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "org.uimafit.type.AnalyzedText");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnalyzedText_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (AnalyzedText_Type.featOkTst && ((AnalyzedText_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "org.uimafit.type.AnalyzedText");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnalyzedText_Type)jcasType).casFeatCode_text, v);}    
  }

    