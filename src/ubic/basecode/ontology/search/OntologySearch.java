package ubic.basecode.ontology.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class OntologySearch {

    // Lucene cannot properly parse these characters... gives a query parse error.
    // OntologyTerms don't contain them anyway
    private final static char[] INVALID_CHARS = {':', '(', ')', '?', '^', '[', ']', '{', '}', '!', '~', '"', '\''};

    /**
     * Will remove characters that jena is unable to parse. Will also escape and remove leading and trailing white space
     * (which also causes jena to die)
     *
     * @param toStrip the string to clean
     * @return
     */
    public static String stripInvalidCharacters( String toStrip ) {
        String result = StringUtils.strip( toStrip );
        for ( char badChar : INVALID_CHARS ) {
            result = StringUtils.remove( result, badChar );
        }
        /*
         * Queries cannot start with '*' or ?
         */
        result = result.replaceAll( "^\\**", "" );
        result = result.replaceAll( "^\\?*", "" );

        return StringEscapeUtils.escapeJava( result ).trim();
    }
}
