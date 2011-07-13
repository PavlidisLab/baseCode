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
package ubic.basecode.bio.geneset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.util.CancellationException;
import ubic.basecode.util.FileTools;
import ubic.basecode.util.StatusViewer;
import ubic.basecode.util.StringUtil;

/**
 * Reads tab-delimited file to create maps of probes to classes, classes to probes, probes to genes, genes to probes.
 * <p>
 * Maintains the following important data structures, all derived from the input file:
 * <ol>
 * <li>probe-&gt;Classes -- each value is a Set of the Classes that a probe belongs to.
 * <li>Classes-&gt;probe -- each value is a Set of the probes that belong to a class
 * <li>probe-&gt;gene -- each value is the gene name corresponding to the probe.
 * <li>gene-&gt;list of probes -- each value is a list of probes corresponding to a gene
 * <li>probe-&gt;description -- each value is a text description of the probe (actually...of the gene)
 * </ol>
 * 
 * @author Paul Pavlidis
 * @author Shamhil Merchant
 * @author Homin Lee
 * @version $Id$
 */
public class GeneAnnotations {

    /**
     * 
     */
    public static final int AFFYCSV = 1;

    /**
     * 
     */
    public static final int AGILENT = 2;

    /**
     * 
     */
    public static final int DEFAULT = 0;

    /**
     * Whether to filter out probes that hit more than one gene
     */
    public static final boolean DEFAULT_FILTER_NONSPECIFIC = false;

    /**
     * The minimum size of a 'set' of genes.
     */
    private static final int ABSOLUTE_MINIMUM_GENESET_SIZE = 2;

    private static final String AFFY_FIELD_SEPARATION_REGEX = "[,\t]";

    private static Log log = LogFactory.getLog( GeneAnnotations.class.getName() );

    /**
     * String used to indicate a gene has no description associated with it.
     */
    private static final String NO_DESCRIPTION = "[No description]";
    /**
     * The maximum size of gene sets ever considered.
     */
    private static final int PRACTICAL_MAXIMUM_GENESET_SIZE = 5000;
    private Collection<String> activeGeneSetCache;
    private Collection<String> activeProbes = null;
    private Map<String, Collection<String>> classToActiveProbeCache = new HashMap<String, Collection<String>>();
    private boolean filterNonSpecific = DEFAULT_FILTER_NONSPECIFIC;
    private Map<String, Collection<String>> geneSetActiveGenesCache = new HashMap<String, Collection<String>>();
    private Map<String, Collection<String>> geneSetActiveProbesCache = new HashMap<String, Collection<String>>();
    private Map<String, Collection<String>> geneSetToGeneMap; // stores Classes->genes map
    private Map<String, Collection<String>> geneSetToProbeMap; // stores Classes->probes map

    private Map<String, Collection<String>> geneSetToRedundantMap;

    private Collection<String> genesForActiveProbesCache;
    private Map<String, Collection<String>> geneToActiveProbesCache = new HashMap<String, Collection<String>>();
    private Map<String, Collection<String>> geneToGeneSetMap;
    private Map<String, Collection<String>> geneToProbeMap;

    private Multifunctionality multifunctionality;

    private StatusViewer messenger;

    private Map<String, Collection<String>> oldGeneSets;

    private Pattern pipePattern = Pattern.compile( "\\s*[\\s\\|,]\\s*" );

    private Map<String, String> probeToDescription;

    private Map<String, String> probeToGeneName;

    private Map<String, Collection<String>> probeToGeneSetMap;

    private List<String> selectedProbes;

    private List<String> selectedSets;

    private List<String> sortedGeneSets;

    private int tick = 0;

    public GeneAnnotations() {
        this.setUpdataStructures();
    }

    /**
     * @param goNames This is for creating GeneAnnotations by pruning a copy.
     * @param geneData GeneAnnotations copy to prune from
     * @param activeProbes Set only include these probes
     */
    public GeneAnnotations( GeneAnnotations geneData, Set<String> activeProbes ) {

        if ( activeProbes == null || geneData == null )
            throw new IllegalArgumentException( "GeneAnnotations can't be constructed from null data" );

        this.activeProbes = activeProbes;
        activeProbesDirty();

        // make a deep copy of the probeToGeneSetMap
        this.probeToGeneSetMap = new LinkedHashMap<String, Collection<String>>();
        for ( Object element : geneData.probeToGeneSetMap.keySet() ) {
            String key = ( String ) element;
            this.probeToGeneSetMap.put( key, new HashSet<String>( geneData.probeToGeneSetMap.get( key ) ) );
        }

        // make a deep copy of the classToProbeMap, which is a map of sets. Shallow copy is BAD.
        this.geneSetToProbeMap = new LinkedHashMap<String, Collection<String>>();
        for ( Object element : geneData.geneSetToProbeMap.keySet() ) {
            String key = ( String ) element;
            this.geneSetToProbeMap.put( key, new HashSet<String>( geneData.geneSetToProbeMap.get( key ) ) );
        }

        // make a deep copy of the old gene sets.
        this.oldGeneSets = new LinkedHashMap<String, Collection<String>>();
        for ( Object element : geneData.oldGeneSets.keySet() ) {
            String key = ( String ) element;
            this.oldGeneSets.put( key, new HashSet<String>( geneData.oldGeneSets.get( key ) ) );
        }

        probeToGeneName = new HashMap<String, String>( geneData.probeToGeneName ); // shallow copy, okay
        probeToDescription = new HashMap<String, String>( geneData.probeToDescription ); // shallow copy, okay
        geneToProbeMap = new HashMap<String, Collection<String>>( geneData.geneToProbeMap ); // shallow copy, okay?
        geneToGeneSetMap = new HashMap<String, Collection<String>>( geneData.geneToGeneSetMap ); // shallow copy,
        // okay?
        geneSetToRedundantMap = new HashMap<String, Collection<String>>( geneData.geneSetToRedundantMap );
        filterNonSpecific = geneData.getFilterNonSpecific();

        List<String> allProbes = new Vector<String>( probeToGeneName.keySet() );
        for ( String probe : allProbes ) {
            if ( !activeProbes.contains( probe ) ) { // remove probes not in data set.
                removeProbeFromMaps( probe );
            }
        }
        setUp( null ); // creates the classToGene map.
    }

    public GeneAnnotations( InputStream stream, Set<String> activeGenes, StatusViewer messenger, GONames goNames )
            throws IOException {
        this( stream, activeGenes, messenger, goNames, DEFAULT_FILTER_NONSPECIFIC );

    }

    /**
     * @param stream
     * @param activeGenes
     * @param messenger
     * @param goNames
     * @throws IOException
     */
    public GeneAnnotations( InputStream stream, Set<String> activeGenes, StatusViewer messenger, GONames goNames,
            boolean filterNonSpecific ) throws IOException {
        this.filterNonSpecific = filterNonSpecific;
        this.messenger = messenger;
        setUpdataStructures();
        this.read( stream, activeGenes );
        this.activeProbes = null; // using all.
        setUp( goNames );
    }

