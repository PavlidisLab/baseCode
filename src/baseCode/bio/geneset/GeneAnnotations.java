package baseCode.bio.geneset;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import baseCode.util.FileTools;
import baseCode.util.StatusViewer;

/**
 * Reads tab-delimited file to create maps of probes to classes, classes to probes, probes to genes, genes to probes.
 * <p>
 * Maintains the following important data structures, all derived from the input file:
 * 
 * <pre>
 * 
 *  
 *   
 *    
 *     
 *      
 *       
 *        
 *         
 *          
 *                                                   probe-&gt;Classes -- each value is a Set of the Classes that a probe belongs to.
 *                                                   Classes-&gt;probe -- each value is a Set of the probes that belong to a class
 *                                                   probe-&gt;gene -- each value is the gene name corresponding to the probe.
 *                                                   gene-&gt;list of probes -- each value is a list of probes corresponding to a gene
 *                                                   probe-&gt;description -- each value is a text description of the probe (actually...of the gene)
 *               
 *           
 *          
 *         
 *        
 *       
 *      
 *     
 *    
 *   
 *  
 * </pre>
 * 
 * <p>
 * Copyright (c) 2004 Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @author Shamhil Merchant
 * @author Homin Lee
 * @version $Id$
 */

public class GeneAnnotations {

   /**
    * The maximum size of gene sets ever considered.
    */
   private static final int PRACTICAL_MAXIMUM_GENESET_SIZE = 1000;

   /**
    * The minimum size of a 'set' of genes.
    */
   private static final int ABSOLUTE_MINIMUM_GENESET_SIZE = 2;

   private Map probeToGeneSetMap; //stores probe->Classes map todo why are contents an arraylist, should be a set?
   private Map geneSetToProbeMap; //stores Classes->probes map
   private Map probeToGeneName;
   private Map probeToDescription;
   private Map geneToProbeList;
   private Map geneToGeneSetMap;
   private Map geneSetToGeneMap; //stores Classes->genes map. use to get a list
   // of classes.

   private Vector sortedGeneSets;
   private Map geneSetToRedundantMap;
   Vector selectedProbes;
   private Vector selectedSets;

   private StatusViewer messenger;

   /**
    * This is for creating GeneAnnotations by reading from a file
    * 
    * @param filename String
    * @param messenger StatusViewer to print status updates to.
    * @throws IOException
    */
   public GeneAnnotations( String filename, StatusViewer messenger )
         throws IOException {

      setUpDataStructures();
      this.messenger = messenger;

      this.read( filename );
      setUp();
   }

   /**
    * This is for creating GeneAnnotations by pruning a copy.
    * 
    * @param geneData GeneAnnotations copy to prune from
    * @param activeProbes Set only include these probes
    */
   public GeneAnnotations( GeneAnnotations geneData, Set activeProbes ) {

      // FIXME - shallow copies here could cause problems.

      // CAREFUL this is a shallow copy! This is okay?
      probeToGeneSetMap = new LinkedHashMap( geneData.probeToGeneSetMap );

      // make a deep copy of the classToProbeMap, which is a map of sets. Shallow copy is BAD.
      this.geneSetToProbeMap = new LinkedHashMap();
      for ( Iterator iter = geneData.geneSetToProbeMap.keySet().iterator(); iter
            .hasNext(); ) {
         String key = ( String ) iter.next();
         this.geneSetToProbeMap.put( key, new ArrayList(
               ( ArrayList ) geneData.geneSetToProbeMap.get( key ) ) );
      }

      probeToGeneName = new HashMap( geneData.probeToGeneName ); // shallow copy, okay
      probeToDescription = new HashMap( geneData.probeToDescription ); // shallow copy, okay
      geneToProbeList = new HashMap( geneData.geneToProbeList ); // shallow copy, okay?
      geneToGeneSetMap = new HashMap( geneData.geneToGeneSetMap ); // shallow copy, okay?
      geneSetToRedundantMap = new HashMap( geneData.geneSetToRedundantMap );

      Vector allProbes = new Vector( probeToGeneName.keySet() );
      for ( Iterator iter = allProbes.iterator(); iter.hasNext(); ) {
         String probe = ( String ) iter.next();
         if ( !activeProbes.contains( probe ) ) { // remove probes not in data set.
            removeProbeFromMaps( probe );
         }
      }
      setUp(); //creates the classToGene map.

      //      System.err.println( "Orig: GO:0019058 has probes: "
      //            + geneData.numProbesInGeneSet( "GO:0019058" ) );
      //      System.err.println( "New: GO:0019058 has probes: "
      //            + this.numProbesInGeneSet( "GO:0019058" ) );
   }

