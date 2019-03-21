/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.io.reader;

/**
 * @author pavlidis
 * 
 */
final class IndexScoreDyad implements Comparable<IndexScoreDyad> {

    int key;
    double value;

    /**
     * @param key
     * @param value
     */
    public IndexScoreDyad( int key, double value ) {
        this.key = key;
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( IndexScoreDyad o ) {
        return o.getKey() - key;
    }

    /**
     * @return Returns the key.
     */
    public int getKey() {
        return key;
    }

    /**
     * @return Returns the value.
     */
    public double getValue() {
        return value;
    }

}
