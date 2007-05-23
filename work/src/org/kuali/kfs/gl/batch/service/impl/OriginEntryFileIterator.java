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
package org.kuali.module.gl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.kuali.module.gl.bo.OriginEntry;

/**
 * This class lazy loads the origin entries in a flat file.  This implementation uses a
 * limited amount of memory because it does not pre-load all of the origin entries at once.
 * (Assuming that the Java garbage collector is working well).  However, if the code that
 * uses this iterator stores the contents of this iterator in a big list somewhere, then
 * a lot of memory may be consumed, depending on the size of the file.
 */
public class OriginEntryFileIterator implements Iterator<OriginEntry>{
    private static Logger LOG = Logger.getLogger(OriginEntryFileIterator.class);

    protected OriginEntry nextEntry;
    protected BufferedReader reader;
    protected int lineNumber;
    
    public OriginEntryFileIterator(BufferedReader reader) {
        if (reader == null) {
            LOG.error("reader is null in the OriginEntryFileIterator!");
            throw new IllegalArgumentException("reader is null!");
        }
        this.reader = reader;
        nextEntry = null;
        lineNumber = 0;
    }
    
    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (nextEntry == null) {
            fetchNextEntry();
            return nextEntry != null;
        }
        else {
            // we have a next entry loaded
            return true;
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    public OriginEntry next() {
        if (nextEntry != null) {
            // an entry may have been fetched by hasNext()
            OriginEntry temp = nextEntry;
            nextEntry = null;
            return temp;
        }
        else {
            // maybe next() is called repeatedly w/o calling hasNext.  This is a bad idea, but the
            // interface allows it
            fetchNextEntry();
            if (nextEntry == null) {
                throw new NoSuchElementException();
            }
            
            // clear out the nextEntry to signal that no record has been loaded
            OriginEntry temp = nextEntry;
            nextEntry = null;
            return temp;
        }
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove entry from collection");
    }
    
    protected void fetchNextEntry() {
        try {
            String line = reader.readLine();
            if (line == null) {
                nextEntry = null;
                reader.close();
            }
            else {
                nextEntry = new OriginEntry();
                nextEntry.setFromTextFile(line, lineNumber);
                lineNumber++;
                // if there's an LoadException, then we'll just let it propagate up
            }
        }
        catch (IOException e) {
            LOG.error("error in the CorrectionDocumentServiceImpl iterator", e);
            throw new RuntimeException("error retrieving origin entries");
        }
    }

    /**
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (reader != null) {
            reader.close();
        }
    }
}
