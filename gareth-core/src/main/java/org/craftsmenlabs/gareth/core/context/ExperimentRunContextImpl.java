package org.craftsmenlabs.gareth.core.context;

import lombok.Getter;
import lombok.Setter;
import org.craftsmenlabs.gareth.api.context.ExperimentPartState;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;

import java.time.LocalDateTime;

@Getter
public class ExperimentRunContextImpl {

    private final ExperimentContextImpl experimentContext;

    private final DefaultStorage storage;

    @Setter
    private ExperimentPartState baselineState, assumeState, successState, failureState;

    @Setter
    private LocalDateTime baselineRun, assumeRun, successRun, failureRun;

    private boolean finished;

    private ExperimentRunContextImpl(final Builder builder) {
        this.experimentContext = builder.experimentContext;
        this.storage = builder.storage;

        // State
        this.baselineState = builder.baselineState;
        this.assumeState = builder.assumeState;
        this.successState = builder.successState;
        this.failureState = builder.failureState;
    }

    public String getHash() {
        return experimentContext.getHash();
    }

    public boolean hasFailures() {
        return null != failureRun;
    }

    public boolean isRunning() {
        return (null != baselineRun || null != assumeRun)
                && !(null != successRun || null != failureRun);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public static class Builder {
        private ExperimentContextImpl experimentContext;
        private DefaultStorage storage;

        private ExperimentPartState baselineState = ExperimentPartState.NON_EXISTENT;

        private ExperimentPartState assumeState = ExperimentPartState.NON_EXISTENT;

        private ExperimentPartState successState = ExperimentPartState.NON_EXISTENT;

        private ExperimentPartState failureState = ExperimentPartState.NON_EXISTENT;

        public Builder(final ExperimentContextImpl experimentContext, final DefaultStorage storage) {
            this.experimentContext = experimentContext;
            this.storage = storage;
        }

        public ExperimentRunContextImpl build() {
            if (null == experimentContext) {
                throw new IllegalStateException("Cannot build experiment run context without experiment context");
            }
            if (null != experimentContext.getBaseline()) baselineState = ExperimentPartState.OPEN;
            if (null != experimentContext.getAssume()) assumeState = ExperimentPartState.OPEN;
            if (null != experimentContext.getSuccess()) successState = ExperimentPartState.OPEN;
            if (null != experimentContext.getFailure()) failureState = ExperimentPartState.OPEN;
            return new ExperimentRunContextImpl(this);
        }
    }
}