    /**
     * Constructor designed for use when a file is not the immediate input of the data.
     * 
     * @param probes A List of probes
     * @param geneSymbols A List of gene symbols (e.g., ACTB), corresponding to the probes (in the same order)
     * @param geneNames A List of gene names (e.g., "Actin"), corresponding to the probes (in the same order). This can
     *        be null.
     * @param goTerms A List of Collections of Strings corresponding to the GO terms for each probe.
     * @throws IllegaArgumentException if any of the required arguments are null, don't have sizes that match, etc.
     */
    public GeneAnnotations( List<String> probes, List<String> geneSymbols, List<String> geneNames,
            List<Collection<String>> goTerms ) {
        checkValidData( probes, geneSymbols, geneNames, goTerms );
        assert probes != null;
        setUpdataStructures();

        this.activeProbes = probes;
        activeProbesDirty();

        Collection<String> probeIds = new ArrayList<String>();
        for ( int i = 0; i < probes.size(); i++ ) {
            String probe = probes.get( i );
            String geneSymbol = geneSymbols.get( i );
            String geneName = null;
            if ( geneNames != null ) {
                geneName = geneNames.get( i );
            }
            Collection<String> goTermsForProbe = goTerms.get( i );

            storeProbeAndGene( probeIds, probe, geneSymbol );

            if ( geneName != null ) {
                probeToDescription.put( probe.intern(), geneName.intern() );
            } else {
                probeToDescription.put( probe.intern(), NO_DESCRIPTION );
            }

            for ( String string : goTermsForProbe ) {
                String go = string.intern();
                probeToGeneSetMap.get( probe ).add( go );

                if ( !geneSetToProbeMap.containsKey( go ) ) {
                    geneSetToProbeMap.put( go, new HashSet<String>() );
                }
                geneSetToProbeMap.get( go ).add( probe );
            }

            if ( messenger != null && i % 500 == 0 ) {
                messenger.showStatus( "Read " + i + " probes" );
                try {
                    Thread.sleep( 10 );
                } catch ( InterruptedException e ) {
                    throw new RuntimeException( "Interrupted" );
                }
            }
        }
        resetSelectedProbes();
        this.setUp( null );
    }

    public GeneAnnotations( String fileName, Set<String> activeGenes, StatusViewer messenger, GONames goNames )
            throws IOException {
        this( fileName, activeGenes, messenger, goNames, DEFAULT_FILTER_NONSPECIFIC );
    }

    /**
     * @param goNames
     * @param fileName
     */
    public GeneAnnotations( String fileName, Set<String> activeGenes, StatusViewer messenger, GONames goNames,
            boolean filterNonSpecific ) throws IOException {
        this.messenger = messenger;
        this.filterNonSpecific = filterNonSpecific;
        setUpdataStructures();
        InputStream i = FileTools.getInputStreamFromPlainOrCompressedFile( fileName );
        this.read( i, activeGenes );
        this.activeProbes = this.probeToGeneName.keySet();
        if ( activeProbes != null ) activeProbesDirty();
        setUp( goNames );
    }

    /**
     * Create GeneAnnotations by reading from a file
     * 
     * @param goNames
     * @param filename String
     * @param messenger StatusViewer to print status updates to.
     * @throws IOException
     */
    public GeneAnnotations( String filename, StatusViewer messenger, GONames goNames ) throws IOException {
        this( filename, messenger, goNames, DEFAULT, DEFAULT_FILTER_NONSPECIFIC );
    }

    public GeneAnnotations( String filename, StatusViewer messenger, GONames goNames, boolean filterNonSpecific )
            throws IOException {
        this( filename, messenger, goNames, DEFAULT, filterNonSpecific );
    }

    public GeneAnnotations( String filename, StatusViewer messenger, GONames goNames, int format ) throws IOException {
        this( filename, messenger, goNames, format, DEFAULT_FILTER_NONSPECIFIC );
    }

    /**
     * Create GeneAnnotations by reading from a file, with a selected input file format.
     * 
     * @param filename
     * @param messenger
     * @param goNames
     * @param format
     * @throws IOException
     */
    public GeneAnnotations( String filename, StatusViewer messenger, GONames goNames, int format,
            boolean filterNonSpecific ) throws IOException {
        log.debug( "Entering GeneAnnotations constructor" );
        setUpdataStructures();
        this.messenger = messenger;
        this.filterNonSpecific = filterNonSpecific;

        if ( format == DEFAULT ) {
            this.read( filename );
        } else if ( format == AFFYCSV ) {
            this.readAffyCsv( filename );
        } else if ( format == AGILENT ) {
            this.readAgilent( filename );
        } else {
            throw new IllegalArgumentException( "Unknown format" );
        }
        this.activeProbes = null; // using all.
        setUp( goNames );
    }

    /**
     * Add a new gene set. Used to set up user-defined gene sets.
     * <p>
     * 
     * @param id String class to be added
     * @param probesForNew collection of members.
     */
    public void addGeneSet( String geneSetId, Collection<String> probesForNew ) {

        if ( probesForNew == null ) throw new IllegalArgumentException( "Null probes for new gene set" );

        if ( probesForNew.size() == 0 ) {
            log.debug( "No probes to add for " + geneSetId );
            return;
        }

        if ( geneSetToProbeMap.containsKey( geneSetId ) ) {
            // then we should save a backup.
            log.info( "Saving backup version of " + geneSetId );
            oldGeneSets.put( geneSetId, new HashSet<String>( geneSetToProbeMap.get( geneSetId ) ) );
        }

        geneSetToProbeMap.put( geneSetId, new HashSet<String>( probesForNew ) );
        Set<String> genes = new HashSet<String>();
        for ( String probe : probesForNew ) {
            if ( !probeToGeneSetMap.containsKey( probe ) ) {
                probeToGeneSetMap.put( probe, new HashSet<String>() );
            }
            probeToGeneSetMap.get( probe ).add( geneSetId );

            if ( !probeToGeneName.containsKey( probe ) ) continue;
            genes.add( probeToGeneName.get( probe ) );
        }

        for ( Object element : genes ) {
            String gene = ( String ) element;
            if ( !geneToGeneSetMap.containsKey( gene ) ) geneToGeneSetMap.put( gene, new HashSet<String>() );
            geneToGeneSetMap.get( gene ).add( geneSetId );
        }
        geneSetToGeneMap.put( geneSetId, genes );

        log.debug( "Added new gene set: " + genes.size() + " genes to gene set id " + geneSetId + " with "
                + probesForNew.size() + " probes" );

        resetSelectedSets();

        this.multifunctionality = new Multifunctionality( this ); // possibly could avoid.
    }

    /**
     * @param parents
     */
    public void addGoTermsToGene( String gene, Collection<String> parents ) {

        for ( String id : parents ) {

            if ( !geneSetToGeneMap.containsKey( id ) ) geneSetToGeneMap.put( id, new HashSet<String>() );
            if ( !geneSetToProbeMap.containsKey( id ) ) geneSetToProbeMap.put( id, new HashSet<String>() );

            geneSetToGeneMap.get( id ).add( gene );
            geneToGeneSetMap.get( gene ).add( id );
            Collection<String> probes = geneToProbeMap.get( gene );

            geneSetToProbeMap.get( id ).addAll( probes );

            for ( Object element : probes ) {
                String probe = ( String ) element;
                probeToGeneSetMap.get( probe ).add( id );
            }
        }
    }

    /**
     * Returns true if the class is in the classToProbe map
     * 
     * @param id String a class id
     * @return boolean
     */
    public boolean geneSetExists( String id ) {
        return geneSetToProbeMap.containsKey( id );
    }

    /**
     * @return Map
     */
    public Map<String, Collection<String>> geneSetToRedundantMap() {
        return geneSetToRedundantMap;
    }

