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

import java.util.ArrayList;
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

    private DoubleMatrix<Integer, Integer> sMatrix;

    private DoubleMatrix<R, Integer> uMatrix;

    private DoubleMatrix<Integer, C> vMatrix;

    /**
     * @param matrix
     */
    public SingularValueDecomposition( DoubleMatrix<R, C> matrix ) {
        double[][] mat = matrix.getRawMatrix();
        DoubleMatrix2D dm = new DenseDoubleMatrix2D( mat );
        this.rowNames = matrix.getRowNames();
        this.columnNames = matrix.getColNames();
        this.svd = new cern.colt.matrix.linalg.SingularValueDecomposition( dm );

        List<Integer> componentIds = new ArrayList<Integer>();
        for ( int i = 0; i < columnNames.size(); i++ ) {
            componentIds.add( i );
        }

        this.uMatrix = new DenseDoubleMatrix<R, Integer>( svd.getU().toArray() );
        uMatrix.setRowNames( this.rowNames );
        uMatrix.setColumnNames( componentIds );

        this.vMatrix = new DenseDoubleMatrix<Integer, C>( svd.getV().toArray() );
        vMatrix.setRowNames( componentIds );
        vMatrix.setColumnNames( this.columnNames );

        this.sMatrix = new DenseDoubleMatrix<Integer, Integer>( svd.getS().toArray() );
        sMatrix.setRowNames( componentIds );
        sMatrix.setColumnNames( componentIds );
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
    public DoubleMatrix<Integer, Integer> getS() {
        return this.sMatrix;
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
    public DoubleMatrix<R, Integer> getU() {
        return this.uMatrix;

    }

    /**
     * @return
     * @see cern.colt.matrix.linalg.SingularValueDecomposition#getV()
     */
    public DoubleMatrix<Integer, C> getV() {
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
