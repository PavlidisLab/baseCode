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
package ubic.basecode.ontology.ncbo;

/**
 * This represents an Ontology terms returned by the AnnotatorClient
 */
public class AnnotatorResponse implements Comparable<AnnotatorResponse> {

    private String valueUri = "";
    private String matchType = "";
    private String txtMatched = "";
    private Integer indexFrom = null;
    private Integer indexTo = null;
    private String ontologyUsed = "";
    private String searchQueryUsed = "";

    private boolean exactSynonym = false;
    private boolean exactMatch = false;

    public AnnotatorResponse( String valueUri, String matchType, String txtMatched, Integer indexFrom, Integer indexTo,
            String ontologyUsed, String searchQueryUsed ) {
        super();
        this.valueUri = valueUri;
        this.matchType = matchType;
        this.txtMatched = txtMatched;
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.ontologyUsed = ontologyUsed;
        this.searchQueryUsed = searchQueryUsed;

        if ( indexFrom.equals( 1 ) && indexTo.equals( searchQueryUsed.length() ) ) {

            if ( matchType.equals( "SYN" ) ) {
                exactSynonym = true;
            } else {
                exactMatch = true;
            }

        }

    }

    @Override
    public String toString() {
        return "AnnotatorResponse [valueUri=" + valueUri + ", matchType=" + matchType + ", txtMatched=" + txtMatched
                + ", indexFrom=" + indexFrom + ", indexTo=" + indexTo + ", ontologyUsed=" + ontologyUsed
                + ", searchQueryUsed=" + searchQueryUsed + ", exactSynonym=" + exactSynonym + ", exactMatch="
                + exactMatch + "]";
    }

    @Override
    public int compareTo( AnnotatorResponse annotatorResponse ) {

        boolean exactMatchFound = exactMatch;
        boolean exactMatchCompare = annotatorResponse.isExactMatch();
        boolean isSynonyme = exactSynonym;
        boolean isSynonymeCompare = annotatorResponse.isExactSynonym();
        boolean diseaseOntology = isDiseaseUsed();
        boolean diseaseCompare = annotatorResponse.isDiseaseUsed();

        if ( diseaseOntology ) {

            if ( exactMatchFound ) {
                return -1;
            } else if ( isSynonyme ) {
                if ( !diseaseCompare ) {
                    return -1;
                } else if ( !exactMatchCompare ) {
                    return -1;
                }
            }
        } else if ( exactMatchFound || isSynonyme ) {
            if ( diseaseCompare ) {
                if ( exactMatchCompare || isSynonymeCompare ) {
                    return 1;
                }
            }
            if ( exactMatchFound ) {
                return -1;
            } else if ( isSynonyme && !exactMatchCompare ) {
                return -1;
            }
        }
        return 1;
    }

    public boolean isDiseaseUsed() {
        if ( ontologyUsed.equalsIgnoreCase( AnnotatorClient.DOID_ONTOLOGY ) ) {
            return true;
        }
        return false;
    }

    public boolean isHpUsed() {
        if ( ontologyUsed.equalsIgnoreCase( AnnotatorClient.HP_ONTOLOGY ) ) {
            return true;
        }
        return false;
    }

    public String getValueUri() {
        return valueUri;
    }

    public void setValueUri( String valueUri ) {
        this.valueUri = valueUri;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType( String matchType ) {
        this.matchType = matchType;
    }

    public String getTxtMatched() {
        return txtMatched;
    }

    public void setTxtMatched( String txtMatched ) {
        this.txtMatched = txtMatched;
    }

    public Integer getIndexFrom() {
        return indexFrom;
    }

    public void setIndexFrom( Integer indexFrom ) {
        this.indexFrom = indexFrom;
    }

    public Integer getIndexTo() {
        return indexTo;
    }

    public void setIndexTo( Integer indexTo ) {
        this.indexTo = indexTo;
    }

    public String getOntologyUsed() {
        return ontologyUsed;
    }

    public void setOntologyUsed( String ontologyUsed ) {
        this.ontologyUsed = ontologyUsed;
    }

    public String getSearchQueryUsed() {
        return searchQueryUsed;
    }

    public void setSearchQueryUsed( String searchQueryUsed ) {
        this.searchQueryUsed = searchQueryUsed;
    }

    public boolean isExactSynonym() {
        return exactSynonym;
    }

    public void setExactSynonym( boolean exactSynonym ) {
        this.exactSynonym = exactSynonym;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch( boolean exactMatch ) {
        this.exactMatch = exactMatch;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( indexFrom == null ) ? 0 : indexFrom.hashCode() );
        result = prime * result + ( ( indexTo == null ) ? 0 : indexTo.hashCode() );
        result = prime * result + ( ( matchType == null ) ? 0 : matchType.hashCode() );
        result = prime * result + ( ( ontologyUsed == null ) ? 0 : ontologyUsed.hashCode() );
        result = prime * result + ( ( searchQueryUsed == null ) ? 0 : searchQueryUsed.hashCode() );
        result = prime * result + ( ( txtMatched == null ) ? 0 : txtMatched.hashCode() );
        result = prime * result + ( ( valueUri == null ) ? 0 : valueUri.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        AnnotatorResponse other = ( AnnotatorResponse ) obj;
        if ( indexFrom == null ) {
            if ( other.indexFrom != null ) return false;
        } else if ( !indexFrom.equals( other.indexFrom ) ) return false;
        if ( indexTo == null ) {
            if ( other.indexTo != null ) return false;
        } else if ( !indexTo.equals( other.indexTo ) ) return false;
        if ( matchType == null ) {
            if ( other.matchType != null ) return false;
        } else if ( !matchType.equals( other.matchType ) ) return false;
        if ( ontologyUsed == null ) {
            if ( other.ontologyUsed != null ) return false;
        } else if ( !ontologyUsed.equals( other.ontologyUsed ) ) return false;
        if ( searchQueryUsed == null ) {
            if ( other.searchQueryUsed != null ) return false;
        } else if ( !searchQueryUsed.equals( other.searchQueryUsed ) ) return false;
        if ( txtMatched == null ) {
            if ( other.txtMatched != null ) return false;
        } else if ( !txtMatched.equals( other.txtMatched ) ) return false;
        if ( valueUri == null ) {
            if ( other.valueUri != null ) return false;
        } else if ( !valueUri.equals( other.valueUri ) ) return false;
        return true;
    }

    // method used by Phenocarta
    public String findCondition( boolean modifiedSearch ) {

        String condition = null;

        if ( isDiseaseUsed() ) {

            if ( isExactMatch() ) {
                condition = "A) Found Exact With Disease Annotator";

            } else if ( isExactSynonym() ) {
                condition = "B) Found Synonym With Disease Annotator Synonym";
            }
        } else if ( isHpUsed() ) {

            if ( isExactMatch() ) {
                condition = "C) Found Exact With HP Annotator";
            } else if ( isExactSynonym() ) {
                condition = "D) Found Synonym With HP Annotator Synonym";
            }
        }

        if ( condition != null && modifiedSearch ) {
            condition = condition + " modified search ";
        }

        return condition;
    }

}
