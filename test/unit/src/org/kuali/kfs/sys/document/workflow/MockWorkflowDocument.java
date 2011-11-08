/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.workflow;

import java.sql.Timestamp;
import java.util.Set;

import org.kuali.rice.kew.dto.ReturnPointDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This class is the base class for a MockWorkflowDocument. It can be extended by any other kind of mock document that needs to
 * override certain methods. This class has absolutely no state or behavior. There is no public constructor, and no member
 * variables. All void methods do nothing. All methods with a return value return null. All state and behavior needs to be added via
 * a subclass.
 */
public abstract class MockWorkflowDocument implements KualiWorkflowDocument {

    /**
     * Constructs a MockWorkflowDocument.java.
     */
    protected MockWorkflowDocument() {
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getApplicationContent()
     */
    public String getApplicationContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setApplicationContent(java.lang.String)
     */
    public void setApplicationContent(String applicationContent) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#clearAttributeContent()
     */
    public void clearAttributeContent() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAttributeContent()
     */
    public String getAttributeContent() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#addAttributeDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void addAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#removeAttributeDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void removeAttributeDefinition(WorkflowAttributeDefinitionDTO attributeDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#clearAttributeDefinitions()
     */
    public void clearAttributeDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAttributeDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getAttributeDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#addSearchableDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void addSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#removeSearchableDefinition(org.kuali.rice.kew.clientapp.vo.WorkflowAttributeDefinitionDTO)
     */
    public void removeSearchableDefinition(WorkflowAttributeDefinitionDTO searchableDefinition) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#clearSearchableDefinitions()
     */
    public void clearSearchableDefinitions() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getSearchableDefinitions()
     */
    public WorkflowAttributeDefinitionDTO[] getSearchableDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRouteHeader()
     */
    public RouteHeaderDTO getRouteHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRouteHeaderId()
     */
    public Long getRouteHeaderId()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setAppDocId(java.lang.String)
     */
    public void setAppDocId(String appDocId) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAppDocId()
     */
    public String getAppDocId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getInitiatorNetworkId()
     */
    public String getInitiatorNetworkId() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getInitiatorPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }
    
        /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRoutedByPrincipalId()
     */
    public String getRoutedByPrincipalId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getTitle()
     */
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#saveDocument(java.lang.String)
     */
    public void saveDocument(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#routeDocument(java.lang.String)
     */
    public void routeDocument(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#disapprove(java.lang.String)
     */
    public void disapprove(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#approve(java.lang.String)
     */
    public void approve(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserApprove(java.lang.String)
     */
    public void superUserApprove(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#cancel(java.lang.String)
     */
    public void cancel(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String)
     */
    public void blanketApprove(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#blanketApprove(java.lang.String, java.lang.Integer)
     */
    public void blanketApprove(String annotation, Integer routeLevel)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#saveRoutingData()
     */
    public void saveRoutingData()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#acknowledge(java.lang.String)
     */
    public void acknowledge(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#fyi()
     */
    public void fyi()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#delete()
     */
    public void delete()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#refreshContent()
     */
    public void refreshContent()  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#setTitle(java.lang.String)
     */
    public void setTitle(String title)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getDocumentType()
     */
    public String getDocumentType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isAdHocRequested()
     */
    public boolean isAdHocRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isAcknowledgeRequested()
     */
    public boolean isAcknowledgeRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isApprovalRequested()
     */
    public boolean isApprovalRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isCompletionRequested()
     */
    public boolean isCompletionRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isFYIRequested()
     */
    public boolean isFYIRequested() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isBlanketApproveCapable()
     */
    public boolean isBlanketApproveCapable() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getDocRouteLevel()
     */
    public Integer getDocRouteLevel() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getDocRouteLevelName()
     */
    public String getDocRouteLevelName()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRouteTypeName()
     */
    public String getRouteTypeName()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#complete(java.lang.String)
     */
    public void complete(String annotation)  {
        // TODO Auto-generated method stub

    }

    public void returnToPreviousNode(String annotation, String nodeName)  {
    }
    public void returnToPreviousNode(String annotation, ReturnPointDTO returnPoint)  {
    }
    public void setReceiveFutureRequests()  {
    }
    public void setDoNotReceiveFutureRequests()  {
    }
    public void setClearFutureRequests()  {
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#returnToPreviousRouteLevel(java.lang.String, java.lang.Integer)
     */
    public void returnToPreviousRouteLevel(String annotation, Integer destRouteLevel)  {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#logDocumentAction(java.lang.String)
     */
    public void logDocumentAction(String annotation)  {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsInitiated()
     */
    public boolean stateIsInitiated() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsSaved()
     */
    public boolean stateIsSaved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsEnroute()
     */
    public boolean stateIsEnroute() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsFinal()
     */
    public boolean stateIsFinal() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsException()
     */
    public boolean stateIsException() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsCanceled()
     */
    public boolean stateIsCanceled() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsDisapproved()
     */
    public boolean stateIsDisapproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsApproved()
     */
    public boolean stateIsApproved() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#stateIsProcessed()
     */
    public boolean stateIsProcessed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getStatusDisplayValue()
     */
    public String getStatusDisplayValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getCreateDate()
     */
    public Timestamp getCreateDate() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#userIsInitiator(org.kuali.rice.kns.bo.user.KualiUser)
     */
    public boolean userIsInitiator(Person user) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getNodeNames()
     */
    public String[] getNodeNames()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getAllPriorApprovers()
     */
    public Set<Person> getAllPriorApprovers()  {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getCurrentRouteNodeNames()
     */
    public String getCurrentRouteNodeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#getRoutedByUserNetworkId()
     */
    public String getRoutedByUserNetworkId() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#isStandardSaveAllowed()
     */
    public boolean isStandardSaveAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserActionRequestApprove(java.lang.Long, java.lang.String)
     */
    public void superUserActionRequestApprove(Long actionRequestId, String annotation)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserCancel(java.lang.String)
     */
    public void superUserCancel(String annotation)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#superUserDisapprove(java.lang.String)
     */
    public void superUserDisapprove(String annotation)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#userIsRoutedByUser(org.kuali.rice.kim.bo.Person)
     */
    public boolean userIsRoutedByUser(Person user) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#adHocRouteDocumentToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean ignorePreviousActions, String actionRequestLabel)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#adHocRouteDocumentToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public void adHocRouteDocumentToGroup(String actionRequested, String routeTypeName, String annotation, String groupId, String responsibilityDesc, boolean ignorePreviousActions)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#adHocRouteDocumentToPrincipal(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean ignorePreviousActions, String actionRequestLabel)  {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowDocument#adHocRouteDocumentToPrincipal(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public void adHocRouteDocumentToPrincipal(String actionRequested, String routeTypeName, String annotation, String principalId, String responsibilityDesc, boolean ignorePreviousActions)  {
        // TODO Auto-generated method stub
        
    }

    
}

