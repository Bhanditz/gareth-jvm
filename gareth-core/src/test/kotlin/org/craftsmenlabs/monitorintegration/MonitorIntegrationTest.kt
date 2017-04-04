package org.craftsmenlabs.monitorintegration

import mockit.Expectations
import mockit.Injectable
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth.Application
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.GluelineValidator
import org.craftsmenlabs.gareth.model.*
import org.craftsmenlabs.gareth.monitors.BaseMonitor
import org.craftsmenlabs.gareth.time.TimeService
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class, MonitorTestConfig::class))
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test", "mock")
@Ignore
class MonitorIntegrationTest {

    @Autowired
    lateinit var dbSetup: DBSetup

    @Autowired
    lateinit var monitors: List<BaseMonitor>

    @Autowired
    lateinit var wrappedGlueLineExecutor: WrappedGlueLineExecutor

    @Autowired
    lateinit var wrappedGlueLineLookup: WrappedGluelineValidator

    @Autowired
    lateinit var wrappedDateTimeService: WrappedDateTimeService

    @Autowired
    lateinit var experimentStorage: ExperimentStorage

    val TEST_NAME = "TEST_NAME"
    val TEST_BASELINE = "TEST_BASELINE"
    val TEST_ASSUMPTION = "TEST_ASSUMPTION"
    val TEST_TIME = "2 seconds"
    val TEST_SUCCESS = "TEST_SUCCESS"
    val TEST_FAILURE = "TEST_FAILURE"
    val TEST_VALUE = 111
    val TEST_ID = "TEST_ID"

    val now = LocalDateTime.of(LocalDate.of(2000, 10, 1), LocalTime.of(0, 0, 0, 0))

    val localDateTimeCreated_20 = now.minusHours(4)
    val localDateTimeReady_21 = now.minusHours(3)
    val localDateTimeStarted_22 = now.minusHours(2)
    val localDateTimeWaitBaseline_23 = now.minusHours(1)
    val localDateTimeExecBaseline_now1 = now
    val localDateTimePlanAssume_now2 = now
    val localDateTimeWaitAssume_01 = now.plusHours(1)
    val localDateTimeExecAssume_02 = now.plusHours(2)
    val localDateTimeWaitFinalize_03 = now.plusHours(3)
    val localDateTimeExecFinalize_04 = now.plusHours(4)
    val localDateTimeCompleted_05 = now.plusHours(5)

    @Injectable lateinit var glueLineExecutor: GlueLineExecutor
    @Injectable lateinit var gluelineValidator: GluelineValidator
    @Injectable lateinit var dateTimeService: TimeService

    lateinit var experiment: Experiment

    @Before
    fun setUp() {
        val entity = dbSetup.createExperiment()
        object : Expectations() {
            init {
                dateTimeService.now()
                returns(localDateTimeReady_21,
                        localDateTimeStarted_22,
                        localDateTimeWaitBaseline_23,
                        localDateTimeExecBaseline_now1,
                        localDateTimePlanAssume_now2,
                        localDateTimeWaitAssume_01,
                        localDateTimeExecAssume_02,
                        localDateTimeWaitFinalize_03,
                        localDateTimeExecFinalize_04,
                        localDateTimeCompleted_05)
                minTimes = 0
            }
        }

        wrappedGlueLineExecutor.mock = glueLineExecutor
        wrappedGlueLineLookup.mock = gluelineValidator
        wrappedDateTimeService.mock = dateTimeService

        val details = Gluelines(
                baseline = TEST_BASELINE,
                assume = TEST_ASSUMPTION,
                time = TEST_TIME,
                success = TEST_SUCCESS,
                failure = TEST_FAILURE
        )
        val timing = ExperimentTiming(localDateTimeCreated_20)
        experiment = Experiment(id = entity.id.toString(), name = TEST_NAME, glueLines = details, timing = timing)
    }

    @Test
    fun shouldLoadAllMonitors_whenBooted() {
        assertThat(monitors).hasSize(9);
    }

    @Test
    fun shouldExecuteSuccessPipeline_whenExecutorReturnImmediateAndWithSuccess() {

        object : Expectations() {
            init {
                gluelineValidator.gluelinesAreValid(withAny(experiment.glueLines))
                result = true;

                glueLineExecutor.executeBaseline(withAny(experiment))
                times = 1

                glueLineExecutor.getDuration(withAny(experiment))
                times = 0

                glueLineExecutor.executeAssume(withAny(experiment))
                result = ExecutionResult(status = ExecutionStatus.SUCCESS, environment = ExperimentRunEnvironment())
                times = 1

            }
        }

        experimentStorage.updateExperiment(experiment)
        waitForPipeline()

        val storedExperiment = getStoredExperiment()
        var copiedExp = storedExperiment.copy(timing = storedExperiment.timing.copy(due = wrappedDateTimeService.now()))

        experimentStorage.updateExperiment(copiedExp)

        waitForAssumePlanning()

        val resultExp = getStoredExperiment()
        assertThat(resultExp.timing.created).isEqualTo(localDateTimeCreated_20)
        assertThat(resultExp.timing.due).isEqualTo(localDateTimeStarted_22)
        assertThat(resultExp.timing.baselineExecuted).isEqualTo(localDateTimeExecBaseline_now1)
        assertThat(resultExp.timing.completed).isEqualTo(localDateTimeCompleted_05)
    }

    @Test
    fun shouldExecuteFailurePipeline_whenExecutorReturnImmediateAndWithFailure() {

        object : Expectations() {
            init {
                gluelineValidator.gluelinesAreValid(withAny(experiment.glueLines))
                result = true;

                glueLineExecutor.executeBaseline(withAny(experiment))
                times = 1

                glueLineExecutor.getDuration(withAny(experiment))
                times = 0

                glueLineExecutor.executeAssume(withAny(experiment))
                result = ExecutionResult(status = ExecutionStatus.FAILURE, environment = ExperimentRunEnvironment());
                times = 1

            }
        }

        experimentStorage.updateExperiment(experiment)

        waitForPipeline()

        val storedExperiment = getStoredExperiment()
        var copiedExp = storedExperiment.copy(timing = storedExperiment.timing.copy(due = wrappedDateTimeService.now()))

        experimentStorage.updateExperiment(copiedExp)

        waitForAssumePlanning()

        val resultExp = getStoredExperiment()
        assertThat(resultExp.timing.created).isEqualTo(localDateTimeCreated_20)
        assertThat(resultExp.timing.due).isEqualTo(localDateTimeStarted_22)
        assertThat(resultExp.timing.baselineExecuted).isEqualTo(localDateTimeExecBaseline_now1)
        assertThat(resultExp.timing.completed).isEqualTo(localDateTimeCompleted_05)
    }

    private fun waitForPipeline() {
        Thread.sleep(1000)
    }

    private fun waitForAssumePlanning() {
        Thread.sleep(5000)
    }

    private fun getStoredExperiment() = experimentStorage.loadAllExperiments()
            .find { it.id == experiment.id!! }!!
}
