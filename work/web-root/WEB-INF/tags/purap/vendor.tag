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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>
              
<%@ attribute name="displayPaymentRequestFields" required="false"
              description="Boolean to indicate if PREQ specific fields should be displayed" %>              
              
<%@ attribute name="displayCreditMemoFields" required="false"
              description="Boolean to indicate if CM specific fields should be displayed" %>              
              
<%@ attribute name="purchaseOrderAwarded" required="false"
              description="Boolean to indicate if this is a PO that has been awarded" %>              

<c:set var="vendorReadOnly" value="${(not empty KualiForm.editingMode['lockVendorEntry'])}" />
<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="editPreExtract"	value="${(not empty KualiForm.editingMode['editPreExtract'])}" />
<c:set var="currentUserCampusCode" value="${UserSession.universalUser.campusCode}" />

<kul:tab tabTitle="Vendor" defaultOpen="${not (displayRequisitionFields or displayPurchaseOrderFields)}" tabErrorKey="${PurapConstants.VENDOR_ERRORS}">
    <div class="tab-container" align=center>
        <html:hidden property="document.vendorHeaderGeneratedIdentifier" />
        <html:hidden property="document.vendorDetailAssignedIdentifier" />

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
            <tr>
                <td colspan="4" class="subhead">Vendor Address</td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b" width="25%">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="${not (fullEntryMode or amendmentEntry) or vendorReadOnly or displayPaymentRequestFields or displayCreditMemoFields or purchaseOrderAwarded}" />
                    <c:if test="${(fullEntryMode or amendmentEntry) and (displayRequisitionFields or displayPurchaseOrderFields) and (!purchaseOrderAwarded)}" >
                        <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorDetail" 
                        lookupParameters="'Y':activeIndicator, 'PO':vendorHeader.vendorTypeCode"
                        fieldConversions="vendorHeaderGeneratedIdentifier:document.vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:document.vendorDetailAssignedIdentifier,defaultAddressLine1:document.vendorLine1Address,defaultAddressLine2:document.vendorLine2Address,defaultAddressCity:document.vendorCityName,defaultAddressPostalCode:document.vendorPostalCode,defaultAddressStateCode:document.vendorStateCode,defaultAddressCountryCode:document.vendorCountryCode"/>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b" width="25%">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCityName}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" readOnly="${not (fullEntryMode or amendmentEntry) or displayCreditMemoFields}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorDetail.vendorNumber" readOnly="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorStateCode}" /><br> *required for US</div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" readOnly="${not (fullEntryMode or amendmentEntry) or displayCreditMemoFields}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine1Address}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.vendorLine1Address" readOnly="${not (fullEntryMode or amendmentEntry) or displayCreditMemoFields}" />
                    <c:if test="${(fullEntryMode or amendmentEntry) and vendorReadOnly}">
                        <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorAddress" 
                        readOnlyFields="active, vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes"
                        lookupParameters="'Y': active,document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" 
                        fieldConversions="vendorAddressGeneratedIdentifier:document.vendorAddressGeneratedIdentifier"/>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPostalCode}" /><br> *required for US</div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" readOnly="${not (fullEntryMode or amendmentEntry) or displayCreditMemoFields}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine2Address}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" readOnly="${not (fullEntryMode or amendmentEntry) or displayCreditMemoFields}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCountryCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode" readOnly="${not (fullEntryMode or amendmentEntry) or displayCreditMemoFields}" />
                </td>
            </tr>

            <tr>
                <td colspan="4" class="subhead">Vendor Info</td>
            </tr>

            <c:if test="${displayPurchaseOrderFields}">
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderVendorChoiceCode}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderVendorChoiceCode}" property="document.purchaseOrderVendorChoiceCode" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </tr>
            </c:if>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCustomerNumber}" /></div>
                </th>

                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCustomerNumber}" property="document.vendorCustomerNumber" readOnly="${not (fullEntryMode or amendmentEntry) or displayCreditMemoFields}" />
                    <c:if test="${(fullEntryMode or amendmentEntry) and vendorReadOnly}">
                        <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorCustomerNumber" readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes"
                        lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" fieldConversions="vendorCustomerNumber:document.vendorCustomerNumber"/>
                    </c:if>
                </td>

                <c:if test="false">
                <!-- Placeholder for alternate payee logic -->
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternateVendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternateVendorName}" property="document.alternateVendorName" readOnly="true" />
                        <c:if test="${not empty document.alternateVendorNumber}">
                            &nbsp;(<kul:htmlControlAttribute attributeEntry="${documentAttributes.alternateVendorNumber}" property="document.alternateVendorNumber" readOnly="true" />)&nbsp;
                        </c:if>
                        <c:if test="${(fullEntryMode or amendmentEntry) and displayPurchaseOrderFields}">
                            <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorDetail" fieldConversions="vendorHeaderGeneratedIdentifier:document.alternateVendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:document.alternateVendorDetailAssignedIdentifier"/>
                        </c:if>
                        <c:if test="${(fullEntryMode or amendmentEntry) and displayPaymentRequestFields}">
                            select
                        </c:if>
                    </td>
                </c:if>                                                 
                <c:if test="true">
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </c:if>                                                 
            </tr>

            <tr>
                <c:if test="${displayRequisitionFields or displayPurchaseOrderFields}">
                    <th align=right valign=middle class="bord-l-b" rowspan="3">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNoteText}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell" rowspan="3">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNoteText}" property="document.vendorNoteText" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>
                </c:if>                                                 
                <c:if test="${displayPaymentRequestFields or displayCreditMemoFields}">
                    <th align=right valign=middle class="bord-l-b" rowspan="3">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.noteLine1Text}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.noteLine1Text}" property="document.noteLine1Text" readOnly="${not (fullEntryMode or amendmentEntry or editPreExtract)}" />
                    </td>
                </c:if>
                <c:if test="${not displayCreditMemoFields}">                                                 
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPaymentTermsCode}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPaymentTermsCode}" 
	                    property="document.vendorPaymentTermsCode" 
	                    extraReadOnlyProperty="document.vendorPaymentTerms.vendorPaymentTermsDescription"
	                    readOnly="${not (fullEntryMode or amendmentEntry) or displayRequisitionFields}" />
	                </td>
				</c:if>	
				<c:if test="${displayCreditMemoFields}">
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </c:if>                    
            </tr> 

            <tr>
                <!-- left column populated by note row span for PUR docs-->
                <c:if test="${displayPaymentRequestFields or displayCreditMemoFields}">
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.noteLine2Text}" property="document.noteLine2Text" readOnly="${not (fullEntryMode or amendmentEntry or editPreExtract)}" />
                    </td>
                </c:if>                                                 
                <c:if test="${not displayCreditMemoFields}">
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorShippingTitleCode}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorShippingTitleCode}" 
	                    property="document.vendorShippingTitleCode" 
	                    extraReadOnlyProperty="document.vendorDetail.vendorShippingTitle.vendorShippingTitleDescription"
	                    readOnly="${not (fullEntryMode or amendmentEntry) or not displayPurchaseOrderFields}" />
	                </td>
                </c:if>
                <c:if test="${displayCreditMemoFields}">
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </c:if>    
            </tr> 

            <tr>
                <!-- left column populated by note row span for PUR docs-->
                <c:if test="${displayPaymentRequestFields or displayCreditMemoFields}">
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.noteLine3Text}" property="document.noteLine3Text" readOnly="${not (fullEntryMode or amendmentEntry or editPreExtract)}" />
                    </td>
                </c:if> 
                <c:if test="${not displayCreditMemoFields}">                                                
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorShippingPaymentTermsCode}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorShippingPaymentTermsCode}" 
	                    property="document.vendorShippingPaymentTermsCode" 
	                    extraReadOnlyProperty="document.vendorDetail.vendorShippingPaymentTerms.vendorShippingPaymentTermsDescription"
	                    readOnly="${not (fullEntryMode or amendmentEntry) or not displayPurchaseOrderFields}" />
	                </td>
				</c:if>
				<c:if test="${displayCreditMemoFields}">
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </c:if>    	                
            </tr> 

            <c:if test="${displayRequisitionFields or displayPurchaseOrderFields}">
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContractName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContractName}" property="document.vendorContractName" readOnly="true"/>
                        <c:if test="${(fullEntryMode or amendmentEntry)}">
                            <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorContract" readOnlyFields="vendorCampusCode" lookupParameters="'${currentUserCampusCode}':vendorCampusCode" fieldConversions="vendorContractGeneratedIdentifier:document.vendorContractGeneratedIdentifier" />
                        </c:if>
                    </td>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContactsLabel}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContactsLabel}" property="document.vendorContactsLabel" readOnly="true"/>                    
                        <c:if test="${vendorReadOnly}" >
                            <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorContact" readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes" 
                            lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" 
                            hideReturnLink="true" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" />                    
                        </c:if>
                    </td>
                </tr>            

                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPhoneNumber}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPhoneNumber}" property="document.vendorPhoneNumber" readOnly="true"/>                    
                        <c:if test="${vendorReadOnly}" >
                            <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorPhoneNumber" readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes" 
                            lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" 
                            hideReturnLink="true" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" />                    
                        </c:if>
                    </td>
                    <th align=right valign=middle class="bord-l-b" rowspan="2">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.supplierDiversityLabel}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell" rowspan="2">
                          <c:if test="${not empty KualiForm.document.vendorDetail.vendorHeader.vendorSupplierDiversities}">
                              <c:forEach var="item" items="${KualiForm.document.vendorDetail.vendorHeader.vendorSupplierDiversities}" varStatus="status">
                                  <c:if test="${!(status.first)}"><br></c:if>${item.vendorSupplierDiversity.vendorSupplierDiversityDescription}
                              </c:forEach>
                          </c:if>&nbsp;
                    </td>
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorFaxNumber}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorFaxNumber}" property="document.vendorFaxNumber" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>
                </tr>
            </c:if>

            <c:if test="${displayPaymentRequestFields}">
                <tr>
                    <th align=right valign=middle class="bord-l-b" rowspan="3">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.specialHandlingInstructionLine1Text}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.specialHandlingInstructionLine1Text}" property="document.specialHandlingInstructionLine1Text" readOnly="${not (fullEntryMode or editPreExtract)}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </tr> 
    
                <tr>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.specialHandlingInstructionLine2Text}" property="document.specialHandlingInstructionLine2Text" readOnly="${not (fullEntryMode or editPreExtract)}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </tr> 
    
                <tr>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.specialHandlingInstructionLine3Text}" property="document.specialHandlingInstructionLine3Text" readOnly="${not (fullEntryMode or editPreExtract)}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </tr> 
            </c:if>

        </table>

        <c:if test="${displayRequisitionFields}">
            <div class="h2-container">
                <h2>Additional Suggested Vendor Names</h2>
            </div>

            <table cellpadding="0" cellspacing="0" class="datatable" summary="Additional Vendor Section">
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate1VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate1VendorName}" property="document.alternate1VendorName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate2VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate2VendorName}" property="document.alternate2VendorName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate3VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate3VendorName}" property="document.alternate3VendorName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>
                </tr>
                <tr>    
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate4VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate4VendorName}" property="document.alternate4VendorName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>
                </tr>
                <tr>    
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate5VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate5VendorName}" property="document.alternate5VendorName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    </td>                                                
                </tr>
            </table>
        </c:if>

    </div>
</kul:tab>