/*
 * The baseCode project
 * 
 * Copyright (c) 2009 University of British Columbia
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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;

/**
 * Originally based on: PersistentOntology.java Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development
 * Company, LP
 * 
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id$
 */
public class PersistentOntology {

    /**
     * @param maker
     * @return
     */
    public OntModelSpec getModelSpec( ModelMaker maker ) {
        OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM_RDFS_INF );
        spec.setImportModelMaker( maker );
        return spec;
    }

    /**
     * @return
     */
    public Model getRDBMaker() {
        try {
            // Create database connection
            SDBConnection conn = OntologyDataSource.getConnection();
            StoreDesc storeDesc = new StoreDesc( LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL );
            Store store = SDBFactory.connectStore( conn, storeDesc );
            Model model = SDBFactory.connectDefaultModel( store );
            return model;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    /**
     * @param maker
     * @param source
     */
    public OntModel loadDB( ModelMaker maker, String source ) {
        // use the model maker to get the base model as a persistent model
        // strict=false, so we get an existing model by that name if it exists
        // or create a new one
        Model base = maker.createModel( source, false );

        // now we plug that base model into an ontology model that also uses
        // the given model maker to create storage for imported models
        OntModel m = ModelFactory.createOntologyModel( getModelSpec( maker ), base );

        // now load the source document, which will also load any imports
        m.read( source );

        return m;
    }

}
