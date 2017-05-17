/*
 * The baseCode project
 *
 * Copyright (c) 2017 University of British Columbia
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

package ubic.basecode.math.linearmodels;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.io.reader.DoubleMatrixReader;
import ubic.basecode.io.reader.StringMatrixReader;
import ubic.basecode.math.linearmodels.DesignMatrix;
import ubic.basecode.math.linearmodels.GenericAnovaResult;
import ubic.basecode.math.linearmodels.LeastSquaresFit;
import ubic.basecode.math.linearmodels.LinearModelSummary;
import ubic.basecode.math.linearmodels.ModeratedTstat;
import ubic.basecode.util.RegressionTesting;

/**
 * TODO Document Me
 *
 * @author paul
 */
public class ModeratedTstatTest {

    @Test
    public void testLimma() throws Exception {

        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testMatrix = f.read( this.getClass().getResourceAsStream(
                "/data/limmatest.data.txt" ) );

        StringMatrixReader of = new StringMatrixReader();
        StringMatrix<String, String> sampleInfo = of.read( this.getClass()
                .getResourceAsStream( "/data/limmatest.design.txt" ) );

        DesignMatrix d = new DesignMatrix( sampleInfo, true );

        LeastSquaresFit fit = new LeastSquaresFit( d, testMatrix );
        Map<String, LinearModelSummary> sums = fit.summarizeByKeys( true );
        assertEquals( 100, sums.size() );

        for ( LinearModelSummary lms : sums.values() ) {
            GenericAnovaResult a = lms.getAnova();
            assertNotNull( a );
        }

        LinearModelSummary s = sums.get( "Gene 1" );
        assertNotNull( s );
        assertEquals( 0.2264, s.getContrastCoefficients().get( 0, 0 ), 0.001 );
        //
        f = new DoubleMatrixReader();

        DoubleMatrix<String, String> expectedEffects = f.read( this.getClass().getResourceAsStream(
                "/data/limmatest.fit.effects.txt" ) );
        double[] expEffects1 = expectedEffects.getRowByName( "Gene 1" );

        Double[] effects = s.getEffects();
        assertArrayEquals( ArrayUtils.subarray( expEffects1, 0, 2 ), ArrayUtils.toPrimitive( effects ), 1e-7 );

        Double[] stdevUnscaled = s.getStdevUnscaled(); //
        assertEquals( 0.5773502692, stdevUnscaled[0], 1e-8 );
        assertEquals( 0.8164965809, stdevUnscaled[1], 1e-8 );

        Double sigma = s.getSigma();
        assertEquals( 0.3069360050, sigma, 0.0001 );

        ModeratedTstat.ebayes( fit );
        DoubleMatrix1D squeezedVars = fit.getVarPost();
        DoubleMatrix<String, String> expectedvars = f.read( this.getClass().getResourceAsStream(
                "/data/limmatest.fit.squeezevar.txt" ) );

        assertArrayEquals( expectedvars.viewColumn( 0 ).toArray(), squeezedVars.toArray(), 1e-7 );

        sums = fit.summarizeByKeys( true );

    }

