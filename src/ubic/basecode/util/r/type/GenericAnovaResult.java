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

package ubic.basecode.util.r.type;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

/**
 * A generic parse of an R ANOVA table, including interactions.
 * 
 * @author paul
 * @version $Id$
 */
public class GenericAnovaResult extends AnovaResult implements Serializable {

    Map<String, AnovaEffect> mainEffects = new HashMap<String, AnovaEffect>();

    Map<InteractionFactor, AnovaEffect> interactionEffects = new HashMap<InteractionFactor, AnovaEffect>();

    /**
     * Represents a set of factors in an interaction term.
     */
    class InteractionFactor {

        // Treeset - keep them sorted to be consistent across instances.
        private Set<String> factorNames = new TreeSet<String>();

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
            return StringUtils.join( factorNames, "_x_x_" ).hashCode();
        }

        public InteractionFactor( String... factorNames ) {
            for ( String f : factorNames ) {
                this.factorNames.add( f );
            }
        }
    }

    /**
     * @return names of main effects factors like 'f', 'g'.
     */
    public Collection<String> getMainEffectFactorNames() {
        return mainEffects.keySet();
    }

    public boolean hasInteractions() {
        return !interactionEffects.isEmpty();
    }

    public Double getMainEffectP( String factorName ) {
        if ( mainEffects.get( factorName ) == null ) {
            return Double.NaN;
        }
        return mainEffects.get( factorName ).getPValue();
    }

    /**
     * @param factorsName e.g. f,g
     * @return
     */
    public Double getInteractionEffectP( String... factorNames ) {
        InteractionFactor interactionFactor = new InteractionFactor( factorNames );
        if ( !interactionEffects.containsKey( interactionFactor ) ) {
            return Double.NaN;
        }
        return interactionEffects.get( interactionFactor ).getPValue();
    }

    /**
     * @param rAnovaTable from R call to 'anova(...)'
     */
    public GenericAnovaResult( REXP rAnovaTable ) {

        try {

            String[] names = rAnovaTable.getAttribute( "row.names" ).asStrings();
            double[] pvs = rAnovaTable.asList().at( "Pr(>F)" ).asDoubles();
            int[] dfs = rAnovaTable.asList().at( "Df" ).asIntegers();
            double[] fs = rAnovaTable.asList().at( "F value" ).asDoubles();

            double[] ssq = rAnovaTable.asList().at( "Sum Sq" ).asDoubles();
            double[] meansq = rAnovaTable.asList().at( "Mean Sq" ).asDoubles();

            for ( int i = 0; i < pvs.length; i++ ) {
                String termLabel = names[i];
                boolean isInteraction = termLabel.contains( ":" );

                AnovaEffect ae = new AnovaEffect( termLabel, pvs[i], fs[i], dfs[i], ssq[i], meansq[i], isInteraction );

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

    @Override
    public String toString() {
        // TODO
        return super.toString();
    }

}