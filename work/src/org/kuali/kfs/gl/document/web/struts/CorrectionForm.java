/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.kuali.module.gl.web.struts.form;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.struts.upload.FormFile;
import org.kuali.core.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.document.CorrectionDocument;

public class CorrectionForm extends KualiDocumentFormBase {
    static final private long serialVersionUID = 123456789L;

    /**
     * This is a list of names of attributes of OriginEntry that can be both
     * searched on and replaced via the GL Error Correction Document. This static
     * List is referenced by the JSP via the Struts form. 
     */
    static final public List fieldNames = new ArrayList();
  
    private String chooseSystem;
    private String editMethod;
    protected FormFile sourceFile;
    private String[] groupIdList;
    private String deleteOutput;
    private String matchCriteriaOnly;
    private String editableFlag;
    private String manualEditFlag;
    /**
     * This is a Map of operators that can be used in searches from the GL
     * Error Correction Document. Each value in this Map corresponds to a
	 * case in CorrectionActionHelper.isMatch(Object, 
     * CorrectionSearchCriterion).
     */
    static final public Map searchOperators = new TreeMap();

    /* Statically initialize fieldNames and searchOperators. */
    static {
        Field[] fields = OriginEntry.class.getDeclaredFields();
        Method[] methods = OriginEntry.class.getDeclaredMethods();
        Set validMethods = new TreeSet();
        
        // Only fields which are not business objects can be replaced from the form.
        for(int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            Class c = m.getReturnType();
            if(m.getName().startsWith("get") && (
                    c.equals(String.class)
                    || c.equals(Integer.class)
                    || c.equals(java.sql.Date.class)
                    || c.equals(KualiDecimal.class)
                    )) {
                char ch = Character.toLowerCase(m.getName().charAt(3));
                validMethods.add(
                    new StringBuffer("").append(ch).append(m.getName().substring(4)).toString());
            }
        }
        
        for(int i = 0; i < fields.length; i++) {
            if(!"serialVersionUID".equals(fields[i].getName()) 
                    && validMethods.contains(fields[i].getName()) 
                    && !"entryGroupId".equals(fields[i].getName()) 
                    && !"entryId".equals(fields[i].getName())) {
                fieldNames.add(fields[i].getName());
            }
        }
        
        searchOperators.put("eq", "Equals");
        searchOperators.put("ne", "Not equal to");
        searchOperators.put("gt", "Greater than");
        searchOperators.put("lt", "Less than");
        searchOperators.put("sw", "Starts with");
        searchOperators.put("ew", "Ends with");
        searchOperators.put("ct", "Contains");
    }
    
    /**
     * The entries that match search criteria submitted from the GL Error
     * Correction Document are stored in this Set. This Set is then
     * referenced from the JSP for display on screen.
     */
    private Collection allEntries;
    private OriginEntry eachEntryForManualEdit;
    private Map allEntriesForManualEditHashMap;
   
    
    

    
    /**
     * 
     *
     */
	public CorrectionForm() {
        super();
		setDocument(new CorrectionDocument());
        
        /*allEntriesForManualEdit = new ArrayList();
        eachEntryForManualEdit = null;
        allEntriesForManualEditHashMap = new HashMap();
        updatedEntriesFromManualEdit = new ArrayList();
        updatedEntriesFromManualEdit.clear();*/
        
        
        // create a blank TransactionalDocumentActionFlags instance, since form-recreation needs it
        setDocumentActionFlags(new TransactionalDocumentActionFlags());
    }
    
//  /**
//   * 
//   * @return
//   */
//  public Collection getAllOriginEntryGroupSourceCodes() {
//      OriginEntrySourceDao originEntrySourceDao = new OriginEntrySourceDaoOjb();
//      List codes = (List) originEntrySourceDao.findAll();
//      Collections.sort(codes, new Comparator() {
//          /* (non-Javadoc)
//           * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
//           */
//          public int compare(Object o1, Object o2) {
//              OriginEntrySource s1 = (OriginEntrySource) o1;
//              OriginEntrySource s2 = (OriginEntrySource) o2;
//              return s1.getName().compareTo(s2.getName());
//          }
//          
//      });
//      return Collections.unmodifiableList(codes);
//  }
    
