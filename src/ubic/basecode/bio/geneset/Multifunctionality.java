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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.list.DoubleArrayList;

import ubic.basecode.math.Distance;
import ubic.basecode.math.Rank;

/**
 * Implementation of multifunctionality computations as described in Gillis and Pavlidis (2011) PLoS ONE 6:2:e17258.
 * This is designed with ErmineJ in mind.
 * 
 * @author paul
 * @version $Id$
 */
public class Multifunctionality {

    private static Log log = LogFactory.getLog( Multifunctionality.class );

    private Map<String, Double> multifunctionality = new HashMap<String, Double>();
    Map<String, Integer> goGroupSizes = new HashMap<String, Integer>();

    Map<String, Integer> numGoTerms = new HashMap<String, Integer>();

    Map<String, Double> goTermMultifunctionality = new HashMap<String, Double>();

    Map<String, Double> goTermMultifunctionalityRank = new HashMap<String, Double>();

    Map<String, Double> multifunctionalityRank = new HashMap<String, Double>();
    private GeneAnnotations go;

    private Collection<String> genesWithGoTerms;

    /**
     * Construct Multifunctionality information based on the state of the GO annotations -- this accounts only for the
     * 'active' (used) probes in the annotations. Genes with no GO terms are completely ignored.
     * 
     * @param go
     */
    public Multifunctionality( GeneAnnotations go ) {
        StopWatch timer = new StopWatch();

        timer.start();
        this.go = go;

        genesWithGoTerms = new HashSet<String>();
        for ( String goset : go.getGeneSets() ) {
            Collection<String> geneSetGenes = go.getGeneSetGenes( goset );
            if ( geneSetGenes.isEmpty() ) continue;
            genesWithGoTerms.addAll( geneSetGenes );
            goGroupSizes.put( goset, geneSetGenes.size() );
        }

        int numGenes = genesWithGoTerms.size();

        for ( String gene : go.getGenes() ) {
            if ( !genesWithGoTerms.contains( gene ) ) continue;

            double mf = 0.0;
            Collection<String> sets = go.getGeneGeneSets( gene );
            this.numGoTerms.put( gene, sets.size() ); // genes with no go terms are ignored.
            for ( String goset : sets ) {
                if ( !goGroupSizes.containsKey( goset ) ) {
                    log.debug( "No size recorded for " + goset );
                    continue;
                }
                int inGroup = goGroupSizes.get( goset );
                int outGroup = numGenes - inGroup;
                assert outGroup > 0;
                assert inGroup > 0;
                mf += 1.0 / ( inGroup * outGroup );
            }
            this.multifunctionality.put( gene, mf );
        }

        Map<String, Integer> rawGeneMultifunctionalityRanks = Rank.rankTransform( this.multifunctionality, true );
        for ( String gene : rawGeneMultifunctionalityRanks.keySet() ) {
            // 1-base the rank before calculating ratio
            double geneMultifunctionalityRankRatio = ( rawGeneMultifunctionalityRanks.get( gene ) + 1 )
                    / ( double ) numGenes;
            this.multifunctionalityRank.put( gene, Math.max( 0.0, 1.0 - geneMultifunctionalityRankRatio ) );
        }

        computeGoTermMultifunctionalityRanks( rawGeneMultifunctionalityRanks );

        if ( timer.getTime() > 1000 ) {
            log.info( "Multifunctionality computation: " + timer.getTime() + "ms" );
        }
    }

    /**
     * @param rankedGoTerms, with the "best" GO term first.
     * @return the rank correlation of the given list with the ranks of the GO term multifunctionality of the terms. A
     *         positive correlation means the given list of terms is "multifunctionality-biased".
     */
    public double correlationWithGoTermMultifunctionality( List<String> rankedGoTerms ) {
        DoubleArrayList rawVals = new DoubleArrayList();
        for ( String goTerm : rankedGoTerms ) {
            double mf = this.getGOTermMultifunctionality( goTerm );
            rawVals.add( mf );
        }
        return -Distance.spearmanRankCorrelation( rawVals );
    }

    /**
     * @param geneScores
     * @return studentized residuals of the gene scores
     */
    public Map<String, Double> regressGeneMultifunctionality( Map<String, Double> geneScores ) {

        /*
         * regress
         */

        /*
         * Get residuals
         */

        return null;
    }

    /**
     * @param rankedGenes, with the "best" gene first.
     * @return the rank correlation of the given list with the ranks of the multifunctionality of the genes. A positive
     *         correlation means the given list is "multifunctionality-biased". Genes lacking GO terms are ignored.
     */
    public double correlationWithGeneMultifunctionality( List<String> rankedGenes ) {

        DoubleArrayList rawVals = new DoubleArrayList();
        for ( String gene : rankedGenes ) {
            if ( !this.multifunctionality.containsKey( gene ) ) continue;
            double mf = this.getMultifunctionalityScore( gene );
            rawVals.add( mf );
        }

        return -Distance.spearmanRankCorrelation( rawVals );
    }

