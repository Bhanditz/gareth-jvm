package org.craftsmenlabs.gareth.rest.v1.resource;

import org.craftsmenlabs.gareth.api.exception.GarethUnknownExperimentException;
import org.craftsmenlabs.gareth.api.model.AssumptionBlock;
import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExperimentRerunResourceTest {

    @Mock
    private ExperimentEngine experimentEngine;

    private ExperimentContext experimentContext;

    @InjectMocks
    private ExperimentRerunResource experimentRerunResource;


    @Before
    public void before() {
        experimentRerunResource = new ExperimentRerunResource();
        experimentContext = new ExperimentContext.Builder("experiment", new AssumptionBlock()).build("hash");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRerunExperiment() throws Exception {
        final String hash = "hash";
        when(experimentEngine.findExperimentContextForHash(hash)).thenReturn(experimentContext);
        final Response response = experimentRerunResource.rerunExperiment(hash);
        verify(experimentEngine).planExperimentContext(experimentContext);
        assertEquals(202, response.getStatus());
    }

    @Test
    public void testRerunExperimentWithUnknownExperiment() throws Exception {
        final String hash = "hash";
        when(experimentEngine.findExperimentContextForHash(hash)).thenThrow(new GarethUnknownExperimentException("b"));
        final Response response = experimentRerunResource.rerunExperiment(hash);
        verify(experimentEngine, never()).planExperimentContext(any());
        assertEquals(404, response.getStatus());
    }
}