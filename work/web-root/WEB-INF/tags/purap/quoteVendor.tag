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
              description="The DataDictionary entry containing attributes for this doc's fields." %>
<%@ attribute name="vendorQuoteAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="ctr" required="true" description="vendor count"%>
<%@ attribute name="isPurchaseOrderAwarded" required="true" description="has the PO been awarded?" %>
<%@ attribute name="isSysVendor" required="false" description="vendor is from system?" %>
<%@ attribute name="isAwarded" required="false" description="vendor has been awarded?" %>
<%@ attribute name="isTransmitPrintDisplayed" required="false" description="vendor quote is ready to print?" %>
<%@ attribute name="isTrasnmitted" required="false" description="PO transmitted to vendor?" %>

        <tr>
			<td colspan="5" class="subhead">
				<span class="subhead-left">Vendor ${ctr + 1}</span>
			</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorName}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorName}" property="document.purchaseOrderVendorQuote[${ctr}].vendorName" 
                readOnly="${isPurchaseOrderAwarded || isSysVendor || !preRouteChangeMode}" />
			    <html:hidden property="document.purchaseOrderVendorQuote[${ctr}].documentNumber" />
			    <html:hidden property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderVendorQuoteIdentifier" />
			    <html:hidden property="document.purchaseOrderVendorQuote[${ctr}].versionNumber" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorHeaderGeneratedIdentifier}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
            	<c:if test="${not isSysVendor}">N/A</c:if>
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorHeaderGeneratedIdentifier}" property="document.purchaseOrderVendorQuote[${ctr}].vendorHeaderGeneratedIdentifier" readOnly="true" />-
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorHeaderGeneratedIdentifier}" property="document.purchaseOrderVendorQuote[${ctr}].vendorDetailAssignedIdentifier" readOnly="true" />
            </td>
           	<c:if test="${!isPurchaseOrderAwarded && !isTrasnmitted && preRouteChangeMode}">
   	         <td rowspan="9">
   	        	<html:image
	property="methodToCall.deleteVendor.line${ctr}"
	src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
	alt="delete vendor" title="delete vendor"
	styleClass="tinybutton" />&nbsp;
				</td>
			</c:if>
           	<c:if test="${isPurchaseOrderAwarded || isTrasnmitted || !preRouteChangeMode}">
   	         <td rowspan="9">
			 	&nbsp;
			 </td>
			</c:if>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" property="document.purchaseOrderVendorQuote[${ctr}].vendorLine1Address" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" property="document.purchaseOrderVendorQuote[${ctr}].vendorPhoneNumber" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" property="document.purchaseOrderVendorQuote[${ctr}].vendorLine2Address" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" property="document.purchaseOrderVendorQuote[${ctr}].vendorFaxNumber" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorCityName}" />
                &nbsp;/&nbsp;
                <kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorStateCode}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorCityName}" property="document.purchaseOrderVendorQuote[${ctr}].vendorCityName" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorStateCode}" property="document.purchaseOrderVendorQuote[${ctr}].vendorStateCode" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" property="document.purchaseOrderVendorQuote[${ctr}].vendorPostalCode" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitTypeCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitTypeCode}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteTransmitTypeCode" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            	<c:if test="${!isPurchaseOrderAwarded && preRouteChangeMode}">
					<html:image
	property="methodToCall.transmitPurchaseOrderQuote.line${ctr}"
	src="${ConfigProperties.externalizable.images.url}tinybutton-transmit.gif"
	alt="transmit quote" title="transmit quote" 
	styleClass="tinybutton" />
					<c:if test="${isTransmitPrintDisplayed}">
							<!-- html:image
			property="methodToCall.printPoQuote.line${ctr}"
			src="${ConfigProperties.externalizable.images.url}tinybutton-downldtransquoreq.gif"
			alt="print quote request" title="print quote request" 
			styleClass="tinybutton" / -->
						<input type="hidden" name="methodToCall.printPoQuote.line<c:out value="${ctr}" />.x" value="1" />
						<input type="hidden" name="methodToCall.printPoQuote.line<c:out value="${ctr}" />.y" value="1" />
						 <SCRIPT language="JavaScript">
						function printPOQuote() {
							var kualiForm = document.forms['KualiForm'];
							kualiForm.submit();
	  					}
	  					window.onload = printPOQuote;
						</SCRIPT>
					</c:if>
				</c:if>
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" property="document.purchaseOrderVendorQuote[${ctr}].vendorAttentionName" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitDate}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitDate}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteTransmitDate" readOnly="true" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuotePriceExpirationDate}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuotePriceExpirationDate}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuotePriceExpirationDate" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteStatusCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteStatusCode}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteStatusCode" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteRankNumber}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteRankNumber}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteRankNumber" 
                readOnly="${isPurchaseOrderAwarded || !preRouteChangeMode}" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
            	<c:if test="${isPurchaseOrderAwarded}">
            		Awarded:
            	</c:if>
            	<c:if test="${!isPurchaseOrderAwarded}">
            		Award:
            	</c:if>
            </th>
            <td align=left valign=middle class="datacell">
            	<c:if test="${isSysVendor && not isPurchaseOrderAwarded}">
	            	<html:radio property="awardedVendorNumber" value="${ctr}" disabled="${!preRouteChangeMode}" />
            	</c:if>
            	<c:if test="${isPurchaseOrderAwarded}">
	           		<c:if test="${!isAwarded}">
	           			No
	           		</c:if>
	           		<c:if test="${isAwarded}">
	           			Yes
	           		</c:if>
            	</c:if>
				&nbsp;
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteAwardDate}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteAwardDate}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteAwardDate" readOnly="true" />
            </td>
        </tr>