    /**
     * @param goId
     * @return the computed multifunctionality score for the GO term. This is the area under the ROC curve for the genes
     *         in the group, in the ranking of all genes for multifunctionality. Higher values indicate higher
     *         multifunctionality
     */
    public double getGOTermMultifunctionality( String goId ) {
        if ( !this.goTermMultifunctionality.containsKey( goId ) ) {
            throw new IllegalArgumentException( "GO term: " + goId + " not found" );
        }
        return this.goTermMultifunctionality.get( goId );
    }

    /**
     * @param goId
     * @return the relative rank of the GO group in multifunctionality, where 1 is the highest multifunctionality, 0 is
     *         lowest
     */
    public double getGOTermMultifunctionalityRank( String goId ) {
        if ( !this.goTermMultifunctionalityRank.containsKey( goId ) ) {
            throw new IllegalArgumentException( "GO term: " + goId + " not found" );
        }
        return this.goTermMultifunctionalityRank.get( goId );
    }

    /**
     * @param gene
     * @return relative rank of the gene in multifunctionality where 1 is the highest multifunctionality, 0 is lowest
     */
    public double getMultifunctionalityRank( String gene ) {
        if ( !this.multifunctionalityRank.containsKey( gene ) ) {
            // throw new IllegalArgumentException( "Gene: " + gene + " not found" );
            return 0.0;
        }
        return this.multifunctionalityRank.get( gene );
    }

    /**
     * @param gene
     * @return multifunctionality score. Note that this score by itself is not all that useful; use the rank instead.
     *         Higher values indicate higher multifunctionality
     */
    public double getMultifunctionalityScore( String gene ) {
        if ( !this.multifunctionality.containsKey( gene ) ) {
            // throw new IllegalArgumentException( "Gene: " + gene + " not found" );
            return 0.0;
        }
        return this.multifunctionality.get( gene );
    }

    /**
     * @param gene
     * @return number of GO terms for the given gene.
     */
    public int getNumGoTerms( String gene ) {
        if ( !this.numGoTerms.containsKey( gene ) ) {
            throw new IllegalArgumentException( "Gene: " + gene + " not found" );
        }
        return this.numGoTerms.get( gene );
    }

    /**
     * Implementation of algorithm for computing AUC, described in Section 1 of the supplement to Gillis and Pavlidis;
     * see {@link http://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U}.
     * 
     * @param rawGeneMultifunctionalityRanks in descending order
     */
    private void computeGoTermMultifunctionalityRanks( Map<String, Integer> rawGeneMultifunctionalityRanks ) {
        int numGenes = genesWithGoTerms.size();
        int numGoGroups = go.getGeneSets().size();
        /*
         * For each go term, compute it's AUC w.r.t. the multifunctionality ranking.. We work with the
         * multifunctionality ranks, rawGeneMultifunctionalityRanks
         */
        for ( String goset : go.getGeneSets() ) {

            if ( !goGroupSizes.containsKey( goset ) ) {
                log.info( "No size recorded for: " + goset );
                continue;
            }

            int inGroup = goGroupSizes.get( goset );
            int outGroup = numGenes - inGroup;

            double t1 = inGroup * ( inGroup + 1.0 ) / 2.0;
            double t2 = inGroup * outGroup;

            /*
             * Extract the ranks of the genes in the goset, where highest ranking is the best.
             */
            double sumOfRanks = 0.0;
            for ( String gene : go.getGeneSetGenes( goset ) ) {
                int rank = rawGeneMultifunctionalityRanks.get( gene ) + 1; // +1 cuz ranks are zero-based.
                sumOfRanks += rank;
            }

            double t3 = sumOfRanks - t1;

            double auc = Math.max( 0.0, 1.0 - t3 / t2 );

            assert auc >= 0.0 && auc <= 1.0;
            goTermMultifunctionality.put( goset, auc );
        }

        // convert to relative ranks, where 1.0 is the most multifunctional
        Map<String, Integer> rankedGOMf = Rank.rankTransform( this.goTermMultifunctionality, true );
        for ( String goTerm : rankedGOMf.keySet() ) {
            double rankRatio = ( rankedGOMf.get( goTerm ) + 1 ) / ( double ) numGoGroups;
            this.goTermMultifunctionalityRank.put( goTerm, Math.max( 0.0, 1 - rankRatio ) );
        }
    }
}
