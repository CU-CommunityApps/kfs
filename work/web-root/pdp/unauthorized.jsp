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
<html>
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
<head>
<title>Unauthorized</title>
</head>

<h1><strong>Unauthorized</strong></h1>
<jsp:include page="TestEnvironmentWarning.jsp" flush="true"/>
<p>You are not authorized for this action. If this is an error, please contact <a href="mailto:test@email.com">test@email.com</a> for assistance. </p>
</body>
</html>
