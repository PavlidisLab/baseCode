package baseCode.math;

import java.util.Random;

/**
 * Fill arrays with random values given a source of values.
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution:: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class RandomChooser {

   private RandomChooser() {
   }

   private static Random generator = new Random( System.currentTimeMillis() );

   /**
    * Fill randomvals with random things from sourcedata, without replacement.
    * 
    * @param randomvals answers go here.
    * @param sourcedata Data to be randomly selected
    * @param deck an array pre-filled with integers from 0 to max, but they
    *        don't have to be in order.
    * @param max how many values we need.
    * @param n int
    */
   public static void chooserandom( double[] randomvals, double[] sourcedata,
         int[] deck, int max, int n ) {
      int rand;
      int i;
      int temp;
      for ( i = 0; i < n; i++ ) {
         rand = generator.nextInt( max - i ) + i; // a value between i and max.
         temp = deck[rand];
         deck[rand] = deck[i];
         deck[i] = temp;
         randomvals[i] = sourcedata[temp];
      }
   }

   /**
    * choose n random integers from 0 to max without repeating
    * 
    * @param randomnums answers go here.
    * @param deck an array pre-filled with integers from 0 to max, but they
    *        don't have to be in order.
    * @param max how many values we need.
    * @param n int
    */
   public static void chooserandom( int[] randomnums, int[] deck, int max, int n ) {
      int rand;
      int i;
      for ( i = 0; i < n; i++ ) {
         rand = generator.nextInt( max - i ) + i; // a value between i and max.
         randomnums[i] = deck[rand];
         deck[rand] = deck[i];
         deck[i] = randomnums[i];
      }
   }

   /**
    * choose n random integers from 0 to max without repeating
    * 
    * @param randomnums int[]
    * @param recLog record of what values are already chosen.
    * @param max int
    * @param n int
    */
   public static void chooserandom( int[] randomnums, boolean[] recLog,
         int max, int n ) {
      int numgot;
      int i;
      int newnum;

      numgot = 0;

      while ( numgot < n ) { /* numgot is the index of the last gotten item */
         newnum = generator.nextInt( max );
         if ( !recLog[newnum] ) {
            randomnums[numgot] = newnum;
            recLog[newnum] = true;
            numgot++;
         }
      }

      // reset all elements in recLog to false
      for ( i = 0; i < n; i++ ) {
         recLog[randomnums[i]] = false;
      }

   }

   /**
    * Same as chooserandom, but with replacement -- that is, repeats are
    * allowed.
    * 
    * @param randomnums int[]
    * @param max int
    * @param n int
    */
   public static void chooserandomWrep( int[] randomnums, int max, int n ) {
    for ( int i = 0; i < n; i++ ) {
         int newnum = ( char ) ( generator.nextInt() % max );
         randomnums[i] = newnum;
      }
   }

}