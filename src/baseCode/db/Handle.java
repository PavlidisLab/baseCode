package baseCode.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: Database handle container and query convenience functions.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */

public class Handle {

   private Connection con;
   //  private Statement stat;
   /**
    *
    * @param host
    * @param database
    * @param user
    * @param password
    */
   public Handle(String host, String database, String user, String password) throws SQLException {
      try {
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         String url = "jdbc:mysql://" + host + "/" + database + "?relaxAutoCommit=true";
         con = DriverManager.getConnection(url, user, password);
         //       stat = newStatement();
      }
      catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
      catch (InstantiationException e) {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    *
    * @return
    */
   public Connection getCon() {
      return con;
   }

   /**
    *
    * @return
    * @throws SQLException
    */
   public Statement newStatement() throws SQLException {
      return con.createStatement();
   }

   //  public boolean isClosed() throws SQLException  {
//      return stat.getResultSet().close();
//   }

   /**
    * Close the database connection.
    */
   public void closeCon() {
      try {
         this.con.close();
      }
      catch (SQLException ex) {
         ex.printStackTrace();
      }
      con = null;
   }

// This is really only here for debugging.
   public Handle() throws SQLException {
      this("localhost", "tmm", "javauser", "toast");
   }

   public Handle(String host, String database) throws SQLException {
      this(host, database, "javauser", "toast");
   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public int runUpdateQuery(String query) throws SQLException {
      return newStatement().executeUpdate(query);
   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public ResultSet runQuery(String query) throws SQLException {
      return newStatement().executeQuery(query);
   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public PreparedStatement prepareStatement(String query) throws SQLException {
      return con.prepareStatement(query);
   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public Map queryToMap(String query) throws SQLException {
      Map result = new HashMap();

      ResultSet k = runQuery(query);

      if (k != null) {
         while (k.next()) {
            result.put(k.getObject(1), k.getObject(2));
         }
         k.close();
      }

      return result;
   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public Set queryToSet(String query) throws SQLException {
      Set result = new HashSet();

      ResultSet k = runQuery(query);

      if (k != null) {
         while (k.next()) {
            result.add(k.getObject(1));
         }
         k.close();
      }

      return result;

   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public List queryToList(String query) throws SQLException {
      List result = new Vector();

      ResultSet k = runQuery(query);

      if (k != null) {
         while (k.next()) {
            result.add(k.getObject(1));
         }
         k.close();
      }

      return result;
   }

   /**
    *
    * @param query
    * @return Object containing the first result obtained. If no result, it return null.
    * @throws SQLException
    */
   public Integer queryToInt(String query) throws SQLException {

      ResultSet k = runQuery(query);
      if (k != null) {
         while (k.next()) {
            return new Integer(k.getInt(1));
         }
         k.close();
      }
      return null;

   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public Double queryToDouble(String query) throws SQLException {

      ResultSet k = runQuery(query);
      if (k != null) {
         while (k.next()) {
            return new Double(k.getDouble(1));
         }
         k.close();
      }
      return null;

   }

   /**
    *
    * @param query
    * @return
    * @throws SQLException
    */
   public String queryToString(String query) throws SQLException {

      ResultSet k = runQuery(query);
      if (k != null) {
         while (k.next()) {
            return new String(k.getString(1));
         }
         k.close();
      }
      return null;

   }

   public static void main(String[] args) {
      try {
         Handle f = new Handle();
         List k = f.queryToList("SELECT official_name FROM gene WHERE official_name LIKE 'MAT%'");
         for (Iterator it = k.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            System.err.println(name);
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

}