    /**
     * 
     */
    public Collection<String> getActiveGeneSetGenes( String geneSetId ) {
        if ( activeProbes == null ) return this.geneSetToGeneMap.get( geneSetId );

        if ( !geneSetActiveGenesCache.containsKey( geneSetId ) ) {
            Collection<String> finalList = new HashSet<String>();
            Collection<String> genes = geneSetToGeneMap.get( geneSetId );
            for ( String gene : genes ) {
                Collection<String> probes = geneToProbeMap.get( gene );
                if ( probes == null ) continue;
                for ( String probe : probes ) {
                    if ( activeProbes.contains( probe ) ) {
                        finalList.add( gene );
                        break;
                    }
                }
            }
            geneSetActiveGenesCache.put( geneSetId, finalList );
            return finalList;
        }
        return geneSetActiveGenesCache.get( geneSetId );
    }

    /**
     * @return Returns the activeProbes.
     */
    public Collection<String> getActiveProbes() {
        return this.activeProbes;
    }

    /**
     * @param id String class id
     * @return List list of probes in class
     */
    public Collection<String> getClassToProbes( String id ) {
        if ( activeProbes == null ) {
            return geneSetToProbeMap.get( id );
        }

        assert classToActiveProbeCache != null;
        if ( !classToActiveProbeCache.containsKey( id ) ) {
            Collection<String> finalList = new HashSet<String>();
            Collection<String> startingList = geneSetToProbeMap.get( id );
            if ( startingList == null ) {
                log.warn( "No probes for class: " + id );
                return finalList;
            }
            for ( Object element : startingList ) {
                String probe = ( String ) element;
                if ( activeProbes.contains( probe ) ) {
                    finalList.add( probe );
                }
            }
            classToActiveProbeCache.put( id, finalList );
            return finalList;
        }

        return classToActiveProbeCache.get( id );

    }

    public boolean getFilterNonSpecific() {
        return filterNonSpecific;
    }

    /**
     * Get the gene sets a gene belongs to.
     * 
     * @param gene
     * @return
     */
    public Collection<String> getGeneGeneSets( String gene ) {
        return this.geneToGeneSetMap.get( gene );
    }

    /**
     * Get a list of the probes that correspond to a particular gene.
     * 
     * @param g String a gene name
     * @return Collection of the probes for gene g
     */
    public Collection<String> getGeneProbeList( String gene ) {
        if ( activeProbes == null ) {
            return geneToProbeMap.get( gene );
        }

        if ( !geneToActiveProbesCache.containsKey( gene ) ) {
            Collection<String> finalList = new HashSet<String>();
            Collection<String> probes = geneToProbeMap.get( gene );
            if ( probes == null ) {
                log.debug( "No probes for " + gene );
                return null;
            }
            for ( Object element : probes ) {
                String probe = ( String ) element;
                if ( activeProbes.contains( probe ) ) {
                    finalList.add( probe );
                }
            }
            geneToActiveProbesCache.put( gene, finalList );
            return finalList;
        }
        return geneToActiveProbesCache.get( gene );
    }

    // /**
    // * @return Returns the classToGeneMap.
    // */
    // public Map getGeneSetToGeneMap() {
    // return geneSetToGeneMap;
    // }

    /**
     * @param geneSymbol
     * @return
     */
    public Collection<String> getGeneProbes( String geneSymbol ) {
        if ( activeProbes == null ) return this.geneToProbeMap.get( geneSymbol );

        Collection<String> finalList = new HashSet<String>();
        Collection<String> probes = geneToProbeMap.get( geneSymbol );
        for ( String probe : probes ) {
            if ( activeProbes.contains( probe ) ) {
                finalList.add( probe );
            }
        }
        return finalList;

    }

    /**
     * Return a collection of all currently active genes.
     * 
     * @return currently active genes
     */
    public Collection<String> getGenes() {
        if ( activeProbes == null ) return geneToGeneSetMap.keySet();

        if ( genesForActiveProbesCache == null ) {
            Collection<String> finalList = new HashSet<String>();
            for ( Object element : geneToGeneSetMap.keySet() ) {
                String gene = ( String ) element;
                Collection<String> probes = this.getGeneProbeList( gene );
                if ( probes != null && probes.size() > 0 ) {
                    finalList.add( gene );
                }
            }
            genesForActiveProbesCache = finalList;
            return finalList;
        }
        return genesForActiveProbesCache;
    }

    /**
     * Get a class by an integer index i from the sorted list.
     * 
     * @param i
     * @return
     */
    public String getGeneSetByIndex( int i ) {
        return sortedGeneSets.get( i );
    }

    /**
     * @param goset
     * @return set of genes in the given gene set (if any), based on the currently active probes
     */
    public Collection<String> getGeneSetGenes( String goset ) {
        Collection<String> genes = new HashSet<String>();
        Collection<String> probes = this.getGeneSetProbes( goset );

        for ( String p : probes ) {

            String gene = this.getProbeToGeneMap().get( p );

            /*
             * FIXME for the multigene case
             */

            // assert geneProbeList != null : goset + ": " + p;

            // genes.addAll( geneProbeList );
            genes.add( gene );
        }

        return genes;

    }

    /**
     * @param geneSetId
     * @return active probes for the given gene set
     */
    public Collection<String> getGeneSetProbes( String geneSetId ) {
        if ( activeProbes == null ) return this.geneSetToProbeMap.get( geneSetId );

        if ( !geneSetActiveProbesCache.containsKey( geneSetId ) ) {
            Collection<String> finalList = new HashSet<String>();
            Collection<String> probes = geneSetToProbeMap.get( geneSetId );
            if ( probes == null ) return finalList;
            for ( String probe : probes ) {
                if ( activeProbes.contains( probe ) ) {
                    finalList.add( probe );
                }
            }
            geneSetActiveProbesCache.put( geneSetId, finalList );
            return finalList;
        }
        return geneSetActiveProbesCache.get( geneSetId );
    }

    /**
     * Get a collection of all (active) gene sets.
     * 
     * @return
     */
    public Collection<String> getGeneSets() {
        if ( activeProbes == null ) return geneSetToGeneMap.keySet();

        if ( activeGeneSetCache == null ) {
            Collection<String> finalSet = new HashSet<String>();
            for ( Object element : geneSetToGeneMap.keySet() ) {
                String geneSet = ( String ) element;
                Collection<String> probes = getClassToProbes( geneSet );
                if ( probes.size() > 0 ) {
                    finalSet.add( geneSet );
                }
            }
            activeGeneSetCache = finalSet;
            return finalSet;
        }
        return activeGeneSetCache;
    }

    /** 
     */
    public Map<String, Collection<String>> getGeneToProbeMap() {
        return geneToProbeMap;
    }

    public Multifunctionality getMultifunctionality() {
        return multifunctionality;
    }

    /**
     * Get the description for a gene.
     * 
     * @param p String
     * @return String
     */
    public String getProbeDescription( String p ) {
        return probeToDescription.get( p );
    }

    /**
     * Get the gene that a probe belongs to.
     * 
     * @param p String
     * @return String
     */
    public String getProbeGeneName( String p ) {
        return probeToGeneName.get( p );
    }

    /**
     * @return Map
     */
    public Map<String, String> getProbeToGeneMap() {
        return probeToGeneName;
    }

    /**
     * @return Map
     */
    public Map<String, Collection<String>> getProbeToGeneSetMap() {
        return probeToGeneSetMap;
    }

    /**
     * @return the list of selected probes. Note that selected probes are distinct from active probes. Selected probes
     *         is more transient.
     */
    public List<String> getSelectedProbes() {
        return selectedProbes;
    }

    /**
     * @return list of selected gene sets.
     */
    public List<String> getSelectedSets() {
        return selectedSets;
    }

    /**
     * @param id
     * @return
     */
    public boolean hasGeneSet( String id ) {
        if ( activeProbes == null ) return this.geneSetToGeneMap.containsKey( id );

        return this.getGeneSets().contains( id );
    }

