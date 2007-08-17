/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.context;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.impl.DocumentServiceImpl;
import org.kuali.core.service.impl.KualiConfigurationServiceImpl;
import org.kuali.core.service.impl.PersistenceStructureServiceImpl;
import org.kuali.core.util.spring.Cached;
import org.kuali.core.util.spring.ClassOrMethodAnnotationFilter;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.impl.BalanceTypServiceImpl;
import org.kuali.test.ConfigureContext;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

@ConfigureContext
public class SpringAOPUsageTest extends KualiTestBase {

    public void testCaching() throws Exception {
        ClassOrMethodAnnotationFilter classOrMethodAnnotationFilter = new ClassOrMethodAnnotationFilter(Cached.class);
        assertTrue(KualiConfigurationServiceImpl.class.isAnnotationPresent(Cached.class));
        assertFalse(BalanceTypServiceImpl.class.isAnnotationPresent(Cached.class));
        assertTrue(classOrMethodAnnotationFilter.matches(KualiConfigurationServiceImpl.class));
        assertTrue(classOrMethodAnnotationFilter.matches(BalanceTypServiceImpl.class));
        // should be cached cause of method annotation
        SpringContext.getBean(BalanceTypService.class).getAllBalanceTyps();
        assertTrue(TestUtils.methodIsCached(BalanceTypService.class.getMethod("getAllBalanceTyps", new Class[] {}), new Object[] {}));
        // should not be cached cause no method annotation and no class annotation
        SpringContext.getBean(BalanceTypService.class).getEncumbranceBalanceTypes();
        assertFalse(TestUtils.methodIsCached(BalanceTypService.class.getMethod("getEncumbranceBalanceTypes", new Class[] {}), new Object[] {}));
        // should not be cached, cause no annotations on the class or its methods
        SpringContext.getBean(PriorYearAccountService.class).getByPrimaryKey("BL", "1031490");
        assertFalse(TestUtils.methodIsCached(PriorYearAccountService.class.getMethod("getByPrimaryKey", new Class[] { String.class, String.class }), new Object[] { "BL", "1031490" }));
    }
    
    /**
     * Assures the removeCacheKey method of methodCacheInterceptor is actually removing the method cache.
     * Depends on method implementations for BalanceTypService.getAllBalanceTyps() and PersistenceStructureService.getPrimaryKeys(Class clazz) 
     * having the @Cached annotation.
     */
    public void testClearMethodCache() throws Exception {
        SpringContext.getBean(BalanceTypService.class).getAllBalanceTyps();
        assertTrue(TestUtils.methodIsCached(BalanceTypService.class.getMethod("getAllBalanceTyps", new Class[] {}), new Object[] {}));
        TestUtils.removeCachedMethod(BalanceTypService.class.getMethod("getAllBalanceTyps", new Class[] {}), new Object[] {});
        assertFalse(TestUtils.methodIsCached(BalanceTypService.class.getMethod("getAllBalanceTyps", new Class[] {}), new Object[] {}));
    }

    @Transactional
    public void testTransactions() throws Exception {
        ClassOrMethodAnnotationFilter classOrMethodAnnotationFilter = new ClassOrMethodAnnotationFilter(Transactional.class);
        Exception exception = null;
        try {
            classOrMethodAnnotationFilter.matches(getClass());
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("The @Transactional annotation should be specified at the class level and overriden at the method level, if need be.", exception.getMessage());
        Advisor transactionAdvisor = SpringContext.getBean(Advisor.class);
        // should be transaction applicable because the class has the annotation
        assertTrue(AopUtils.canApply(transactionAdvisor, DocumentServiceImpl.class));
        // should not be transaction applicable since there's no annotation in the class hierarchy
        assertFalse(AopUtils.canApply(transactionAdvisor, PersistenceStructureServiceImpl.class));
        TransactionAttributeSource transactionAttributeSource = SpringContext.getBean(TransactionAttributeSource.class);
        // should be transactionalized because the class that defines it has the transactional annotation
        TransactionAttribute documentServiceSaveDocumentAttribute = transactionAttributeSource.getTransactionAttribute(DocumentService.class.getMethod("saveDocument", new Class[] { Document.class }), DocumentServiceImpl.class);
        assertNotNull(documentServiceSaveDocumentAttribute);
        TransactionAttribute documentServiceValidateAndPersistDocumentAttribute = transactionAttributeSource.getTransactionAttribute(DocumentService.class.getMethod("validateAndPersistDocument", new Class[] { Document.class, KualiDocumentEvent.class }), DocumentServiceImpl.class);
        assertNotNull(documentServiceValidateAndPersistDocumentAttribute);
        assertTrue(TransactionDefinition.PROPAGATION_REQUIRED == documentServiceValidateAndPersistDocumentAttribute.getPropagationBehavior());
    }
}