   /**
    * Make a new GeneAnnotations that only includes the probes in the parameter 'probes'.
    * 
    * @param stream
    * @param activeGenes Only genes in this set are left.
    * @throws IOException
    */
   public GeneAnnotations( InputStream stream, Set activeGenes,
         StatusViewer messenger ) throws IOException {
      this.messenger = messenger;
      setUpDataStructures();
      this.read( stream, activeGenes );
      setUp();
   }

   /**
    * @param fileName
    */
   public GeneAnnotations( String fileName, Set activeGenes,
         StatusViewer messenger ) throws IOException {
      this.messenger = messenger;
      FileInputStream fis = new FileInputStream( fileName );
      BufferedInputStream bis = new BufferedInputStream( fis );
      setUpDataStructures();
      this.read( bis, activeGenes );
      setUp();
   }

   /**
    * @return Map
    */
   public Map getProbeToGeneMap() {
      return probeToGeneName;
   }

   /**
    * @return Map
    */
   public Map getGeneToProbeList() {
      return geneToProbeList;
   }

   /**
    * @return Map
    */
   public Map getGeneSetToProbeMap() {
      return geneSetToProbeMap;
   }

   /**
    * @param id String class id
    * @return ArrayList list of probes in class
    */
   public ArrayList getClassToProbes( String id ) {
      return ( ArrayList ) geneSetToProbeMap.get( id );
   }

