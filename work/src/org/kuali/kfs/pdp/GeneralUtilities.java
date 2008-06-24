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
/*
 * Created on Dec 10, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.kfs.pdp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.pdp.exception.ConfigurationError;
import org.kuali.kfs.sys.service.ParameterService;


/**
 * @author HSTAPLET
 */
public class GeneralUtilities {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralUtilities.class);

    // GENERAL UTILITIES SECTION

    public static int getParameterInteger(ParameterService parameterService, Class componentClass, String parm) {
        String srpp = parameterService.getParameterValue(componentClass, parm);
        if (srpp != null) {
            try {
                return Integer.parseInt(srpp);
            }
            catch (NumberFormatException e) {
                throw new ConfigurationError(parm + " is not a number");
            }
        }
        else {
            throw new ConfigurationError("Unable to find " + parm);
        }
    }

    public static int getParameterInteger(ParameterService parameterService, Class componentClass, String parm, int defaultValue) {
        String srpp = parameterService.getParameterValue(componentClass, parm);
        if (srpp != null) {
            try {
                return Integer.parseInt(srpp);
            }
            catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        else {
            return defaultValue;
        }
    }

    /**
     * Method to return the name of the button that was pressed. Returns the full name.
     */
    public static String whichButtonWasPressed(HttpServletRequest request) {
        String paramName = new String();

        Enumeration enumer = request.getParameterNames();
        while (enumer.hasMoreElements()) {
            paramName = (String) enumer.nextElement();
            if (paramName.startsWith("btn")) { // All the button names start with btn
                if (paramName.indexOf(".") > -1) {
                    return paramName.substring(0, paramName.indexOf("."));
                }
                else {
                    return paramName;
                }
            }
        }
        return "";
    }

    // FORM VALIDATION SECTION

    /**
     * Method to check if a String field is null or an empty string. Return true if String is empty, otherwise return false.
     */
    public static boolean isStringEmpty(String field) {
        if (field == null || field.equals("")) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method to check how many times char x occurs in the given String. Return the number of occurences.
     */
    public static int numberOfOccurences(String string, char x) {
        LOG.debug("numberOfOccurences() Enter method with string " + string + " and char " + x);
        int occurenceCtr = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == x) {
                ++occurenceCtr;
            }
        }
        return occurenceCtr;
    }

    /**
     * formatStringWithoutGivenChar takes a String and removes the given char out of the string. (i.e. remove commas: ',')
     * 
     * @param myString string to be formatted
     * @param removeChar char to be removed from string
     * @return String formatted string
     */
    public static String formatStringWithoutGivenChar(String myString, char removeChar) {
        if (!isStringEmpty(myString)) {
            for (int i = 0; i < myString.length(); i++) {
                if (myString.charAt(i) == removeChar) {
                    myString = myString.substring(0, i) + myString.substring(i + 1);
                    i--; // repeat check on last position to keep from skipping the next char
                }
            }
            return myString;
        }
        return "";
    }

    /**
     * Method to check if a String field is n chars long. Return true if String is n chars long, otherwise return false.
     */
    public static boolean isStringFieldNLength(String field, int nLength) {
        LOG.debug("Entered isStringFieldNLength");
        if (!isStringEmpty(field)) {
            if (field.length() == nLength) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check if a String field is n chars long. Return true if String is n chars long, otherwise return false.
     */
    public static boolean isStringFieldAtMostNLength(String field, int nLength) {
        LOG.debug("Entered isStringFieldNLength");
        if (!isStringEmpty(field)) {
            if (field.length() >= nLength) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check if a String field is all numbers or letters. Return true if String is numeric/alphabetic, otherwise return
     * false.
     */
    public static boolean isStringAllNumbersOrLetters(String field) {
        if (!isStringEmpty(field)) {
            field = field.trim();
            LOG.debug("Entered isStringAllNumbersOrLetters: field is " + field);
            for (int x = 0; x < field.length(); x++) {
                char c = field.charAt(x);

                if (!Character.isLetterOrDigit(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Method to check if a String field is all numbers or a given character. Return true if String is numbers and the given
     * character, otherwise return false.
     */
    public static boolean isStringAllNumbersOrACharacter(String string, char c) {
        if (!isStringEmpty(string)) {
            string = string.trim();
            LOG.debug("isStringAllNumbersOrACharacter(): entered method with string " + string + " , character " + c);
            for (int x = 0; x < string.length(); x++) {
                char ch = string.charAt(x);

                if (!Character.isDigit(ch) && !(ch == c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Method to check if a String field is all numbers or a given character. Return true if String is numbers and the given
     * character, otherwise return false.
     */
    public static boolean isStringAllNumbersOrASingleCharacter(String string, char c) {
        if (!isStringEmpty(string)) {
            string = string.trim();
            LOG.debug("isStringAllNumbersOrASingleCharacter(): entered method with string " + string + " , character " + c);
            int charIndex = string.indexOf(c);
            if (charIndex < 0) {
                return isStringAllNumbers(string);
            }
            else {
                for (int x = 0; x < string.length(); x++) {
                    char ch = string.charAt(x);

                    if (!Character.isDigit(ch) && !(ch == c)) {
                        return false;
                    }
                    if ((ch == c) && (x != charIndex)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check if a String field is all numbers. Return true if String is numeric, otherwise return false.
     * 
     * NOTE: This returns true if the string is all spaces which is probably bad but since it is used
     * so many places, I'm not changing it now.
     */
    public static boolean isStringAllNumbers(String field) {
        LOG.debug("Entered isStringAllNumbers().");
        if (!isStringEmpty(field)) {
            field = field.trim();
            for (int x = 0; x < field.length(); x++) {
                char c = field.charAt(x);

                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static Integer convertStringToInteger(String s) {
        Integer i = null;
        try {
            if (!isStringEmpty(s)) {
                i = new Integer(Integer.parseInt(s));
            }
        }
        catch (Exception e) {
            LOG.error("convertStringToInteger() error occurred; return -1.", e);
            i = new Integer(-1);
        }
        return i;
    }

    public static Long convertStringToLong(String s) {
        Long l = null;
        try {
            if (!isStringEmpty(s)) {
                l = new Long(Long.parseLong(s));
            }
        }
        catch (Exception e) {
            LOG.error("convertStringToLong() error occurred; return -1.", e);
            l = new Long(-1);
        }
        return l;
    }

    public static Date convertStringToDate(String s) {
        Date d = null;
        try {
            if (!isStringEmpty(s)) {
                d = DateHandler.makeStringSqlDate(s);
            }
        }
        catch (Exception e) {
            LOG.error("convertStringToDate() error occurred.", e);
            d = null;
        }
        return d;
    }

    public static BigDecimal convertStringToBigDecimal(String s) {
        BigDecimal bd = null;
        try {
            if (!isStringEmpty(s)) {
                bd = new BigDecimal(s);
            }
        }
        catch (Exception e) {
            LOG.error("convertStringToBigDecimal() error occurred; return -1.", e);
            bd = new BigDecimal(-1);
        }
        return bd;
    }

    public static String convertIntegerToString(Integer i) {
        String s = "";
        try {
            if (i != null) {
                s = i.toString();
            }
        }
        catch (Exception e) {
            LOG.error("convertIntegerToString() error occurred; return empty string.", e);
            s = "";
        }
        return s;
    }

    public static String convertLongToString(Long l) {
        String s = "";
        try {
            if (l != null) {
                s = l.toString();
            }
        }
        catch (Exception e) {
            LOG.error("convertLongToString() error occurred; return empty string.", e);
            s = "";
        }
        return s;
    }

    public static String convertDateToString(Date d) {
        String s = "";
        try {
            if (d != null) {
                s = DateHandler.makeDateString(d);
            }
        }
        catch (Exception e) {
            LOG.error("convertDateToString() error occurred; return empty string.", e);
            s = "";
        }
        return s;
    }

    public static String convertBigDecimalToString(BigDecimal bd) {
        String s = "";
        try {
            if (bd != null) {
                s = bd.toString();
            }
        }
        catch (Exception e) {
            LOG.error("convertBigDecimalToString() error occurred; return empty string.", e);
            s = "";
        }
        return s;
    }

    public static boolean isRegexMatch(String regex, String input) {
        Pattern regexPattern = Pattern.compile(regex);
        Matcher regexMatcher = regexPattern.matcher(input);

        return regexMatcher.find();
    }

    public static boolean isUsZipCodeValid(String test) {
        if (test == null) {
            return false;
        }
        return isRegexMatch("(^\\d{5}$)|(^\\d{5}-\\d{4}$)", test);
    }
}
