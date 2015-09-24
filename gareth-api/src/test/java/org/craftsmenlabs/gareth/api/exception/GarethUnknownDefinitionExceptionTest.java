package org.craftsmenlabs.gareth.api.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
public class GarethUnknownDefinitionExceptionTest {

    private GarethUnknownDefinitionException garethUnknownDefinitionException;

    @Before
    public void before() throws Exception {
        garethUnknownDefinitionException = new GarethUnknownDefinitionException("message");
    }

    @Test
    public void testMessage() {
        assertTrue(garethUnknownDefinitionException.getMessage().contains("message"));
    }

    @Test
    public void testCause() {
        assertNull(garethUnknownDefinitionException.getCause());
    }

}