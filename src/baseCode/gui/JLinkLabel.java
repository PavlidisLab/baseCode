package baseCode.gui;

import javax.swing.JLabel;
import java.awt.Cursor;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * A clickable link label that contains a URL.
 * When a mouse pointer is placed over it, it turns into a hand.
 *
 * @author  Will Braynen
 */
public class JLinkLabel
       extends JLabel
       implements MouseListener {
   
   protected String m_url = null;
   protected String m_text = "";
          
   /** Creates a new instance of JLinkLabel */
   public JLinkLabel() {
      super();
      setCursor( new Cursor( Cursor.HAND_CURSOR ) );
      addMouseListener( this );
   }
   
   public JLinkLabel( String text ) {
      this();
      setText( text );
   }

   public JLinkLabel( String text, String url ) {
      this();
      setText( text, url );
   }
   
   public void setText( String text ) {
      if ( m_url != null ) {
         setText( text, m_url );
      }
      else {
         setText( text, text );
      }
   }
   
   public void setURL( String url ) {
      setText( m_text, url );
   }
      
   public String getURL() {
      return m_url;
   }

   public void setText( String text, String url ) {
      m_text = text;
      m_url = url;
      
      super.setText( "<html><a href=\"" + url + "\">" + text + "</a></html>" );
   }
   
   public void mouseClicked( MouseEvent e ) {
      if ( m_url != null ) {
         JWebBrowser webBrowser = new JWebBrowser( m_url );
      }      
   }
   
   public void mouseEntered( MouseEvent e ) {}
   public void mouseExited( MouseEvent e ) {}
   public void mousePressed( MouseEvent e ) {}
   public void mouseReleased( MouseEvent e ) {}
   
}
