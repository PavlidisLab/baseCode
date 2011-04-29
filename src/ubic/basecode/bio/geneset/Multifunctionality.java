/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
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
package ubic.basecode.bio.geneset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ubic.basecode.math.Rank;

/**
 * Implementation of multifunctionality as described in Gillis and Pavlidis (2011) PLoS ONE 6:2:e17258
 * 
 * @author paul
 * @version $Id$
 */
public class Multifunctionality {

    private Map<String, Double> multifunctionality = new HashMap<String, Double>();
    Map<String, Integer> goGroupSizes = new HashMap<String, Integer>();

    Map<String, Integer> numGoTerms = new HashMap<String, Integer>();

    // note this is from high to low
    Map<String, Double> multifunctionalityRank = new HashMap<String, Double>();

    /**
     * Construct Multifuncationity information based on the state of the GO annotations -- this accounts only for the
     * 'active' (used) probes in the annotations.
     * 
     * @param go
     */
    public Multifunctionality( GeneAnnotations go ) {

        Collection<String> allGenes = go.getGenes();
        int numGenes = allGenes.size();

        for ( String goset : go.getGeneSets() ) {
            Collection<String> geneSetGenes = go.getGeneSetGenes( goset );
            goGroupSizes.put( goset, geneSetGenes.size() );
        }

        for ( String gene : go.getGenes() ) {
            double mf = 0.0;
            Collection<String> sets = go.getGeneGeneSets( gene );
            this.numGoTerms.put( gene, sets.size() );
            for ( String goset : sets ) {
                int inGroup = goGroupSizes.get( goset );
                int outGroup = numGenes - inGroup;
                assert outGroup > 0;
                assert inGroup > 0;
                mf += 1.0 / ( inGroup * outGroup );
            }
            this.multifunctionality.put( gene, mf );
        }

        Map<String, Integer> ranked = Rank.rankTransform( this.multifunctionality );
        for ( String string : ranked.keySet() ) {
            this.multifunctionalityRank.put( string, ( numGenes - ranked.get( string ) ) / ( double ) numGenes );
        }

    }

    public int getNumGoTerms( String gene ) {
        if ( !this.numGoTerms.containsKey( gene ) ) {
            throw new IllegalArgumentException( "Gene: " + gene + " not found" );
        }
        return this.numGoTerms.get( gene );
    }

    /**
     * @param gene
     * @return relative rank of the gene in multifunctionality where 0 is the highest multifunctionality.
     */
    public double getMultifunctionalityRank( String gene ) {
        if ( !this.multifunctionalityRank.containsKey( gene ) ) {
            throw new IllegalArgumentException( "Gene: " + gene + " not found" );
        }
        return this.multifunctionalityRank.get( gene );
    }

    /**
     * @param gene
     * @return multifunctionality score
     */
    public double getMultifunctionalityScore( String gene ) {
        if ( !this.multifunctionality.containsKey( gene ) ) {
            throw new IllegalArgumentException( "Gene: " + gene + " not found" );
        }
        return this.multifunctionality.get( gene );
    }
}
