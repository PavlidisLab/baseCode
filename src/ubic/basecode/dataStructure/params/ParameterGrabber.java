package ubic.basecode.dataStructure.params;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generates a parameter map from a class using reflection, usefull for storing experimental parameters
 * 
 * @author leon
 */
public class ParameterGrabber {
    public static String paramsToLine( Map<String, String> params ) {
        return paramsToLine( params, true );
    }

    
    public static String paramsToLine( Map<String, String> params, boolean sort ) {
        List<String> sortedKeys = new LinkedList<String>( params.keySet() );

        if ( sort ) Collections.sort( sortedKeys );

        String line = "";
        for ( String key : sortedKeys ) {
            line += key + "=" + params.get( key ) + ", ";
        }
        line = line.substring( 0, line.length() - 2 );
        return line;
    }

    /**
     * Given an instance of a class this method will extract its primitive variable names and their values using
     * reflection.
     * 
     * @param c - class
     * @param o - object
     * @return
     */
    public static Map<String, String> getParams( Class<?> c, Object o ) {
        // go up to parents?
        Map<String, String> params = new HashMap<String, String>();
        for ( Field f : c.getDeclaredFields() ) {
            // a bit pushy here
            f.setAccessible( true );
            String name = f.getName();
            String type = f.getType().toString();

            if ( ( type.startsWith( "class" ) || type.startsWith( "interface" ) )
                    && !type.equals( "class java.lang.String" ) ) continue;
            // System.out.format( "Name: %s%n", f.getName() );
            // System.out.format( "Type: %s%n", f.getType() );
            // System.out.format( "GenericType: %s%n", f.getGenericType() );
            try {
                // System.out.format( "Value: %s%n", f.get( this ) );
                // System.out.println( name + "=" + f.get( this ) );
                Object value = f.get( o );
                String stringValue = "" + value;
                params.put( name, stringValue );
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
        if ( !c.getSuperclass().equals( Object.class ) ) {
            Map<String, String> parents = getParams( c.getSuperclass(), o );
            params.putAll( parents );
        }
        return params;
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        // TODO Auto-generated method stub

    }

}
