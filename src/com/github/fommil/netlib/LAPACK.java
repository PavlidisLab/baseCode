package com.github.fommil.netlib;

import org.netlib.util.doubleW;
import org.netlib.util.intW;

/**
 * @deprecated use {@link dev.ludovic.netlib.lapack.LAPACK} instead
 */
@Deprecated
public class LAPACK {

    public static LAPACK getInstance() {
        return new LAPACK( dev.ludovic.netlib.lapack.LAPACK.getInstance() );
    }

    private final dev.ludovic.netlib.lapack.LAPACK lapack;

    private LAPACK( dev.ludovic.netlib.lapack.LAPACK lapack ) {
        this.lapack = lapack;
    }

    public void dsysv( String netlib, int numRows, int i, double[] newData, int ld, int[] ipiv, double[] xd, int ld1, double[] work, int i1, intW info ) {
        lapack.dsysv( netlib, numRows, i, newData, ld, ipiv, xd, ld1, work, i1, info );
    }

    public void dposv( String netlib, int numRows, int i, double[] clone, int ld, double[] xd, int ld1, intW info ) {
        lapack.dposv( netlib, numRows, i, clone, ld, xd, ld1, info );
    }

    public void dgbsv( int numRows, int kd, int kd1, int i, double[] data, int ld, int[] ipiv, double[] xd, int ld1, intW info ) {
        lapack.dgbsv( numRows, kd, kd1, i, data, ld, ipiv, xd, ld1, info );
    }

    public void dpbsv( String netlib, int numRows, int kd, int i, double[] clone, int ld, double[] xd, int ld1, intW info ) {
        lapack.dpbsv( netlib, numRows, kd, i, clone, ld, xd, ld1, info );
    }

    public void dspsv( String netlib, int numRows, int i, double[] clone, int[] ipiv, double[] xd, int ld, intW info ) {
        lapack.dspsv( netlib, numRows, i, clone, ipiv, xd, ld, info );
    }

    public void dppsv( String netlib, int numRows, int i, double[] clone, double[] xd, int ld, intW info ) {
        lapack.dppsv( netlib, numRows, i, clone, xd, ld, info );
    }

    public void dtbtrs( String netlib, String netlib1, String netlib2, int numRows, int kd, int i, double[] data, int ld, double[] xd, int ld1, intW info ) {
        lapack.dtbtrs( netlib, netlib1, netlib2, numRows, kd, i, data, ld, xd, ld1, info );
    }

    public void dtrtrs( String netlib, String netlib1, String netlib2, int numRows, int i, double[] data, int max, double[] xd, int ld, intW info ) {
        lapack.dtrtrs( netlib, netlib1, netlib2, numRows, i, data, max, xd, ld, info );
    }

    public void dtptrs( String netlib, String netlib1, String netlib2, int numRows, int i, double[] data, double[] xd, int ld, intW info ) {
        lapack.dtptrs( netlib, netlib1, netlib2, numRows, i, data, xd, ld, info );
    }

    public void dpbtrf( String netlib, int n, int kd, double[] data, int ld, intW info ) {
        lapack.dpbtrf( netlib, n, kd, data, ld, info );
    }

    public void dpbcon( String netlib, int n, int kd, double[] data, int ld, double anorm, doubleW rcond, double[] work, int[] lwork, intW info ) {
        lapack.dpbcon( netlib, n, kd, data, ld, anorm, rcond, work, lwork, info );
    }

    public void dpbtrs( String netlib, int n, int kd, int i, double[] data, int ld, double[] data1, int ld1, intW info ) {
        lapack.dpbtrs( netlib, n, kd, i, data, ld, data1, ld1, info );
    }

    public void dgbtrf( int n, int n1, int kl, int ku, double[] data, int i, int[] ipiv, intW info ) {
        lapack.dgbtrf( n, n1, kl, ku, data, i, ipiv, info );
    }

