/**
 * Copyright 2007-2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.dkpro.core.stanfordnlp;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.text.StringReader;
import de.tudarmstadt.ukp.dkpro.core.textnormalizer.transformation.JCasHolder;

public class StanfordPtbTransformerTest
{
    @Test
    public void test()
        throws Exception
    {
        String expected = "``Hey you!'', John said.";
        String input = "\"Hey you!\", John said.";

        AnalysisEngineDescription normalizer = createEngineDescription(StanfordPtbTransformer.class);

        assertTransformedText(expected, input, "en", normalizer);
    }
    
    public static void assertTransformedText(String normalizedText, String inputText,
            String language, AnalysisEngineDescription... aEngines)
            throws ResourceInitializationException
    {
        CollectionReaderDescription reader = createReaderDescription(StringReader.class,
                StringReader.PARAM_DOCUMENT_TEXT, inputText, StringReader.PARAM_LANGUAGE, language);


        List<AnalysisEngineDescription> engines = new ArrayList<AnalysisEngineDescription>();
        for (AnalysisEngineDescription e : aEngines) {
            engines.add(e);
        }

        engines.add(createEngineDescription(JCasHolder.class));


        for (JCas jcas : SimplePipeline.iteratePipeline(reader,
                engines.toArray(new AnalysisEngineDescription[engines.size()]))) {
            // iteratePipeline does not support CAS multipliers. jcas is not updated after the
            // multiplier. In order to access the new CAS, we use the JCasHolder (not thread-safe!)
            assertEquals(normalizedText, JCasHolder.get().getDocumentText());
        }
    }
}
