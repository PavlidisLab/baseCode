package baseCodeTest.algorithm.learning.unsupervised.cluster;

import java.util.Collection;
import java.util.HashSet;

import cern.colt.list.ObjectArrayList;

import baseCode.algorithm.learning.unsupervised.cluster.DistanceFactory;
import baseCode.algorithm.learning.unsupervised.cluster.Distanceable;
import baseCode.algorithm.learning.unsupervised.cluster.Distancer;
import baseCode.algorithm.learning.unsupervised.cluster.HierarchicalClusterer;
import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class HierarchicalClustererTest extends TestCase {

   
   HierarchicalClusterer foo;
   ObjectArrayList testData;
   
   /*
    * @see TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
       testData = new ObjectArrayList(new DoubleDistanceable[]{
      new DoubleDistanceable(5),
      new DoubleDistanceable(4),
      new DoubleDistanceable(5),
      new DoubleDistanceable(4),
      new DoubleDistanceable(5),
      new DoubleDistanceable(4),
      new DoubleDistanceable(5),
      new DoubleDistanceable(4),
      new DoubleDistanceable(8),
      new DoubleDistanceable(3),
      new DoubleDistanceable(2),
      new DoubleDistanceable(1)
       });
      
      foo = new HierarchicalClusterer(testData);
   }

   public void testRun() {
      foo.run();
      DoubleDistanceable[] expectedResults = new DoubleDistanceable[]{
            new DoubleDistanceable(8),
            new DoubleDistanceable(5),
            new DoubleDistanceable(5),
            new DoubleDistanceable(5),
            new DoubleDistanceable(5),
            new DoubleDistanceable(4),
            new DoubleDistanceable(4),
            new DoubleDistanceable(4),
            new DoubleDistanceable(4),
            new DoubleDistanceable(3),
            new DoubleDistanceable(2),
            new DoubleDistanceable(1)
      
      };
      
      DoubleDistanceable[] actualResults = ( DoubleDistanceable[] ) foo.getResults();
      
      assertEquals(actualResults, expectedResults);
   }

}


///** helpers **

class DoubleDistanceable implements Distanceable {

   Double value;
   Distancer f;

   /**
    * @param value
    */
   public DoubleDistanceable( double value ) {
      super();
      this.value = new Double(value);
      f = new DoubleDifferenceFactory().getDistancer();
   }

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.algorithm.learning.unsupervised.cluster.Distanceable#distanceTo(baseCode.algorithm.learning.unsupervised.cluster.Distanceable)
    */
   public double distanceTo( Distanceable a ) {
      return f.distance(a, this);
   }

   public static int compare( double d1, double d2 ) {
      return Double.compare( d1, d2 );
   }

   public static long doubleToLongBits( double value ) {
      return Double.doubleToLongBits( value );
   }

   public static long doubleToRawLongBits( double value ) {
      return Double.doubleToRawLongBits( value );
   }

   public static boolean isInfinite( double v ) {
      return Double.isInfinite( v );
   }

   public static boolean isNaN( double v ) {
      return Double.isNaN( v );
   }

   public static double longBitsToDouble( long bits ) {
      return Double.longBitsToDouble( bits );
   }

   public static double parseDouble( String s ) throws NumberFormatException {
      return Double.parseDouble( s );
   }

   public static String toString( double d ) {
      return Double.toString( d );
   }

   public static Double valueOf( String s ) throws NumberFormatException {
      return Double.valueOf( s );
   }

   public byte byteValue() {
      return value.byteValue();
   }

   public int compareTo( Double anotherDouble ) {
      return value.compareTo( anotherDouble );
   }

   public int compareTo( Object o ) {
      return value.compareTo( o );
   }

   public double doubleValue() {
      return value.doubleValue();
   }

   public boolean equals( Object obj ) {
      return value.equals( obj );
   }

   public float floatValue() {
      return value.floatValue();
   }

   public int hashCode() {
      return value.hashCode();
   }

   public int intValue() {
      return value.intValue();
   }

   public boolean isInfinite() {
      return value.isInfinite();
   }

   public boolean isNaN() {
      return value.isNaN();
   }

   public long longValue() {
      return value.longValue();
   }

   public short shortValue() {
      return value.shortValue();
   }

   public String toString() {
      return value.toString();
   }

   /* (non-Javadoc)
    * @see baseCode.algorithm.learning.unsupervised.cluster.Visitable#isVisited()
    */
   
   private boolean visited;
   
   public boolean isVisited() {
      return visited;
   }

   /* (non-Javadoc)
    * @see baseCode.algorithm.learning.unsupervised.cluster.Visitable#mark()
    */
   public void mark() {
      visited = true;
   }

   /* (non-Javadoc)
    * @see baseCode.algorithm.learning.unsupervised.cluster.Visitable#unMark()
    */
   public void unMark() {
      visited = false;
   }
}

class DoubleDifference implements Distancer {

   /*
    * (non-Javadoc)
    * 
    * @see baseCode.algorithm.learning.unsupervised.cluster.Distancer#distance(java.lang.Object, java.lang.Object)
    */
   public double distance( Object a, Object b ) {
      DoubleDistanceable ad = ( DoubleDistanceable ) a;
      DoubleDistanceable bd = ( DoubleDistanceable ) b;
      return Math.abs( ad.value.doubleValue() - bd.value.doubleValue() );
   }

}

class DoubleDifferenceFactory extends DistanceFactory {

   private DoubleDifference d;

   public DoubleDifferenceFactory() {
      d = new DoubleDifference();
   }

   public Distancer getDistancer() {
      return d;
   }

}

