package baseCode.xml;

import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import baseCode.dataStructure.*;

public class GOParser {

   private Graph m;

   public GOParser() throws IOException, ParserConfigurationException, SAXException,
       ParserConfigurationException {

      FileReader r = new FileReader( GOParser.class.getResource( "/data/go-termdb-sample.xml" ).
                                     getFile() );
      System.setProperty( "org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser" );
      XMLReader xr = XMLReaderFactory.createXMLReader();
      GOHandler handler = new GOHandler();
      xr.setContentHandler( handler );
      xr.setErrorHandler( handler );
      xr.parse( new InputSource( r ) );

      Graph m = handler.getResults();

      System.err.println(m.toString());

   }

}

class GOHandler
    extends DefaultHandler {

   private Graph m;

   public Graph getResults() { return m;}

   public GOHandler() {
      super();
      m = new Graph("head");
   }

   private boolean inTerm = false;
   private boolean inDef = false;
   private boolean inAcc = false;
   private boolean inName = false;
   private boolean inPartOf = false;
   private boolean inIsa = false;
   private boolean inSyn = false;

   private StringBuffer nameBuf;
   private StringBuffer accBuf;
   private StringBuffer defBuf;
   private String currentTerm;

   public void startElement( String uri, String name,
                             String qName, Attributes atts ) {

   //   System.err.println( "Element: " + name + " " + qName );

      if ( name.equals( "term" ) ) {
         inTerm = true;
      } else if ( name.equals( "accession" ) ) {
         accBuf = new StringBuffer();
         inAcc = true;
      } else if ( name.equals( "definition" ) ) {
         defBuf = new StringBuffer();
         inDef = true;
      } else if ( name.equals( "is_a" ) ) {
         inIsa = true;
         String res = atts.getValue("rdf:resource");
         String parent = res.substring(res.lastIndexOf('#') + 1, res.length());
      } else if ( name.equals( "part_of" ) ) {
         inPartOf = true;
         String res = atts.getValue("rdf:resource");
         String parent = res.substring(res.lastIndexOf('#') + 1, res.length());
      } else if ( name.equals( "synonym" ) ) {
         inSyn = true;
      } else if ( name.equals( "name" ) ) {
         nameBuf = new StringBuffer();
         inName = true;
      }
   }

   public void endElement( String uri, String name, String qName ) {
      if ( name.equals( "term" ) ) {
         inTerm = false;
      } else if ( name.equals( "accession" ) ) {
         inAcc = false;
         currentTerm = accBuf.toString();
         m.addMember(currentTerm, new OntologyEntry(currentTerm));
      } else if ( name.equals( "definition" ) ) {
         ((OntologyEntry)m.getMember(accBuf.toString())).setDefinition(defBuf.toString());
         inDef = false;
      } else if ( name.equals( "is_a" ) ) {
         inIsa = false;
      } else if ( name.equals( "part_of" ) ) {
         inPartOf = false;
      } else if ( name.equals( "synonym" ) ) {
         inSyn = false;
      } else if ( name.equals( "name" ) ) {
         inName = false;
         ((OntologyEntry)m.getMember(currentTerm)).setName(nameBuf.toString());
      }
   }

   public void characters( char ch[], int start, int length ) {

      if ( inTerm ) {
         if ( inAcc ) {
            accBuf.append( ch, start, length );
         } else if ( inDef ) {
            defBuf.append( ch, start, length );
         } else if ( inName ) {
            nameBuf.append( ch, start, length );
         }
         if ( inSyn ) {
//
         }

         if ( inIsa ) {
//
         }
         if ( inPartOf ) {
//
         }

      }
   }

   public void setDocumentLocator( Locator locator ) {}

   public void startDocument() throws SAXException {}

   public void endDocument() throws SAXException {}

   public void startPrefixMapping( String prefix, String uri ) throws SAXException {}

   public void endPrefixMapping( String prefix ) throws SAXException {}

   public void skippedEntity( String name ) throws SAXException {}

   public void ignorableWhitespace( char[] text, int start, int length ) throws SAXException {}

   public void processingInstruction( String target, String data ) throws SAXException {}

}
