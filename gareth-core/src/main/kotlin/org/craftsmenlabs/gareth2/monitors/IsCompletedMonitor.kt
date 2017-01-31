package org.craftsmenlabs.gareth2.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
class IsCompletedMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: DateTimeService,
        experimentStorage: ExperimentStorage)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.FINALISATION_EXECUTED) {

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .map { it.copy(timing = it.timing.copy(completed = dateTimeService.now())) }
    }
}
