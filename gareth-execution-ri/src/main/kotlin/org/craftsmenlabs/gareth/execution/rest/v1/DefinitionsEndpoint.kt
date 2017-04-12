package org.craftsmenlabs.gareth.execution.rest.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.services.DefinitionService
import org.craftsmenlabs.gareth.validator.DefinitionsResource
import org.craftsmenlabs.gareth.validator.model.Duration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("gareth/validator/v1/")
@Api("Endpoint for definitions")
class DefinitionsEndpoint @Autowired constructor(val definitionService: DefinitionService) : DefinitionsResource {

    @RequestMapping(value = "definitions/baseline/{glueline}", method = arrayOf(RequestMethod.GET))
    @ApiOperation(value = "Gets the result of the match for the user and the given perspective")
    override fun getBaselineByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.BASELINE)

    @RequestMapping(value = "definitions/assume/{glueline}", method = arrayOf(RequestMethod.GET))
    override fun getAssumeByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.ASSUME)

    @RequestMapping(value = "definitions/success/{glueline}", method = arrayOf(RequestMethod.GET))
    override fun getSuccessByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.SUCCESS)

    @RequestMapping(value = "definitions/failure/{glueline}", method = arrayOf(RequestMethod.GET))
    override fun getFailureByGlueline(@PathVariable("glueline") glueLine: String) =
            definitionService.getInfoByType(glueLine, ExecutionType.FAILURE)

    @RequestMapping(value = "definitions/time/{glueline}", method = arrayOf(RequestMethod.GET))
    override fun getDurationByGlueline(@PathVariable("glueline") glueLine: String): Duration =
            definitionService.getTime(glueLine)
}

