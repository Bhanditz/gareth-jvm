package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;


public class RestServiceFactoryImplTest {


    private RestServiceFactoryImpl restServiceFactory;

    @Mock
    private ExperimentEngineImpl mockExperimentEngine;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        restServiceFactory = new RestServiceFactoryImpl();
    }

    @Test
    public void testCreate() throws Exception {
        final RestServiceImpl restService1 = restServiceFactory.create(mockExperimentEngine, 80);
        final RestServiceImpl restService2 = restServiceFactory.create(mockExperimentEngine, 80);
        assertNotNull(restService1);
        assertNotNull(restService2);
        assertNotSame(restService1, restService2);
    }
}