package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class ExecuteBaselineMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentStorage: ExperimentStorage,
        private val glueLineExecutor: GlueLineExecutor)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.WAITING_FOR_BASELINE) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .map { it.copy(environment = glueLineExecutor.executeBaseline(it).environment) }
                .map { it.copy(timing = it.timing.copy(baselineExecuted = dateTimeService.now())) }
    }
}
