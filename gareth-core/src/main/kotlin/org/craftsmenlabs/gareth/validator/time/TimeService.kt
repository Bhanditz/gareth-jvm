package org.craftsmenlabs.gareth.validator.time

import org.craftsmenlabs.gareth.validator.model.DateTimeDTO
import org.craftsmenlabs.gareth.validator.model.ExecutionInterval
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


interface TimeService {

    fun midnight(): LocalDate

    fun now(): LocalDateTime

    fun toDate(dateTime: LocalDateTime): Date

    fun toDate(dto: DateTimeDTO): LocalDateTime

    fun fromDate(dateTime: Date): LocalDateTime

    fun getSecondsUntil(inFuture: LocalDateTime): Long

    fun getDelay(now: LocalDateTime, interval: ExecutionInterval): LocalDateTime
}