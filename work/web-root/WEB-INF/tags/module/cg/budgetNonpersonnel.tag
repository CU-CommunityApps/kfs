<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
<c:set var="budgetNonpersonnel" value="${DataDictionary.BudgetNonpersonnel.attributes}" />
<c:set var="budgetNonpersonnelCategory" value="${DataDictionary.NonpersonnelCategory.attributes}" />
<c:set var="budgetNonpersonnelSubCategory" value="${DataDictionary.NonpersonnelSubCategory.attributes}" />
<c:set var="budgetNonpersonnelObjectCode" value="${DataDictionary.NonpersonnelObjectCode.attributes}" />
<c:set var="subcontractorAttributes" value="${DataDictionary.RoutingFormSubcontractor.attributes}" />

<div align="right">
	<kul:help documentTypeName="${DataDictionary.BudgetDocument.documentTypeName}" pageName="${CGConstants.NONPERSONNEL_HEADER_TAB}" altText="page help"/>
</div>

  <cg:budgetDetailSelection includeSummary="false" />
  
 <div id="workarea">

  <logic:iterate id="nonpersonnelCategory" name="KualiForm" property="nonpersonnelCategories" indexId="i">
    
    <c:set var="categoryItemErrors" value="newNonpersonnel[${i}].*"/>
    <c:forEach items="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].itemIndexes}" var="categoryItemIndex" varStatus="status">
      <c:set var="categoryItemErrors" value="${categoryItemErrors},document.budget.nonpersonnelItem[${categoryItemIndex}].*"/>
    </c:forEach>
    <c:set var="transparentBackground" value="false" />
    <c:if test="${i eq 0}"><c:set var="transparentBackground" value="true" /></c:if>

    <kul:tab 
    	tabTitle="${nonpersonnelCategory.name}" 
    	tabItemCount="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].numItems}" 
    	transparentBackground="${transparentBackground}" 
    	defaultOpen="false" 
    	tabErrorKey="${categoryItemErrors}" 
    	auditCluster="nonpersonnelAuditErrors" 
    	tabAuditKey="document.budget.audit.nonpersonnelItem.category.${nonpersonnelCategory.code}">
    	<div class="tab-container-error"><div class="left-errmsg-tab"><cg:auditErrors cluster="nonpersonnelAuditErrors" keyMatch="document.budget.audit.nonpersonnelItem.category.${nonpersonnelCategory.code}" isLink="false" includesTitle="true"/></div></div>
    	
        <div class="tab-container" id="G02" style="" align="center">
            
            <c:if test="${CGConstants.SUBCONTRACTOR_CATEGORY_CODE eq nonpersonnelCategory.code}">
            	<div class="message-container"><bean:message key="message.kra.subcontractorReminder" /></div>
            </c:if>
            
              <a name="NonPersonnel"></a><h3>${nonpersonnelCategory.name}</h3>
            
              <table class="datatable" align="center" cellpadding="0" cellspacing="0">
                <tbody>
                <tr>
                  <th class="bord-l-b"><div align="left"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelSubCategoryCode}" skipHelpUrl="true" noColon="true" /></div></th>
                  <th class="bord-l-b">
                    <div align="left">
                      <c:choose>
                        <c:when test="${CGConstants.SUBCONTRACTOR_CATEGORY_CODE ne nonpersonnelCategory.code}">
                          <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelDescription}" skipHelpUrl="true" noColon="true" />
                        </c:when>
                        <c:otherwise>
                          * Name
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </th>
                  <c:if test="${! viewOnly }"><th class="bord-l-b"><div align="center">Copy to Future Periods </div></th></c:if>
                  <th class="bord-l-b"><div align="center"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.agencyRequestAmount}" skipHelpUrl="true" noColon="true" /></div></th>
                  <th class="bord-l-b"><div align="center"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetInstitutionCostShareAmount}" skipHelpUrl="true" noColon="true" /></div></th>
                  <th class="bord-l-b"><div align="center"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetNonpersonnel.attributes.budgetThirdPartyCostShareAmount}" skipHelpUrl="true" noColon="true" /></div></th>
                  <c:if test="${! viewOnly }"><th class="bord-l-b"><div align="center">Actions</div></th></c:if>
                </tr>

                <!--  ADD LINE -->
                <c:if test="${! viewOnly }">
                <tr>
                  <td class="infoline">
		                <input type="hidden" name="newNonpersonnel[${i}].documentNumber" value="${KualiForm.document.documentNumber}" />
		                <input type="hidden" name="newNonpersonnel[${i}].budgetTaskSequenceNumber" value="${KualiForm.currentTaskNumber}" />
		                <input type="hidden" name="newNonpersonnel[${i}].budgetPeriodSequenceNumber" value="${KualiForm.currentPeriodNumber}" />
		                <input type="hidden" name="newNonpersonnel[${i}].budgetNonpersonnelCategoryCode" value="${nonpersonnelCategory.code}" />

                    <html:select title="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelSubCategoryCode.label}" property="newNonpersonnel[${i}].budgetNonpersonnelSubCategoryCode" >
                      <html:option value="">&nbsp;</html:option>
                      <logic:iterate id="nonpersonnelObjectCode" name="nonpersonnelCategory" property="nonpersonnelObjectCodes">
                        <html:option value="${nonpersonnelObjectCode.nonpersonnelSubCategory.code}">${nonpersonnelObjectCode.nonpersonnelSubCategory.name}</html:option>
                      </logic:iterate>
                    </html:select>
                  </td>
                  <td class="infoline">
                    <div align="left">
                      <c:choose>
                        <c:when test="${CGConstants.SUBCONTRACTOR_CATEGORY_CODE ne nonpersonnelCategory.code}">
                          <kul:htmlControlAttribute property="newNonpersonnel[${i}].budgetNonpersonnelDescription" attributeEntry="${budgetNonpersonnel.budgetNonpersonnelDescription}" readOnly="${viewOnly}" />
                        </c:when>
                        <c:otherwise>
            <kul:htmlControlAttribute property="newNonpersonnel[${i}].subcontractorNumber" attributeEntry="${subcontractorAttributes.routingFormSubcontractorNumber}" onblur="onblur_subcontractorNumber_nonPersonnel('newNonpersonnel[${i}].subcontractorNumber','', 'budgetNonpersonnelDescription')"/>
                          <kul:lookup boClassName="org.kuali.kfs.module.cg.businessobject.SubContractor" fieldConversions="subcontractorNumber:newNonpersonnel[${i}].subcontractorNumber,subcontractorName:newNonpersonnel[${i}].budgetNonpersonnelDescription" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&newNonpersonnel[${i}].subcontractorNumber=484&newNonpersonnel[${i}].budgetNonpersonnelDescription=TO BE NAMED" anchor="NonPersonnel"/>
          
                      <div id="newNonpersonnel[${i}].budgetNonpersonnelDescription.div" >
                        <c:if test="${!empty KualiForm.newNonpersonnelList[i].subcontractorNumber}">
                        	<c:choose>
								<c:when test="${empty KualiForm.newNonpersonnelList[i].budgetNonpersonnelDescription}">
									<span style='color: red;'><c:out value="subcontractor not found" /> </span>
								</c:when>
								<c:otherwise>
									<c:out value="${KualiForm.newNonpersonnelList[i].budgetNonpersonnelDescription}" />
								</c:otherwise>
						</c:choose>                        
                        </c:if>

            		</div>
          
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </td>
                  <c:if test="${! viewOnly }">
                  <td class="infoline">
                    <div align="center">
                      <input type="checkbox" name="checkbox" value="checkbox" disabled="true">
                    </div></td>
                  </c:if>  
                  <td class="infoline" align="right">
                    <div align="center"><kul:htmlControlAttribute property="newNonpersonnel[${i}].agencyRequestAmount" attributeEntry="${budgetNonpersonnel.agencyRequestAmount}" styleClass="amount" />
                  </div></td>
                  <td class="infoline" align="right">
                    <div align="center"><kul:htmlControlAttribute property="newNonpersonnel[${i}].budgetInstitutionCostShareAmount" attributeEntry="${budgetNonpersonnel.budgetInstitutionCostShareAmount}" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}" styleClass="amount" />
                    </div></td>
                  <td class="infoline" align="right">
                    <div align="center"><kul:htmlControlAttribute property="newNonpersonnel[${i}].budgetThirdPartyCostShareAmount" attributeEntry="${budgetNonpersonnel.budgetThirdPartyCostShareAmount}" disabled="${! KualiForm.document.budget.budgetThirdPartyCostShareIndicator}" styleClass="amount" />
                    </div></td>
                  <td class="infoline"><div align="center"><html:image property="methodToCall.insertNonpersonnelLine.anchorNonPersonnel.line${i}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" title="add nonpersonnel line" alt="add nonpersonnel line"/></div></td>
                </tr>
                </c:if>
                
                <logic:iterate id="nonpersonnelItem"  name="KualiForm" property="document.budget.nonpersonnelItems" indexId="ctr">

                  <!-- Used to detect if this items has been copied over (and thus disable a few fields). -->
                  <c:set var="copiedOver" value="${nonpersonnelItem.copiedOverItem}"/>

	                <c:if test="${nonpersonnelItem.budgetNonpersonnelCategoryCode eq nonpersonnelCategory.code and nonpersonnelItem.budgetPeriodSequenceNumber eq KualiForm.currentPeriodNumber and nonpersonnelItem.budgetTaskSequenceNumber eq KualiForm.currentTaskNumber}">
		                <tr>
		                  <td class="datacell">
						            

                        <c:choose>
                        <c:when test="${! viewOnly }">
		                    <html:select title="${DataDictionary.BudgetNonpersonnel.attributes.budgetNonpersonnelSubCategoryCode.label}" property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelSubCategoryCode" disabled="${copiedOver}">
		                      <html:option value="">select:</html:option>
		                      <logic:iterate id="nonpersonnelObjectCode" name="nonpersonnelCategory" property="nonpersonnelObjectCodes">
		                        <html:option value="${nonpersonnelObjectCode.nonpersonnelSubCategory.code}">${nonpersonnelObjectCode.nonpersonnelSubCategory.name}</html:option>
		                      </logic:iterate>
		                    </html:select>
		                    </c:when>
		                    <c:otherwise>
		                      ${KualiForm.document.budget.nonpersonnelItems[ctr].nonpersonnelObjectCode.nonpersonnelSubCategory.name}
		                    </c:otherwise>
		                    </c:choose>
		                  </td>
		                  <td class="datacell">
                        <div align="left">
                          <c:choose>
                            <c:when test="${CGConstants.SUBCONTRACTOR_CATEGORY_CODE ne nonpersonnelCategory.code}">
						                  <kul:htmlControlAttribute property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelDescription" attributeEntry="${budgetNonpersonnel.budgetNonpersonnelDescription}" disabled="${copiedOver}" readOnly="${viewOnly}" />
                            </c:when>
                            <c:otherwise>
            <kul:htmlControlAttribute property="document.budget.nonpersonnelItem[${ctr}].subcontractorNumber" attributeEntry="${subcontractorAttributes.routingFormSubcontractorNumber}" onblur="onblur_subcontractorNumber_nonPersonnel('document.budget.nonpersonnelItem[${ctr}].subcontractorNumber','', 'budgetNonpersonnelDescription')"/>
                              <!-- logic for disabling copied over items -->
			                        <c:choose>
				                        <c:when test="${nonpersonnelItem.copiedOverItem || viewOnly}">
			  	                        <!-- display hidden: is a copied over item -->
                                </c:when>
				                <c:otherwise>
                                  <kul:lookup boClassName="org.kuali.kfs.module.cg.businessobject.SubContractor" fieldConversions="subcontractorNumber:document.budget.nonpersonnelItem[${ctr}].subcontractorNumber,subcontractorName:document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelDescription" anchor="NonPersonnel"/>

				                      <div id="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelDescription.div" >
				                        <c:if test="${!empty nonpersonnelItem.subcontractorNumber}">
				                        	<c:choose>
												<c:when test="${empty nonpersonnelItem.budgetNonpersonnelDescription}">
													<span style='color: red;'><c:out value="subcontractor not found" /> </span>
												</c:when>
												<c:otherwise>
													<c:out value="${nonpersonnelItem.budgetNonpersonnelDescription}" />
												</c:otherwise>
										</c:choose>                        
				                        </c:if>
				
				            		</div>
				
                                </c:otherwise>
                              </c:choose>
                            </c:otherwise>
                          </c:choose>
		                    </div>
                      </td>
                      <c:if test="${! viewOnly }">
                          <td class="datacell">
                            <div align="center"><html:checkbox title="Copy to Future Periods" property="document.budget.nonpersonnelItem[${ctr}].copyToFuturePeriods" disabled="${copiedOver}"/>
                          </div></td>
                      </c:if>
                          <td class="datacell">
                            <div align="center"><kul:htmlControlAttribute property="document.budget.nonpersonnelItem[${ctr}].agencyRequestAmount" attributeEntry="${budgetNonpersonnel.agencyRequestAmount}" readOnly="${viewOnly}" styleClass="amount" />
                          </div></td>
                          <td class="datacell">
                            <div align="center"><kul:htmlControlAttribute property="document.budget.nonpersonnelItem[${ctr}].budgetInstitutionCostShareAmount" attributeEntry="${budgetNonpersonnel.budgetInstitutionCostShareAmount}" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}"  readOnly="${viewOnly}" styleClass="amount"/>
                          </div></td>
                          <td class="datacell">
                            <div align="center"><kul:htmlControlAttribute property="document.budget.nonpersonnelItem[${ctr}].budgetThirdPartyCostShareAmount" attributeEntry="${budgetNonpersonnel.budgetThirdPartyCostShareAmount}" disabled="${! KualiForm.document.budget.budgetThirdPartyCostShareIndicator}"  readOnly="${viewOnly}" styleClass="amount"/>
                            </div></td>
                          <c:if test="${! viewOnly }">
                            <td class="datacell-nowrap" align="center">
                              <div align="center"><html:image property="methodToCall.deleteNonpersonnel.anchorNonPersonnel.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" title="delete"/>
		                        </div></td>
		                      </c:if>
		                </tr>
	                </c:if>
                </logic:iterate>

                <!--  TOTALS LINE -->
                <tr>
                  <td colspan="2" class="infoline">&nbsp;</td>
                  <c:if test="${! viewOnly }"><td class="infoline">&nbsp;</td></c:if>
                  <td class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].agencyTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong></div></td>
                  <td class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].univCostShareTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong></div></td>
                  <td class="infoline"><div align="right"><strong><fmt:formatNumber value="${KualiForm.budgetNonpersonnelFormHelper.nonpersonnelCategoryHelperMap[nonpersonnelCategory.code].thirdPartyCostShareTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong></div></td>
                  <c:if test="${! viewOnly }"><td class="infoline">&nbsp;</td></c:if>
                </tr>
                <tr>
                  <td height="30" colspan="7" class="infoline" ><div align="right"> </div><div align="center"><html:image property="methodToCall.nonpersonnelCopyOver.code${nonpersonnelCategory.code}." src="${ConfigProperties.externalizable.images.url}tinybutton-viewperalloc.gif" styleClass="tinybutton" /><c:if test="${! viewOnly }">&nbsp; <html:image property="methodToCall.recalculate.anchorNonPersonnel" src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" styleClass="tinybutton" alt="recalculate" title="recalculate" /></c:if></div></td>
                </tr>
              </tbody></table>
            </div>
    </kul:tab>
  </logic:iterate>
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>

</div>
