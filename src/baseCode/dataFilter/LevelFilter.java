package baseCode.dataFilter;

import baseCode.math.Stats;

/**
 * Abstract class representing a filter that removes things from matrices
 * based on the values themselves.
 * 
 * Copyright (c) 2004 Columbia University
 * @author Owner
 * @version $Id$
 */
public abstract class LevelFilter extends AbstractFilter {

   protected double lowCut = -Double.MAX_VALUE;
   protected double highCut = Double.MAX_VALUE;
   protected boolean useLowAsFraction = false;
   protected boolean useHighAsFraction = false;
   
   /**
    * Set the low threshold for removal.
    * 
    * @param lowCut the threshold
    */
   protected void setLowCut( double lowCut ) {
      this.lowCut = lowCut;
   }

   /**
    * 
    * @param lowCut
    * @param isFraction
    */
   protected void setLowCut( double lowCut, boolean isFraction ) {
      setLowCut( lowCut );
      setUseLowCutAsFraction( isFraction );
      useLowAsFraction = isFraction;
   }

   /**
    * Set the high threshold for removal. If not set, no filtering will occur.
    * 
    * @param h the threshold
    */
   protected void setHighCut( double h ) {
      highCut = h;
   }

   /**
    * 
    * @param highCut
    * @param isFraction
    */
   public void setHighCut( double highCut, boolean isFraction ) {
      setHighCut( highCut );
      setUseHighCutAsFraction( isFraction );
      useHighAsFraction = isFraction;
   }

   /**
    * 
    * @param setting
    */
   public void setUseHighCutAsFraction( boolean setting ) {
      if ( setting == true && !Stats.isValidFraction( highCut ) ) {
         throw new IllegalArgumentException(
               "Value for cut(s) are invalid for using "
                     + "as fractions, must be >0.0 and <1.0," );
      }
      useHighAsFraction = setting;
   }

   /**
    * 
    * @param setting
    */
   public void setUseLowCutAsFraction( boolean setting ) {
      if ( setting == true && !Stats.isValidFraction( lowCut ) ) {
         throw new IllegalArgumentException(
               "Value for cut(s) are invalid for using "
                     + "as fractions, must be >0.0 and <1.0," );
      }
      useLowAsFraction = setting;
   }
   
   /**
    * Set the filter to interpret the low and high cuts as fractions; that is,
    * if true, lowcut 0.1 means remove 0.1 of the rows with the lowest values.
    * Otherwise the cuts are interpeted as actual values. Default = false.
    * 
    * @param setting boolean
    */
   public void setUseAsFraction( boolean setting ) {
      setUseHighCutAsFraction( setting );
      setUseLowCutAsFraction( setting );
   }

   


}
