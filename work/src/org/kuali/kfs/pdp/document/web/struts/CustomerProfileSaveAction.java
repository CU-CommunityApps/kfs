/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Jul 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.kfs.pdp.document.web.struts;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.businessobject.Bank;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementType;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.service.BankService;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.ReferenceService;
import org.kuali.kfs.pdp.web.struts.BaseAction;
import org.kuali.kfs.pdp.web.struts.CustomerBankForm;
import org.kuali.kfs.sys.context.SpringContext;


/**
 * @author delyea
 */
public class CustomerProfileSaveAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileSaveAction.class);
    private CustomerProfileService customerProfileService;
    private ReferenceService referenceService;
    private BankService bankService;
    private AccountService accountService;
    private SubAccountService subAccountService;
    private ObjectCodeService objectCodeService;
    private SubObjectCodeService subObjectCodeService;
    private ProjectCodeService projectCodeService;

    public CustomerProfileSaveAction() {
        super();
        setCustomerProfileService(SpringContext.getBean(CustomerProfileService.class));
        setReferenceService(SpringContext.getBean(ReferenceService.class));
        setBankService(SpringContext.getBean(BankService.class));
        setAccountService(SpringContext.getBean(AccountService.class));
        setSubAccountService(SpringContext.getBean(SubAccountService.class));
        setObjectCodeService(SpringContext.getBean(ObjectCodeService.class));
        setSubObjectCodeService(SpringContext.getBean(SubObjectCodeService.class));
        setProjectCodeService(SpringContext.getBean(ProjectCodeService.class));
    }

    public void setCustomerProfileService(CustomerProfileService c) {
        customerProfileService = c;
    }

    public void setReferenceService(ReferenceService r) {
        referenceService = r;
    }

    public void setBankService(BankService b) {
        bankService = b;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public void setProjectCodeService(ProjectCodeService projectCodeService) {
        this.projectCodeService = projectCodeService;
    }

    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }

    public void setSubObjectCodeService(SubObjectCodeService subObjectCodeService) {
        this.subObjectCodeService = subObjectCodeService;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("executeLogic() starting");
        CustomerProfileForm customerProfileForm = (CustomerProfileForm) form;
        CustomerProfile cp = customerProfileForm.getCustomerProfile();
        String forward = "list";

        ActionMessages actionErrors = new ActionMessages();
        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

        if (buttonPressed != null && actionErrors.isEmpty()) {
            LOG.debug("executeLogic() buttonPressed is " + buttonPressed);
            if (buttonPressed.startsWith("btnClear")) {
                actionErrors.clear();
                CustomerProfileForm newProfile = (CustomerProfileForm) form;

                CustomerBankForm[] dtl = newProfile.getCustomerBankForms();
                for (int i = 0; i < dtl.length; i++) {
                    CustomerBankForm cbf = dtl[i];
                    cbf.setBankId(null);
                    newProfile.setCustomerBankForms(i, cbf);
                }
                newProfile.clearForm();
                request.setAttribute("CustomerProfileForm", newProfile);
                forward = "edit";
            }
            else if (buttonPressed.startsWith("btnCancel")) {
                actionErrors.clear();
                forward = "list";
            }
            else {
                // first we need to validate that this customer doesn't exist already
                String chartCode = cp.getChartCode();
                String orgCode = cp.getOrgCode();
                String subUnitCode = cp.getSubUnitCode();
                if (customerAlreadyExists(chartCode, orgCode, subUnitCode) && StringUtils.isEmpty(customerProfileForm.getId())) {
                    actionErrors.add("errors", new ActionMessage("customerProfileForm.profileExists", chartCode + "-" + orgCode + "-" + subUnitCode));
                }
                // Validate the accounting information
                LOG.debug("executeLogic() Validating accounting fields");
                Account acct = accountService.getByPrimaryId(customerProfileForm.getDefaultChartCode(), customerProfileForm.getDefaultAccountNumber());
                if (acct == null) {
                    actionErrors.add("defaultAccountNumber", new ActionMessage("customerProfileForm.invalidAccountNumber"));
                }
                else {
                    if (!"-----".equals(customerProfileForm.getDefaultSubAccountNumber())) {
                        SubAccount subAcct = subAccountService.getByPrimaryId(customerProfileForm.getDefaultChartCode(), customerProfileForm.getDefaultAccountNumber(), customerProfileForm.getDefaultSubAccountNumber());
                        if (subAcct == null) {
                            actionErrors.add("defaultSubAccountNumber", new ActionMessage("customerProfileForm.invalidSubAccountNumber"));
                        }
                    }
                    ObjectCode oc = objectCodeService.getByPrimaryIdForCurrentYear(customerProfileForm.getDefaultChartCode(), customerProfileForm.getDefaultObjectCode());
                    if (oc == null) {
                        actionErrors.add("defaultObjectCode", new ActionMessage("customerProfileForm.invalidObjectCode"));
                    }
                    else {
                        if (!"---".equals(customerProfileForm.getDefaultSubObjectCode())) {
                            SubObjCd soc = subObjectCodeService.getByPrimaryIdForCurrentYear(customerProfileForm.getDefaultChartCode(), customerProfileForm.getDefaultAccountNumber(), customerProfileForm.getDefaultObjectCode(), customerProfileForm.getDefaultSubObjectCode());
                            if (soc == null) {
                                actionErrors.add("defaultSubObjectCode", new ActionMessage("customerProfileForm.invalidSubObjectCode"));
                            }
                        }
                    }
                }
                if (!actionErrors.isEmpty()) { // If we had errors, save them.
                    saveErrors(request, actionErrors);
                    return mapping.getInputForward();
                }

                LOG.debug("executeLogic() Saving customer profile");

                CustomerBankForm[] dtl = customerProfileForm.getCustomerBankForms();
                CustomerProfile storedProfile = customerProfileService.get(GeneralUtilities.convertStringToInteger(customerProfileForm.getId()));

                cp.setLastUpdateUser(getUser(request));

                List storedCustomerBanks = null;
                if (!(storedProfile == null)) {
                    storedCustomerBanks = storedProfile.getCustomerBanks();
                }
                customerProfileService.save(cp);
                LOG.debug("executeLogic() storedCustomerBanks = " + storedCustomerBanks);

                for (int i = 0; i < dtl.length; i++) {
                    CustomerBankForm element = dtl[i];
                    LOG.debug("executeLogic() Checking CustomerBanks for CustomerProfil ID " + cp.getId() + " with Disbursement Type of " + element.getDisbursementDescription());
                    LOG.debug("executeLogic() DisbTypeCode = " + element.getDisbursementTypeCode());
                    LOG.debug("executeLogic() The profile from the DB is " + storedProfile);

                    if (!(storedProfile == null)) {
                        LOG.debug("executeLogic() 1");
                    }

                    if ((!(storedProfile == null)) && (!(storedCustomerBanks.equals(null)))) {
                        // Profile exists and at least one CustomerBank Exists
                        LOG.debug("executeLogic() At least one CustomerBank and profile stored");
                        LOG.debug("executeLogic() The Bank ID entered is " + element.getBankId());
                        if (!(element.getBankId().equals(new Integer(0)))) {
                            // User has stored a bankId
                            CustomerBank cb = new CustomerBank();
                            DisbursementType dt = (DisbursementType) referenceService.getCode("DisbursementType", element.getDisbursementTypeCode());
                            cb.setDisbursementType(dt);
                            cb.setCustomerProfile(cp);
                            cb.setLastUpdateUser(getUser(request));

                            for (Iterator iter = storedCustomerBanks.iterator(); iter.hasNext();) {
                                CustomerBank storedCustomerBank = (CustomerBank) iter.next();
                                if (element.getDisbursementTypeCode().equals(storedCustomerBank.getDisbursementType().getCode())) {
                                    // UPDATE CUSTOMERBANK storedCustomerBank by setting Version and ID
                                    LOG.debug("executeLogic() The next CustomerBank stored will be an UPDATE from existing");
                                    cb.setId(storedCustomerBank.getId());
                                    cb.setVersion(storedCustomerBank.getVersion());
                                }
                            }

                            Bank b = bankService.get(element.getBankId());
                            cb.setBank(b);
                            LOG.debug("executeLogic() Now storing CustomerBank with bankId " + element.getBankId() + " and DisbursementType of " + dt.getDescription());
                            customerProfileService.saveCustomerBank(cb);
                        }
                        else {
                            // User has stored null as the bankId
                            for (Iterator iter = storedCustomerBanks.iterator(); iter.hasNext();) {
                                CustomerBank storedCustomerBank = (CustomerBank) iter.next();
                                if (element.getDisbursementTypeCode().equals(storedCustomerBank.getDisbursementType().getCode())) {
                                    // DELETE CUSTOMERBANK storedCustomerBank
                                    LOG.debug("executeLogic() Now deleting CustomerBank with id of " + storedCustomerBank.getId());
                                    storedCustomerBank.setLastUpdateUser(getUser(request));
                                    customerProfileService.deleteCustomerBank(storedCustomerBank);
                                }
                            }
                        }
                    }
                    else {
                        // There are no stored CustomerBanks or no profile (therefore no CustomerBanks
                        if (!(element.getBankId().equals(new Integer(0)))) {
                            // Array for this ACH type contains a bankId
                            LOG.debug("executeLogic() No CustomerBanks or profile stored... so now storing with bankId " + element.getBankId());
                            DisbursementType dt = (DisbursementType) referenceService.getCode("DisbursementType", element.getDisbursementTypeCode());
                            Bank b = bankService.get(element.getBankId());

                            CustomerBank cb = new CustomerBank();
                            cb.setDisbursementType(dt);
                            cb.setCustomerProfile(cp);
                            cb.setBank(b);
                            cb.setLastUpdateUser(getUser(request));
                            customerProfileService.saveCustomerBank(cb);
                        }
                    }
                }

                if (storedProfile == null) {
                    LOG.debug("executeLogic() 1");
                    customerProfileService.save(cp);
                }
                LOG.debug("executeLogic() custId is: " + cp.getId());
                actionErrors.add("success", new ActionMessage("success.profile.saved"));
                LOG.debug("executeLogic() Save or Update complete; exit method.");
                forward = "list";
            }
        }

        // If we had errors, save them.
        if (!actionErrors.isEmpty()) {
            saveErrors(request, actionErrors);
        }

        return mapping.findForward(forward);
    }

    private boolean customerAlreadyExists(String chartCode, String orgCode, String subUnitCode) {
        // attempt to retrieve this through the CustomerService

        if (!StringUtils.isEmpty(chartCode) && !StringUtils.isEmpty(orgCode) && !StringUtils.isEmpty(subUnitCode)) {
            CustomerProfile existingProfile = customerProfileService.get(chartCode, orgCode, subUnitCode);
            if (existingProfile != null) {
                return true;
            }
        }
        return false;
    }
}
