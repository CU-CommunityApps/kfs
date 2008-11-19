<%--
 Copyright 2007 The Kuali Foundation.
 
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


<kul:tab tabTitle="Tax Information" defaultOpen="true" tabErrorKey="${PurapConstants.PAYMENT_REQUEST_TAX_TAB_ERRORS}">
    <div class="tab-container" align=center>
    	<c:if test="${taxAreaEditable}">
    		<h3>Tax Area Edits</h3>  
    	</c:if>  	
    	<c:if test="${!taxAreaEditable}">
    		<h3>Tax Information</h3>
    	</c:if>  	

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Tax Info Section">

        	<tr>
            	<th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.taxClassificationCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.taxClassificationCode}" property="document.taxClassificationCode" readOnly="${not taxAreaEditable}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.foreignSourceIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.foreignSourceIndicator}" property="document.foreignSourceIndicator" readOnly="${not taxAreaEditable}" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.federalTaxPercent}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.federalTaxPercent}" property="document.federalTaxPercent" readOnly="${not taxAreaEditable}" />
					&nbsp;                
                    <c:if test="${taxAreaEditable}">
                   		<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent"
                    		lookupParameters="document.taxClassificationCode:incomeClassCode,'F':incomeTaxTypeCode,'Y':active"
                        	fieldConversions="incomeTaxPercent:document.federalTaxPercent"/>   
                    </c:if>                
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.taxExemptTreatyIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.taxExemptTreatyIndicator}" property="document.taxExemptTreatyIndicator" readOnly="${not taxAreaEditable}" />
                </td>
            </tr>
                        
            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.stateTaxPercent}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.stateTaxPercent}" property="document.stateTaxPercent" readOnly="${not taxAreaEditable}" />
					&nbsp;                
                    <c:if test="${taxAreaEditable}">
                   		<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent"
                    		lookupParameters="document.taxClassificationCode:incomeClassCode,'S':incomeTaxTypeCode,'Y':active"
                        	fieldConversions="incomeTaxPercent:document.stateTaxPercent"/>   
                    </c:if>                
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.otherTaxExemptIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.otherTaxExemptIndicator}" property="document.otherTaxExemptIndicator" readOnly="${not taxAreaEditable}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.taxCountryCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.taxCountryCode}" property="document.taxCountryCode" readOnly="${not taxAreaEditable}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.grossUpIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.grossUpIndicator}" property="document.grossUpIndicator" readOnly="${not taxAreaEditable}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.taxNQIId}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.taxNQIId}" property="document.taxNQIId" readOnly="${not taxAreaEditable}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.taxUSAIDPerDiemIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.taxUSAIDPerDiemIndicator}" property="document.taxUSAIDPerDiemIndicator" readOnly="${not taxAreaEditable}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right">&nbsp;</div>
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td>                
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.taxSpecialW4Amount}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.taxSpecialW4Amount}" property="document.taxSpecialW4Amount" readOnly="${not taxAreaEditable}" />
                </td>
            </tr>

		</table> 				

    </div>
</kul:tab>
