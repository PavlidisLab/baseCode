/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.math;

import baseCode.util.RCommand;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class LinearModel {

    double[] coefficients;
    double[] pValues;
    double[][] designMatrix;
    double[] variable;

    private RCommand rc;

    public LinearModel( double[] variable, double[] a, double[] b ) {
        rc = RCommand.newInstance();
        rc.assign( "y", variable );
        rc.assign( "a", a );
        rc.assign( "b", b );
    }

    public void fitNoInteractions() {
        rc.voidEval( "m<-lm(y~a+b)" );
        coefficients = ( double[] ) rc.eval( "coefficients(m)" ).getContent();
    }

    public double[] getCoefficients() {
        return this.coefficients;
    }

}
