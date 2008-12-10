/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.web.renderers;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.util.RendererUtil;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.pojo.PojoFormBase;

/**
 * Renders a quick field for an element
 */
public class QuickFinderRenderer extends FieldRendererBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickFinderRenderer.class);
    
    private int tabIndex = -1;
    
    /**
     * Gets the tabIndex attribute. 
     * @return Returns the tabIndex.
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Sets the tabIndex attribute value.
     * @param tabIndex The tabIndex to set.
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * Renders the quick finder to the page context
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.rice.kns.bo.BusinessObject)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write(buildQuickFinderHtml(pageContext));
        } catch (IOException ioe) {
            throw new JspException("Cannot render quick finder for field "+getField(), ioe);
        }
    }
    
    /**
     * Creates the HTML for a quick finder icon
     * @param businessObjectToRender the business object we're rendering
     * @return the html for the quick finder
     */
    protected String buildQuickFinderHtml(PageContext pageContext) {
        StringBuilder quickFinderHtml = new StringBuilder();
        quickFinderHtml.append("&nbsp;<input type=\"image\" ");
        //quickFinderHtml.append("tabindex=\"${tabindex}\" ");
        quickFinderHtml.append("name=\"").append(buildQuickFinderName(pageContext)).append("\" ");
        
        quickFinderHtml.append("src=\"");
        quickFinderHtml.append(SpringContext.getBean(KualiConfigurationService.class).getPropertyString("kr.externalizable.images.url"));
        quickFinderHtml.append("searchicon.gif");
        quickFinderHtml.append("\" ");
        
        quickFinderHtml.append("border=\"0\" ");
        
        quickFinderHtml.append("class=\"tinybutton\" ");
        
        quickFinderHtml.append("valign=\"middle\" ");
        
        quickFinderHtml.append("alt=\"Search ");
        quickFinderHtml.append(getField().getFieldLabel());
        quickFinderHtml.append("\" ");
        
        quickFinderHtml.append("title=\"Search ");
        quickFinderHtml.append(getAccessibleTitle());
        quickFinderHtml.append("\" ");
        
        if (tabIndex > -1) {
            quickFinderHtml.append(" tabIndex=\"");
            quickFinderHtml.append(getTabIndex());
            quickFinderHtml.append("\"");
        }
        
        quickFinderHtml.append("/> ");
        
        return quickFinderHtml.toString();
    }
    
    /**
     * Builds the (quite complex) name for the quick finder field
     * @return the name of the quick finder field
     */
    protected String buildQuickFinderName(PageContext pageContext) {
        StringBuilder nameBuf = new StringBuilder();
        nameBuf.append("methodToCall.performLookup.");
        
        nameBuf.append("(!!");
        nameBuf.append(getField().getQuickFinderClassNameImpl());
        nameBuf.append("!!).");
        
        nameBuf.append("(((");
        nameBuf.append(getField().getFieldConversions());
        nameBuf.append("))).");
        
        nameBuf.append("((#");
        nameBuf.append(getField().getLookupParameters());
        nameBuf.append("#)).");
        
        nameBuf.append("((<>))."); // hide return link
        
        nameBuf.append("(([]))."); // extra button source
        
        nameBuf.append("((**))."); // extra button params
        
        nameBuf.append("((^^))."); // supress actions
        
        nameBuf.append("((&&))."); // read only fields
        
        nameBuf.append("((/");
        nameBuf.append(getField().getReferencesToRefresh());
        nameBuf.append("/)).");
        
        nameBuf.append("((~~))."); // auto-search
        
        nameBuf.append("anchor"); // anchor
        
        String name = nameBuf.toString();
        RendererUtil.registerEditableProperty(pageContext, name);
        return name;
    }

    /**
     * A quick finder for a quick finder?  I fear not
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }

    /**
     * Clears the tab index
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    @Override
    public void clear() {
        super.clear();
        tabIndex = -1;
    }

}