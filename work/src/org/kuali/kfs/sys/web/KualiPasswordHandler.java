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
package org.kuali.cas.auth;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.util.SpringServiceLocator;

import edu.yale.its.tp.cas.auth.provider.WatchfulPasswordHandler;

public class KualiPasswordHandler extends WatchfulPasswordHandler {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiPasswordHandler.class);
    /**
     * Authenticates the given username/password pair, returning true on success
     * and false on failure.
     */
    public boolean authenticate(javax.servlet.ServletRequest request, String username, String password) {
        if (super.authenticate(request, username, password) != false) {
            try {
                if (username != null) {
                    // check the username and password against the db
                    // return true if they are there and have a valid password
                    //if ( LOG.isDebugEnabled() ) {
                    //    LOG.debug( "Attempting login for user id: " + username + " and password hash: " + SpringServiceLocator.getEncryptionService().hash( password.trim() ) );
                    //}
                    // obtain the universal user record
                    UniversalUser user = SpringServiceLocator.getUniversalUserService().getUniversalUser( new AuthenticationUserId( username.trim() ) );
                    //if ( LOG.isDebugEnabled() ) {
                    //    LOG.debug( "Found user " + user.getPersonName() + " with password hash: " + user.getFinancialSystemsEncryptedPasswordText() );
                    //}
                    // check if the password needs to be checked
                    if ( SpringServiceLocator.getKualiConfigurationService().getApplicationParameterIndicator( Constants.CoreApcParms.GROUP_CORE_MAINT_EDOCS, Constants.CoreApcParms.CAS_PASSWORD_ENABLED ) ) {
                        // if so, hash the passed in password and compare to the hash retrieved from the database
                        String hashedPassword = user.getFinancialSystemsEncryptedPasswordText();
                        if ( hashedPassword == null ) {
                            hashedPassword = "";
                        }
                        hashedPassword = StringUtils.stripEnd( hashedPassword, EncryptionService.HASH_POST_PREFIX );
                        if ( SpringServiceLocator.getEncryptionService().hash( password.trim() ).equals( hashedPassword ) ) {
                            return true; // password matched
                        }
                    } else {
                        return true; // no need to check password - user's existence is enough
                    }
                }
            } catch ( GeneralSecurityException ex ) {
                return false; // fail if the hash function fails
            } catch ( UserNotFoundException ex ) {
                return false; // fail if user does not exist
            }

        }
        return false; // fail if we get to this point
    }
}