    /**
     * @param probeId
     * @return
     */
    public boolean hasProbe( String probeId ) {
        return this.probeToGeneName.containsKey( probeId );
    }

    /**
     * Redefine a class.
     * 
     * @param classId String class to be modified
     * @param probesForNew Collection current user-defined list of members. The gene set is recreated to look like this
     *        one.
     */
    public void modifyGeneSet( String classId, Collection<String> probesForNew ) {
        if ( !geneSetToProbeMap.containsKey( classId ) ) {
            log.warn( "No such class to modify: " + classId );
            return;
        }

        log.debug( "Saving backup version of " + classId + ", replacing with new version that has "
                + probesForNew.size() + " probes." );
        oldGeneSets.put( classId, new HashSet<String>( geneSetToProbeMap.get( classId ) ) );

        removeClassFromMaps( classId );
        addGeneSet( classId, probesForNew );
    }

    /**
     * Get the number of genes in a gene set, identified by id.
     * 
     * @param id String a class id
     * @return int number of genes in the class
     */
    public int numActiveGenesInGeneSet( String id ) {
        if ( !geneSetToGeneMap.containsKey( id ) ) {
            return 0;
        }
        return getActiveGeneSetGenes( id ).size();
    }

    /**
     * Get the number of probes in a gene set, identified by id.
     * 
     * @param id String a class id
     * @return int number of probes in the class
     */
    public int numActiveProbesInGeneSet( String id ) {
        if ( !geneSetToProbeMap.containsKey( id ) ) {
            log.debug( "No such gene set " + id );
            return 0;
        }
        if ( activeProbes == null ) return geneSetToProbeMap.get( id ).size();

        int result = 0;
        Collection<String> startingList = geneSetToProbeMap.get( id );
        for ( Iterator<String> iter = startingList.iterator(); iter.hasNext(); ) {
            String probe = iter.next();
            if ( activeProbes.contains( probe ) ) {
                result++;
            }
        }
        return result;
    }

    /**
     * Compute how many genes have Gene set annotations.
     * 
     * @return
     */
    public int numAnnotatedGenes() {
        int count = 0;
        for ( Object element2 : geneToGeneSetMap.keySet() ) {
            Collection<String> element = geneToGeneSetMap.get( element2 );
            if ( element.size() > 0 ) {
                count++;
            }
        }
        return count;
    }

    /**
     * How many genes are currently available
     */
    public int numGenes() {
        if ( activeProbes == null ) {
            return geneToProbeMap.size();
        }
        return this.getGenes().size();
    }

    /**
     * Get the number of gene sets currently available.
     * 
     * @return
     */
    public int numGeneSets() {
        return this.getGeneSets().size();
    }

    /**
     * @param id
     * @return
     */
    public int numGenesInGeneSet( String id ) {
        if ( !geneSetToGeneMap.containsKey( id ) ) {
            return 0;
        }
        return geneSetToGeneMap.get( id ).size();
    }

    /**
     * @return
     */
    public int numProbes() {
        assert activeProbes != null;
        return this.activeProbes.size();
    }

    /**
     * Get how many probes point to the same gene. This is like the old "numReplicates".
     * 
     * @param g
     * @return
     */
    public int numProbesForGene( String g ) {
        if ( !geneToProbeMap.containsKey( g ) ) return 0;
        if ( activeProbes == null ) return geneToProbeMap.get( g ).size();

        return this.getGeneProbeList( g ).size();
    }

    /**
     * @param id
     * @return
     */
    public int numProbesInGeneSet( String id ) {
        if ( !geneSetToProbeMap.containsKey( id ) ) {
            log.debug( "No such gene set " + id );
            return 0;
        }
        return this.geneSetToProbeMap.get( id ).size();
    }

    /**
     * @return the number of probes currently on the 'selected' list.
     */
    public int numSelectedProbes() {
        return selectedProbes.size();
    }

    /**
     * Print out the gene annotations in the same format we got them in, but if the gene sets have been modified, this
     * will be reflected.
     * 
     * @param out
     * @throws IOException
     */
    public void print( Writer out ) throws IOException {
        out.write( "Probe\tSymbol\tName\tGeneSets\n" );
        out.flush();
        for ( String probe : probeToGeneName.keySet() ) {
            String gene = probeToGeneName.get( probe );
            String desc = getProbeDescription( probe );
            out.write( probe + "\t" + gene + "\t" + desc + "\t" );
            Collection<String> geneSets = probeToGeneSetMap.get( probe );

            for ( String element : geneSets ) {
                out.write( element + "|" );
            }
            out.write( "\n" );
        }
    }

    /**
     * @param probe
     * @return
     */
    public String probeToGene( String probe ) {
        return this.probeToGeneName.get( probe );
    }

    /**
     * Remove a gene set (class) from all the maps that reference it.
     * <p>
     * 
     * @param id
     */
    public void removeClassFromMaps( String id ) {
        if ( geneSetToProbeMap.containsKey( id ) ) {
            for ( String probe : geneSetToProbeMap.get( id ) ) {
                if ( probeToGeneSetMap.containsKey( probe ) && probeToGeneSetMap.get( probe ).contains( id ) ) {
                    if ( !probeToGeneSetMap.get( probe ).remove( id ) ) {
                        log.error( "Couldn't remove " + id + " from probe to class map for" + probe );
                    }
                }
            }
            if ( geneSetToProbeMap.remove( id ) == null )
                log.error( "Couldn't remove " + id + " from classToProbeMap" );

            if ( geneSetToGeneMap.remove( id ) == null ) log.error( "Couldn't remove " + id + " from classToGeneMap" );
        }
        if ( geneSetToRedundantMap.containsKey( id ) ) geneSetToRedundantMap.remove( id );
        if ( this.getSelectedSets() != null ) this.getSelectedSets().remove( id );
    }

    /**
     * Set the selected gene set to be the entire set.
     */
    public void resetSelectedProbes() {
        selectedProbes = new Vector<String>( probeToGeneName.keySet() );
    }

    /**
     * Set the selected gene set to be the entire set.
     */
    public void resetSelectedSets() {
        selectedSets = new Vector<String>( geneSetToProbeMap.keySet() );
    }

    /**
     * Restore the previous version of a gene set. If no previous version is found, then nothing is done.
     * 
     * @param id
     */
    public void restoreGeneSet( String id ) {
        if ( !oldGeneSets.containsKey( id ) ) return;
        log.info( "Restoring " + id );
        removeClassFromMaps( id );
        addGeneSet( id, oldGeneSets.get( id ) );
    }

    /**
     * @return the number of sets currently on the 'selected' list.
     */
    public int selectedSets() {
        return selectedSets.size();
    }

    /*******************************************************************************************************************
     * Private or protected methods
     ******************************************************************************************************************/

    /**
     * Create a selected probes list based on a search string.
     * 
     * @param searchOn A string to be searched.
     */
    public void selectProbesBySearch( String searchOn ) {

        String searchOnUp = searchOn.toUpperCase();
        resetSelectedProbes();
        Set<String> removeUs = new HashSet<String>();
        for ( String probe : probeToGeneName.keySet() ) {
            String candidate = probeToGeneName.get( ( probe ) ).toUpperCase();

            // look in descriptions.
            String candidateD = probeToDescription.get( ( probe ) ).toUpperCase();

            if ( !candidate.startsWith( searchOnUp ) && candidateD.indexOf( searchOnUp ) < 0 ) {
                removeUs.add( probe );
            }

        }

        for ( Object element : removeUs ) {
            selectedProbes.remove( element );
        }
    }

