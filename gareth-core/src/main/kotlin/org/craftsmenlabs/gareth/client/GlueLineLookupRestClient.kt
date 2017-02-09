package org.craftsmenlabs.gareth.client

import org.craftsmenlabs.gareth.GlueLineLookup
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.time.DurationExpressionParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!mock")
class GlueLineLookupRestClient : GlueLineLookup {

    val log: Logger = LoggerFactory.getLogger(GlueLineLookupRestClient::class.java)

    @Autowired
    private lateinit var client: ExecutionRestClient

    @Autowired
    private lateinit var durationExpressionParser: DurationExpressionParser

    override fun isExperimentReady(experiment: Experiment): Boolean {
        val details = experiment.details
        val lines = mapOf<String, String>(
                Pair("assume", details.assume),
                Pair("baseline", details.baseline),
                Pair("failure", details.failure),
                Pair("success", details.success))
        val isValidTime = durationExpressionParser.parse(details.time) != null || client.isValidTimeGlueLine(details.time)
        return isValidTime && lines.all { client.isValidGlueLine(it.key, it.value) }
    }
}