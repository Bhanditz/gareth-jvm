package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.ExecutionStatus
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth2.GlueLineExecutor
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class ExecuteSuccessMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentStorage: ExperimentStorage,
        private val glueLineExecutor: GlueLineExecutor)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.WAITING_FOR_FINALISATION) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .filter { it.results.status == ExecutionStatus.SUCCESS }
                .map { it.copy(environment = glueLineExecutor.executeSuccess(it).environment) }
                .map { it.copy(timing = it.timing.copy(finalizingExecuted = dateTimeService.now())) }
    }
}
