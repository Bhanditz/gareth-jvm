package org.craftsmenlabs.gareth.rest.example.definition;

import org.craftsmenlabs.gareth.api.annotation.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Created by hylke on 14/08/15.
 */
public class SampleDefinition {

    @Baseline(glueLine = "Sample baseline")
    public void sampleBaseline() {
        System.out.println("Baseline was executed");
    }

    @Assume(glueLine = "Sample assume")
    public void sampleAssume() {
        System.out.println("Assume was executed");
    }

    @Time(glueLine = "1 minute")
    public Duration sampleTime() {
        return Duration.of(1L, ChronoUnit.MINUTES);
    }

    @Time(glueLine = "1 month")
    public Duration sampleTimeOneMonth() {
        return Duration.of(2L, ChronoUnit.MINUTES);
    }

    @Time(glueLine = "1 year")
    public Duration sampleTimeOneYear() {
        return Duration.of(3L, ChronoUnit.MINUTES);
    }

    @Success(glueLine = "Sample success")
    public void sampleSuccess() {
        System.out.println("Success was executed");
    }

    @Failure(glueLine = "Sample failure")
    public void sampleFailure() {
        System.out.println("Failure was executed");
    }
}