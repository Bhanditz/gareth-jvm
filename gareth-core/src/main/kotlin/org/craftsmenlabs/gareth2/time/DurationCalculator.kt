package org.craftsmenlabs.gareth2.time

import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class DurationCalculator @Autowired constructor(private val durationExpressionParser: DurationExpressionParser, private val executor: GlueLineExecutor) {

    fun getDuration(experiment: Experiment): Duration {
        val time = durationExpressionParser.parse(experiment.details.time)
        if (time != null) {
            return time
        } else {
            return executor.getDuration(experiment)
        }
    }

}