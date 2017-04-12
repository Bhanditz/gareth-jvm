package org.craftsmenlabs.gareth.execution.services

import org.craftsmenlabs.gareth.execution.RunContext
import org.craftsmenlabs.gareth.execution.definitions.ExecutionType
import org.craftsmenlabs.gareth.execution.dto.DurationBuilder
import org.craftsmenlabs.gareth.validator.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class DefinitionService @Autowired constructor(val definitionRegistry: DefinitionRegistry) {

    val log: Logger = LoggerFactory.getLogger(DefinitionService::class.java)

    fun executeBaseline(dto: ExecutionRequest): ExecutionResult {
        val context: ExecutionRunContext = executeMandatoryGlueline(dto.glueLines.baseline, ExecutionType.BASELINE, dto)
        return context.toExecutionResult(ExecutionStatus.RUNNING)
    }

    fun executeAssumption(dto: ExecutionRequest): ExecutionResult {
        val successAndContext = definitionRegistry.invokeAssumptionMethod(dto.glueLines.assume, dto)
        val isSuccess = successAndContext.first
        val result = successAndContext.second.toExecutionResult(ExecutionStatus.RUNNING)
        val copy = dto.copy(environment = result.environment)
        if (isSuccess) {
            return executeSuccess(copy)
        } else {
            return executeFailure(copy)
        }
    }

    private fun executeSuccess(dto: ExecutionRequest): ExecutionResult {
        val context = executeOptionalGlueline(dto.glueLines.success, ExecutionType.SUCCESS, dto)
        return context.toExecutionResult(ExecutionStatus.SUCCESS)
    }

    private fun executeFailure(dto: ExecutionRequest): ExecutionResult {
        val context = executeOptionalGlueline(dto.glueLines.failure, ExecutionType.FAILURE, dto)
        return context.toExecutionResult(ExecutionStatus.FAILURE)
    }

    fun getInfoByType(glueLine: String, type: ExecutionType): DefinitionInfo {
        val method = definitionRegistry.getMethodDescriptorForExecutionType(glueLine, type)
        return DefinitionInfo(
                glueline = method.getRegexPatternForGlueLine(),
                method = method.getMethodName(),
                className = method.getClassName(),
                humanReadable = method.humanReadable,
                description = method.description)
    }

    fun getTime(glueline: String): Duration = DurationBuilder.createForMinutes(definitionRegistry.getTimeForGlueline(glueline).second)

    private fun executeMandatoryGlueline(glueline: String, type: ExecutionType, request: ExecutionRequest): ExecutionRunContext = definitionRegistry.invokeVoidMethodByType(glueline, type, request)

    private fun executeOptionalGlueline(glueline: String?, type: ExecutionType, request: ExecutionRequest): ExecutionRunContext {
        //if the glueline is empty, there's nothing to invoke
        return if (glueline == null) RunContext.create(request)
        else
            definitionRegistry.invokeVoidMethodByType(glueline, type, request)
    }

}






