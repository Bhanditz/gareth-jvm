package org.craftsmenlabs.gareth.rest.v2.resources;

import org.craftsmenlabs.gareth.core.ExperimentEngine;
import org.craftsmenlabs.gareth.core.context.ExperimentContext;
import org.craftsmenlabs.gareth.rest.assembler.Assembler;
import org.craftsmenlabs.gareth.rest.v2.assembler.ExperimentAssembler;
import org.craftsmenlabs.gareth.rest.v2.entity.Experiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v2/experiments")
@EnableAutoConfiguration
@CrossOrigin
public class ExperimentResource {

    private ExperimentEngine experimentEngine;

    @Autowired
    public ExperimentResource(ExperimentEngine experimentEngine) {
        this.experimentEngine = experimentEngine;
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON})
    public List<Experiment> get() {
        return assembleExperiments(experimentEngine
                .getExperimentContexts());
    }

    private List<Experiment> assembleExperiments(final List<ExperimentContext> experimentContexts) {
        final Assembler<ExperimentContext, Experiment> assembler = new ExperimentAssembler();
        final List<Experiment> experiments = new ArrayList<>();
        for (final ExperimentContext experimentContext : experimentContexts) {
            experiments.add(assembler.assembleOutbound(experimentContext));
        }

        return experiments;
    }
}