    /**
     * @param searchOn
     * @param goData
     */
    public void selectSets( String searchOn, GONames goData ) {

        String searchOnUp = searchOn.toUpperCase();
        resetSelectedSets();
        Set<String> removeUs = new HashSet<String>();
        for ( Object element : geneSetToProbeMap.keySet() ) {
            String candidate = ( String ) element;

            // look in the name too
            if ( goData.getNameForId( candidate ) == null ) continue;
            String candidateN = goData.getNameForId( candidate ).toUpperCase();

            if ( !candidate.toUpperCase().startsWith( searchOnUp ) && candidateN.indexOf( searchOnUp ) < 0 ) {
                removeUs.add( candidate );
            }
        }

        for ( Object element : removeUs ) {
            selectedSets.remove( element );
        }
    }

    /**
     * Identify gene sets that contain a particular gene or probe.
     * 
     * @param searchOn
     * @param goData
     */
    public void selectSetsByGene( String searchOn ) {

        String searchOnUp = searchOn.toUpperCase();
        resetSelectedSets();
        Set<String> removeUs = new HashSet<String>();

        for ( String candidateGeneSet : geneSetToProbeMap.keySet() ) {
            boolean found = false;
            Collection<String> probes = geneSetToProbeMap.get( candidateGeneSet );

            for ( Object element : probes ) {
                String candidate = ( String ) element;
                if ( candidate.toUpperCase().startsWith( searchOnUp ) ) {
                    found = true;
                    log.debug( "Found " + candidate + " in " + candidateGeneSet );
                    break;
                }
            }

            if ( found ) continue;

            Collection<String> genes = geneSetToGeneMap.get( candidateGeneSet );
            for ( String candidate : genes ) {
                if ( candidate.toUpperCase().startsWith( searchOnUp ) ) {
                    found = true;
                    log.debug( "Found " + candidate + " in " + candidateGeneSet );
                    break;
                }

            }

            if ( !found ) {
                removeUs.add( candidateGeneSet );
            }
        }

        for ( Object element : removeUs ) {
            selectedSets.remove( element );
        }
    }

    /**
     * @param set
     */
    public void setActiveProbes( Collection<String> set ) {
        this.activeProbes = set;
        activeProbesDirty();
    }

    public void setFilterNonSpecific( boolean filterNonSpecific ) {
        this.filterNonSpecific = filterNonSpecific;
    }

    /**
     * Select a given set of gene sets.
     * 
     * @param selectedGeneSets
     */
    public void setSelectedSets( Collection<String> selectedGeneSets ) {
        this.selectedSets.clear();
        this.selectedSets.addAll( selectedGeneSets );
    }

    /**
     * Sort the gene sets, filling out the sortedGeneSets. This should be called after any changes have been made to the
     * classToProbeMap. The sort is just in order of id.
     */
    public void sortGeneSets() {

        if ( this.geneSetToProbeMap.size() == 0 ) {
            throw new IllegalStateException( "Could not sort because there are no gene sets in the geneSetToProbeMap" );
        }

        if ( this.sortedGeneSets == null ) {
            this.sortedGeneSets = new Vector<String>();
        }

        List<String> vec = new Vector<String>( geneSetToProbeMap.keySet() );
        Collections.sort( vec );
        for ( String string : vec ) {
            this.sortedGeneSets.add( string );
        }
    }

    /**
     * @return
     */
    public List<String> sortGeneSetsBySize() {

        List<GeneSet> sets = new Vector<GeneSet>();
        for ( String name : geneSetToGeneMap.keySet() ) {
            sets.add( new GeneSet( name, geneSetToGeneMap.get( name ) ) );
        }

        Collections.sort( sets, new ClassSizeComparator() );

        List<String> returnVal = new Vector<String>();
        for ( GeneSet geneSet : sets ) {
            returnVal.add( geneSet.getName() );
        }

        return returnVal;
    }

    public int ticks() {
        return tick;
    }

    /**
     * @return
     */
    public TableModel toTableModel() {
        return new AbstractTableModel() {
            private static final long serialVersionUID = 1L;
            private String[] columnNames = { "Probe", "Gene", "Description" };

            public int getColumnCount() {
                return 3;
            }

            @Override
            public String getColumnName( int i ) {
                return columnNames[i];
            }

            public int getRowCount() {
                return selectedProbes.size();
            }

            public Object getValueAt( int i, int j ) {

                String probeid = selectedProbes.get( i );
                switch ( j ) {
                    case 0:
                        return probeid;
                    case 1:
                        return getProbeGeneName( probeid );
                    case 2:
                        return getProbeDescription( probeid );
                    default:
                        return null;
                }
            }

        };
    }

    /**
     * 
     */
    private void activeProbesDirty() {
        genesForActiveProbesCache = null;
        activeGeneSetCache = null;
        if ( geneToActiveProbesCache != null ) geneToActiveProbesCache.clear();
        if ( classToActiveProbeCache != null ) classToActiveProbeCache.clear();
        if ( geneSetActiveGenesCache != null ) geneSetActiveGenesCache.clear();
        if ( geneSetActiveProbesCache != null ) geneSetActiveProbesCache.clear();
    }

    /**
     * @param probes
     * @param geneSymbols
     * @param geneNames
     * @param goTerms
     */
    private void checkValidData( List<String> probes, List<String> geneSymbols, List<String> geneNames,
            List<Collection<String>> goTerms ) {
        if ( probes == null || geneSymbols == null || goTerms == null ) {
            throw new IllegalArgumentException( "Probes, gene symbols, GO terms and GO data must not be null" );
        }
        int size = probes.size();
        if ( size == 0 ) {
            throw new IllegalArgumentException( "Empty list" );
        }
        if ( size != geneSymbols.size() && size != geneNames.size() && size != goTerms.size() ) {
            throw new IllegalArgumentException( "All lists must have same number of elements" );
        }

    }

    /**
     * @param classIds
     * @param probe
     */
    private void extractPipeDelimitedGoIds( String classIds, String probe ) {
        String[] classIdAry = pipePattern.split( classIds );
        if ( classIdAry.length == 0 ) return;

        Collection<String> probeCol = probeToGeneSetMap.get( probe );
        probeCol.addAll( Arrays.asList( classIdAry ) );

        for ( String element : classIdAry ) {
            String go = element.intern();

            if ( !geneSetToProbeMap.containsKey( go ) ) {
                geneSetToProbeMap.put( go, new HashSet<String>() );
            }
            geneSetToProbeMap.get( go ).add( probe );
        }
    }

    /**
     * @param limit
     * @param header
     * @param pattern
     */
    private int findField( String header, String sep, String pattern ) {
        String[] fields = header.split( sep );
        if ( fields == null || fields.length == 0 ) throw new IllegalArgumentException( "No header!" );
        for ( int i = 0; i < fields.length; i++ ) {
            if ( fields[i].replaceAll( "\"", "" ).matches( pattern ) ) {
                return i;
            }
        }
        return -1;
    }

