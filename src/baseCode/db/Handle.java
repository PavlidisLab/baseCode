package baseCode.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Database handle container and query convenience functions.
 * </p>
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */

public class Handle {

   private Connection con;

   //  private Statement stat;
   /**
    * @param host String
    * @param database String
    * @param user String
    * @param password String
    * @throws SQLException
    */
   public Handle( String host, String database, String user, String password ) throws SQLException {
      try {
         Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
         String url = "jdbc:mysql://" + host + "/" + database + "?relaxAutoCommit=true";
         con = DriverManager.getConnection( url, user, password );
         //       stat = newStatement();
      } catch ( ClassNotFoundException e ) {
         throw new RuntimeException( e );
      } catch ( InstantiationException e ) {
         throw new RuntimeException( e );
      } catch ( IllegalAccessException e ) {
         throw new RuntimeException( e );
      }
   }

   /**
    * @return java.sql.Connection
    */
   public Connection getCon() {
      return con;
   }

   public String quote( String k ) throws SQLException {
      return con.nativeSQL( k );
   }

   /**
    * @return java.sql.Statement
    * @throws SQLException
    */
   public Statement newStatement() throws SQLException {
      return con.createStatement();
   }

   //  public boolean isClosed() throws SQLException {
   //      return stat.getResultSet().close();
   //   }

   /**
    * Close the database connection.
    */
   public void closeCon() {
      try {
         this.con.close();
      } catch ( SQLException ex ) {
         ex.printStackTrace();
      }
      con = null;
   }

   // This is really only here for debugging.
   public Handle() throws SQLException {
      this( "localhost", "tmm", "pavlidis", "toast" );
   }

   public Handle( String host, String database ) throws SQLException {
      this( host, database, "pavlidis", "toast" );
   }

   /**
    * @param query String
    * @return int
    * @throws SQLException
    */
   public int runUpdateQuery( String query ) throws SQLException {
      return newStatement().executeUpdate( query );
   }

   /**
    * @param query String
    * @return java.sql.ResultSet
    * @throws SQLException
    */
   public ResultSet runQuery( String query ) throws SQLException {
      return newStatement().executeQuery( query );
   }

   /**
    * @param query String
    * @return java.sql.PreparedStatement
    * @throws SQLException
    */
   public PreparedStatement prepareStatement( String query ) throws SQLException {
      return con.prepareStatement( query );
   }

   /**
    * @param query String
    * @return java.util.Map
    * @throws SQLException
    */
   public Map queryToMap( String query ) throws SQLException {
      Map result = new HashMap();

      ResultSet k = runQuery( query );

      if ( k != null ) {
         while ( k.next() ) {
            result.put( k.getObject( 1 ), k.getObject( 2 ) );
         }
         k.close();
      }

      return result;
   }

   /**
    * @param query String
    * @return java.util.Set
    * @throws SQLException
    */
   public Set queryToSet( String query ) throws SQLException {
      Set result = new HashSet();

      ResultSet k = runQuery( query );

      if ( k != null ) {
         while ( k.next() ) {
            result.add( k.getObject( 1 ) );
         }
         k.close();
      }

      return result;

   }

   /**
    * @param query String
    * @return java.util.List
    * @throws SQLException
    */
   public List queryToList( String query ) throws SQLException {
      List result = new Vector();

      ResultSet k = runQuery( query );

      if ( k != null ) {
         while ( k.next() ) {
            result.add( k.getObject( 1 ) );
         }
         k.close();
      }

      return result;
   }

   /**
    * @param query String
    * @return Object containing the first result obtained. If no result, it return null.
    * @throws SQLException
    */
   public Integer queryToInt( String query ) throws SQLException {

      ResultSet k = runQuery( query );
      if ( k != null ) {
         while ( k.next() ) {
            return new Integer( k.getInt( 1 ) );
         }
         k.close();
      }
      return null;

   }

   /**
    * @param query String
    * @return java.lang.Double
    * @throws SQLException
    */
   public Double queryToDouble( String query ) throws SQLException {

      ResultSet k = runQuery( query );
      if ( k != null ) {
         while ( k.next() ) {
            return new Double( k.getDouble( 1 ) );
         }
         k.close();
      }
      return null;

   }

   /**
    * @param query String
    * @return java.lang.String
    * @throws SQLException
    */
   public String queryToString( String query ) throws SQLException {

      ResultSet k = runQuery( query );
      if ( k != null ) {
         while ( k.next() ) {
            return new String( k.getString( 1 ) );
         }
         k.close();
      }
      return null;

   }

}