/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.math;

import java.util.List;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;

/**
 * SVD for DoubleMatrix.
 * 
 * @author paul
 * @version $Id$
 */
public class SingularValueDecomposition<R, C> {

    private cern.colt.matrix.linalg.SingularValueDecomposition svd;
    private List<C> columnNames;
    private List<R> rowNames;
    private DoubleMatrix<C, C> cMatrix;

    private DoubleMatrix<R, C> uMatrix;

    private DenseDoubleMatrix<R, C> vMatrix;

    /**
     * @param matrix
     */
    public SingularValueDecomposition( DoubleMatrix<R, C> matrix ) {
        double[][] mat = matrix.getRawMatrix();
        DoubleMatrix2D dm = new DenseDoubleMatrix2D( mat );
        this.rowNames = matrix.getRowNames();
        this.columnNames = matrix.getColNames();
        this.svd = new cern.colt.matrix.linalg.SingularValueDecomposition( dm );

        this.uMatrix = new DenseDoubleMatrix<R, C>( svd.getU().toArray() );
        uMatrix.setRowNames( this.rowNames );
        uMatrix.setColumnNames( this.columnNames ); // not really

        this.vMatrix = new DenseDoubleMatrix<R, C>( svd.getV().toArray() );
        vMatrix.setRowNames( this.rowNames );
        vMatrix.setColumnNames( this.columnNames );

        this.cMatrix = new DenseDoubleMatrix<C, C>( svd.getS().toArray() );
        cMatrix.setRowNames( this.columnNames ); // not really
        cMatrix.setColumnNames( this.columnNames );
    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#cond()
     */
    public double cond() {
        return svd.cond();
    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#getS()
     */
    public DoubleMatrix<C, C> getS() {
        return this.cMatrix;
    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#getSingularValues()
     */
    public double[] getSingularValues() {
        return svd.getSingularValues();
    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#getU()
     */
    public DoubleMatrix<R, C> getU() {
        return this.uMatrix;

    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#getV()
     */
    public DoubleMatrix<R, C> getV() {
        return this.vMatrix;
    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#norm2()
     */
    public double norm2() {
        return svd.norm2();
    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#rank()
     */
    public int rank() {
        return svd.rank();
    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#toString()
     */
    public String toString() {
        return svd.toString();
    }

}
