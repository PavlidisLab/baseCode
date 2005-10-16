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

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class StringDistance {

    /**
     * The Hamming distance H is defined only for strings of the same length. For two strings s and t, H(s, t) is the
     * number of places in which the two string differ, i.e., have different characters.
     * 
     * @param a
     * @param b
     * @return
     */
    public static int hammingDistance( String a, String b ) {
        if ( a.length() != b.length() ) throw new IllegalArgumentException( "Strings must be the same length" );
        int result = 0;
        for ( int i = 0; i < a.length(); i++ ) {
            result += a.charAt( i ) == b.charAt( i ) ? 0 : 1;
        }
        return result;
    }

    /**
     * The edit distance counts the differences between two strings, where we would count a difference not only when
     * strings have different characters but also when one has a character whereas the other does not. The formal
     * definition follows.
     * <p>
     * For a string s, let s(i) stand for its ith character. For two characters a and b, define r(a, b) = 0 if a = b.
     * Let r(a, b) = 1, otherwise.
     * <p>
     * Assume we are given two strings s and t of length n and m, respectively. We are going to fill an (n+1) x (m+1)
     * array d with integers such that the low right corner element d(n+1, m+1) will furnish the required values of the
     * Levenshtein distance L(s, t).
     * <p>
     * The definition of entries of d is recursive. First set d(i, 0) = i, i = 0, 1,..., n, and d(0, j) = j, j = 0, 1,
     * ..., m. For other pairs i, j use (1) d(i, j) = min(d(i-1, j)+1, d(i, j-1)+1, d(i-1, j-1) + r(s(i), t(j)))
     * <p>
     * That last step is also described as:
     * <li>Set cell d[i,j] of the matrix equal to the minimum of:<br>
     * a. The cell immediately above plus 1: d[i-1,j] + 1.<br>
     * b. The cell immediately to the left plus 1: d[i,j-1] + 1.<br>
     * c. The cell diagonally above and to the left plus the cost: d[i-1,j-1] + cost.<br>
     * <p>
     * (Description partly cribbed from http://www.cut-the-knot.org/do_you_know/Strings.shtml and
     * http://www.merriampark.com/ld.htm)
     * </p>
     * 
     * @param a
     * @param b
     * @return
     */
    public static int editDistance( String s, String t ) {
        int n = s.length();
        int m = t.length();

        if ( n == 0 ) return m;
        if ( m == 0 ) return n;

        // matrix has m+1 rows and n+1 columns.
        int[][] mat = new int[m + 1][n + 1];

        // initialize all to zero except for first row and columns.
        for ( int i = 0; i < mat.length; i++ ) {
            for ( int j = 0; j < mat[i].length; j++ ) {
                mat[i][j] = 0;
                mat[0][j] = j;
            }
            mat[i][0] = i;
        }

        // recursively fill in the matrix

        for ( int col = 1; col <= n; col++ ) {
            char sc = s.charAt( col - 1 );

            for ( int row = 1; row <= m; row++ ) {
                char tc = t.charAt( row - 1 );

                // minimum of: cell directly above + 1, the cell to the left + 1 and the cell above to the left + cost.
                int p = mat[row - 1][col] + 1; // above
                int q = mat[row][col - 1] + 1; // to the left
                int r = mat[row - 1][col - 1] + ( sc == tc ? 0 : 1 ); // above left + cost

                mat[row][col] = Math.min( Math.min( p, q ), r );
            }

            // // debug
            // for ( int k = 0; k < mat.length; k++ ) {
            // for ( int e = 0; e < mat[k].length; e++ ) {
            // System.err.print( mat[k][e] + " " );
            // }
            // System.err.print( "\n" );
            //            }
            //            System.err.print( "\n" );
        }

        return mat[m][n];
    }
}
