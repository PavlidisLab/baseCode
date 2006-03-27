package ubic.basecode.dataStructure.graph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.Visitable;

/**
 * <p>
 * Copyright (c) Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public abstract class AbstractGraphNode extends Visitable implements GraphNode {
    protected Object key;
    protected Object item;
    protected Graph graph; // the graph this belongs to.
    protected boolean visited = false;
    protected static Log log = LogFactory.getLog( GraphNode.class );

    /**
     * Get the actual contents of the node.
     * 
     * @return
     */
    public Object getItem() {
        return item;
    }

    /**
     * Get the key for the node.
     * 
     * @return Object
     */
    public Object getKey() {
        return key;
    }

    /**
     * Create a new node with key and value given. The key is stored by the graph and is used to retrieve nodes. Keys
     * and nodes can be any kind of object.
     * 
     * @param key
     * @param value
     * @param graph
     */
    public AbstractGraphNode( Object key, Object value, Graph graph ) {
        if ( key == null || value == null || graph == null ) throw new NullPointerException( "Null value given" );
        this.setValue( key, value );
        this.graph = graph;
    }

    /**
     * Create a new node when given only a key.
     * 
     * @param key
     */
    public AbstractGraphNode( Object key ) {
        this.key = key;
        this.item = null;
    }

    /**
     * Set the graph this belongs to.
     * 
     * @param graph Graph
     */
    public void setGraph( Graph graph ) {
        this.graph = graph;
    }

    public void setValue( Object key, Object value ) {
        this.item = value;
        this.key = key;
    }

    public void setItem( Object value ) {
        this.item = value;
    }

    public String toString() {
        return item.toString();
    }

    public Graph getGraph() {
        return graph;
    }

    public void mark() {
        visited = true;
    }

    public void unMark() {
        visited = false;
    }

    public boolean isVisited() {
        return visited;
    }

}