   /**
    * Sort the gene sets, filling out the sortedGeneSets. This should be called after any changes have been made to the
    * classToProbeMap. The sort is just in order of id.
    */
   public void sortGeneSets() {

      if ( geneSetToProbeMap.size() == 0 ) {
         throw new IllegalStateException(
               "Could not sort because there are no gene sets in the classToProbeMap" );
      }

      if ( sortedGeneSets == null ) {
         sortedGeneSets = new Vector();
      }

      Vector vec = new Vector( geneSetToProbeMap.keySet() );
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
      for ( Iterator iter = getGeneSetToGeneMap().keySet().iterator(); iter
            .hasNext(); ) {
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
    * @return Map
    */
   public Map getProbeToGeneSetMap() {
      return probeToGeneSetMap;
   }

   /**
    * @return Map
    */
   public Map geneSetToRedundantMap() {
      return geneSetToRedundantMap;
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
    * Get the description for a gene.
    * 
    * @param p String
    * @return String
    */
   public String getProbeDescription( String p ) {
      return ( String ) probeToDescription.get( p );
   }

   /**
    * Get a list of the probes that correspond to a particular gene.
    * 
    * @param g String a gene name
    * @return ArrayList list of the probes for gene g
    */
   public ArrayList getGeneProbeList( String g ) {
      return ( ArrayList ) geneToProbeList.get( g );
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
    * Get how many probes point to the same gene. This is like the old "numReplicates".
    * 
    * @param g
    * @return
    */
   public int numProbesForGene( String g ) {
      if ( !geneToProbeList.containsKey( g ) ) return 0;
      return ( ( ArrayList ) geneToProbeList.get( g ) ).size();
   }

   /**
    * Get the number of classes. This is computed from the sortedGeneSets.
    * 
    * @return
    */
   public int numGeneSets() {
      if ( geneSetToGeneMap == null ) {
         throw new IllegalStateException( "classToGeneMap was null" );
      }
      return geneSetToGeneMap.size();
   }

   /**
    * How many genes are in the file?
    */
   public int numGenes() {
      return geneToProbeList.size();
   }

   /**
    * Get the number of probes in a gene set, identified by id.
    * 
    * @param id String a class id
    * @return int number of probes in the class
    */
   public int numProbesInGeneSet( String id ) {
      if ( !geneSetToProbeMap.containsKey( id ) ) {
         return 0;
      }
      //      System.err.println( "GO:0019058 has probes: "
      //            + ( ( ArrayList ) classToProbeMap.get( "GO:0019058" ) ).size() );
      return ( ( ArrayList ) geneSetToProbeMap.get( id ) ).size();
   }

   /**
    * Get the number of genes in a gene set, identified by id.
    * 
    * @param id String a class id
    * @return int number of genes in the class
    */
   public int numGenesInGeneSet( String id ) {
      if ( !geneSetToGeneMap.containsKey( id ) ) {
         return 0;
      }
      return ( ( Set ) geneSetToGeneMap.get( id ) ).size();
   }

   /**
    * Add a class
    * 
    * @param id String class to be added
    * @param probesForNew ArrayList user-defined list of members.
    */
   public void addClass( String id, ArrayList probesForNew ) {
      geneSetToProbeMap.put( id, probesForNew );

      Iterator probe_it = probesForNew.iterator();
      while ( probe_it.hasNext() ) {
         String probe = new String( ( String ) probe_it.next() );
         ( ( ArrayList ) probeToGeneSetMap.get( probe ) ).add( id );
      }

      Set genes = new HashSet();
      Iterator probe_it2 = probesForNew.iterator();
      while ( probe_it2.hasNext() ) {
         genes.add( probeToGeneName.get( probe_it2.next() ) );
      }
      geneSetToGeneMap.put( id, genes );

      geneToGeneSetMap.put( id, probeToGeneSetMap.get( id ) );

      resetSelectedSets();
   }

   /**
    * Redefine a class.
    * 
    * @param classId String class to be modified
    * @param probesForNew ArrayList current user-defined list of members. The "real" version of the class is modified to
    *        look like this one.
    */
   public void modifyClass( String classId, ArrayList probesForNew ) {
      ArrayList orig_probes = ( ArrayList ) geneSetToProbeMap.get( classId );
      Iterator orig_probe_it = orig_probes.iterator();
      while ( orig_probe_it.hasNext() ) {
         String orig_probe = new String( ( String ) orig_probe_it.next() );
         if ( !probesForNew.contains( orig_probe ) ) {
            Set ptc = new HashSet( ( Collection ) probeToGeneSetMap
                  .get( orig_probe ) );
            ptc.remove( classId );
            probeToGeneSetMap.remove( orig_probe );
            probeToGeneSetMap.put( orig_probe, new ArrayList( ptc ) );
         }
      }
      Iterator probe_it = probesForNew.iterator();
      while ( probe_it.hasNext() ) {
         String probe = ( String ) probe_it.next();
         if ( !orig_probes.contains( probe ) ) {
            ( ( ArrayList ) probeToGeneSetMap.get( probe ) ).add( classId );
         }
      }
      geneSetToProbeMap.put( classId, probesForNew );
      resetSelectedSets();
   }

   /**
    * @return
    */
   public TableModel toTableModel() {
      return new AbstractTableModel() {
         private String[] columnNames = {
               "Probe", "Gene", "Description"
         };

         public String getColumnName( int i ) {
            return columnNames[i];
         }

         public int getColumnCount() {
            return 3;
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
    * Create a selected probes list based on a search string.
    * 
    * @param searchOn A string to be searched.
    */
   public void selectProbes( String searchOn ) {

      String searchOnUp = searchOn.toUpperCase();
      resetSelectedProbes();
      Set removeUs = new HashSet();
      for ( Iterator it = probeToGeneName.keySet().iterator(); it.hasNext(); ) {
         String probe = ( String ) it.next();

         String candidate = ( ( String ) probeToGeneName.get( ( probe ) ) )
               .toUpperCase();

         // look in descriptions.
         String candidateD = ( ( String ) probeToDescription.get( ( probe ) ) )
               .toUpperCase();

         if ( !candidate.startsWith( searchOnUp )
               && candidateD.indexOf( searchOnUp ) < 0 ) {
            removeUs.add( probe );
         }

      }

      for ( Iterator it = removeUs.iterator(); it.hasNext(); ) {
         selectedProbes.remove( it.next() );
      }
   }

   /**
    * Set the selected gene set to be the entire set.
    */
   public void resetSelectedProbes() {
      selectedProbes = new Vector( probeToGeneName.keySet() );
   }

   /**
    * @return the list of selected probes.
    */
   public List getSelectedProbes() {
      return selectedProbes;
   }

   /**
    * @return the number of probes currently on the 'selected' list.
    */
   public int selectedProbes() {
      return selectedProbes.size();
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
         String candidateN = goData.getNameForId( candidate ).toUpperCase();

         if ( !candidate.toUpperCase().startsWith( searchOnUp )
               && candidateN.indexOf( searchOnUp ) < 0 ) {
            removeUs.add( candidate );
         }
      }

      for ( Iterator it = removeUs.iterator(); it.hasNext(); ) {
         selectedSets.remove( it.next() );
      }
   }

   /**
    * Set the selected gene set to be the entire set.
    */
   public void resetSelectedSets() {
      selectedSets = new Vector( geneSetToProbeMap.keySet() );
   }

   /**
    * @return list of selected sets.
    */
   public List getSelectedSets() {
      return selectedSets;
   }

   /**
    * @return the number of sets currently on the 'selected' list.
    */
   public int selectedSets() {
      return selectedSets.size();
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
         List geneSets = ( ArrayList ) probeToGeneSetMap.get( probe );

         for ( Iterator iterator = geneSets.iterator(); iterator.hasNext(); ) {
            String element = ( String ) iterator.next();
            out.write( element + "|" );
         }
         out.write( "\n" );
      }
   }

   /**
    * @return Returns the classToGeneMap.
    */
   public Map getGeneSetToGeneMap() {
      return geneSetToGeneMap;
   }

   /**
    * @return Returns the geneToClassMap.
    */
   public Map getGeneToGeneSetMap() {
      return geneToGeneSetMap;
   }

   /**
    * Compute how many genes have Gene set annotations.
    * 
    * @return
    */
   public int numAnnotatedGenes() {
      int count = 0;
      for ( Iterator iter = probeToGeneSetMap.keySet().iterator(); iter.hasNext(); ) {
         List element = ( ArrayList ) probeToGeneSetMap.get( iter.next() );
         if ( element.size() > 0 ) {
            count++;
         }
      }
      return count;
   }

   /********************************************************************************************************************
    * Private or protected methods
    *******************************************************************************************************************/

   /**
    * 
    */
   private void setUpDataStructures() {
      probeToGeneSetMap = new LinkedHashMap();
      geneSetToProbeMap = new LinkedHashMap();
      probeToGeneName = new HashMap();
      probeToDescription = new HashMap();
      geneToProbeList = new HashMap();
      geneToGeneSetMap = new HashMap();
      geneSetToRedundantMap = new HashMap();
   }

   /**
    * Initialize the gene sets and other data structures that needs special handling before use.
    */
   private void setUp() {
      this.geneSetToGeneMap = makeClassToGeneMap();

      GeneSetMapTools.collapseGeneSets( this, messenger );
      prune( ABSOLUTE_MINIMUM_GENESET_SIZE, PRACTICAL_MAXIMUM_GENESET_SIZE );
      resetSelectedProbes();
      resetSelectedSets();
      sortGeneSets();

   }

   /**
    * Remove a gene set (class) from all the maps that reference it.
    * 
    * @param id
    */
   public void removeClassFromMaps( String id ) {
      if ( geneSetToProbeMap.containsKey( id ) ) {
         for ( Iterator pit = ( ( ArrayList ) geneSetToProbeMap.get( id ) )
               .iterator(); pit.hasNext(); ) {
            String probe = ( String ) pit.next();
            if ( probeToGeneSetMap.containsKey( probe )
                  && ( ( ArrayList ) probeToGeneSetMap.get( probe ) )
                        .contains( id ) ) {
               if ( !( ( ArrayList ) probeToGeneSetMap.get( probe ) ).remove( id ) ) {
                  System.err.println( "Couldn't remove " + id
                        + " from probe to class map for" + probe );
               }
            }
         }
         if ( geneSetToProbeMap.remove( id ) == null )
               System.err.println( "Couldn't remove " + id
                     + " from classToProbeMap" );

         if ( geneSetToGeneMap.remove( id ) == null )
               System.err.println( "Couldn't remove " + id
                     + " from classToGeneMap" );
      }
      if ( geneSetToRedundantMap.containsKey( id ) )
            geneSetToRedundantMap.remove( id );
   }

   /**
    * @param probe
    */
   private void removeProbeFromMaps( String probe ) {
      if ( probeToGeneName.containsKey( probe ) ) {
         String gene = ( String ) probeToGeneName.get( probe );
         probeToGeneName.remove( probe );
         if ( geneToProbeList.containsKey( gene ) ) {
            ( ( ArrayList ) geneToProbeList.get( gene ) ).remove( probe );
         }
      }
      if ( probeToGeneSetMap.containsKey( probe ) ) {
         Iterator cit = ( ( ArrayList ) probeToGeneSetMap.get( probe ) )
               .iterator();
         while ( cit.hasNext() ) {
            String geneSet = ( String ) cit.next();
            if ( geneSetToProbeMap.containsKey( geneSet ) ) {
               ( ( ArrayList ) geneSetToProbeMap.get( geneSet ) )
                     .remove( probe );
            }
         }
         if ( probeToGeneSetMap.remove( probe ) == null ) {
            System.err.println( "Could not remove " + probe
                  + " from probeToClassMap" );
         }
      }
      if ( probeToDescription.containsKey( probe ) )
            probeToDescription.remove( probe );
   }

   /**
    * Fill in the classToGeneMap with information from the classToProbeMap.
    * 
    * @return mapping of gene sets to genes.
    */
   private Map makeClassToGeneMap() {
      Map gsToGeneMap = new HashMap();
      for ( Iterator iter = geneSetToProbeMap.keySet().iterator(); iter
            .hasNext(); ) {
         String geneSetId = ( String ) iter.next();
         List probesInSet = ( ArrayList ) geneSetToProbeMap.get( geneSetId );

         Set genesInSet = new HashSet();
         for ( Iterator biter = probesInSet.iterator(); biter.hasNext(); ) {
            String probe = ( String ) biter.next();
            genesInSet.add( probeToGeneName.get( probe ) );
         }
         gsToGeneMap.put( geneSetId, genesInSet );
      }
      return gsToGeneMap;
   }

   private void read( InputStream bis ) throws IOException {
      this.read( bis, null );
   }

   private void read( InputStream bis, Set activeGenes ) throws IOException {
      if ( bis == null ) {
         throw new IOException( "Inputstream was null" );
      }

      BufferedReader dis = new BufferedReader( new InputStreamReader( bis ) );
      ArrayList probeIds = new ArrayList();
      String classIds = null;

      // loop through rows. Makes hash map of probes to go, and map of go to
      // probes.
      int n = 0;
      String line = "";

      while ( ( line = dis.readLine() ) != null ) {
         if ( line.startsWith( "#" ) ) continue;
         StringTokenizer st = new StringTokenizer( line, "\t" );

         if ( !st.hasMoreTokens() ) {
            continue; // blank line
         }

         String probe = st.nextToken().intern();

         /* read gene name */
         if ( !st.hasMoreTokens() ) {
            continue; // no gene name or anything else.
         }

         String group = st.nextToken().intern();
         
         if ( activeGenes != null && !activeGenes.contains( group ) ) {
            continue;
         }

         probeToGeneName.put( probe.intern(), group.intern() );

         // create the list if need be.
         if ( geneToProbeList.get( group ) == null ) {
            geneToProbeList.put( group.intern(), new ArrayList() );
         }
         ( ( ArrayList ) geneToProbeList.get( group ) ).add( probe.intern() );

         probeIds.add( probe );
         probeToGeneSetMap.put( probe.intern(), new ArrayList() );
         geneToGeneSetMap.put( group, probeToGeneSetMap.get( probe ) );

         /* read gene description */
         if ( st.hasMoreTokens() ) {
            String description = st.nextToken().intern();
            if ( !description.startsWith( "GO:" ) ) { // this happens when
               // there is no
               // desription and we
               // skip to the GO
               // terms.
               probeToDescription.put( probe.intern(), description.intern() );
            } else {
               probeToDescription.put( probe.intern(), "[No description]" );
            }
         } else {
            probeToDescription.put( probe.intern(), "[No description]" );
         }

         /* read GO data */
         if ( st.hasMoreTokens() ) {
            classIds = st.nextToken();

            //another tokenizer is required since the ClassesID's are
            // seperated by the | character
            StringTokenizer st1 = new StringTokenizer( classIds, "|" );
            while ( st1.hasMoreTokens() ) {
               String go = st1.nextToken().intern();

               // add this go to the probe->go map.
               ( ( ArrayList ) probeToGeneSetMap.get( probe ) ).add( go );

               // add this probe this go->probe map.
               if ( !geneSetToProbeMap.containsKey( go ) ) {
                  geneSetToProbeMap.put( go, new ArrayList() );
               }
               ( ( ArrayList ) geneSetToProbeMap.get( go ) ).add( probe );
            }
         }
         if ( messenger != null && n % 500 == 0 ) {
            messenger.setStatus( "Read " + n + " probes" );
         }
         n++;
      }

      /* Fill in the genegroupreader and the classmap */
      dis.close();
      resetSelectedProbes();
   }

   //read in from a file.
   private void read( String filename ) throws IOException {

      if ( !FileTools.testFile( filename ) ) {
         throw new IOException( "Could not read from " + filename );
      }

      FileInputStream fis = new FileInputStream( filename );
      BufferedInputStream bis = new BufferedInputStream( fis );
      read( bis );
   }

   /**
    * remove classes that have too few members todo this doesn't affect the tree representation of the genesets. todo
    * this overlaps with functionality in GeneSetMapTools
    * 
    * @param lowThreshold
    * @param highThreshold
    */
   private void prune( int lowThreshold, int highThreshold ) {

      Set removeUs = new HashSet();
      for ( Iterator it = geneSetToProbeMap.keySet().iterator(); it.hasNext(); ) {
         String id = ( String ) it.next();
         if ( numProbesInGeneSet( id ) < lowThreshold
               || numGenesInGeneSet( id ) < lowThreshold
               || numProbesInGeneSet( id ) > highThreshold
               || numGenesInGeneSet( id ) > highThreshold ) {
            removeUs.add( id );
         }
      }

      for ( Iterator it = removeUs.iterator(); it.hasNext(); ) {
         String id = ( String ) it.next();
         removeClassFromMaps( id );
      }

      sortGeneSets();
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

   public static void main( String[] args ) {
   }
}

// used for the comparator.

class GeneSet {
   private String name;
   private Set items;

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
    * @param items The items to set.
    */
   public void setItems( Set items ) {
      this.items = items;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
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