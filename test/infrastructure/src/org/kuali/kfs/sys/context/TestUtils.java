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
package org.kuali.kfs.sys.context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.properties.PropertyTree;
import org.springframework.aop.framework.ProxyFactory;

/**
 * This class provides utility methods for use during manual testing.
 */
@AnnotationTestSuite(PreCommitSuite.class)
public class TestUtils {
    private static final Log LOG = LogFactory.getLog(TestUtils.class);
    private static Integer fiscalYearForTesting;

    private static final String PLACEHOLDER_FILENAME = "placeholder.txt";
    
    private static ParameterService parameterService;

    public static ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
    
    /**
     * Disables all scheduled tasks, to make debugging easier.
     */
    public static void disableScheduledTasks() {
        Timer timer = SpringContext.getBean(Timer.class);
        timer.cancel();
    }


    /**
     * Iterates through the given Collection, printing toString of each item in the collection to stderr
     * 
     * @param collection
     */
    public static void dumpCollection(Collection collection) {
        dumpCollection(collection, new ItemStringFormatter());
    }

    /**
     * Iterates through the given Collection, printing f.format() of each item in the collection to stderr
     * 
     * @param collection
     * @param formatter ItemFormatter used to format each item for printing
     */
    public static void dumpCollection(Collection collection, ItemFormatter formatter) {
        LOG.error(formatCollection(collection, formatter));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param collection
     * @param formatter
     * @return String composed of contents of the given Collection, one per line, formatted by the given ItemFormatter
     */
    public static String formatCollection(Collection collection, ItemFormatter formatter) {
        StringBuffer formatted = new StringBuffer("size= ");
        formatted.append(collection.size());

        for (Iterator i = collection.iterator(); i.hasNext();) {
            formatted.append(formatter.format(i.next()));
            formatted.append("\n");
        }

        return formatted.toString();
    }


    /**
     * Iterates through the entries of the given Map, printing toString of each (key,value) to stderr
     * 
     * @param map
     */
    public static void dumpMap(Map map) {
        dumpMap(map, new EntryStringFormatter());
    }

    /**
     * Iterates through the entries of the given Map, printing formatter.format() of each Map.Entry to stderr
     * 
     * @param map
     * @param formatter
     */
    public static void dumpMap(Map map, EntryFormatter formatter) {
        LOG.error(formatMap(map, formatter));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param m
     * @return String composed of contents of the given Map, one entry per line, formatted by the given EntryFormatter
     */
    public static String formatMap(Map map, EntryFormatter formatter) {
        StringBuffer formatted = new StringBuffer("size= ");
        formatted.append(map.size());

        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            formatted.append(formatter.format((Map.Entry) i.next()));
            formatted.append("\n");
        }

        return formatted.toString();
    }


    /**
     * Recursively prints the contents of the given PropertyTree to stderr
     * 
     * @param tree
     */
    public static void dumpTree(PropertyTree tree) {
        LOG.error(formatTree(tree));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param tree
     * @return String composed of the contents of the given PropertyTree, one entry per line
     */
    public static String formatTree(PropertyTree tree) {
        StringBuffer formatted = new StringBuffer("total size= " + tree.size());

        formatted.append(formatLevel(tree, 0));

        return formatted.toString();
    }

    private static String formatLevel(PropertyTree tree, int level) {
        StringBuffer formatted = new StringBuffer();

        String prefix = buildIndent(level) + ": ";

        Map children = tree.getDirectChildren();
        for (Iterator i = children.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            formatted.append(prefix);

            String key = (String) e.getKey();
            PropertyTree subtree = (PropertyTree) e.getValue();
            String directValue = subtree.toString();
            if (directValue == null) {
                formatted.append(key);
            }
            else {
                formatted.append("(");
                formatted.append(key);
                formatted.append("=");
                formatted.append(directValue);
                formatted.append(")");
            }
            formatted.append("\n");

            formatted.append(formatLevel(subtree, level + 1));
        }

        return formatted.toString();
    }

    private static String buildIndent(int level) {
        int indentSize = level * 4;
        char[] indent = new char[indentSize];
        for (int i = 0; i < indentSize; ++i) {
            indent[i] = ' ';
        }

        return new String(indent);
    }


    public interface ItemFormatter {
        public String format(Object o);
    }

    public interface EntryFormatter {
        public String format(Map.Entry e);
    }


    private static class ItemStringFormatter implements ItemFormatter {
        public String format(Object o) {
            String result = "<null>";

            if (o != null) {
                result = o.toString();
            }

            return result;
        }
    }

    private static class EntryStringFormatter implements EntryFormatter {
        public String format(Map.Entry e) {
            String key = "<null>";
            String value = "<null>";

            if (e != null) {
                if (e.getKey() != null) {
                    key = e.getKey().toString();
                }
                if (e.getValue() != null) {
                    value = e.getValue().toString();
                }
            }

            return "(" + key + "," + value + ")";
        }
    }


    /**
     * Given a list of classnames of TestCase subclasses, assembles a TestSuite containing all tests within those classes, then runs
     * those tests and logs the results.
     * <p>
     * Created this method so that I could use OptimizeIt, which was asking for a main() method to run.
     * 
     * @param args
     */
    public static void main(String args[]) {
        TestSuite tests = new TestSuite();
        for (int i = 0; i < args.length; ++i) {
            String className = args[i];

            Class testClass = null;
            try {
                testClass = Class.forName(className);

            }
            catch (ClassNotFoundException e) {
                LOG.error("unable to load class '" + className + "'");
            }

            if (testClass != null) {
                tests.addTestSuite(testClass);
            }
        }


        if (tests.countTestCases() == 0) {
            LOG.error("no tests to run, exiting");
        }
        else {
            TestRunner.run(tests);
        }
    }

    /**
     * This sets a given system parameter and clears the method cache for retrieving the parameter.
     */
    public static void setSystemParameter(Class componentClass, String parameterName, String parameterText) {
        // check that we are in a test that is set to roll-back the transaction
        Exception ex = new Exception();
        ex.fillInStackTrace();
        Boolean willCommit = null;
        // loop over the stack trace
        for ( StackTraceElement ste : ex.getStackTrace() ) {
            try {
                Class clazz = Class.forName( ste.getClassName() );
                // for efficiency, only check classes that extend from KualiTestBase
                if ( KualiTestBase.class.isAssignableFrom(clazz) ) {
                    //System.err.println( "Checking Method: " + ste.toString() );
                    // check the class-level annotation to set the default for test methods in that class
                    ConfigureContext a = (ConfigureContext)clazz.getAnnotation(ConfigureContext.class);
                    if ( a != null ) {
                        willCommit = a.shouldCommitTransactions();
                    }
                    // now, check the method-level annotation
                    try {
                        Method m = clazz.getMethod(ste.getMethodName(), (Class[])null);
                        // if the method-level annotation is present, it overrides the class-level annotation
                        a = (ConfigureContext)m.getAnnotation(ConfigureContext.class);
                        if ( a != null ) {
                            willCommit = a.shouldCommitTransactions();
                        }
                    } catch ( NoSuchMethodException e ) {
                        // do nothing
                        
                    }
                }
            } catch ( Exception e ) {
                LOG.error( "Error checking stack trace element: " + ste.toString(), e );
            }
        }
        if ( willCommit == null || willCommit ) {
            throw new RuntimeException( "Attempt to set system parameter in unit test set to commit database changes.");
        }
        
        getParameterService().setParameterForTesting(componentClass, parameterName, parameterText);
    }

    /**
     * Converts an InputStream to a String using UTF-8 encoding.
     * 
     * @param inputStream - InputStream to convert.
     * @return String - converted from InputStream
     * @throws IOException
     */
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, "UTF-8");
    }

