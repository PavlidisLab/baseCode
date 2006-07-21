package ubic.basecode.gui;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import javax.swing.JFrame;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BufferedImageApp {
    private static Log log = LogFactory.getLog( BufferedImageApp.class );

    /**
     * 
     *
     */
    public static void main( String[] args ) {
        BufferedImage bi = new BufferedImage( 100, 100, 1 );
        Raster r = bi.getRaster();
        log.info( "raster: " + r );
        DataBuffer buffer = r.getDataBuffer();
        log.info( "data buffer: " + buffer );
        SampleModel sm = r.getSampleModel();

        log.info( "num data elements to transfer 1 pixel: " + r.getNumDataElements() );

        double[] array = new double[10];
        for ( int i = 0; i < array.length; i++ ) {
            array[i] = i * 20 + 3.3;
            buffer.setElemDouble( i, array[i] );
            log.info( "data in buffer: " + buffer.getElemDouble( i ) );
        }

        ColorModel colorModel = ColorModel.getRGBdefault();
        log.info( "color model: " + colorModel );
        log.info( bi.getData() );
        // Graphics2D g2 = bi.createGraphics();
        // g2.drawImage( bi, new AffineTransformOp( new AffineTransform(), 1 ), 0, 0 );
        // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // GraphicsDevice gd = ge.getDefaultScreenDevice();
        // GraphicsConfiguration gc = gd.getDefaultConfiguration();
        // JFrame f = new JFrame( gc );
        // Canvas c = new Canvas( gc );
        // f.getContentPane().add( c );
        // f.pack();
        // f.show();

    }

}
