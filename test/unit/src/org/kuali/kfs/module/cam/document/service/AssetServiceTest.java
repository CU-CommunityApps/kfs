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
package org.kuali.kfs.module.cam.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

public class AssetServiceTest extends KualiTestBase {

    private AssetService assetService;

    @Override
    @ConfigureContext(session = UserNameFixture.KHUNTLEY, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        assetService = SpringContext.getBean(AssetService.class);
    }


    /**
     * Test isObjectSubTypeCompatible
     * 
     * @throws Exception
     */
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = false)
    public void testIsObjectSubTypeCompatible_Success() throws Exception {
        List<String> ls = new ArrayList<String>();
        ls.add("UC");
        ls.add("UF");
        ls.add("UO");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));
        
        ls.clear();
        ls.add("LI");
        ls.add("LI");        
        assertTrue(assetService.isObjectSubTypeCompatible(ls));
        
        ls.clear();
        ls.add("LI");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));

        // no failure because it's 1 payment
        ls.clear();
        ls.add("IF");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));

        // no failure because it's of same object sub type
        ls.clear();
        ls.add("IF");
        ls.add("IF");
        assertTrue(assetService.isObjectSubTypeCompatible(ls));
    }
    
    /**
     * Test isObjectSubTypeCompatible
     * 
     * @throws Exception
     */
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = false)
    public void testIsObjectSubTypeCompatible_Failure() throws Exception {
        List<String> ls = new ArrayList<String>();
        ls.add("BD");
        ls.add("UF");
        ls.add("UO");
        assertFalse(assetService.isObjectSubTypeCompatible(ls));
        
        ls.clear();
        ls.add("UF");
        ls.add("LI");        
        assertFalse(assetService.isObjectSubTypeCompatible(ls));
    }
}
