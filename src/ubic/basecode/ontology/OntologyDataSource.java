/*
 * The baseCode project
 * 
 * Copyright (c) 2010 University of British Columbia
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

package ubic.basecode.ontology;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;

/**
 * A pooled datasource for Ontology data
 * 
 * @author paul
 * @version $Id$
 */
public class OntologyDataSource {

    private static Log log = LogFactory.getLog( OntologyDataSource.class );

    private static BasicDataSource dataSource;

    static {
        String dbUrl = Configuration.getString( "jena.db.url" );
        String driver = Configuration.getString( "jena.db.driver" );

        assert driver != null;
        try {
            Class.forName( driver );
        } catch ( Exception e ) {
            log.error( "Failed to load driver: " + driver );
            throw new RuntimeException( e );
        }

        String pwd = Configuration.getString( "jena.db.password" );

        String user = Configuration.getString( "jena.db.user" );

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName( driver );
        dataSource.setUsername( user );
        dataSource.setPassword( pwd );
        dataSource.setUrl( dbUrl );

        /*
         * Connections: how many, how long we wait until failing.
         */
        dataSource.setMaxActive( 20 );
        dataSource.setMaxWait( 20000L );
        dataSource.setInitialSize( 4 );

    }

    /**
     * @return a connection to the Ontology store
     */
    public static IDBConnection getConnection() {
        String type = Configuration.getString( "jena.db.type" );
        try {
            return new DBConnection( dataSource.getConnection(), type );
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dataSource.close();
    }

}
