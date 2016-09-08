package org.craftsmenlabs.gareth.rest.v1.resource;

import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.core.context.ExperimentContextImpl;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v1.assembler.ExperimentAssembler;
import org.craftsmenlabs.gareth.rest.v1.entity.Experiment;
import org.craftsmenlabs.gareth.rest.v1.media.GarethMediaType;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/experiments")
public class ExperimentResource {

    @Inject
    private ExperimentEngineImpl experimentEngine;

    @GET
    @Produces({GarethMediaType.APPLICATION_JSON_EXPERIMENTS_V1, MediaType.APPLICATION_JSON})
    public Response get() {
        return Response
                .status(200)
                .entity(new GenericEntity<List<Experiment>>(assembleExperiments(experimentEngine
                        .getExperimentContexts())) {
                })
                .build();
    }

    private List<Experiment> assembleExperiments(final List<ExperimentContextImpl> experimentContexts) {
        final Assembler<ExperimentContextImpl, Experiment> assembler = new ExperimentAssembler();
        final List<Experiment> experiments = new ArrayList<>();
        for (final ExperimentContextImpl experimentContext : experimentContexts) {
            experiments.add(assembler.assembleOutbound(experimentContext));
        }
        return experiments;
    }
}
