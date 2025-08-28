package com.github.fommil.netlib;

/**
 * @deprecated use {@link dev.ludovic.netlib.blas.BLAS} instead
 */
@Deprecated
public class BLAS {

    public static BLAS getInstance() {
        return new BLAS( dev.ludovic.netlib.blas.BLAS.getInstance() );
    }

    private final dev.ludovic.netlib.blas.BLAS blas;

    public BLAS( dev.ludovic.netlib.blas.BLAS blas ) {
        this.blas = blas;
    }

    public void dsyr( String uplo, int n, double alpha, double[] x, int incx, double[] a, int lda ) {
        blas.dsyr( uplo, n, alpha, x, incx, a, lda );
    }

    public void dsyr2k( String netlib, String netlib1, int numRows, int i, double alpha, double[] bd, int max, double[] cd, int max1, int i1, double[] data, int max2 ) {
        blas.dsyr2k( netlib, netlib1, numRows, i, alpha, bd, max, cd, max1, i1, data, max2 );
    }

    public void dsyrk( String netlib, String netlib1, int numRows, int numRows1, double alpha, double[] cd, int max, int i, double[] data, int max1 ) {
        blas.dsyrk( netlib, netlib1, numRows, numRows1, alpha, cd, max, i, data, max1 );
    }

    public void dsymv( String netlib, int numRows, double alpha, double[] data, int max, double[] xd, int i, int i1, double[] yd, int i2 ) {
        blas.dsymv( netlib, numRows, alpha, data, max, xd, i, i1, yd, i2 );
    }

    public void dsyr2( String netlib, int numRows, double alpha, double[] xd, int i, double[] yd, int i1, double[] data, int max ) {
        blas.dsyr2( netlib, numRows, alpha, xd, i, yd, i1, data, max );
    }

    public void dsymm( String netlib, String netlib1, int i, int i1, double alpha, double[] data, int max, double[] bd, int max1, int i2, double[] cd, int max2 ) {
        blas.dsymm( netlib, netlib1, i, i1, alpha, data, max, bd, max1, i2, cd, max2 );
    }

    public void dsbmv( String netlib, int numRows, int kd, double alpha, double[] data, int i, double[] xd, int i1, int i2, double[] yd, int i3 ) {
        blas.dsbmv( netlib, numRows, kd, alpha, data, i, xd, i1, i2, yd, i3 );
    }

    public void dspmv( String netlib, int numRows, double alpha, double[] data, double[] xd, int i, int i1, double[] yd, int i2 ) {
        blas.dspmv( netlib, numRows, alpha, data, xd, i, i1, yd, i2 );
    }

    public void dspr( String netlib, int numRows, double alpha, double[] xd, int i, double[] data ) {
        blas.dspr( netlib, numRows, alpha, xd, i, data );
    }

    public void dspr2( String netlib, int numRows, double alpha, double[] xd, int i, double[] yd, int i1, double[] data ) {
        blas.dspr2( netlib, numRows, alpha, xd, i, yd, i1, data );
    }

    public void dtbmv( String netlib, String netlib1, String netlib2, int numRows, int kd, double[] data, int i, double[] yd, int i1 ) {
        blas.dtbmv( netlib, netlib1, netlib2, numRows, kd, data, i, yd, i1 );
    }

    public void dtrmv( String netlib, String netlib1, String netlib2, int numRows, double[] data, int max, double[] yd, int i ) {
        blas.dtrmv( netlib, netlib1, netlib2, numRows, data, max, yd, i );
    }

    public void dtrmm( String netlib, String netlib1, String netlib2, String netlib3, int i, int i1, double alpha, double[] data, int max, double[] cd, int max1 ) {
        blas.dtrmm( netlib, netlib1, netlib2, netlib3, i, i1, alpha, data, max, cd, max1 );
    }

    public void dtpmv( String netlib, String netlib1, String netlib2, int numRows, double[] data, double[] yd, int i ) {
        blas.dtpmv( netlib, netlib1, netlib2, numRows, data, yd, i );
    }

    public void dgbmv( String netlib, int numRows, int numColumns, int kl, int ku, double alpha, double[] data, int i, double[] xd, int i1, int i2, double[] yd, int i3 ) {
        blas.dgbmv( netlib, numRows, numColumns, kl, ku, alpha, data, i, xd, i1, i2, yd, i3 );
    }

    public void dgemm( String netlib, String netlib1, int i, int i1, int numColumns, double alpha, double[] data, int max, double[] bd, int max1, int i2, double[] cd, int max2 ) {
        blas.dgemm( netlib, netlib1, i, i1, numColumns, alpha, data, max, bd, max1, i2, cd, max2 );
    }

    public void dger( int numRows, int numColumns, double alpha, double[] xd, int i, double[] yd, int i1, double[] data, int max ) {
        blas.dger( numRows, numColumns, alpha, xd, i, yd, i1, data, max );
    }

    public void dgemv( String netlib, int numRows, int numColumns, double alpha, double[] data, int max, double[] xd, int i, int i1, double[] yd, int i2 ) {
        blas.dgemv( netlib, numRows, numColumns, alpha, data, max, xd, i, i1, yd, i2 );
    }
}
