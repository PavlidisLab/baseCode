package baseCode.dataStructure;

import java.util.Vector;
import java.util.Iterator;



/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public interface NamedMatrix   {


   /**
    *
    * @param s
    */
   public void addColumnName(String s);

   /**
    *
    * @param s
    */
   public void addRowName(String s);

   /**
    *
    * @param s
    * @return
    */
   public int getRowIndexByName(String s) ;

   /**
    *
    * @param s
    * @return
    */
   public int getColIndexByName(String s);

   /**
    *
    * @param i
    * @return
    */
   public String getRowName(int i) ;

   /**
    *
    * @param i
    * @return
    */
   public String getColName(int i);

   /**
    *
    * @return
    */
   public boolean hasRowNames();

   /**
    *
    * @return
    */
   public boolean hasColNames() ;

   /**
    *
    * @param v
    */
   public void setRowNames(Vector v);

   /**
    *
    * @param v
    */
   public void setColumnNames(Vector v);

   public Vector getColNames();


   /**
    *
    * @param r
    * @return
    */
   public boolean hasRow(String r);

   /**
    *
    * @return
    */
   public Iterator getRowNameMapIterator();

/**
 * 
 * @return
 */
	public int rows();
	
	/**
	 * 
	 * @return
	 */
	public int columns();


public void set (int i, int j, Object val);

public Object[] getRowObj(int i);
public Object[] getColObj(int i);


}