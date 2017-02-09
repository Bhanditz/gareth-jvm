package org.craftsmenlabs.gareth.atdd

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener

@Configuration
open class CucumberConfig {

    @Autowired
    lateinit var context: ApplicationContext

    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent) {
        GarethServerEnvironment.addInstance(GarethServerEnvironment.createGarethInstance())
        GarethServerEnvironment.addInstance(GarethServerEnvironment.createExecutionInstance())
        GarethServerEnvironment.start()
    }

    @EventListener
    fun handleContextClosed(event: ContextClosedEvent) {
        GarethServerEnvironment.shutDown()
    }

}