    private int getAffyAlternateGeneSymbolIndex( String header ) {

        String[] alternates = new String[] { "Transcript ID", "Transcript ID(Array Design)", "UniGene ID", "swissprot",
                "unigene" };
        for ( String pattern : alternates ) {
            int i = findField( header, AFFY_FIELD_SEPARATION_REGEX, pattern );
            if ( i >= 0 ) return i;
        }
        return -1;
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyBpIndex( String header ) throws IOException {
        String pattern = "(Gene Ontology Biological Process|GO_biological_process)";
        return findField( header, AFFY_FIELD_SEPARATION_REGEX, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyCcIndex( String header ) throws IOException {
        String pattern = "(Gene Ontology Cellular Component|GO_cellular_component)";
        return findField( header, AFFY_FIELD_SEPARATION_REGEX, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyGeneNameIndex( String header ) throws IOException {
        String pattern = "(Gene Title|gene_assignment)";
        return findField( header, AFFY_FIELD_SEPARATION_REGEX, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyGeneSymbolIndex( String header ) throws IOException {
        String pattern = "(Gene Symbol|gene_assignment)";
        return findField( header, AFFY_FIELD_SEPARATION_REGEX, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyMfIndex( String header ) throws IOException {
        String pattern = "(Gene Ontology Molecular Function|GO_molecular_function)";
        return findField( header, AFFY_FIELD_SEPARATION_REGEX, pattern );
    }

    /**
     * @param header
     * @return
     */
    private int getAffyNumFields( String header ) {

        String[] fields = header.split( AFFY_FIELD_SEPARATION_REGEX );
        return fields.length;
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyProbeIndex( String header ) throws IOException {
        String pattern = "(Probe Set ID|probeset_id)";
        return findField( header, AFFY_FIELD_SEPARATION_REGEX, pattern );
    }

    /**
     * @param header
     * @return
     */
    private int getAgilentGeneNameIndex( String header ) {
        String pattern = "GeneName";
        return findField( header, "\t", pattern );
    }

    /**
     * @param header
     * @return
     */
    private int getAgilentGeneSymbolIndex( String header ) {
        String pattern = "GeneSymbol";
        return findField( header, "\t", pattern );
    }

    /**
     * @param header
     * @return
     */
    private int getAgilentGoIndex( String header ) {
        String pattern = "GO";
        return findField( header, "\t", pattern );
    }

    /**
     * @param header
     * @return
     */
    private int getAgilentNumFields( String header ) {
        String[] fields = header.split( "\t" );
        return fields.length;
    }

    /**
     * @param header
     * @return
     */
    private int getAgilentProbeIndex( String header ) {
        String pattern = "ProbeID";
        return findField( header, "\t", pattern );
    }

    /**
     * Fill in the classToGeneMap with information from the classToProbeMap.
     * 
     * @return mapping of gene sets to genes.
     */
    private Map<String, Collection<String>> makeClassToGeneMap() {
        Map<String, Collection<String>> gsToGeneMap = new HashMap<String, Collection<String>>();
        for ( String geneSetId : geneSetToProbeMap.keySet() ) {
            Collection<String> probesInSet = geneSetToProbeMap.get( geneSetId );

            Set<String> genesInSet = new HashSet<String>();
            for ( String probe : probesInSet ) {
                genesInSet.add( probeToGeneName.get( probe ) );
            }
            gsToGeneMap.put( geneSetId, genesInSet );
        }
        return gsToGeneMap;
    }

    /**
     * @param go
     * @return
     */
    private String padGoTerm( String go ) {
        String goPadded = go;
        if ( !goPadded.startsWith( "GO:" ) ) {
            int needZeros = 7 - goPadded.length();
            for ( int j = 0; j < needZeros; j++ ) {
                goPadded = "0" + goPadded;
            }
            goPadded = "GO:" + goPadded;
        }
        return goPadded;
    }

    /**
     * @param probe
     * @param pat
     * @param goi
     */
    private void parseGoTerm( String probe, Pattern pat, String goi ) {
        Matcher mat = pat.matcher( goi );
        if ( mat.find() ) {
            int start = mat.start();
            int end = mat.end();
            String go = goi.substring( start, end );

            go = padGoTerm( go );

            assert go.matches( "GO:[0-9]{7}" ) : "Trying to fix up : " + goi;

            this.probeToGeneSetMap.get( probe ).add( go );

            if ( !geneSetToProbeMap.containsKey( go ) ) {
                geneSetToProbeMap.put( go, new HashSet<String>() );
            }
            this.geneSetToProbeMap.get( go ).add( probe );
        }
    }

    /**
     * Remove classes that have too few members
     * <p>
     * FIXME this overlaps with functionality in GeneSetMapTools
     * 
     * @param lowThreshold
     * @param highThreshold
     */
    private void prune( int lowThreshold, int highThreshold ) {

        if ( this.geneSetToProbeMap.isEmpty() ) {
            throw new IllegalStateException( "No gene sets!" );
        }

        Set<String> removeUs = new HashSet<String>();
        for ( String id : geneSetToProbeMap.keySet() ) {
            int numActiveProbesInGeneSet = numActiveProbesInGeneSet( id );
            int numActiveGenesInGeneSet = numActiveGenesInGeneSet( id );
            if ( numActiveProbesInGeneSet < lowThreshold || numActiveGenesInGeneSet < lowThreshold
                    || numActiveProbesInGeneSet > highThreshold || numActiveGenesInGeneSet > highThreshold ) {
                // log.debug( "Pruning gene set : " + id + ", size =" + numActiveProbesInGeneSet + " probes, "
                // + numActiveGenesInGeneSet + " genes." );
                removeUs.add( id );
            }
        }

        if ( !removeUs.isEmpty() ) {
            log.info( removeUs.size() + " terms pruned" );
        }

        for ( Object element : removeUs ) {
            String id = ( String ) element;
            removeClassFromMaps( id );
        }

        if ( this.geneSetToProbeMap.isEmpty() ) {
            throw new IllegalStateException(
                    "All gene sets were removed due to being too small or too big; size range=" + lowThreshold + " - "
                            + highThreshold + ". Your annotation file may contain too few GO terms." );
        }

        sortGeneSets();
    }

    private void read( InputStream bis ) throws IOException {
        this.read( bis, null );
    }

    // read in from a file.
    private void read( String fileName ) throws IOException {
        InputStream i = FileTools.getInputStreamFromPlainOrCompressedFile( fileName );
        read( i );
    }

    /**
     * @param bis
     */
    private void readAffyCsv( InputStream bis ) throws IOException {
        this.readAffyCsv( bis, null );
    }

    /**
     * @param filename
     */
    private void readAffyCsv( String fileName ) throws IOException {
        InputStream i = FileTools.getInputStreamFromPlainOrCompressedFile( fileName );
        readAffyCsv( i );
    }

    private void readAgilent( InputStream bis ) throws IOException {
        this.readAgilent( bis, null );
    }

    /**
     * @param filename
     */
    private void readAgilent( String fileName ) throws IOException {
        InputStream i = FileTools.getInputStreamFromPlainOrCompressedFile( fileName );
        readAgilent( i );
    }

    /**
     * @param probe
     */
    private void removeProbeFromMaps( String probe ) {
        if ( probeToGeneName.containsKey( probe ) ) {
            String gene = probeToGeneName.get( probe );
            probeToGeneName.remove( probe );
            if ( geneToProbeMap.containsKey( gene ) ) {
                geneToProbeMap.get( gene ).remove( probe );
            }
        }
        if ( probeToGeneSetMap.containsKey( probe ) ) {
            Iterator<String> cit = probeToGeneSetMap.get( probe ).iterator();
            while ( cit.hasNext() ) {
                String geneSet = cit.next();
                if ( geneSetToProbeMap.containsKey( geneSet ) ) {
                    geneSetToProbeMap.get( geneSet ).remove( probe );
                }
            }
            if ( probeToGeneSetMap.remove( probe ) == null ) {
                log.warn( "Could not remove " + probe + " from probeToClassMap" );
            }
        }
        if ( probeToDescription.containsKey( probe ) ) probeToDescription.remove( probe );
    }

    /**
     * 
     */
    private void setUpdataStructures() {
        probeToGeneSetMap = new LinkedHashMap<String, Collection<String>>();
        geneSetToProbeMap = new LinkedHashMap<String, Collection<String>>();
        probeToGeneName = new HashMap<String, String>();
        probeToDescription = new HashMap<String, String>();
        geneToProbeMap = new HashMap<String, Collection<String>>();
        geneToGeneSetMap = new HashMap<String, Collection<String>>();
        geneSetToRedundantMap = new HashMap<String, Collection<String>>();
        oldGeneSets = new HashMap<String, Collection<String>>();
        classToActiveProbeCache = new HashMap<String, Collection<String>>();
        geneToActiveProbesCache = new HashMap<String, Collection<String>>();
        geneSetActiveGenesCache = new HashMap<String, Collection<String>>();
        geneSetActiveProbesCache = new HashMap<String, Collection<String>>();
    }

    /**
     * @param probeIds
     * @param probe
     * @param geneSymbol
     */
    private void storeProbeAndGene( Collection<String> probeIds, String probe, String geneSymbol ) {

        if ( StringUtils.isBlank( geneSymbol ) ) {
            throw new IllegalArgumentException( "Blank gene symbol" );
        }
        if ( StringUtils.isBlank( probe ) ) {
            throw new IllegalArgumentException( "Blank probe name" );
        }

        probeToGeneName.put( probe, geneSymbol );

        // create the list if need be.
        if ( geneToProbeMap.get( geneSymbol ) == null ) {
            geneToProbeMap.put( geneSymbol, new HashSet<String>() );
        }
        geneToProbeMap.get( geneSymbol ).add( probe );

        probeIds.add( probe );
        if ( !probeToGeneSetMap.containsKey( probe ) ) {
            probeToGeneSetMap.put( probe.intern(), new HashSet<String>() );
        }
        geneToGeneSetMap.put( geneSymbol, probeToGeneSetMap.get( probe ) );
    }

    /**
     * 
     */
    private void tick() {
        tick++;
    }

    /**
     * @param bis
     * @param activeGenes
     * @throws IOException
     */
    protected void read( InputStream bis, Set<String> activeGenes ) throws IOException {
        log.debug( "Entering GeneAnnotations.read" );
        if ( bis == null ) {
            throw new IOException( "Inputstream was null" );
        }

        if ( bis.available() == 0 ) {
            throw new IOException( "No bytes to read from the annotation file." );
        }

        BufferedReader dis = new BufferedReader( new InputStreamReader( bis ) );
        Collection<String> probeIds = new ArrayList<String>();
        String classIds = null;

        // loop through rows. Makes hash map of probes to go, and map of go to
        // probes.
        int n = 0;
        String line = "";
        tick();
        while ( ( line = dis.readLine() ) != null ) {

            if ( line.startsWith( "#" ) ) continue;

            // String[] tokens = StringUtils.splitPreserveAllTokens( line, "\t" );
            String[] tokens = line.split( "\t" );
            int length = tokens.length;
            if ( length < 2 ) continue;

            String probe = tokens[0].intern();
            String gene = tokens[1].intern();

            if ( filterNonSpecific && ( gene.contains( "|" ) || gene.contains( "," ) ) ) {
                continue;
            }

            if ( activeGenes != null && !activeGenes.contains( probe ) ) {
                continue;
            }

            // if ( log.isDebugEnabled() ) log.debug( "probe: " + probe );

            storeProbeAndGene( probeIds, probe, gene );

            /* read gene description */
            if ( length >= 3 ) {
                String description = tokens[2].intern();
                if ( description.length() > 0 ) {
                    probeToDescription.put( probe.intern(), description.intern() );
                } else {
                    probeToDescription.put( probe.intern(), NO_DESCRIPTION );
                }
            } else {
                probeToDescription.put( probe.intern(), NO_DESCRIPTION );
                continue;
            }

            /* read GO data */
            if ( length >= 4 ) {
                classIds = tokens[3];
                extractPipeDelimitedGoIds( classIds, probe );
            }

            if ( messenger != null && n % 500 == 0 ) {
                messenger.showStatus( "Read " + n + " probes" );
                try {
                    Thread.sleep( 2 );
                } catch ( InterruptedException e ) {
                    dis.close();
                    throw new CancellationException();
                }
            }
            n++;

        }

        /* Fill in the genegroupreader and the classmap */
        dis.close();
        resetSelectedProbes();

        if ( probeToGeneName.size() == 0 || geneSetToProbeMap.size() == 0 ) {
            throw new IllegalArgumentException(
                    "The gene annotations had invalid information. Please check the format." );
        }

    }

    /**
     * Parse affy formatted files (CSV or tabbed should be okay)
     * 
     * @param bis
     * @param object
     */
    protected void readAffyCsv( InputStream bis, Set<String> activeGenes ) throws IOException {
        if ( bis == null ) {
            throw new IOException( "Inputstream was null" );
        }
        BufferedReader dis = new BufferedReader( new InputStreamReader( bis ) );
        Collection<String> probeIds = new ArrayList<String>();
        String classIds = null;

        /*
         * Skip comment lines (new format)
         */
        String line = "";
        while ( ( line = dis.readLine() ) != null ) {
            /* line is blank, or starts with "#" , keep reading */
            if ( !StringUtils.isBlank( line ) && !line.matches( "^#.+" ) ) {
                break;
            }
        }

        String header = line;

        if ( StringUtils.isBlank( header ) ) {
            throw new IOException( "File had no header" );
        }

        int numFields = getAffyNumFields( header );
        int probeIndex = getAffyProbeIndex( header );
        int goBpIndex = getAffyBpIndex( header );
        int goCcIndex = getAffyCcIndex( header );
        int goMfIndex = getAffyMfIndex( header );
        int geneNameIndex = getAffyGeneNameIndex( header );
        int geneSymbolIndex = getAffyGeneSymbolIndex( header );

        int alternateGeneSymbolIndex = getAffyAlternateGeneSymbolIndex( header );

        if ( probeIndex < 0 ) {
            throw new IllegalStateException( "Invalid AFFY file format: could not find the probe set id column" );
        }
        if ( geneNameIndex < 0 ) {
            throw new IllegalStateException( "Invalid AFFY file format: could not find the gene name column" );
        }
        if ( geneSymbolIndex < 0 ) {
            throw new IllegalStateException( "Invalid AFFY file format: could not find the gene symbol column" );
        }

        if ( goBpIndex < 0 ) {
            throw new IllegalStateException( "Invalid AFFY file format: No biological process data were found" );
        } else if ( goCcIndex < 0 ) {
            throw new IllegalStateException( "Invalid AFFY file format: No cellular component data were found" );
        } else if ( goMfIndex < 0 ) {
            throw new IllegalStateException( "Invalid AFFY file format: No molecular function data were found" );
        }

        log.debug( "Read header" );

        tick();
        assert ( numFields > probeIndex + 1 && numFields > geneSymbolIndex + 1 );

        /*
         * This pattern is designed to work with old or new formats (old was not padded to 7 digits and lacks "GO:").
         */
        Pattern pat = Pattern.compile( "(GO:)?[0-9]{1,7}$" );

        // loop through rows. Makes hash map of probes to go, and map of go to
        // probes.
        int n = 0;

        log.debug( "File opened okay, parsing Affy annotation file" );

        while ( ( line = dis.readLine() ) != null ) {

            if ( Thread.currentThread().isInterrupted() ) {
                dis.close();
                throw new CancellationException();
            }

            /*
             * New files are tabbed...
             */
            String[] fields = null;

            if ( line.matches( ".+\t.+" ) ) {
                fields = StringUtils.splitPreserveAllTokens( line, '\t' );
            } else {
                fields = StringUtil.csvSplit( line );
            }

            if ( fields.length < probeIndex + 1 || fields.length < geneSymbolIndex + 1 ) {
                continue; // skip lines that don't meet criteria.
            }

            String probe = fields[probeIndex];
            String gene = fields[geneSymbolIndex];

            if ( StringUtils.isBlank( probe ) || probe.equals( "---" ) ) {
                throw new IllegalStateException( "Probe name was missing or invalid at line " + n
                        + "; it is possible the file format is not readable; contact the developers." );
            }

            if ( StringUtils.isBlank( gene ) || gene.equals( "---" ) ) {
                gene = fields[alternateGeneSymbolIndex];
                if ( StringUtils.isBlank( gene ) || gene.equals( "---" ) ) {
                    // throw new IllegalStateException( "Gene name was missing or invalid at line " + n + "; (probe="
                    // + probe + " it is possible the file format is not readable; contact the developers." );
                    // log.warn( "Skipping unannotated probe: " + probe );
                    continue;
                }
            }

            if ( activeGenes != null && !activeGenes.contains( gene ) ) {
                continue;
            }

            // log.debug("Probe=" + probe + " Gene=" + gene); // PP temporary for user problems.

            storeProbeAndGene( probeIds, probe, gene );

            /* read gene description */

            String description = fields[geneNameIndex].intern();
            if ( !description.startsWith( "GO:" ) ) {
                this.probeToDescription.put( probe.intern(), description.intern() );
            } else {
                this.probeToDescription.put( probe.intern(), NO_DESCRIPTION );
            }

            /*
             * Each field is like this: 0000166 // nucleotide binding // inferred from electronic annotation OR in new
             * format, it has the GO:XXXXXX directly.
             */

            classIds = " // " + fields[goBpIndex] + " // " + fields[goMfIndex] + " // " + fields[goCcIndex];
            String[] goinfo = classIds.split( "/+" );
            for ( String element : goinfo ) {
                if ( StringUtils.isBlank( element ) ) {
                    continue;
                }
                element = StringUtils.strip( element );
                parseGoTerm( probe, pat, element );
            }

            if ( messenger != null && n % 5000 == 0 ) {
                messenger.showStatus( "Read " + n + " probes" );
                try {
                    Thread.sleep( 10 );
                } catch ( InterruptedException e ) {
                    dis.close();
                    throw new RuntimeException( "Interrupted" );
                }
            }
            n++;

        }

        /* Fill in the genegroupreader and the classmap */
        dis.close();
        tick();
        resetSelectedProbes();

        if ( this.probeToGeneName.size() == 0 || this.geneSetToProbeMap.size() == 0 ) {
            throw new IllegalArgumentException( "The gene annotations had invalid information. "
                    + this.probeToGeneName.size() + " probe names, " + this.geneSetToProbeMap.size()
                    + " gene groups. Please check the format." );
        }

    }

    /**
     * @param bis
     * @param activeGenes
     * @throws IOException
     */
    protected void readAgilent( InputStream bis, Set<String> activeGenes ) throws IOException {
        if ( bis == null ) {
            throw new IOException( "Inputstream was null" );
        }
        BufferedReader dis = new BufferedReader( new InputStreamReader( bis ) );
        Collection<String> probeIds = new ArrayList<String>();
        String classIds = null;

        String header = dis.readLine();
        int numFields = getAgilentNumFields( header );
        int probeIndex = getAgilentProbeIndex( header );
        int goIndex = getAgilentGoIndex( header );
        int geneNameIndex = getAgilentGeneNameIndex( header );
        int geneSymbolIndex = getAgilentGeneSymbolIndex( header );

        tick();
        assert ( numFields > probeIndex + 1 && numFields > geneSymbolIndex + 1 );
        Pattern pat = Pattern.compile( "[0-9]+" );
        // loop through rows. Makes hash map of probes to go, and map of go to
        // probes.
        int n = 0;
        String line = "";
        while ( ( line = dis.readLine() ) != null ) {

            if ( Thread.currentThread().isInterrupted() ) {
                dis.close();
                throw new CancellationException();
            }

            String[] fields = StringUtils.splitPreserveAllTokens( line, '\t' );
            if ( fields.length < probeIndex + 1 || fields.length < geneSymbolIndex + 1 ) {
                continue; // skip lines that don't meet criteria.
            }

            String probe = fields[probeIndex];
            String gene = fields[geneSymbolIndex];

            if ( activeGenes != null && !activeGenes.contains( gene ) ) {
                continue;
            }

            storeProbeAndGene( probeIds, probe, gene );

            /* read gene description */

            String description = fields[geneNameIndex].intern();
            if ( !description.startsWith( "GO:" ) ) {
                probeToDescription.put( probe.intern(), description.intern() );
            } else {
                probeToDescription.put( probe.intern(), NO_DESCRIPTION );
            }

            if ( fields.length < goIndex + 1 ) {
                continue;
            }

            classIds = fields[goIndex];

            if ( StringUtils.isNotBlank( classIds ) ) {
                String[] goinfo = classIds.split( "\\|" );
                for ( String element : goinfo ) {
                    String goi = element.intern();
                    parseGoTerm( probe, pat, goi );
                }
            }

            if ( messenger != null && n % 500 == 0 ) {
                messenger.showStatus( "Read " + n + " probes" );
                try {
                    Thread.sleep( 10 );
                } catch ( InterruptedException e ) {
                    dis.close();
                    throw new RuntimeException( "Interrupted" );
                }
            }
            n++;

        }

        /* Fill in the genegroupreader and the classmap */
        dis.close();
        tick();
        resetSelectedProbes();

        if ( probeToGeneName.size() == 0 || geneSetToProbeMap.size() == 0 ) {
            throw new IllegalArgumentException(
                    "The gene annotations had invalid information. Please check the format." );
        }

    }

    /**
     * Initialize the gene sets and other data structures that needs special handling before use.
     * 
     * @param goNames
     */
    protected void setUp( GONames goNames ) {
        this.geneSetToGeneMap = makeClassToGeneMap();
        if ( goNames != null ) GeneSetMapTools.addParents( this, goNames, messenger );
        GeneSetMapTools.collapseGeneSets( this, messenger );
        prune( ABSOLUTE_MINIMUM_GENESET_SIZE, PRACTICAL_MAXIMUM_GENESET_SIZE );
        tick();
        resetSelectedProbes();
        resetSelectedSets();
        sortGeneSets();
        this.multifunctionality = new Multifunctionality( this );
    }

}

class ClassSizeComparator implements Comparator<GeneSet> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( GeneSet a, GeneSet b ) {

        int sizea = a.size();
        int sizeb = b.size();

        if ( sizea > sizeb ) {
            return 1;
        } else if ( sizeb < sizea ) {
            return -1;
        }

        return 0;
    }
}

// used for the comparator.

class GeneSet {
    private Collection<String> items;
    private String name;

    public GeneSet( String name, Collection<String> items ) {
        this.name = name;
        this.items = items;
    }

    /**
     * @return Returns the items.
     */
    public Collection<String> getItems() {
        return items;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param items The items to set.
     */
    public void setItems( Set<String> items ) {
        this.items = items;
    }

    /**
     * @param name The name to set.
     */
    public void setName( String name ) {
        this.name = name;
    }

    public int size() {
        return items.size();
    }
}