package org.craftsmenlabs.gareth.rest.example;

import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.rest.RestServiceFactoryImpl;
import org.craftsmenlabs.gareth.rest.example.definition.SampleDefinition;

/**
 * Hello world!
 *
 */
public class GarethRestApplication
{
    public static void main( String[] args )
    {
        final RestServiceFactory restServiceFactory = new RestServiceFactoryImpl(); // Create a new rest service factory

        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfigImpl
                .Builder()
                .addDefinitionClass(SampleDefinition.class)
                .addInputStreams(GarethRestApplication.class.getClass().getResourceAsStream("/experiments/businessgoal-01.experiment"))
                .setIgnoreInvocationExceptions(true)
                .build();
        final ExperimentEngine experimentEngine = new ExperimentEngineImpl
                .Builder(experimentEngineConfig)
                .setRestServiceFactory(restServiceFactory)
                .build();
        experimentEngine.start();
    }
}