    @Test
    public void testFdist() {
        double[] x = new double[] { 0.30232520254346584299,
                1.2587139907883573287,
                2.0240947459164679856,
                1.3173359748527122548,
                1.2939746589607614702,
                0.94840671150632327446,
                0.61447313184876728442,
                0.8902037123242685368,
                1.6434385371581794466,
                1.5338456384344794081,
                1.8693396370149581998,
                2.4611664510167110542,
                0.63397720519047617849,
                1.7349622569136011752,
                1.0498928059690204595,
                0.47002356809001444304,
                0.96196714542170125295,
                1.5599374206292948575,
                0.71751554483321655642,
                1.063604497013816097,
                1.3053827692251576131,
                2.3189475705362436742,
                1.3580619393152200125,
                0.95174911537147166563,
                0.43469878517674137575,
                2.2143189808841414745,
                2.9474034257347128118,
                1.1991277120337802131,
                1.1784342356369026383,
                1.5785961856334371767,
                1.7796216292386706215,
                0.74972769329538446748,
                1.0536395435846341861,
                0.43047612065723450669,
                1.6601774958162751616,
                1.2771936358386075661,
                0.46603761724121628429,
                0.79398642097171134857,
                1.1773795371506889929,
                1.2395685899737831637,
                0.46982243224851727437,
                1.4858828634002363422,
                1.6126603974023236976,
                0.4268711755609173597,
                2.0034615521916472325,
                2.4145616290101932222,
                3.0194248816641504618,
                3.0159052575760272319,
                8.0343270926054497494,
                0.13498271661933478049,
                1.6427943654309600241,
                1.1641007130379361634,
                1.4911776412456962948,
                0.76670333277130309213,
                1.4521154624148258083,
                1.6509375299627260247,
                0.24270003666993020253,
                1.4791928651055363808,
                1.1217945174234764671,
                1.3626624361552859277,
                0.33959672095353571342,
                0.34115286592320381853,
                1.2846832678451005627,
                0.74447709249622817662,
                2.7931665832011107753,
                0.43258102179980667534,
                1.8698561980559311735,
                1.3895517560704246929,
                0.87525496922730172678,
                1.2515572943043009602,
                0.89447243726299607847,
                1.047010435680864715,
                2.4232153061673851191,
                1.7072766276669044672,
                1.2815027503208382686,
                0.88618459460442955411,
                0.75360919523644709361,
                0.464584291292655438,
                0.41517499402172436396,
                0.46995735844141434123,
                0.49873665629095587093,
                0.47448118961970647822,
                0.65749687756215469125,
                1.1703813632124464572,
                0.96751033642495487541,
                3.1811230382103570236,
                1.7676684213405762236,
                2.3023718075763692781,
                1.0511880790473360214,
                1.1018508289489394869,
                0.66448593865758720511,
                1.2717064171054943689,
                0.28427148223900100543,
                0.60061815966622811302,
                0.62610631792546678209,
                0.58023360243084076693,
                0.73838780576776485987,
                1.6227135882232157638,
                0.59470012513348813332,
                0.91864958973763821692 };

        // x was generated with x <- rf(100,df1=8,df2=16).
        // fitFDist(x, df1 = 8)
        double[] expected = new double[] { 1.1056298866365792399, 14.544682227013733922 };
        double[] actual = ModeratedTstat.fitFDist( new DenseDoubleMatrix1D( x ), 8 );
        assertTrue( RegressionTesting.closeEnough( actual, expected, 1e-10 ) );

    }

    @Test
    public void testSqueezeVar() {

        double[] s2 = new double[] { 1.7837804639416141583,
                0.32411186620655069168,
                0.18750362204593082338,
                0.7340608406949435949,
                0.84846200919003345042,
                0.49854708464318148176,
                0.87067912044480400002,
                2.1014900506235241195,
                0.49783053618211009494,
                1.9627067036621308471,
                1.0949289573268001785,
                0.45883145861230423268,
                2.2923386473988114354,
                2.7849256010365417424,
                0.73148557590083596036,
                1.4929731181234273674,
                1.001362671921577352,
                1.6273398718171947497,
                0.56578600627572539494,
                0.48078128591210089748 };

        // s2 <- rchisq(20,df=5)/5

        //    squeezeVar(s2, df=5)$var.post
        double[] expected = new double[] { 1.1346316090723296277,
                1.0235740856472144156,
                1.0131803748422600897,
                1.0547646708992308717,
                1.0634687770708084464,
                1.0368458264983575479,
                1.0651591452526298909,
                1.1588042465600849606,
                1.0367913085772184623,
                1.148245045087848748,
                1.0822209848724206882,
                1.0338241001454639978,
                1.1733247839888614195,
                1.2108028027853292574,
                1.0545687342800276198,
                1.1125058034815864527,
                1.0751020813423211031,
                1.1227289725756237626,
                1.0419616371185715931,
                1.0354941322769402046 };

        double[] actual = ModeratedTstat.squeezeVar( new DenseDoubleMatrix1D( s2 ), 5, null ).toArray();
        assertTrue( RegressionTesting.closeEnough( actual, expected, 1e-10 ) );

    }

}
