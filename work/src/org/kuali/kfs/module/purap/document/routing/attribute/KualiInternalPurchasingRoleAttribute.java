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
package org.kuali.workflow.module.purap.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocumentBase;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.UnqualifiedRoleAttribute;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * A document should stop in the Internal Purchasing Review route node if the following conditions are met:<br>
 * <ol>
 * <li>The document is not an 'Automatic Purchase Order'
 * <li>The 'routing user' was not a contract manager
 * <li>The 'po internal amount limit' is not null and the document total amount is greater than the 'po internal amount limit'<br>
 * (see
 * {@link org.kuali.module.purap.service.PurchaseOrderService#getInternalPurchasingDollarLimit(org.kuali.module.purap.document.PurchaseOrderDocument, String, String)}
 * for more details on the 'po internal amount limit')
 * </ol>
 */
public class KualiInternalPurchasingRoleAttribute extends UnqualifiedRoleAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiInternalPurchasingRoleAttribute.class);

    private static final String INTERNAL_PURCHASING_ROLE_KEY = "INTERNAL_PURCHASING_REVIEWER";
    private static final String INTERNAL_PURCHASING_ROLE_LABEL = "Internal Purchasing Reviewer";
    private static final Role ROLE = new Role(KualiInternalPurchasingRoleAttribute.class, INTERNAL_PURCHASING_ROLE_KEY, INTERNAL_PURCHASING_ROLE_LABEL);

    private static final List ROLES;
    static {
        ArrayList list = new ArrayList(0);
        list.add(ROLE);
        ROLES = Collections.unmodifiableList(list);
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.UnqualifiedRoleAttribute#getRoleNames()
     */
    @Override
    public List<Role> getRoleNames() {
        return ROLES;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.AbstractRoleAttribute#isMatch(edu.iu.uis.eden.routeheader.DocumentContent, java.util.List)
     */
    @Override
    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        String documentNumber = docContent.getRouteContext().getDocument().getRouteHeaderId().toString();
        String authenticationId = "(not found)";
        try {
            authenticationId = docContent.getRouteContext().getDocument().getRoutedByUser().getAuthenticationUserId().getAuthenticationId();
            if (StringUtils.isNotEmpty(authenticationId)) {
                String contractManagersGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.WorkflowParameters.PurchaseOrderDocument.CONTRACT_MANAGERS_WORKGROUP_NAME);
                if (!SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(authenticationId.toUpperCase()).isMember(contractManagersGroupName)) {
                    // get the document id number from the routeContext doc content
                    PurchasingDocumentBase document = (PurchasingDocumentBase) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
                    document.refreshNonUpdateableReferences();
                    KualiDecimal internalPurchasingLimit = SpringContext.getBean(PurchaseOrderService.class).getInternalPurchasingDollarLimit((PurchaseOrderDocument)document);
                    return ((ObjectUtils.isNull(internalPurchasingLimit)) || (internalPurchasingLimit.compareTo(KualiWorkflowUtils.getFinancialDocumentTotalAmount(docContent.getDocument())) < 0));
                }
                return false;
            }
            else {
                String errorMsg = "Error while processing doc id '" + documentNumber + "'... Authentication Id not found from routed by user.";
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }
        catch (EdenUserNotFoundException u) {
            String errorMsg = "Error trying to get routed by user from doc id '" + documentNumber + "'";
            LOG.error(errorMsg, u);
            throw new RuntimeException(errorMsg, u);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error trying to get document using doc id '" + documentNumber + "'";
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
        catch (UserNotFoundException e) {
            String errorMsg = "Error while processing doc id '" + documentNumber + "'... User not found using Authentication Id '" + authenticationId + "'";
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * TODO delyea - documentation
     * 
     * @param routeContext the RouteContext
     * @param roleName the role name
     * @return a ResolvedQualifiedRole
     */
    @Override
    public ResolvedQualifiedRole resolveRole(RouteContext routeContext, String roleName) throws EdenUserNotFoundException {
        // assume isMatch above has done it's job
        String workgroupName = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.WorkflowParameters.PurchaseOrderDocument.INTERNAL_PURCHASING_WORKGROUP_NAME);
        return new ResolvedQualifiedRole(INTERNAL_PURCHASING_ROLE_LABEL, Arrays.asList(new Id[] { new GroupNameId(workgroupName) }));
    }
}
