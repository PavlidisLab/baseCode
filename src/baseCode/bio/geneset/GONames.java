package baseCode.bio.geneset;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXException;

import baseCode.bio.GOEntry;
import baseCode.dataStructure.graph.DirectedGraph;
import baseCode.dataStructure.graph.DirectedGraphNode;
import baseCode.xml.GOParser;

/**
 * Rea data from GO XML file, store in easy-to-use data structure.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @author Homin Lee
 * @version $Id$
 */
public class GONames {

   private static Map goNameMap;
   private Set newGeneSets = new HashSet();
   private GOParser parser;

   /**
    * @param filename <code>String</code> The XML file containing class to name mappings. First column is the class
    *        id, second is a description that will be used int program output.
    * @throws IOException
    * @throws SAXException
    */
   public GONames( String filename ) throws SAXException, IOException {
      if ( filename == null || filename.length() == 0 ) {
         throw new IllegalArgumentException(
               "Invalid filename or no filename was given" );
      }

      InputStream i = new FileInputStream( filename );
      parser = new GOParser( i );
      goNameMap = parser.getGONameMap();
   }

   /**
    * @param inputStream
    * @throws IOException
    * @throws SAXException
    */
   public GONames( InputStream inputStream ) throws IOException, SAXException {
      if ( inputStream == null ) {
         throw new IOException( "Input stream was null" );
      }

      parser = new GOParser( inputStream );
      goNameMap = parser.getGONameMap();
   }

   /**
    * Get the graph representation of the GO hierarchy. This can be used to support JTree representations.
    * 
    * @return
    */
   public DirectedGraph getGraph() {
      return parser.getGraph();
   }

   /**
    * @param id
    * @return a Set containing the ids of geneSets which are immediately below the selected one in the hierarchy.
    */
   public Set getChildren( String id ) {
      Set returnVal = new HashSet();
      Set children = ( ( DirectedGraphNode ) getGraph().get( id ) )
            .getChildNodes();
      for ( Iterator it = children.iterator(); it.hasNext(); ) {
         DirectedGraphNode child = ( DirectedGraphNode ) it.next();
         String childKey = ( ( GOEntry ) child.getItem() ).getId()
               .intern();
         returnVal.add( childKey.intern() );
      }
      return returnVal;
   }

   /**
    * @param id
    * @return a Set containing the ids of geneSets which are immediately above the selected one in the hierarchy.
    */
   public Set getParents( String id ) {
      Set returnVal = new HashSet();
      Set parents = ( ( DirectedGraphNode ) getGraph().get( id ) )
            .getParentNodes();
      for ( Iterator it = parents.iterator(); it.hasNext(); ) {
         DirectedGraphNode parent = ( DirectedGraphNode ) it.next();
         String parentKey = ( ( GOEntry ) parent.getItem() ).getId()
               .intern();
         returnVal.add( parentKey.intern() );
      }
      return returnVal;
   }

   /**
    * Get the Map representation of the GO id - name associations.
    * 
    * @return Map
    */
   public Map getMap() {
      return goNameMap;
   }

   /**
    * @param go_ID String
    * @return String
    */
   public String getNameForId( String go_ID ) {

      if ( !goNameMap.containsKey( go_ID ) ) {
         return "<no description available>";
      }

      return ( ( String ) ( goNameMap.get( go_ID ) ) ).intern();
   }
   
   
   /**
    * Get the aspect (molecular_function etc) for an id.
    * @param go_ID
    * @return
    */
   public String getAspectForId( String go_ID ) {
      if ( !goNameMap.containsKey( go_ID ) ) {
         return "<no aspect available>";
      }
      return ((GOEntry)getGraph().getNodeContents(go_ID)).getAspect();
   }
   

   /**
    * @param id String
    * @param name String
    * @todo this should modify the tree representation too.
    */
   public void addClass( String id, String name ) {
      goNameMap.put( id, name );
      newGeneSets.add( id );
   }

   /**
    * @param id String
    * @param name String
    * @todo this should modify the tree representation too.
    */
   public void modifyClass( String id, String name ) {
      goNameMap.put( id, name );
      newGeneSets.add( id );
   }

   /**
    * Check if a gene set is already defined.
    * 
    * @param id
    * @return
    */
   public boolean newSet( String id ) {
      return newGeneSets.contains( id );
   }

   /**
    * Return the Set of all new gene sets (ones which were added after loading the file)
    * 
    * @return
    */
   public Set getNewGeneSets() {
      return newGeneSets;
   }

}