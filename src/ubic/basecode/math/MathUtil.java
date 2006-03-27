package ubic.basecode.math;

public class MathUtil {

    /**
     * Create an array filled with the integer values from a to b, inclusive. Works if a<b, a>b or a==b.
     * 
     * @param a
     * @param b
     * @return
     */
    public static int[] fillRange( int a, int b ) {
        int[] result = new int[Math.abs( b - a ) + 1];
        for ( int i = 0; i <= Math.abs( b - a ); i++ ) {
            if ( a < b ) {
                result[i] = a + i;
            } else {
                result[i] = a - i;
            }
        }
        return result;
    }

    /**
     * @param array of integers
     * @return The sum of the values in the array.
     */
    public static int sumArray( int[] array ) {
        if ( array == null ) return 0;
        int result = 0;
        for ( int i = 0; i < array.length; i++ ) {
            result += array[i];
        }
        return result;
    }

}