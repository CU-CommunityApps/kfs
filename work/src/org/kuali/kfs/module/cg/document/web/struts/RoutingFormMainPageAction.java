/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.lookup.LookupResultsService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.cg.businessobject.Keyword;
import org.kuali.kfs.module.cg.businessobject.RoutingFormKeyword;
import org.kuali.kfs.module.cg.businessobject.RoutingFormOrganizationCreditPercent;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

public class RoutingFormMainPageAction extends RoutingFormAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormMainPageAction.class);

    /**
     * When a person other than PD/Co-PD/Contact is added to the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward addProjectDirectorPersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addPerson(routingForm.getNewRoutingFormProjectDirector());
        routingForm.setNewRoutingFormProjectDirector(new RoutingFormPersonnel());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * When a person other than PD/Co-PD/Contact is added to the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward addOtherPersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addPerson(routingForm.getNewRoutingFormOtherPerson());
        routingForm.setNewRoutingFormOtherPerson(new RoutingFormPersonnel());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * When a person is deleted from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward deletePersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().getRoutingFormPersonnel().remove(super.getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * When an org is added to the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward addOrganizationCreditPercentLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addOrganizationCreditPercent(routingForm.getNewRoutingFormOrganizationCreditPercent());
        routingForm.setNewRoutingFormOrganizationCreditPercent(new RoutingFormOrganizationCreditPercent());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * When an org is deleted from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward deleteOrganizationCreditPercentLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().getRoutingFormOrganizationCreditPercents().remove(super.getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * When a keyword is deleted from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteRoutingFormKeyword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().getRoutingFormKeywords().remove(super.getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * All keywords are deleted from the routing form list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteAllRoutingFormKeyword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().getRoutingFormKeywords().clear();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.cg.document.web.struts.RoutingFormAction#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();

        retrieveMainPageReferenceObjects(routingFormDocument);

        return super.save(mapping, form, request, response);
    }

    public ActionForward clearFedPassthrough(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();

        routingFormDocument.setAgencyFederalPassThroughNumber(null);
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObject(routingFormDocument, "federalPassThroughAgency");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Refresh method on Main Page does several things for lookups:
     * <ul>
     * <li>If we return from a multi value lookup then it was a keywords lookup and we add them to our keywords list.</li>
     * <li>If TBN is selected it clears the according key field for the appropriate lookup.</li>
     * <li>If an item is selected it clears the TBN field for the appropriate lookup.</li>
     * <li>For personnel lookups it sets the chart / org to the appropriate line (or new) field.</li>
     * </ul>
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();

        // check to see if we are coming back from a lookup
        if (KFSConstants.MULTIPLE_VALUE.equals(routingForm.getRefreshCaller())) {
            // Multivalue lookup. Note that the multivalue keyword lookup results are returned persisted to avoid using session.
            // Since URLs have a max length of 2000 chars, field conversions can not be done.
            String lookupResultsSequenceNumber = routingForm.getLookupResultsSequenceNumber();
            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                Class lookupResultsBOClass = Class.forName(routingForm.getLookupResultsBOClassName());
                Collection<PersistableBusinessObject> rawValues = SpringContext.getBean(LookupResultsService.class).retrieveSelectedResultBOs(lookupResultsSequenceNumber, lookupResultsBOClass, GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier());

                if (lookupResultsBOClass.isAssignableFrom(Keyword.class)) {
                    for (Iterator iter = rawValues.iterator(); iter.hasNext();) {
                        Keyword keyword = (Keyword) iter.next();
                        RoutingFormKeyword routingFormKeyword = new RoutingFormKeyword(routingFormDocument.getDocumentNumber(), keyword);
                        // ignore / drop duplicates
                        if (!routingFormDocument.getRoutingFormKeywords().contains(routingFormKeyword)) {
                            routingFormDocument.addRoutingFormKeyword(routingFormKeyword);
                        }
                    }
                }
            }
        }
        else if (KFSConstants.KUALI_LOOKUPABLE_IMPL.equals(routingForm.getRefreshCaller()) || KFSConstants.KUALI_USER_LOOKUPABLE_IMPL.equals(routingForm.getRefreshCaller())) {
            if (request.getParameter("document.routingFormAgency.agencyNumber") != null) {
                // coming back from an Agency lookup - Agency selected
                routingFormDocument.setRoutingFormAgencyToBeNamedIndicator(false);
                routingFormDocument.getRoutingFormAgency().refreshReferenceObject("agency");
            }
            else if ("true".equals(request.getParameter("document.routingFormAgencyToBeNamedIndicator"))) {
                // coming back from Agency lookup - To Be Named selected
                routingFormDocument.getRoutingFormAgency().setAgencyNumber(null);
                routingFormDocument.getRoutingFormAgency().refreshReferenceObject("agency");
            }
            else if (request.getParameter("document.agencyFederalPassThroughNumber") != null) {
                // coming back from Agency Federal Pass Through lookup - Agency selected
                routingFormDocument.setAgencyFederalPassThroughNotAvailableIndicator(false);
            }
            else if ("true".equals(request.getParameter("document.agencyFederalPassThroughNotAvailableIndicator"))) {
                // coming back from Agency Federal Pass Through lookup - Name Later selected
                routingFormDocument.setAgencyFederalPassThroughNumber(null);
                routingFormDocument.refreshReferenceObject("federalPassThroughAgency");
            }
            else if (request.getParameter("newRoutingFormProjectDirector.personUniversalIdentifier") != null) {
                RoutingFormPersonnel newRoutingFormPerson = routingForm.getNewRoutingFormProjectDirector();

                // coming back from new Person lookup - person selected. Unset TBN indicated and set chart / org.
                newRoutingFormPerson.populateWithUserServiceFields();
                newRoutingFormPerson.setPersonToBeNamedIndicator(false);
            }
            else if ("true".equals(request.getParameter("newRoutingFormProjectDirector.personToBeNamedIndicator"))) {
                // coming back from new Person lookup - Name Later selected
                routingForm.getNewRoutingFormProjectDirector().setPersonUniversalIdentifier(null);
                routingForm.getNewRoutingFormProjectDirector().getUser();
            }
            else if (request.getParameter("newRoutingFormOtherPerson.personUniversalIdentifier") != null) {
                RoutingFormPersonnel newRoutingFormPerson = routingForm.getNewRoutingFormOtherPerson();

                // coming back from new Person lookup - person selected. Unset TBN indicated and set chart / org.
                newRoutingFormPerson.populateWithUserServiceFields();
                newRoutingFormPerson.setPersonToBeNamedIndicator(false);
            }
            else if ("true".equals(request.getParameter("newRoutingFormOtherPerson.personToBeNamedIndicator"))) {
                // coming back from new Person lookup - Name Later selected
                routingForm.getNewRoutingFormOtherPerson().setPersonUniversalIdentifier(null);
                routingForm.getNewRoutingFormOtherPerson().getUser();
            }
            else {
                // Must be related to personnel lookup, first find which item this relates to.
                int personIndex = determinePersonnelIndex(request);

                // Next do the regular clearing of appropriate fields. If the above enumeration didn't find an item
                // we print a warn message at the end of this if block.
                if (request.getParameter("document.routingFormPersonnel[" + personIndex + "].personUniversalIdentifier") != null) {
                    RoutingFormPersonnel routingFormPersonnel = routingFormDocument.getRoutingFormPersonnel().get(personIndex);

                    // coming back from Person lookup - Person selected. Unset TBN indicated and set chart / org.
                    routingFormPersonnel.populateWithUserServiceFields();
                    routingFormPersonnel.setPersonToBeNamedIndicator(false);
                }
                else if ("true".equals(request.getParameter("document.routingFormPersonnel[" + personIndex + "].personToBeNamedIndicator"))) {
                    // coming back from Person lookup - To Be Named selected
                    routingFormDocument.getRoutingFormPersonnel().get(personIndex).setPersonUniversalIdentifier(null);
                    routingFormDocument.getRoutingFormPersonnel().get(personIndex).getUser();
                }
                else {
                    LOG.warn("Personnel lookup TBN reset code wasn't able to find person: personIndexStr=" + personIndex);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Checks what index document.routingFormPersonnel[?] refers to.
     * 
     * @param request
     * @return index of the personnel item referred to
     */
    private int determinePersonnelIndex(HttpServletRequest request) {
        int personIndex = -1;

        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parametersName = (String) parameterNames.nextElement();
            String label = "document.routingFormPersonnel[";
            if (parametersName.startsWith(label)) {
                personIndex = Integer.parseInt(parametersName.substring(label.length(), parametersName.indexOf("]")));
                break;
            }
        }
        return personIndex;
    }

    /**
     * Retrieves references objects for main page. Nothing special about this method, it's just consolidating code that's called in
     * multiple places.
     * 
     * @param routingForm
     */
    private void retrieveMainPageReferenceObjects(RoutingFormDocument routingFormDocument) {
        List referenceObjects = new ArrayList();

        referenceObjects.add("routingFormSubcontractors");
        referenceObjects.add("routingFormOtherCostShares");
        referenceObjects.add("routingFormInstitutionCostShares");
        referenceObjects.add("routingFormResearchRisks");
        referenceObjects.add("routingFormOrganizations");
        referenceObjects.add("routingFormQuestions");
        referenceObjects.add("adhocPersons");
        referenceObjects.add("adhocOrgs");
        referenceObjects.add("adhocWorkgroups");

        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(routingFormDocument, referenceObjects);
    }
}
