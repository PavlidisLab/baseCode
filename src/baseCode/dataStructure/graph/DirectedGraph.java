package baseCode.dataStructure.graph;

import java.util.*;

public class DirectedGraph {

   protected Set items;
   protected Map members;
   protected String id;

   public DirectedGraph( String id ) {
      items = new HashSet();
      members = new HashMap();
      this.id = id;
   }

   public void addChildTo( Object item, Object newChild ) {

   }

   public void addParentTo( Object item, Object newParent ) {

   }

   public Set getChildrenOf( Object j ) {
      return null;
   };

   public Set getParentsOf( Object j ) {
      return null;
   }

   public Set getAllChildren( Set list ) {
      if ( list == null ) {
         list = new HashSet();

      }
      DirectedGraph j = null;
      for ( Iterator it = this.getChildIterator(); it.hasNext(); j = ( DirectedGraph ) it.next() ) {
         list.add( j );
         j.getAllChildren( list );
      }
      return list;
   }

   public Iterator getChildIterator() {
      return items.iterator();
   }

   public String getId() {
      return id;
   }

   public void addChild( Object j ) {
      items.add( j );
   }

   public void addMember( Object key, Object value ) {
      members.put( key, value );
   }

   public Set getChildren() {
      return items;
   }

   public Map getMembers() {
      return members;
   }

   public Object getMember( Object j ) {
      return members.get( j );
   }

   public boolean hasChild( Object j ) {
      Set k = this.getAllChildren( null );
      return k.contains( j );
   }

   public boolean hasImmediateChild( Object j ) {
      return items.contains( j );
   }

   public boolean hasMember( Object j ) {
      Set k = this.getAllChildren( null );
      DirectedGraph o = null;
      for ( Iterator it = k.iterator(); it.hasNext(); o = ( DirectedGraph ) it.next() ) {
         if ( o.hasDirectMember( j ) ) {
            return true;
         }
      }
      return false;
   }

   public int depth() {
      int d = 0;
      return d;
   }

   public boolean hasDirectMember( Object j ) {
      return members.containsKey( j );
   }

   public String toString() {
      return this.makeString( new StringBuffer() );
   }

   private String indent() {
      StringBuffer k = new StringBuffer();
      for ( int i = 0; i < depth(); i++ ) {
         k.append( "\t" );
      }
      return k.toString();
   }

   private String makeString( StringBuffer buf ) {
      Object k = null;
      if ( buf == null ) {
         buf = new StringBuffer();
      }

      /* First add the members */
      for ( Iterator it = members.keySet().iterator(); it.hasNext(); k = it.next() ) {
         Object m = members.get( k );
         if ( m != null ) {
            buf.append( indent() + m.toString() + "\n" );
         }
      }

      /* now traverse the children */
      DirectedGraph j = null;
      for ( Iterator it = this.getChildIterator(); it.hasNext(); j = ( DirectedGraph ) it.next() ) {
         j.makeString( buf );
      }

      return buf.toString();
   }

}