    /**
     * Bogus method for Apache PropertyUtils compliance.
     * 
     * @param dummy
     */
    public void setAllOriginEntryGroupSourceCodes(List dummy) {}
    
    /**
     * No! I do not want this to be static. It might screw up PropertyUtils compliance by breaking
     * the usual get/set pattern.
     * 
     * @return Returns the searchOperators.
     */
    public Map getSearchOperators() {
        return searchOperators;
    }

    /**
     * Bogus method for Apache PropertyUtils compliance.
     * 
     * @param map
     */
    public void setSearchOperators(Map map){}

    /**
     * No! I do not want this to be static. It might screw up PropertyUtils compliance by breaking
     * the usual get/set pattern.
     * 
     * @return
     */
    public List getFieldNames() {
        return fieldNames;
    }
    
    /**
     * Added just for Apache PropertyUtils compliance (finds property by looking for getX() setX() methods).
     * 
     * @param bogusFieldNames
     */
    public void setFieldNames(ArrayList bogusFieldNames) {}
    
    /**
     * @return Returns the entriesThatMatchSearchCriteria.
     */
    public Collection getAllEntries() {
        return allEntries;
    }

    /**
     * @param entriesThatMatchSearchCriteria The entriesThatMatchSearchCriteria to set.
     */
    public void setAllEntries(
            Collection allEntriesForManualEdit) {
        this.allEntries = allEntriesForManualEdit;
    }
    
    /**
	 * Expose a method of CorrectionActionHelper. 
     * 
     * @return
     */
/*    public Collection getOriginEntryGroupsPendingProcessing() {
        
        OriginEntryGroupService originEntryGroupService= (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        
        Collection<OriginEntryGroup> groupPendingList = originEntryGroupService.getOriginEntryGroupsPendingProcessing();
        Collection returnCollection = new ArrayList();
        
        Iterator iter = groupPendingList.iterator();
        for(OriginEntryGroup oeg: groupPendingList){
            oeg = (OriginEntryGroup) iter.next();
        
            if (oeg.getSourceCode().equals("GLCP") & !oeg.getValid()){
            } else {returnCollection.add(oeg);}
        }
        return returnCollection;
    }*/

    public String getChooseSystem() {
        return chooseSystem;
    }

    public void setChooseSystem(String chooseSystem) {
        this.chooseSystem = chooseSystem;
    }

    public String getEditMethod() {
        return editMethod;
    }

    public void setEditMethod(String editMethod) {
        this.editMethod = editMethod;
    }

    public Map getAllEntriesForManualEditHashMap() {
        return allEntriesForManualEditHashMap;
    }

    public void setAllEntriesForManualEditHashMap(Map allEntriesForManualEditHashMap) {
        this.allEntriesForManualEditHashMap = allEntriesForManualEditHashMap;
    }

    public OriginEntry getEachEntryForManualEdit() {
        return eachEntryForManualEdit;
    }

    public void setEachEntryForManualEdit(OriginEntry eachEntryForManualEdit) {
        this.eachEntryForManualEdit = eachEntryForManualEdit;
    }

        public FormFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(FormFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String[] getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(String[] groupIdList) {
        this.groupIdList = groupIdList;
    }

    public String getDeleteOutput() {
        return deleteOutput;
    }

    public void setDeleteOutput(String deleteOutput) {
        this.deleteOutput = deleteOutput;
    }

    public String getMatchCriteriaOnly() {
        return matchCriteriaOnly;
    }

    public void setMatchCriteriaOnly(String matchCriteriaOnly) {
        this.matchCriteriaOnly = matchCriteriaOnly;
    }

    public String getEditableFlag() {
        return editableFlag;
    }

    public void setEditableFlag(String editableFlag) {
        this.editableFlag = editableFlag;
    }

    public String getManualEditFlag() {
        return manualEditFlag;
    }

    public void setManualEditFlag(String manualEditFlag) {
        this.manualEditFlag = manualEditFlag;
    }

    
}

