package baseCode.math.metaanalysis;

import baseCode.math.CorrelationStats;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import cern.jet.stat.Probability;

/**
 * Implementation of meta-analysis of correlations along the lines of chapter 18 of Cooper and Hedges, "Handbook of
 * Research Synthesis". Both fixed and random effects models are supported, with z-transformed or untransformed
 * correlations.
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class CorrelationEffectMetaAnalysis extends MetaAnalysis {

   private boolean transform = false;
   private boolean fixed = true;

   private double z; // z score
   private double p; // probability
   private double q; // q-score;
   private double e; // unconditional effect;
   private double v; // unconditional variance;
   private double n; // total sample size
   private double bsv; // between-studies variance component;

  

   public CorrelationEffectMetaAnalysis( boolean fixed, boolean transform ) {
      this.fixed = fixed;
      this.transform = transform;
   }

   public CorrelationEffectMetaAnalysis() {
   }

   /**
    * Following CH section 2.2.
    * <p>
    * There are four possible cases (for now):
    * <ol>
    * <li>Fixed effects, Z-transformed. Weights are computed using CH eqns 18-8 and 18-3
    * <li>Fixed effects, untransformed. Weights are computed using CH eqns 18-10 and 18-3
    * <li>Random effects, Z-transformed. Weights are computed using CH eqns 18-20, 18-8 with 18-3
    * <li>Random effects, untransformed. Weights are computed using CH eqns 18-10, 18-20, 18-24.
    * </ol>
    * The default is untransformed, fixed effects.
    * 
    * @param correlations
    * @param sampleSizes
    * @return p-value. The p-value is also stored in the field p.
    */
   public double run( DoubleArrayList effects, DoubleArrayList sampleSizes ) {

      DoubleArrayList weights;
      DoubleArrayList conditionalVariances;
      this.n = Descriptive.sum( sampleSizes );

      if ( transform ) {
         DoubleArrayList fzte = CorrelationStats.fisherTransform( effects );

         // initial values.
         conditionalVariances = fisherTransformedSamplingVariances( sampleSizes );
         weights = metaFEWeights( conditionalVariances );
         this.q = super.qStatistic( fzte, conditionalVariances, super
               .weightedMean( fzte, weights ) );

         if ( !fixed ) { // adjust the conditional variances and weights.
            this.bsv = metaREVariance( fzte, conditionalVariances, weights );

            for ( int i = 0; i < conditionalVariances.size(); i++ ) {
               conditionalVariances.setQuick( i, conditionalVariances
                     .getQuick( i )
                     + bsv );
            }
            weights = metaFEWeights( conditionalVariances );
         }

         this.e = super.weightedMean( fzte, weights );
      } else {

         conditionalVariances = samplingVariances( effects,
               sampleSizes );
         weights = metaFEWeights( conditionalVariances );
         this.q = super.qStatistic( effects, conditionalVariances, super
               .weightedMean( effects, weights ) );

       

         if ( !fixed ) { // adjust the conditional variances and weights.
            this.bsv = metaREVariance( effects, conditionalVariances, weights );

            for ( int i = 0; i < conditionalVariances.size(); i++ ) {
               conditionalVariances.setQuick( i, conditionalVariances
                     .getQuick( i )
                     + bsv );
            }

            weights = metaFEWeights( conditionalVariances );
         }

         this.e = super.weightedMean( effects, weights );
      }

      this.v = super.metaVariance( conditionalVariances );
      this.z = Math.abs( e ) / Math.sqrt( v );
      this.p = Probability.errorFunctionComplemented( z );
  
//      if ( qTest( q, effects.size() ) < 0.05 ) {
//         System.err.println("Q was significant: " + qTest( q, effects.size() ) );
//      }
      
      return p;
   }

   /**
    * Equation 18-10 from CH. For untransformed correlations.
    * 
    * <pre>
    * v_i = ( 1 - r_i &circ; 2 ) &circ; 2 / ( n_i - 1 )
    * </pre>
    * 
    * @param r
    * @param n
    * @return
    */
   protected double samplingVariance( double r, double numsamples ) {

      if ( numsamples <= 0 )
            throw new IllegalArgumentException( "N must be greater than 0" );

      if ( !CorrelationStats.isValidPearsonCorrelation( r ) )
            throw new IllegalArgumentException(
                  "r is not a valid Pearson correlation" );

      if ( numsamples < 2 ) {
         return Double.NaN;
      }
      double k = 1.0 - r * r;
      return k * k / ( numsamples - 1 );
   }

   /**
    * Run equation CH 18-10 on a list of sample sizes and effects.
    * 
    * @param effectSizes
    * @param sampleSizes
    * @return
    */
   protected DoubleArrayList samplingVariances(
         DoubleArrayList effectSizes, DoubleArrayList sampleSizes ) {

      if ( effectSizes.size() != sampleSizes.size() )
            throw new IllegalArgumentException( "Unequal sample sizes." );

      DoubleArrayList answer = new DoubleArrayList( sampleSizes.size() );
      for ( int i = 0; i < sampleSizes.size(); i++ ) {
         answer.add( samplingVariance( effectSizes.getQuick( i ),
               sampleSizes.getQuick( i ) ) );
      }
      return answer;
   }

   /**
    * Equation 18-8 from CH. For z-transformed correlations.
    * 
    * <pre>
    * v_i = 1 / ( n_i - 3 )
    * </pre>
    * 
    * @param n
    * @return
    */
   protected double fisherTransformedSamplingVariance( double sampleSize ) {

      if ( sampleSize <= 3.0 ) throw new IllegalStateException( "N is too small" );

      return 1.0 / ( sampleSize - 3.0 );
   }

   /**
    * Run equation CH 18-8 on a list of sample sizes.
    * 
    * @param sampleSizes
    * @return
    */
   protected DoubleArrayList fisherTransformedSamplingVariances(
         DoubleArrayList sampleSizes ) {

      DoubleArrayList answer = new DoubleArrayList( sampleSizes.size() );
      for ( int i = 0; i < sampleSizes.size(); i++ ) {
         answer.add( fisherTransformedSamplingVariance( sampleSizes
               .getQuick( i ) ) );
      }
      return answer;
   }
   
   public void setFixed( boolean fixed ) {
      this.fixed = fixed;
   }

   public void setTransform( boolean transform ) {
      this.transform = transform;
   }

   public double getP() {
      return p;
   }

   public double getQ() {
      return q;
   }

   public double getZ() {
      return z;
   }

   public double getE() {
      return e;
   }

   public double getV() {
      return v;
   }

   public double getN() {
      return n;
   }

   public double getBsv() {
      return bsv;
   }
}