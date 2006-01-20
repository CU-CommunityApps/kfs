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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class is used to test TransferOfFundsDocument.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class TransferOfFundsDocumentTest extends TransactionalDocumentTestBase {
    public static final String COLLECTION_NAME = 
        "TransferOfFundsDocumentTest.collection1";
    public static final String USER_NAME = "user1";
    public static final String DOCUMENT_PARAMETER = "documentParameter5";

    private static final String DOCNAME = "internalBillingDocument";
    private static final String USER_INIT = "user_unprivileged";
    private static final String USER_APPROVE1 = "user_accountSet1";
    private static final String USER_APPROVE2 = "user_accountSet2";

    private static final String[] FIXTURE_COLLECTION_NAMES =
        { COLLECTION_NAME };
    
    // AccountingLineParameter fixture members
    private AccountingLineParameter _sourceLine1;
    private AccountingLineParameter _sourceLine2;
    private AccountingLineParameter _sourceLine3;
    private AccountingLineParameter _targetLine1;
    private AccountingLineParameter _targetLine2;
    private AccountingLineParameter _targetLine3;

    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods Start Here                                            //
    ///////////////////////////////////////////////////////////////////////////
    public String[] getFixtureCollectionNames() {
        return FIXTURE_COLLECTION_NAMES;
    }
    
    /**
     * Fixture method to obtain a <code>@{link SourceAccountingLine}</code> 
     * instance that is for a different account than the current user belongs 
     * to.
     * 
     * @return SourceAccountingLine
     */
    protected SourceAccountingLine getSourceAccountingLineDifferentAccount() {
        return ( SourceAccountingLine )getSourceLine1().createLine();
    }

    /**
     * Fixture method to obtain a <code>@{link TargetAccountingLine}</code> 
     * instance that is for a different account than the current user belongs 
     * to.
     * 
     * @return TargetAccountingLine
     */
    protected TargetAccountingLine getTargetAccountingLineDifferentAccount() {
        return ( TargetAccountingLine )getTargetLine1().createLine();
    }

    /**
     * Accessor method for sourceLine1 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @return AccountingLineParameter fixture to get
     */
    public AccountingLineParameter getSourceLine1() {
        return _sourceLine1;
    }
    
    /**
     * Accessor method for sourceLine1 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @param AccountingLineParameter fixture to set
     */
    public void setSourceLine1( AccountingLineParameter p ) {
        _sourceLine1 = p;
    }

    /**
     * Accessor method for sourceLine2 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @return AccountingLineParameter fixture to get
     */
    public AccountingLineParameter getSourceLine2() {
        return _sourceLine2;
    }

    /**
     * Accessor method for sourceLine2 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @param AccountingLineParameter fixture to set
     */
    public void setSourceLine2( AccountingLineParameter p ) {
        _sourceLine2 = p;
    }

    /**
     * Accessor method for sourceLine3 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @return AccountingLineParameter fixture to get
     */
    public AccountingLineParameter getSourceLine3() {
        return _sourceLine3;
    }

    /**
     * Accessor method for sourceLine3
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @param AccountingLineParameter fixture to set
     */
    public void setSourceLine3( AccountingLineParameter p ) {
        _sourceLine3 = p;
    }

    /**
     * Accessor method for targetLine1 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @return AccountingLineParameter fixture to get
     */
    public AccountingLineParameter getTargetLine1() {
        return _targetLine1;
    }

    /**
     * Accessor method for targetLine1 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @param AccountingLineParameter fixture to set
     */
    public void setTargetLine1( AccountingLineParameter p ) {
        _targetLine1 = p;
    }

    /**
     * Accessor method for targetLine2 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @return AccountingLineParameter fixture to get
     */
    public AccountingLineParameter getTargetLine2() {
        return _targetLine2;
    }

    /**
     * Accessor method for targetLine2 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @param AccountingLineParameter fixture to set
     */
    public void setTargetLine2( AccountingLineParameter p ) {
        _targetLine2 = p;
    }

    /**
     * Accessor method for targetLine3 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @return AccountingLineParameter fixture to get
     */
    public AccountingLineParameter getTargetLine3() {
        return _targetLine3;
    }

    /**
     * Accessor method for targetLine3 
     * <code>{@link AccountingLineParameter}</code> fixture.
     * 
     * @param AccountingLineParameter fixture to set
     */
    public void setTargetLine3( AccountingLineParameter p ) {
        _targetLine3 = p;
    }

    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public DocumentParameter getDocumentParameterFixture() {
        return ( TransactionalDocumentParameter )
            getFixtureEntryFromCollection(COLLECTION_NAME,
                                          DOCUMENT_PARAMETER).createObject();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    public List getTargetAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add( getTargetLine1() );
        // list.add( getTargetLine2() );
        // list.add( getTargetLine3() );
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    public List getSourceAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add( getSourceLine1() );
        // list.add( getSourceLine2() );
        // list.add( getSourceLine3() );
        return list;
    }
    
    /**
     * User name fixture to be used for this test.
     *
     * @param String name of user to use.
     */
    protected String getTestUserName() {
        return getUserName();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getUserName()
     */
    public String getUserName() {
        return ( String ) 
            getFixtureEntryFromCollection( COLLECTION_NAME, 
                                           USER_NAME ).createObject();
    }
    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods End Here                                              //
    ///////////////////////////////////////////////////////////////////////////
}