    public void dgbcon( String netlib, int n, int kl, int ku, double[] data, int ld, int[] ipiv, double anorm, doubleW rcond, double[] work, int[] lwork, intW info ) {
        lapack.dgbcon( netlib, n, kl, ku, data, ld, ipiv, anorm, rcond, work, lwork, info );
    }

    public void dgbtrs( String netlib, int n, int kl, int ku, int i, double[] data, int i1, int[] ipiv, double[] data1, int ld, intW info ) {
        lapack.dgbtrs( netlib, n, kl, ku, i, data, i1, ipiv, data1, ld, info );
    }

    public void dpotrf( String netlib, int i, double[] data, int ld, intW info ) {
        lapack.dpotrf( netlib, i, data, ld, info );
    }

    public void dpotrs( String netlib, int i, int i1, double[] data, int ld, double[] data1, int ld1, intW info ) {
        lapack.dpotrs( netlib, i, i1, data, ld, data1, ld1, info );
    }

    public void dpocon( String netlib, int n, double[] data, int ld, double anorm, doubleW rcond, double[] work, int[] iwork, intW info ) {
        lapack.dpocon( netlib, n, data, ld, anorm, rcond, work, iwork, info );
    }

    public void dgetrf( int i, int i1, double[] data, int ld, int[] piv, intW info ) {
        lapack.dgetrf( i, i1, data, ld, piv, info );
    }

    public void dgecon( String netlib, int n, double[] data, int ld, double anorm, doubleW rcond, double[] doubles, int[] ints, intW info ) {
        lapack.dgecon( netlib, n, data, ld, anorm, rcond, doubles, ints, info );
    }

    public void dgetrs( String netlib, int i, int i1, double[] data, int ld, int[] piv, double[] data1, int ld1, intW info ) {
        lapack.dgetrs( netlib, i, i1, data, ld, piv, data1, ld1, info );
    }

    public void dgesv( int numRows, int i, double[] clone, int ld, int[] piv, double[] xd, int ld1, intW info ) {
        lapack.dgesv( numRows, i, clone, ld, piv, xd, ld1, info );
    }

    public void dgels( String netlib, int numRows, int numColumns, int nrhs, double[] newData, int ld, double[] data, int ld1, double[] work, int i, intW info ) {
        lapack.dgels( netlib, numRows, numColumns, nrhs, newData, ld, data, ld1, work, i, info );
    }

    public void dgeev( String netlib, String netlib1, int n, double[] doubles, int ld, double[] doubles1, double[] doubles2, double[] doubles3, int ld1, double[] doubles4, int ld2, double[] worksize, int i, intW info ) {
        lapack.dgeev( netlib, netlib1, n, doubles, ld, doubles1, doubles2, doubles3, ld1, doubles4, ld2, worksize, i, info );
    }

    public void dgelqf( int m, int n, double[] doubles, int ld, double[] doubles1, double[] work, int i, intW info ) {
        lapack.dgelqf( m, n, doubles, ld, doubles1, work, i, info );
    }

    public void dorglq( int m, int n, int m1, double[] doubles, int ld, double[] doubles1, double[] workGen, int i, intW info ) {
        lapack.dorglq( m, n, m1, doubles, ld, doubles1, workGen, i, info );
    }

    public void dpptrf( String netlib, int i, double[] data, intW info ) {
        lapack.dpptrf( netlib, i, data, info );
    }

    public void dpptrs( String netlib, int i, int i1, double[] data, double[] data1, int ld, intW info ) {
        lapack.dpptrs( netlib, i, i1, data, data1, ld, info );
    }

    public void dppcon( String netlib, int n, double[] data, double anorm, doubleW rcond, double[] work, int[] iwork, intW info ) {
        lapack.dppcon( netlib, n, data, anorm, rcond, work, iwork, info );
    }

    public void dlaswp( int i, double[] data, int ld, int i1, int length, int[] pivots, int i2 ) {
        lapack.dlaswp( i, data, ld, i1, length, pivots, i2 );
    }

    public void dgeqlf( int m, int n, double[] doubles, int ld, double[] doubles1, double[] work, int i, intW info ) {
        lapack.dgeqlf( m, n, doubles, ld, doubles1, work, i, info );
    }

