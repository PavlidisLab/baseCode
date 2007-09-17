/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.gui;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test application (not a unit test)
 * 
 * @author pavlidis
 * @version $Id$
 */
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
