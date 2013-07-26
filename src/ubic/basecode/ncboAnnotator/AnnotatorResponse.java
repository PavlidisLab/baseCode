/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.ncboAnnotator;

import java.util.HashSet;

/**
 * This represents an Ontology terms returned by the AnnotatorClient
 */
public class AnnotatorResponse implements Comparable<AnnotatorResponse> {

    private Integer score = 0;
    private String value = "";
    private String valueUri = "";
    private boolean synonym = false;

    // search query used to find this results
    private String searchQuery = "";
    // ontology where the result was found
    private String ontologyUsed = "";

    // list of synonyms associated with this valueUri
    private HashSet<String> synonyms = new HashSet<String>();

    public AnnotatorResponse( Integer score, String value, String valueUri, String searchQuery, String ontologyUsed ) {
        super();
        this.score = score;
        this.value = value;
        this.valueUri = valueUri;
        this.searchQuery = searchQuery;
        this.ontologyUsed = ontologyUsed;
    }

    // this information is given as a long string with space and new lines, parse and add all synonyms
    public void addSynonyms( String line ) {

        String[] tokens = line.split( "\n" );

        for ( String token : tokens ) {

            token = token.trim();

            if ( !token.isEmpty() ) {
                this.synonyms.add( token );
            }
        }
    }

    // term are ordered given priority to DOID terms
    // 1- exact match from DOID
    // 2- synonym from DOID
    // 3- exact match from other resources
    // 4- synonym from other resources
    // 5 - other results
    // to change this rewrite own compareTo method to define required order
    @Override
    public int compareTo( AnnotatorResponse annotatorResponse ) {

        boolean exactMatch = isExactMatch();
        boolean exactMatchCompare = annotatorResponse.isExactMatch();
        boolean isSynonyme = isSynonym();
        boolean isSynonymeCompare = annotatorResponse.isSynonym();
        boolean diseaseOntology = isDiseaseUsed();
        boolean diseaseCompare = annotatorResponse.isDiseaseUsed();

        if ( diseaseOntology ) {

            if ( exactMatch ) {
                return -1;
            } else if ( isSynonyme ) {
                if ( !diseaseCompare ) {
                    return -1;
                } else if ( !exactMatchCompare ) {
                    return -1;
                }
            }
        } else if ( exactMatch || isSynonyme ) {
            if ( diseaseCompare ) {
                if ( exactMatchCompare || isSynonymeCompare ) {
                    return 1;
                }
            }
            if ( exactMatch ) {
                return -1;
            } else if ( isSynonyme && !exactMatchCompare ) {
                return -1;
            }
        }
        return 1;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        AnnotatorResponse other = ( AnnotatorResponse ) obj;
        if ( ontologyUsed == null ) {
            if ( other.ontologyUsed != null ) return false;
        } else if ( !ontologyUsed.equals( other.ontologyUsed ) ) return false;
        if ( score == null ) {
            if ( other.score != null ) return false;
        } else if ( !score.equals( other.score ) ) return false;
        if ( searchQuery == null ) {
            if ( other.searchQuery != null ) return false;
        } else if ( !searchQuery.equals( other.searchQuery ) ) return false;
        if ( valueUri == null ) {
            if ( other.valueUri != null ) return false;
        } else if ( !valueUri.equals( other.valueUri ) ) return false;
        return true;
    }

    public String getOntologyUsed() {
        return ontologyUsed;
    }

    public Integer getScore() {
        return score;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public HashSet<String> getSynonyms() {
        return synonyms;
    }

    public String getValue() {
        return value;
    }

    public String getValueUri() {
        return valueUri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( ontologyUsed == null ) ? 0 : ontologyUsed.hashCode() );
        result = prime * result + ( ( score == null ) ? 0 : score.hashCode() );
        result = prime * result + ( ( searchQuery == null ) ? 0 : searchQuery.hashCode() );
        result = prime * result + ( ( valueUri == null ) ? 0 : valueUri.hashCode() );
        return result;
    }

    public boolean isDiseaseUsed() {
        if ( ontologyUsed.equalsIgnoreCase( "DOID" ) ) {
            return true;
        }
        return false;
    }

    public boolean isExactMatch() {

        if ( this.value.equalsIgnoreCase( this.searchQuery ) ) {
            return true;
        }

        return false;
    }

    public boolean isSynonym() {
        return synonym;
    }

    public void setOntologyUsed( String ontologyUsed ) {
        this.ontologyUsed = ontologyUsed;
    }

    public void setScore( Integer score ) {
        this.score = score;
    }

    public void setSearchQuery( String searchQuery ) {
        this.searchQuery = searchQuery;
    }

    public void setSynonym( boolean synonym ) {
        this.synonym = synonym;
    }

    public void setSynonyms( HashSet<String> synonyms ) {
        this.synonyms = synonyms;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public void setValueUri( String valueUri ) {
        this.valueUri = valueUri;
    }

    @Override
    public String toString() {
        return "AnnotatorResponse [score=" + score + ", value=" + value + ", valueUri=" + valueUri + ", searchQuery="
                + searchQuery + "]";
    }

}