    public void dorgql( int m, int n, int k, double[] doubles, int ld, double[] doubles1, double[] workGen, int i, intW info ) {
        lapack.dorgql( m, n, k, doubles, ld, doubles1, workGen, i, info );
    }

    public void dgeqrf( int m, int n, double[] doubles, int ld, double[] doubles1, double[] work, int i, intW info ) {
        lapack.dgeqrf( m, n, doubles, ld, doubles1, work, i, info );
    }

    public void dorgqr( int m, int n, int k, double[] doubles, int ld, double[] doubles1, double[] workGen, int i, intW info ) {
        lapack.dorgqr( m, n, k, doubles, ld, doubles1, workGen, i, info );
    }

    public void dgeqp3( int m, int n, double[] data, int ld, int[] jpvt, double[] tau, double[] factorWorkOptimalSize, int i, intW info ) {
        lapack.dgeqp3( m, n, data, ld, jpvt, tau, factorWorkOptimalSize, i, info );
    }

    public void dgerqf( int m, int n, double[] doubles, int ld, double[] doubles1, double[] work, int i, intW info ) {
        lapack.dgerqf( m, n, doubles, ld, doubles1, work, i, info );
    }

    public void dorgrq( int m, int n, int m1, double[] doubles, int ld, double[] doubles1, double[] workGen, int i, intW info ) {
        lapack.dorgrq( m, n, m1, doubles, ld, doubles1, workGen, i, info );
    }

    public void dgesdd( String netlib, int m, int n, double[] doubles, int ld, double[] doubles1, double[] doubles2, int ld1, double[] doubles3, int ld2, double[] worksize, int i, int[] iwork, intW info ) {
        lapack.dgesdd( netlib, m, n, doubles, ld, doubles1, doubles2, ld1, doubles3, ld2, worksize, i, iwork, info );
    }

    public void dsbevd( String netlib, String netlib1, int n, int kd, double[] data, int ld, double[] w, double[] doubles, int ld1, double[] work, int length, int[] iwork, int length1, intW info ) {
        lapack.dsbevd( netlib, netlib1, n, kd, data, ld, w, doubles, ld1, work, length, iwork, length1, info );
    }

    public double dlamch( String safeMinimum ) {
        return lapack.dlamch( safeMinimum );
    }

    public void dsyevr( String netlib, String netlib1, String netlib2, int n, double[] doubles, int ld, int i, int i1, int i2, int i3, double abstol, intW intW, double[] doubles1, double[] doubles2, int ld1, int[] isuppz, double[] worksize, int i4, int[] iworksize, int i5, intW info ) {
        lapack.dsyevr( netlib, netlib1, netlib2, n, doubles, ld, i, i1, i2, i3, abstol, intW, doubles1, doubles2, ld1, isuppz, worksize, i4, iworksize, i5, info );
    }

    public void dspevd( String netlib, String netlib1, int n, double[] doubles, double[] doubles1, double[] doubles2, int ld, double[] worksize, int i, int[] iworksize, int i1, intW info ) {
        lapack.dspevd( netlib, netlib1, n, doubles, doubles1, doubles2, ld, worksize, i, iworksize, i1, info );
    }

    public void dstevr( String netlib, String netlib1, int n, double[] doubles, double[] doubles1, int i, int i1, int i2, int i3, double abstol, intW intW, double[] doubles2, double[] doubles3, int ld, int[] isuppz, double[] worksize, int i4, int[] iworksize, int i5, intW info ) {
        lapack.dstevr( netlib, netlib1, n, doubles, doubles1, i, i1, i2, i3, abstol, intW, doubles2, doubles3, ld, isuppz, worksize, i4, iworksize, i5, info );
    }

    public void dgtsv( int numRows, int i, double[] clone, double[] clone1, double[] clone2, double[] xd, int ld, intW info ) {
        lapack.dgtsv( numRows, i, clone, clone1, clone2, xd, ld, info );
    }
}
