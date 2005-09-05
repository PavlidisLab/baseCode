package baseCode.dataFilter;

import baseCode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.dataStructure.matrix.NamedMatrix;

/**
 * Filter that removes individual values that are outside of a range. Removed values are set to NaN.
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Pavlidis
 * @version $Id$
 */
public class ItemLevelFilter extends AbstractLevelFilter {

    public NamedMatrix filter( NamedMatrix data ) {
        if ( !( data instanceof DoubleMatrixNamed ) ) {
            throw new IllegalArgumentException( "Only valid for DoubleMatrixNamed" );
        }

        if ( lowCut == -Double.MAX_VALUE && highCut == Double.MAX_VALUE ) {
            log.info( "No filtering requested" );
            return data;
        }

        int numRows = data.rows();
        int numCols = data.columns();
        DoubleMatrixNamed returnval = DoubleMatrix2DNamedFactory.dense( numRows, numCols );
        for ( int i = 0; i < numRows; i++ ) {

            for ( int j = 0; j < numCols; j++ ) {

                double newVal = ( ( DoubleMatrixNamed ) data ).get( i, j );
                if ( newVal < lowCut || newVal > highCut ) {
                    newVal = Double.NaN;
                }

                returnval.set( i, j, newVal );
            }
        }
        returnval.setColumnNames( data.getColNames() );
        returnval.setRowNames( data.getRowNames() );

        return returnval;
    }

}