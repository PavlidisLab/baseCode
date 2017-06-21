/*
 * The baseCode project
 *
 * Copyright (c) 2010 University of British Columbia
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

package ubic.basecode.math.linearmodels;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

import ubic.basecode.util.r.type.AnovaEffect;
import ubic.basecode.util.r.type.AnovaResult;

/**
 * A generic representation of an R-style ANOVA table, including interactions.
 *
 * @author paul
 */
public class GenericAnovaResult extends AnovaResult implements Serializable {

    /**
     * Represents a set of factors in an interaction term.
     */
    private static class InteractionFactor {

        // used only in hashcode; not a great idea
        private static final String SEPARATOR = "_x_x_";

        // Treeset - keep them sorted to be consistent across instances.
        private Set<String> factorNames = new TreeSet<>();

        public InteractionFactor( String... factorNames ) {
            for ( String f : factorNames ) {
                this.factorNames.add( f );
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( Object obj ) {

            if ( !( obj instanceof InteractionFactor ) ) return false;

            return ( ( InteractionFactor ) obj ).factorNames.size() == this.factorNames.size()
                    && ( ( InteractionFactor ) obj ).factorNames.containsAll( this.factorNames );

        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return StringUtils.join( factorNames, SEPARATOR ).hashCode();
        }
    }

    private static final long serialVersionUID = 1L;

    private Map<InteractionFactor, AnovaEffect> interactionEffects = new HashMap<>();

    private Map<String, AnovaEffect> mainEffects = new LinkedHashMap<>();

    private AnovaEffect residual;

    /**
     * @param effects
     */
    public GenericAnovaResult( Collection<AnovaEffect> effects ) {
        for ( AnovaEffect ae : effects ) {
            String termLabel = ae.getEffectName();
            boolean isInteraction = ae.isInteraction();

            if ( isInteraction ) { // kind of lame way to detect interaction rows.
                interactionEffects.put( new InteractionFactor( StringUtils.split( termLabel, ":" ) ), ae );
            } else if ( termLabel.equals( "Residual" ) ) {
                this.residualDf = ae.getDegreesOfFreedom();
                this.residual = ae;
            } else {
                mainEffects.put( termLabel, ae );
            }
        }
    }

    /**
     * @param rAnovaTable from R call to 'anova(...)'
     */
    public GenericAnovaResult( REXP rAnovaTable ) {

        try {

            String[] names = rAnovaTable.getAttribute( "row.names" ).asStrings();
            double[] pvs = rAnovaTable.asList().at( "Pr(>F)" ).asDoubles();
            double[] dfs = rAnovaTable.asList().at( "Df" ).asDoubles();
            double[] fs = rAnovaTable.asList().at( "F value" ).asDoubles();

            double[] ssq = rAnovaTable.asList().at( "Sum Sq" ).asDoubles();

            for ( int i = 0; i < pvs.length; i++ ) {
                String termLabel = names[i];
                boolean isInteraction = termLabel.contains( ":" );

                AnovaEffect ae = new AnovaEffect( termLabel, pvs[i], fs[i], dfs[i], ssq[i], isInteraction );

                if ( isInteraction ) { // kind of lame way to detect interaction rows.
                    interactionEffects.put( new InteractionFactor( StringUtils.split( termLabel, ":" ) ), ae );
                } else {
                    mainEffects.put( termLabel, ae );
                }
            }

        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    public Double getInteractionEffectF() {
        if ( interactionEffects.size() > 1 ) {
            throw new IllegalArgumentException( "Must specify which interaction" );
        }
        if ( interactionEffects.isEmpty() ) {
            return null;
        }
        return interactionEffects.values().iterator().next().getFStatistic();
    }

    public Double getInteractionEffectP() {
        if ( interactionEffects.size() > 1 ) {
            throw new IllegalArgumentException( "Must specify which interaction" );
        }
        if ( interactionEffects.isEmpty() ) {
            return null;
        }
        return interactionEffects.values().iterator().next().getPValue();
    }

    /**
     * @param factorsName e.g. f,g
     * @return
     */
    public Double getInteractionEffectP( String... factorNames ) {
        InteractionFactor interactionFactor = new InteractionFactor( factorNames );
        if ( interactionEffects.isEmpty() ) {
            return null;
        }
        if ( !interactionEffects.containsKey( interactionFactor ) ) {
            return Double.NaN;
        }
        return interactionEffects.get( interactionFactor ).getPValue();
    }

    public Double getMainEffectDof( String factorName ) {
        if ( mainEffects.get( factorName ) == null ) {
            return null;
        }
        return mainEffects.get( factorName ).getDegreesOfFreedom();
    }

    public Double getMainEffectF( String factorName ) {
        if ( mainEffects.get( factorName ) == null ) {
            return Double.NaN;
        }
        return mainEffects.get( factorName ).getFStatistic();
    }

    /**
     * @return names of main effects factors like 'f', 'g'.
     */
    public Collection<String> getMainEffectFactorNames() {
        return mainEffects.keySet();
    }

    public Double getMainEffectP( String factorName ) {
        if ( mainEffects.get( factorName ) == null ) {
            return Double.NaN;
        }
        return mainEffects.get( factorName ).getPValue();
    }

    public boolean hasInteractions() {
        return !interactionEffects.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append( "ANOVA table " + this.getKey() + " \n" );

        buf.append( StringUtils.leftPad( "\t", 10 ) + "Df\tSSq\tMSq\tF\tP\n" );

        for ( String me : this.getMainEffectFactorNames() ) {
            if ( me.equals( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME ) ) {
                continue;
            }
            AnovaEffect a = mainEffects.get( me );
            buf.append( a + "\n" );
        }

        if ( hasInteractions() ) {
            for ( InteractionFactor ifa : interactionEffects.keySet() ) {
                AnovaEffect a = this.interactionEffects.get( ifa );
                buf.append( a + "\n" );
            }
        }

        buf.append( residual + "\n" );

        return buf.toString();
    }
}