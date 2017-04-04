package org.craftsmenlabs.gareth.monitors

import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.DurationCalculator
import org.craftsmenlabs.gareth.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable
import java.util.concurrent.TimeUnit

@Service
class ExecuteAssumeMonitor @Autowired constructor(
        experimentProvider: ExperimentProvider,
        dateTimeService: TimeService,
        experimentStorage: ExperimentStorage,
        private val experimentExecutor: ExperimentExecutor,
        private val durationCalculator: DurationCalculator)
    : BaseMonitor(
        experimentProvider, dateTimeService, experimentStorage, ExperimentState.BASELINE_EXECUTED) {
    private val delayedExperiments = mutableListOf<String>()

    override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
        return observable
                .filter { !delayedExperiments.contains(it.id) }
                .delay {
                    val delayInSeconds = durationCalculator.getDifferenceInSeconds(
                            dateTimeService.now(),
                            it.timing.baselineExecuted,
                            durationCalculator.getDuration(it))
                    delayedExperiments.add(it.id)
                    val delay = Observable.just(it).delay(delayInSeconds, TimeUnit.SECONDS)
                    return@delay delay
                }
                .map {
                    val finalizedExperiment = experimentExecutor.executeAssume(it)
                    delayedExperiments.remove(finalizedExperiment.id)
                    finalizedExperiment
                }
    }

}
