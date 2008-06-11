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

<kul:tab tabTitle="Select Operation" defaultOpen="true" tabErrorKey="reportSel">
	<div class="tab-container" align="center" id="G02" style="display: block;">
    	<div class="h2-container">
    		<h2>Select Operation</h2>
      	</div>
      	
      	<table width="100%" cellpadding="0" cellspacing="0">
      		 <tr>
                <td>Show Position Pick List </td>
                <td width="200">
                  <div align="center">
                     <html:image property="methodToCall.performPositionPick" src="${ConfigProperties.externalizable.images.url}tinybutton-posnsalset.gif" title="Position Pick" alt="Position Pick" styleClass="tinybutton" />
                  </div>
                </td>
             </tr>
       		 <tr>
                <td>Show Incumbent Pick List </td>
                <td width="200">
                  <div align="center">
                     <html:image property="methodToCall.performIncumbentPick" src="${ConfigProperties.externalizable.images.url}tinybutton-incmbntsalset.gif" title="Incumbent Pick" alt="Incumbent Pick" styleClass="tinybutton" />
                  </div>
                </td>
             </tr>            
      </table>
  </div>
</kul:tab> 
