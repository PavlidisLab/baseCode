/*
 * The basecode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
    public static final int DEFAULT = 0;

    /**
     * The minimum size of a 'set' of genes.
     */
    private static final int ABSOLUTE_MINIMUM_GENESET_SIZE = 2;

    /**
     * String used to indicate a gene has no description associated with it.
     */
    private static final String NO_DESCRIPTION = "[No description]";

    /**
     * The maximum size of gene sets ever considered.
     */
    private static final int PRACTICAL_MAXIMUM_GENESET_SIZE = 2000;

    protected static final Log log = LogFactory.getLog( GeneAnnotations.class );
    private Map geneSetToGeneMap; // stores Classes->genes map
    private Map geneSetToProbeMap; // stores Classes->probes map
    private Map geneSetToRedundantMap;
    private Map geneToGeneSetMap;
    private Map geneToProbeMap;
    private StatusViewer messenger;
    private Map probeToDescription;
    private Map probeToGeneName;
    private Map probeToGeneSetMap;

    private List selectedProbes;

    private List selectedSets;
    private List sortedGeneSets;
    private Map oldGeneSets;
    private int tick = 0;

    private Pattern pipePattern = Pattern.compile( "\\s*[\\s\\|,]\\s*" );

    private Collection activeProbes = null;

    private Map classToActiveProbeCache = new HashMap();

    private Collection genesForActiveProbesCache;

    private Map geneToActiveProbesCache = new HashMap();

    private Map geneSetActiveGenesCache = new HashMap();

    private Map geneSetActiveProbesCache = new HashMap();

    private Collection activeGeneSetCache;

    public GeneAnnotations() {
        this.setUpdataStructures();
    }

    /**
     * @param goNames This is for creating GeneAnnotations by pruning a copy.
     * @param geneData GeneAnnotations copy to prune from
     * @param activeProbes Set only include these probes
     */
    public GeneAnnotations( GeneAnnotations geneData, Set activeProbes ) {

        if ( activeProbes == null || geneData == null )
            throw new IllegalArgumentException( "GeneAnnotations can't be constructed from null data" );

        this.activeProbes = activeProbes;
        activeProbesDirty();

        // make a deep copy of the probeToGeneSetMap
        this.probeToGeneSetMap = new LinkedHashMap();
        for ( Iterator iter = geneData.probeToGeneSetMap.keySet().iterator(); iter.hasNext(); ) {
            String key = ( String ) iter.next();
            this.probeToGeneSetMap.put( key, new HashSet( ( Collection ) geneData.probeToGeneSetMap.get( key ) ) );
        }

        // make a deep copy of the classToProbeMap, which is a map of sets. Shallow copy is BAD.
        this.geneSetToProbeMap = new LinkedHashMap();
        for ( Iterator iter = geneData.geneSetToProbeMap.keySet().iterator(); iter.hasNext(); ) {
            String key = ( String ) iter.next();
            this.geneSetToProbeMap.put( key, new HashSet( ( Collection ) geneData.geneSetToProbeMap.get( key ) ) );
        }

        // make a deep copy of the old gene sets.
        this.oldGeneSets = new LinkedHashMap();
        for ( Iterator iter = geneData.oldGeneSets.keySet().iterator(); iter.hasNext(); ) {
            String key = ( String ) iter.next();
            this.oldGeneSets.put( key, new HashSet( ( Collection ) geneData.oldGeneSets.get( key ) ) );
        }

        probeToGeneName = new HashMap( geneData.probeToGeneName ); // shallow copy, okay
        probeToDescription = new HashMap( geneData.probeToDescription ); // shallow copy, okay
        geneToProbeMap = new HashMap( geneData.geneToProbeMap ); // shallow copy, okay?
        geneToGeneSetMap = new HashMap( geneData.geneToGeneSetMap ); // shallow copy, okay?
        geneSetToRedundantMap = new HashMap( geneData.geneSetToRedundantMap );

        List allProbes = new Vector( probeToGeneName.keySet() );
        for ( Iterator iter = allProbes.iterator(); iter.hasNext(); ) {
            String probe = ( String ) iter.next();
            if ( !activeProbes.contains( probe ) ) { // remove probes not in data set.
                removeProbeFromMaps( probe );
            }
        }
        setUp( null ); // creates the classToGene map.
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
     * @param stream
     * @param activeGenes
     * @param messenger
     * @param goNames
     * @throws IOException
     */
    public GeneAnnotations( InputStream stream, Set activeGenes, StatusViewer messenger, GONames goNames )
            throws IOException {
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
    public GeneAnnotations( List probes, List geneSymbols, List geneNames, List goTerms ) {
        checkValidData( probes, geneSymbols, geneNames, goTerms );

        setUpdataStructures();

        if ( probes != null ) {
            this.activeProbes = probes;
            activeProbesDirty();
        }

        Collection probeIds = new ArrayList();
        for ( int i = 0; i < probes.size(); i++ ) {
            String probe = ( String ) probes.get( i );
            String geneSymbol = ( String ) geneSymbols.get( i );
            String geneName = null;
            if ( geneNames != null ) {
                geneName = ( String ) geneNames.get( i );
            }
            Collection goTermsForProbe = ( Collection ) goTerms.get( i );

            storeProbeAndGene( probeIds, probe, geneSymbol );

            if ( geneName != null ) {
                probeToDescription.put( probe.intern(), geneName.intern() );
            } else {
                probeToDescription.put( probe.intern(), NO_DESCRIPTION );
            }

            for ( Iterator iter = goTermsForProbe.iterator(); iter.hasNext(); ) {
                String go = ( ( String ) iter.next() ).intern();
                ( ( Collection ) probeToGeneSetMap.get( probe ) ).add( go );

                if ( !geneSetToProbeMap.containsKey( go ) ) {
                    geneSetToProbeMap.put( go, new HashSet() );
                }
                ( ( Collection ) geneSetToProbeMap.get( go ) ).add( probe );
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

    /**
     * @param goNames
     * @param fileName
     */
    public GeneAnnotations( String fileName, Set activeGenes, StatusViewer messenger, GONames goNames )
            throws IOException {
        this.messenger = messenger;

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
        this( filename, messenger, goNames, DEFAULT );
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
    public GeneAnnotations( String filename, StatusViewer messenger, GONames goNames, int format ) throws IOException {
        setUpdataStructures();
        this.messenger = messenger;

        if ( format == DEFAULT ) {
            this.read( filename );
        } else if ( format == AFFYCSV ) {
            this.readAffyCsv( filename );
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
    public void addGeneSet( String geneSetId, Collection probesForNew ) {

        if ( probesForNew == null ) throw new IllegalArgumentException( "Null probes for new gene set" );

        if ( probesForNew.size() == 0 ) {
            log.debug( "No probes to add for " + geneSetId );
            return;
        }

        if ( geneSetToProbeMap.containsKey( geneSetId ) ) {
            // then we should save a backup.
            log.info( "Saving backup version of " + geneSetId );
            oldGeneSets.put( geneSetId, new HashSet( ( Collection ) geneSetToProbeMap.get( geneSetId ) ) );
        }

        geneSetToProbeMap.put( geneSetId, new HashSet( probesForNew ) );
        Collection genes = new HashSet();
        for ( Iterator probe_it = probesForNew.iterator(); probe_it.hasNext(); ) {
            String probe = new String( ( String ) probe_it.next() );
            if ( !probeToGeneSetMap.containsKey( probe ) ) {
                probeToGeneSetMap.put( probe, new HashSet() );
            }
            ( ( Collection ) probeToGeneSetMap.get( probe ) ).add( geneSetId );

            if ( !probeToGeneName.containsKey( probe ) ) continue;
            genes.add( probeToGeneName.get( probe ) );
        }

        for ( Iterator iter = genes.iterator(); iter.hasNext(); ) {
            String gene = ( String ) iter.next();
            if ( !geneToGeneSetMap.containsKey( gene ) ) geneToGeneSetMap.put( gene, new HashSet() );
            ( ( Collection ) geneToGeneSetMap.get( gene ) ).add( geneSetId );
        }
        geneSetToGeneMap.put( geneSetId, genes );

        log.debug( "Added new gene set: " + genes.size() + " genes to gene set id " + geneSetId + " with "
                + probesForNew.size() + " probes" );

        resetSelectedSets();
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
        addGeneSet( id, ( Collection ) oldGeneSets.get( id ) );
    }

    /**
     * @param parents
     */
    public void addGoTermsToGene( String gene, Set parents ) {

        for ( Iterator iter = parents.iterator(); iter.hasNext(); ) {

            String id = ( String ) iter.next();

            if ( !geneSetToGeneMap.containsKey( id ) ) geneSetToGeneMap.put( id, new HashSet() );
            if ( !geneSetToProbeMap.containsKey( id ) ) geneSetToProbeMap.put( id, new HashSet() );

            ( ( Collection ) geneSetToGeneMap.get( id ) ).add( gene );
            ( ( Collection ) geneToGeneSetMap.get( gene ) ).add( id );
            Collection probes = ( Collection ) geneToProbeMap.get( gene );

            ( ( Collection ) geneSetToProbeMap.get( id ) ).addAll( probes );

            for ( Iterator iterator = probes.iterator(); iterator.hasNext(); ) {
                String probe = ( String ) iterator.next();
                ( ( Collection ) probeToGeneSetMap.get( probe ) ).add( id );
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
    public Map geneSetToRedundantMap() {
        return geneSetToRedundantMap;
    }

    /**
     * @param id String class id
     * @return List list of probes in class
     */
    public Collection getClassToProbes( String id ) {
        if ( activeProbes == null ) {
            return ( Collection ) geneSetToProbeMap.get( id );
        }

        assert classToActiveProbeCache != null;
        if ( !classToActiveProbeCache.containsKey( id ) ) {
            Collection finalList = new HashSet();
            Collection startingList = ( Collection ) geneSetToProbeMap.get( id );
            for ( Iterator iter = startingList.iterator(); iter.hasNext(); ) {
                String probe = ( String ) iter.next();
                if ( activeProbes.contains( probe ) ) {
                    finalList.add( probe );
                }
            }
            classToActiveProbeCache.put( id, finalList );
            return finalList;
        }

        return ( Collection ) classToActiveProbeCache.get( id );

    }

    /**
     * Return a collection of all currently active genes.
     * 
     * @return
     */
    public Collection getGenes() {
        if ( activeProbes == null ) return geneToGeneSetMap.keySet();

        if ( genesForActiveProbesCache == null ) {
            Collection finalList = new HashSet();
            for ( Iterator iter = geneToGeneSetMap.keySet().iterator(); iter.hasNext(); ) {
                String gene = ( String ) iter.next();
                Collection probes = this.getGeneProbeList( gene );
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
     * Get a list of the probes that correspond to a particular gene.
     * 
     * @param g String a gene name
     * @return Collection of the probes for gene g
     */
    public Collection getGeneProbeList( String gene ) {
        if ( activeProbes == null ) {
            return ( Collection ) geneToProbeMap.get( gene );
        }

        if ( !geneToActiveProbesCache.containsKey( gene ) ) {
            Collection finalList = new HashSet();
            Collection probes = ( Collection ) geneToProbeMap.get( gene );
            if ( probes == null ) {
                log.debug( "No probes for " + gene );
                return null;
            }
            for ( Iterator iter = probes.iterator(); iter.hasNext(); ) {
                String probe = ( String ) iter.next();
                if ( activeProbes.contains( probe ) ) {
                    finalList.add( probe );
                }
            }
            geneToActiveProbesCache.put( gene, finalList );
            return finalList;
        }
        return ( Collection ) geneToActiveProbesCache.get( gene );
    }

    /**
     * Get a class by an integer index i from the sorted list.
     * 
     * @param i
     * @return
     */
    public String getGeneSetByIndex( int i ) {
        return ( String ) sortedGeneSets.get( i );
    }

    // /**
    // * @return Returns the classToGeneMap.
    // */
    // public Map getGeneSetToGeneMap() {
    // return geneSetToGeneMap;
    // }

    /**
     * 
     */
    public Collection getActiveGeneSetGenes( String geneSetId ) {
        if ( activeProbes == null ) return ( Collection ) this.geneSetToGeneMap.get( geneSetId );

        if ( !geneSetActiveGenesCache.containsKey( geneSetId ) ) {
            Collection finalList = new HashSet();
            Collection genes = ( Collection ) geneSetToGeneMap.get( geneSetId );
            for ( Iterator iter = genes.iterator(); iter.hasNext(); ) {
                String gene = ( String ) iter.next();
                Collection probes = ( Collection ) geneToProbeMap.get( gene );
                if ( probes == null ) continue;
                for ( Iterator iterator = probes.iterator(); iterator.hasNext(); ) {
                    String probe = ( String ) iterator.next();
                    if ( activeProbes.contains( probe ) ) {
                        finalList.add( gene );
                        break;
                    }
                }
            }
            geneSetActiveGenesCache.put( geneSetId, finalList );
            return finalList;
        }
        return ( Collection ) geneSetActiveGenesCache.get( geneSetId );
    }

    /**
     * @param geneSetId
     * @return
     */
    public Collection getGeneSetProbes( String geneSetId ) {
        if ( activeProbes == null ) return ( Collection ) this.geneSetToProbeMap.get( geneSetId );

        if ( !geneSetActiveProbesCache.containsKey( geneSetId ) ) {
            Collection finalList = new HashSet();
            Collection probes = ( Collection ) geneSetToProbeMap.get( geneSetId );
            if ( probes == null ) return finalList;
            for ( Iterator iter = probes.iterator(); iter.hasNext(); ) {
                String probe = ( String ) iter.next();
                if ( activeProbes.contains( probe ) ) {
                    finalList.add( probe );
                }
            }
            geneSetActiveProbesCache.put( geneSetId, finalList );
            return finalList;
        }
        return ( Collection ) geneSetActiveProbesCache.get( geneSetId );
    }

    /**
     * Get the gene sets a gene belongs to.
     * 
     * @param gene
     * @return
     */
    public Collection getGeneGeneSets( String gene ) {
        return ( Collection ) this.geneToGeneSetMap.get( gene );
    }

    /**
     * Get the description for a gene.
     * 
     * @param p String
     * @return String
     */
    public String getProbeDescription( String p ) {
        return ( String ) probeToDescription.get( p );
    }

    /**
     * Get the gene that a probe belongs to.
     * 
     * @param p String
     * @return String
     */
    public String getProbeGeneName( String p ) {
        return ( String ) probeToGeneName.get( p );
    }

    /**
     * @return Map
     */
    public Map getProbeToGeneMap() {
        return probeToGeneName;
    }

    /**
     * @deprecated
     */
    public Map getGeneToProbeMap() {
        return geneToProbeMap;
    }

    /**
     * @return Map
     */
    public Map getProbeToGeneSetMap() {
        return probeToGeneSetMap;
    }

    /**
     * @return the list of selected probes. Note that selected probes are distinct from active probes. Selected probes
     *         is more transient.
     */
    public List getSelectedProbes() {
        return selectedProbes;
    }

    /**
     * @return list of selected gene sets.
     */
    public List getSelectedSets() {
        return selectedSets;
    }

    /**
     * Redefine a class.
     * 
     * @param classId String class to be modified
     * @param probesForNew Collection current user-defined list of members. The gene set is recreated to look like this
     *        one.
     */
    public void modifyGeneSet( String classId, Collection probesForNew ) {
        if ( !geneSetToProbeMap.containsKey( classId ) ) {
            log.warn( "No such class to modify: " + classId );
            return;
        }

        log.debug( "Saving backup version of " + classId + ", replacing with new version that has "
                + probesForNew.size() + " probes." );
        oldGeneSets.put( classId, new HashSet( ( Collection ) geneSetToProbeMap.get( classId ) ) );

        removeClassFromMaps( classId );
        addGeneSet( classId, probesForNew );
    }

    /**
     * Compute how many genes have Gene set annotations.
     * 
     * @return
     */
    public int numAnnotatedGenes() {
        int count = 0;
        for ( Iterator iter = geneToGeneSetMap.keySet().iterator(); iter.hasNext(); ) {
            Collection element = ( Collection ) geneToGeneSetMap.get( iter.next() );
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
     * Get a collection of all (active) gene sets.
     * 
     * @return
     */
    public Collection getGeneSets() {
        log.debug( "Entering getGeneSets" );
        if ( activeProbes == null ) return geneSetToGeneMap.keySet();

        if ( activeGeneSetCache == null ) {
            Collection finalSet = new HashSet();
            for ( Iterator iter = geneSetToGeneMap.keySet().iterator(); iter.hasNext(); ) {
                String geneSet = ( String ) iter.next();
                Collection probes = getClassToProbes( geneSet );
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
     * Get the number of gene sets currently available.
     * 
     * @return
     */
    public int numGeneSets() {
        return this.getGeneSets().size();
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
     * @param id
     * @return
     */
    public int numGenesInGeneSet( String id ) {
        if ( !geneSetToGeneMap.containsKey( id ) ) {
            return 0;
        }
        return ( ( Collection ) geneSetToGeneMap.get( id ) ).size();
    }

    /**
     * Get how many probes point to the same gene. This is like the old "numReplicates".
     * 
     * @param g
     * @return
     */
    public int numProbesForGene( String g ) {
        if ( !geneToProbeMap.containsKey( g ) ) return 0;
        if ( activeProbes == null ) return ( ( Collection ) geneToProbeMap.get( g ) ).size();

        return this.getGeneProbeList( g ).size();
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
        if ( activeProbes == null ) return ( ( Collection ) geneSetToProbeMap.get( id ) ).size();

        int result = 0;
        Collection startingList = ( Collection ) geneSetToProbeMap.get( id );
        for ( Iterator iter = startingList.iterator(); iter.hasNext(); ) {
            String probe = ( String ) iter.next();
            if ( activeProbes.contains( probe ) ) {
                result++;
            }
        }
        return result;
    }

    public int numProbesInGeneSet( String id ) {
        if ( !geneSetToProbeMap.containsKey( id ) ) {
            log.debug( "No such gene set " + id );
            return 0;
        }
        return ( ( Collection ) this.geneSetToProbeMap.get( id ) ).size();
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
        for ( Iterator iter = probeToGeneName.keySet().iterator(); iter.hasNext(); ) {
            String probe = ( String ) iter.next();
            String gene = ( String ) probeToGeneName.get( probe );
            String desc = getProbeDescription( probe );
            out.write( probe + "\t" + gene + "\t" + desc + "\t" );
            Collection geneSets = ( Collection ) probeToGeneSetMap.get( probe );

            for ( Iterator iterator = geneSets.iterator(); iterator.hasNext(); ) {
                String element = ( String ) iterator.next();
                out.write( element + "|" );
            }
            out.write( "\n" );
        }
    }

    /**
     * Remove a gene set (class) from all the maps that reference it.
     * <p>
     * 
     * @param id
     */
    public void removeClassFromMaps( String id ) {
        if ( geneSetToProbeMap.containsKey( id ) ) {
            for ( Iterator pit = ( ( Collection ) geneSetToProbeMap.get( id ) ).iterator(); pit.hasNext(); ) {
                String probe = ( String ) pit.next();
                if ( probeToGeneSetMap.containsKey( probe )
                        && ( ( Collection ) probeToGeneSetMap.get( probe ) ).contains( id ) ) {
                    if ( !( ( Collection ) probeToGeneSetMap.get( probe ) ).remove( id ) ) {
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
        selectedProbes = new Vector( probeToGeneName.keySet() );
    }

    /**
     * Set the selected gene set to be the entire set.
     */
    public void resetSelectedSets() {
        selectedSets = new Vector( geneSetToProbeMap.keySet() );
    }

    /**
     * @return the number of probes currently on the 'selected' list.
     */
    public int numSelectedProbes() {
        return selectedProbes.size();
    }

    /**
     * @return the number of sets currently on the 'selected' list.
     */
    public int selectedSets() {
        return selectedSets.size();
    }

    /**
     * Create a selected probes list based on a search string.
     * 
     * @param searchOn A string to be searched.
     */
    public void selectProbesBySearch( String searchOn ) {

        String searchOnUp = searchOn.toUpperCase();
        resetSelectedProbes();
        Set removeUs = new HashSet();
        for ( Iterator it = probeToGeneName.keySet().iterator(); it.hasNext(); ) {
            String probe = ( String ) it.next();

            String candidate = ( ( String ) probeToGeneName.get( ( probe ) ) ).toUpperCase();

            // look in descriptions.
            String candidateD = ( ( String ) probeToDescription.get( ( probe ) ) ).toUpperCase();

            if ( !candidate.startsWith( searchOnUp ) && candidateD.indexOf( searchOnUp ) < 0 ) {
                removeUs.add( probe );
            }

        }

        for ( Iterator it = removeUs.iterator(); it.hasNext(); ) {
            selectedProbes.remove( it.next() );
        }
    }

    /**
     * Select a given set of gene sets.
     * 
     * @param selectedGeneSets
     */
    public void setSelectedSets( Collection selectedGeneSets ) {
        this.selectedSets.clear();
        this.selectedSets.addAll( selectedGeneSets );
    }

    /**
     * @param searchOn
     * @param goData
     */
    public void selectSets( String searchOn, GONames goData ) {

        String searchOnUp = searchOn.toUpperCase();
        resetSelectedSets();
        Set removeUs = new HashSet();
        for ( Iterator it = geneSetToProbeMap.keySet().iterator(); it.hasNext(); ) {
            String candidate = ( String ) it.next();

            // look in the name too
            if ( goData.getNameForId( candidate ) == null ) continue;
            String candidateN = goData.getNameForId( candidate ).toUpperCase();

            if ( !candidate.toUpperCase().startsWith( searchOnUp ) && candidateN.indexOf( searchOnUp ) < 0 ) {
                removeUs.add( candidate );
            }
        }

        for ( Iterator it = removeUs.iterator(); it.hasNext(); ) {
            selectedSets.remove( it.next() );
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
        Set removeUs = new HashSet();

        for ( Iterator it = geneSetToProbeMap.keySet().iterator(); it.hasNext(); ) {
            boolean found = false;
            String candidateGeneSet = ( String ) it.next();
            Collection probes = ( Collection ) geneSetToProbeMap.get( candidateGeneSet );

            for ( Iterator iter = probes.iterator(); iter.hasNext(); ) {
                String candidate = ( String ) iter.next();
                if ( candidate.toUpperCase().startsWith( searchOnUp ) ) {
                    found = true;
                    log.debug( "Found " + candidate + " in " + candidateGeneSet );
                    break;
                }
            }

            if ( found ) continue;

            Collection genes = ( Collection ) geneSetToGeneMap.get( candidateGeneSet );
            for ( Iterator iter = genes.iterator(); iter.hasNext(); ) {
                String candidate = ( String ) iter.next();
                if ( candidate.toUpperCase().startsWith( searchOnUp ) ) {
                    found = true;
                    log.debug( "Found " + candidate + " in " + candidateGeneSet );
                    break;
                }
            }

            if ( !found ) removeUs.add( candidateGeneSet );

        }

        for ( Iterator it = removeUs.iterator(); it.hasNext(); ) {
            selectedSets.remove( it.next() );
        }
    }

    /**
     * Sort the gene sets, filling out the sortedGeneSets. This should be called after any changes have been made to the
     * classToProbeMap. The sort is just in order of id.
     */
    public void sortGeneSets() {

        if ( geneSetToProbeMap.size() == 0 ) {
            throw new IllegalStateException( "Could not sort because there are no gene sets in the classToProbeMap" );
        }

        if ( sortedGeneSets == null ) {
            sortedGeneSets = new Vector();
        }

        List vec = new Vector( geneSetToProbeMap.keySet() );
        Collections.sort( vec );
        for ( Iterator iter = vec.iterator(); iter.hasNext(); ) {
            sortedGeneSets.add( iter.next() );
        }
    }

    /**
     * @return
     */
    public List sortGeneSetsBySize() {

        List sets = new Vector();
        for ( Iterator iter = geneSetToGeneMap.keySet().iterator(); iter.hasNext(); ) {
            String name = ( String ) iter.next();
            sets.add( new GeneSet( name, ( Set ) geneSetToGeneMap.get( name ) ) );
        }

        Collections.sort( sets, new ClassSizeComparator() );

        List returnVal = new Vector();
        for ( Iterator iter = sets.iterator(); iter.hasNext(); ) {
            returnVal.add( ( ( GeneSet ) iter.next() ).getName() );
        }

        return returnVal;
    }

    /**
     * @return
     */
    public TableModel toTableModel() {
        return new AbstractTableModel() {
            private String[] columnNames = { "Probe", "Gene", "Description" };

            public int getColumnCount() {
                return 3;
            }

            public String getColumnName( int i ) {
                return columnNames[i];
            }

            public int getRowCount() {
                return selectedProbes.size();
            }

            public Object getValueAt( int i, int j ) {

                String probeid = ( String ) selectedProbes.get( i );
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
     * @param probes
     * @param geneSymbols
     * @param geneNames
     * @param goTerms
     */
    private void checkValidData( List probes, List geneSymbols, List geneNames, List goTerms ) {
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
        Object test = goTerms.get( 0 );
        if ( !( test instanceof Collection ) ) {
            throw new IllegalArgumentException( "GO terms must be a list of java.util.Collection's" );
        }
        if ( !( probes.get( 0 ) instanceof String ) ) {
            throw new IllegalArgumentException( "Probes must be a list of java.lang.String's" );
        }
        if ( !( geneSymbols.get( 0 ) instanceof String ) ) {
            throw new IllegalArgumentException( "Gene symbols must be a list of java.lang.String's" );
        }
        if ( geneNames != null && !( geneNames.get( 0 ) instanceof String ) ) {
            throw new IllegalArgumentException( "Gene names must be a list of java.lang.String's" );
        }
    }

    /*******************************************************************************************************************
     * Private or protected methods
     ******************************************************************************************************************/

    /**
     * @param limit
     * @param header
     * @param pattern
     */
    private int findField( String header, String pattern ) throws IOException {
        String[] fields = header.split( "," );
        if ( fields == null || fields.length == 0 ) throw new IllegalArgumentException( "No header!" );
        for ( int i = 0; i < fields.length; i++ ) {
            if ( fields[i].replaceAll( "\"", "" ).compareToIgnoreCase( pattern ) == 0 ) {
                return i;
            }
        }
        throw new IOException( "Couldn't find '" + pattern + "' field in header" );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyBpIndex( String header ) throws IOException {
        String pattern = "Gene Ontology Biological Process";
        return findField( header, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyCcIndex( String header ) throws IOException {
        String pattern = "Gene Ontology Cellular Component";
        return findField( header, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyGeneNameIndex( String header ) throws IOException {
        String pattern = "Gene Title";
        return findField( header, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyGeneSymbolIndex( String header ) throws IOException {
        String pattern = "Gene Symbol";
        return findField( header, pattern );
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyMfIndex( String header ) throws IOException {
        String pattern = "Gene Ontology Molecular Function";
        return findField( header, pattern );
    }

    /**
     * @param header
     * @return
     */
    private int getAffyNumFields( String header ) {
        String[] fields = header.split( "," );
        return fields.length;
    }

    /**
     * @throws IOException
     * @param header
     * @return
     */
    private int getAffyProbeIndex( String header ) throws IOException {
        String pattern = "Probe Set ID";
        return findField( header, pattern );
    }

    /**
     * Fill in the classToGeneMap with information from the classToProbeMap.
     * 
     * @return mapping of gene sets to genes.
     */
    private Map makeClassToGeneMap() {
        Map gsToGeneMap = new HashMap();
        for ( Iterator iter = geneSetToProbeMap.keySet().iterator(); iter.hasNext(); ) {
            String geneSetId = ( String ) iter.next();
            Collection probesInSet = ( Collection ) geneSetToProbeMap.get( geneSetId );

            Set genesInSet = new HashSet();
            for ( Iterator biter = probesInSet.iterator(); biter.hasNext(); ) {
                String probe = ( String ) biter.next();
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
        if ( !go.startsWith( "GO:" ) ) {
            int needZeros = 7 - go.length();
            for ( int j = 0; j < needZeros; j++ ) {
                go = "0" + go;
            }
            go = "GO:" + go;
        }
        return go;
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
            ( ( Collection ) probeToGeneSetMap.get( probe ) ).add( go );

            if ( !geneSetToProbeMap.containsKey( go ) ) {
                geneSetToProbeMap.put( go, new HashSet() );
            }
            ( ( Collection ) geneSetToProbeMap.get( go ) ).add( probe );
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

        Set removeUs = new HashSet();
        for ( Iterator it = geneSetToProbeMap.keySet().iterator(); it.hasNext(); ) {
            String id = ( String ) it.next();
            if ( numActiveProbesInGeneSet( id ) < lowThreshold || numActiveGenesInGeneSet( id ) < lowThreshold
                    || numActiveProbesInGeneSet( id ) > highThreshold || numActiveGenesInGeneSet( id ) > highThreshold ) {
                removeUs.add( id );
            }
        }

        for ( Iterator it = removeUs.iterator(); it.hasNext(); ) {
            String id = ( String ) it.next();
            removeClassFromMaps( id );
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

    /**
     * @param probe
     */
    private void removeProbeFromMaps( String probe ) {
        if ( probeToGeneName.containsKey( probe ) ) {
            String gene = ( String ) probeToGeneName.get( probe );
            probeToGeneName.remove( probe );
            if ( geneToProbeMap.containsKey( gene ) ) {
                ( ( Collection ) geneToProbeMap.get( gene ) ).remove( probe );
            }
        }
        if ( probeToGeneSetMap.containsKey( probe ) ) {
            Iterator cit = ( ( Collection ) probeToGeneSetMap.get( probe ) ).iterator();
            while ( cit.hasNext() ) {
                String geneSet = ( String ) cit.next();
                if ( geneSetToProbeMap.containsKey( geneSet ) ) {
                    ( ( Collection ) geneSetToProbeMap.get( geneSet ) ).remove( probe );
                }
            }
            if ( probeToGeneSetMap.remove( probe ) == null ) {
                System.err.println( "Could not remove " + probe + " from probeToClassMap" );
            }
        }
        if ( probeToDescription.containsKey( probe ) ) probeToDescription.remove( probe );
    }

    /**
     * 
     */
    private void setUpdataStructures() {
        probeToGeneSetMap = new LinkedHashMap();
        geneSetToProbeMap = new LinkedHashMap();
        probeToGeneName = new HashMap();
        probeToDescription = new HashMap();
        geneToProbeMap = new HashMap();
        geneToGeneSetMap = new HashMap();
        geneSetToRedundantMap = new HashMap();
        oldGeneSets = new HashMap();
        classToActiveProbeCache = new HashMap();
        geneToActiveProbesCache = new HashMap();
        geneSetActiveGenesCache = new HashMap();
        geneSetActiveProbesCache = new HashMap();
    }

    /**
     * @param probeIds
     * @param probe
     * @param geneSymbol
     */
    private void storeProbeAndGene( Collection probeIds, String probe, String geneSymbol ) {
        probeToGeneName.put( probe.intern(), geneSymbol.intern() );

        // create the list if need be.
        if ( geneToProbeMap.get( geneSymbol ) == null ) {
            geneToProbeMap.put( geneSymbol.intern(), new HashSet() );
        }
        ( ( Collection ) geneToProbeMap.get( geneSymbol ) ).add( probe.intern() );

        probeIds.add( probe );
        if ( !probeToGeneSetMap.containsKey( probe ) ) {
            probeToGeneSetMap.put( probe.intern(), new HashSet() );
        }
        geneToGeneSetMap.put( geneSymbol, probeToGeneSetMap.get( probe ) );
    }

    /**
     * @param bis
     * @param activeGenes
     * @throws IOException
     */
    protected void read( InputStream bis, Set activeGenes ) throws IOException {
        log.debug( "Entering GeneAnnotations.read" );
        if ( bis == null ) {
            throw new IOException( "Inputstream was null" );
        }

        if ( bis.available() == 0 ) {
            throw new IOException( "No bytes to read from the annotation file." );
        }

        BufferedReader dis = new BufferedReader( new InputStreamReader( bis ) );
        Collection probeIds = new ArrayList();
        String classIds = null;

        // loop through rows. Makes hash map of probes to go, and map of go to
        // probes.
        int n = 0;
        String line = "";
        tick();
        while ( ( line = dis.readLine() ) != null ) {

            if ( line.startsWith( "#" ) ) continue;

            String[] tokens = line.split( "\t" );
            int length = tokens.length;
            if ( length < 2 ) continue;

            String probe = tokens[0].intern();
            String gene = tokens[1].intern();
            if ( activeGenes != null && !activeGenes.contains( gene ) ) {
                continue;
            }

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
     * @param classIds
     * @param probe
     */
    private void extractPipeDelimitedGoIds( String classIds, String probe ) {
        String[] classIdAry = pipePattern.split( classIds );
        if ( classIdAry.length == 0 ) return;

        Collection probeCol = ( Collection ) probeToGeneSetMap.get( probe );
        probeCol.addAll( Arrays.asList( classIdAry ) );

        for ( int i = 0; i < classIdAry.length; i++ ) {
            String go = classIdAry[i].intern();

            if ( !geneSetToProbeMap.containsKey( go ) ) {
                geneSetToProbeMap.put( go, new HashSet() );
            }
            ( ( Collection ) geneSetToProbeMap.get( go ) ).add( probe );
        }
    }

    /**
     * @param bis
     * @param object
     */
    protected void readAffyCsv( InputStream bis, Set activeGenes ) throws IOException {
        if ( bis == null ) {
            throw new IOException( "Inputstream was null" );
        }
        BufferedReader dis = new BufferedReader( new InputStreamReader( bis ) );
        Collection probeIds = new ArrayList();
        String classIds = null;

        String header = dis.readLine();
        int numFields = getAffyNumFields( header );
        int probeIndex = getAffyProbeIndex( header );
        int goBpIndex = getAffyBpIndex( header );
        int goCcIndex = getAffyCcIndex( header );
        int goMfIndex = getAffyMfIndex( header );
        int geneNameIndex = getAffyGeneNameIndex( header );
        int geneSymbolIndex = getAffyGeneSymbolIndex( header );

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

            String[] fields = StringUtil.csvSplit( line );
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

            classIds = " // " + fields[goBpIndex] + " // " + fields[goMfIndex] + " // " + fields[goCcIndex];
            String[] goinfo = classIds.split( "/+" );
            for ( int i = 0; i < goinfo.length; i++ ) {
                String goi = goinfo[i].intern();
                parseGoTerm( probe, pat, goi );
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
     * 
     */
    private void tick() {
        tick++;
    }

    public int ticks() {
        return tick;
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
    }

    /**
     * @return
     */
    public int numProbes() {
        assert activeProbes != null;
        return this.activeProbes.size();
    }

    /**
     * @return Returns the activeProbes.
     */
    public Collection getActiveProbes() {
        return this.activeProbes;
    }

    /**
     * @param probeId
     * @return
     */
    public boolean hasProbe( String probeId ) {
        return this.probeToGeneName.containsKey( probeId );
    }

    /**
     * @param geneSymbol
     * @return
     */
    public Collection getGeneProbes( String geneSymbol ) {
        if ( activeProbes == null ) return ( Collection ) this.geneToProbeMap.get( geneSymbol );

        Collection finalList = new HashSet();
        Collection probes = ( Collection ) geneToProbeMap.get( geneSymbol );
        for ( Iterator iter = probes.iterator(); iter.hasNext(); ) {
            String probe = ( String ) iter.next();
            if ( activeProbes.contains( probe ) ) {
                finalList.add( probe );
            }
        }
        return finalList;

    }

    /**
     * @param probe
     * @return
     */
    public String probeToGene( String probe ) {
        return ( String ) this.probeToGeneName.get( probe );
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
     * @param set
     */
    public void setActiveProbes( Collection set ) {
        this.activeProbes = set;
        activeProbesDirty();
    }

}

class ClassSizeComparator implements Comparator {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Object o1, Object o2 ) {
        GeneSet a = ( GeneSet ) o1;
        GeneSet b = ( GeneSet ) o2;

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
    private Set items;
    private String name;

    public GeneSet( String name, Set items ) {
        this.name = name;
        this.items = items;
    }

    /**
     * @return Returns the items.
     */
    public Set getItems() {
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
    public void setItems( Set items ) {
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