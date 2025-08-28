package com.github.fommil.netlib;

import org.netlib.util.doubleW;
import org.netlib.util.intW;

/**
 * @deprecated use {@link dev.ludovic.netlib.arpack.ARPACK} instead
 */
@Deprecated
public class ARPACK {

    public static ARPACK getInstance() {
        return new ARPACK( dev.ludovic.netlib.arpack.ARPACK.getInstance() );
    }

    private final dev.ludovic.netlib.arpack.ARPACK arpack;

    private ARPACK( dev.ludovic.netlib.arpack.ARPACK arpack ) {
        this.arpack = arpack;
    }

    public void dsaupd( intW ido, String bmat, int n, String which, int val, doubleW tol, double[] resid, int ncv, double[] v, int n1, int[] iparam, int[] ipntr, double[] workd, double[] workl, int length, intW info ) {
        arpack.dsaupd( ido, bmat, n, which, val, tol, resid, ncv, v, n1, iparam, ipntr, workd, workl, length, info );
    }

    public void dseupd( boolean b, String a, boolean[] select, double[] d, double[] z, int n, int i, String bmat, int n1, String which, intW nev, double tol, double[] resid, int ncv, double[] v, int n2, int[] iparam, int[] ipntr, double[] workd, double[] workl, int length, intW info ) {
        arpack.dseupd( b, a, select, d, z, n, i, bmat, n1, which, nev, tol, resid, ncv, v, n2, iparam, ipntr, workd, workl, length, info );
    }
}