    /**
     * Converts a String to an InputStream using UTF-8 encoding.
     * 
     * @param string - string to convert
     * @return InputStream - converted from the given string
     * @throws IOException
     */
    public static InputStream convertStringToInputStream(String string) throws IOException {
        return IOUtils.toInputStream(string, "UTF-8");
    }

    /**
     * Returns the size of an InputStream by first converting it to an ByteArrayOutputStream and getting the size of it.
     */
    public static int getInputStreamSize(InputStream inputStream) throws IOException {
        ByteArrayOutputStream copiedOutputStream = null;
        IOUtils.copy(inputStream, copiedOutputStream);

        return copiedOutputStream.size();
    }
    
    /**
     * Returns an invoked instance for the serviceName passed. This uses SpringContext.getService but doesn't return the proxy. Should only
     * be used for unit testing purposes
     * @param serviceName service to return
     * @throws Exception
     */
    public static Object getUnproxiedService(String serviceName) throws Exception {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(SpringContext.getService(serviceName));
        Field privateAdvisedField = invocationHandler.getClass().getDeclaredField("advised");
        privateAdvisedField.setAccessible(true);
        ProxyFactory proxyFactory = (ProxyFactory) privateAdvisedField.get(invocationHandler);
        return proxyFactory.getTargetSource().getTarget();
    }
    
    /**
     * Writes an array to a file.  Useful for GL / LD poster file handling.
     * @param filePath file and path to write
     * @param inputTransactions data to write to pathname
     * @throws IllegalArgumentException if file already exists
     */
    public static void writeFile(String filePath, String[] inputTransactions) {
        File file = new File(filePath);
        
        if (file.exists()) {
            if(!file.delete()) {
                throw new RuntimeException("Attempt to overwrite " + file.getName() + " failed.");
            }
        }
        
        PrintStream outputFileStream = null;
        try {
            outputFileStream = new PrintStream(file);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        for (String line: inputTransactions){
            outputFileStream.printf("%s\n", line);
        }
        outputFileStream.close();
    }
    
    /**
     * Deletes all files from a directory except PLACEHOLDER_FILENAME.
     * @param path of the directory to empty
     */
    public static void deleteFilesInDirectory(String pathname) {
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (!name.equals(PLACEHOLDER_FILENAME));
            }
        };
        
        File directory = new File(pathname);
        File[] directoryListing = directory.listFiles(filenameFilter);
        
        if (directoryListing == null) {
            throw new IllegalArgumentException("Directory doesn't exist: " + pathname);
        } else {
            for (int i = 0; i < directoryListing.length; i++) {
                File file = directoryListing[i];
                if(!file.delete()) {
                    throw new RuntimeException("Delete of " + file.getName() + " failed.");
                }
            }
        }
    }
    
    /**
     * Returns a fiscal year for testing.  If the fiscalYearForTesting property is not null, it returns that;
     * otherwise, it runs the current fiscal year
     * @return a fiscal year suitable for testing purposes
     */
    public static Integer getFiscalYearForTesting() {
        if (fiscalYearForTesting == null) {
            fiscalYearForTesting = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        }
        return fiscalYearForTesting;
    }
}
