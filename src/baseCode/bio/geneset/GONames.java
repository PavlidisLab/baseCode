package baseCode.bio.geneset;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import baseCode.bio.GOEntry;
import baseCode.dataStructure.graph.DirectedGraph;
import baseCode.dataStructure.graph.DirectedGraphNode;
import baseCode.xml.GOParser;

/**
 * Read data from GO XML file, store in easy-to-use data structure.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @author Homin Lee
 * @version $Id$
 */
public class GONames {

    /**
     * 
     */
    private static final String NO_DESCRIPTION_AVAILABLE = "<no description available>";

    protected static final Log log = LogFactory.getLog( GONames.class );

    /**
     * Name for root of tree representing user-defined gene sets.
     */
    public static final String USER_DEFINED = "User-defined";

    /**
     * Name for aspect when none is defined.
     */
    private static final String NO_ASPECT_AVAILABLE = "<no aspect available>";

    private Map goNameMap;
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
            throw new IllegalArgumentException( "Invalid filename " + filename + " or no filename was given" );
        }

        InputStream i = new FileInputStream( filename );
        this.initialize( i );
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
        this.initialize( inputStream );
    }

    /**
     * 
     */
    private void initialize( InputStream inputStream ) throws IOException, SAXException {
        this.parser = new GOParser( inputStream );
        goNameMap = parser.getGONameMap();
        DirectedGraphNode root = this.getGraph().getRoot();
        this.getGraph().addChildTo(
                root.getKey(),
                USER_DEFINED,
                new DirectedGraphNode( USER_DEFINED,
                        new GOEntry( USER_DEFINED, "", USER_DEFINED, NO_ASPECT_AVAILABLE ), this.getGraph() ) );
    }

    /**
     * @return graph representation of the GO hierarchy
     */
    public DirectedGraph getGraph() {
        return parser.getGraph();
    }

    /*
     * 
     */
    public DefaultTreeModel getTreeModel() {
        return getGraph().getTreeModel();
    }

    /**
     * @param id
     * @return a Set containing the ids of geneSets which are immediately below the selected one in the hierarchy.
     */
    public Set getChildren( String id ) {
        Set returnVal = new HashSet();
        Set children = ( ( DirectedGraphNode ) getGraph().get( id ) ).getChildNodes();
        for ( Iterator it = children.iterator(); it.hasNext(); ) {
            DirectedGraphNode child = ( DirectedGraphNode ) it.next();
            String childKey = ( ( GOEntry ) child.getItem() ).getId().intern();
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

        if ( !getGraph().containsKey( id ) ) {
            log.debug( "GeneSet " + id + " doesn't exist in graph" ); // this is not really a problem.
            return returnVal;
        }

        Set parents = ( ( DirectedGraphNode ) getGraph().get( id ) ).getParentNodes();
        for ( Iterator it = parents.iterator(); it.hasNext(); ) {
            DirectedGraphNode parent = ( DirectedGraphNode ) it.next();
            String parentKey = ( ( GOEntry ) parent.getItem() ).getId().intern();
            returnVal.add( parentKey.intern() );
        }
        return returnVal;
    }

    /**
     * @return Map representation of the GO id - name associations.
     */
    public Map getMap() {
        return goNameMap;
    }

    /**
     * @param go_ID
     * @return name of gene set
     */
    public String getNameForId( String go_ID ) {
        if ( !goNameMap.containsKey( go_ID ) ) {
            return NO_DESCRIPTION_AVAILABLE;
        }

        return ( ( String ) ( goNameMap.get( go_ID ) ) ).intern();
    }

    /**
     * @param go_ID
     * @return the aspect (molecular_function etc) for an id.
     */
    public String getAspectForId( String go_ID ) {
        if ( !goNameMap.containsKey( go_ID ) ) {
            return NO_ASPECT_AVAILABLE;
        }
        return ( ( GOEntry ) getGraph().getNodeContents( go_ID ) ).getAspect();
    }

    /**
     * @param id String
     * @param name String
     */
    public void addClass( String id, String name ) {
        goNameMap.put( id, name );
        newGeneSets.add( id );
        addClassToUserDefined( id, name );
    }

    /**
     * @param id
     * @param name
     */
    private void addClassToUserDefined( String id, String name ) {
        log.debug( "Adding user-defined gene set to graph" );
        if ( this.getGraph().get( USER_DEFINED ) == null ) log.error( "No user-defined root node!" );
        this.getGraph().addChildTo( USER_DEFINED, id, new GOEntry( id, name, name, NO_ASPECT_AVAILABLE ) );
    }

    /**
     * @param id String
     * @param name String
     */
    public void modifyClass( String id, String name ) {
        if ( newGeneSets.contains( id ) ) return;
        goNameMap.put( id, name );
        newGeneSets.add( id );
        addClassToUserDefined( id, name );
    }

    /**
     * Check if a gene set is already defined.
     * 
     * @param id
     * @return
     */
    public boolean isUserDefined( String id ) {
        return newGeneSets.contains( id );
    }

    /**
     * Return the Set of all new gene sets (ones which were added/modified after loading the file)
     * 
     * @return
     */
    public Set getUserDefinedGeneSets() {
        return newGeneSets;
    }

    /**
     * @param classID
     */
    public void deleteGeneSet( String classID ) {
        newGeneSets.remove( classID );
        goNameMap.remove( classID );
        this.getGraph().deleteChildFrom( USER_DEFINED, classID );
    }

}