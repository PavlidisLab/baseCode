package baseCode.xml;

import javax.xml.parsers.*;
import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import baseCode.dataStructure.OntologyEntry;
import baseCode.dataStructure.graph.DirectedGraph;

/**
 *
 *
 * <p>Copyright (c) Columbia University
 * @author Paul Pavlidis
 * @version $Id$
 */
public class GOParser {

   private DirectedGraph m;

	public DirectedGraph getGraph() {
		return m;
	}

   public GOParser(InputStream i)
      throws
         IOException,
         ParserConfigurationException,
         SAXException,
         ParserConfigurationException {
      System.setProperty(
         "org.xml.sax.driver",
         "org.apache.xerces.parsers.SAXParser");

      XMLReader xr = XMLReaderFactory.createXMLReader();
      GOHandler handler = new GOHandler();
      xr.setContentHandler(handler);
      xr.setErrorHandler(handler);
      xr.setDTDHandler(handler);
      xr.parse(new InputSource(i));

      m = handler.getResults();
   }

}

class GOHandler extends DefaultHandler {

   private DirectedGraph m;

   public DirectedGraph getResults() {
      return m;
   }

   public GOHandler() {
      super();
      m = new DirectedGraph();
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

   public void startElement(
      String uri,
      String name,
      String qName,
      Attributes atts) {

    if (name.equals("term")) {
         inTerm = true;
      } else if (name.equals("accession")) {
         accBuf = new StringBuffer();
         inAcc = true;
      } else if (name.equals("definition")) {
         defBuf = new StringBuffer();
         inDef = true;
      } else if (name.equals("is_a")) {
         inIsa = true;
         String res = atts.getValue("rdf:resource");
         String parent = res.substring(res.lastIndexOf('#') + 1, res.length());

         if (!m.containsKey(parent)) {
            m.addNode(
               parent,
               new OntologyEntry(parent, "no name yet", "no definition yet"));
         }
         String currentTerm = accBuf.toString();
         m.addParentTo(currentTerm, parent);

      } else if (name.equals("part_of")) {
         inPartOf = true;
         String res = atts.getValue("rdf:resource");
         String parent = res.substring(res.lastIndexOf('#') + 1, res.length());

         if (!m.containsKey(parent)) {
            m.addNode(
               parent,
               new OntologyEntry(parent, "no name yet", "no definition yet"));
         }
         String currentTerm = accBuf.toString();
         m.addParentTo(currentTerm, parent);
      } else if (name.equals("synonym")) {
         inSyn = true;
      } else if (name.equals("name")) {
         nameBuf = new StringBuffer();
         inName = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (name.equals("term")) {
         inTerm = false;
      } else if (name.equals("accession")) {
         inAcc = false;
         String currentTerm = accBuf.toString();
         m.addNode(currentTerm, new OntologyEntry(currentTerm, "no name yet", "no definition yet"));
      } else if (name.equals("definition")) {
         String currentTerm = accBuf.toString();
         ((OntologyEntry)m.getNodeContents(currentTerm)).setDefinition(
            defBuf.toString());
         inDef = false;
      } else if (name.equals("is_a")) {
         inIsa = false;
      } else if (name.equals("part_of")) {
         inPartOf = false;
      } else if (name.equals("synonym")) {
         inSyn = false;
      } else if (name.equals("name")) {
         inName = false;
         String currentTerm = accBuf.toString();
         ((OntologyEntry)m.getNodeContents(currentTerm)).setName(
            nameBuf.toString());
      }
   }

   public void characters(char ch[], int start, int length) {

      if (inTerm) {
         if (inAcc) {
            accBuf.append(ch, start, length);
         } else if (inDef) {
            defBuf.append(ch, start, length);
         } else if (inName) {
            nameBuf.append(ch, start, length);
         }
      }
   }

}
