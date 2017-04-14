package org.craftsmenlabs.gareth.validator.time

import org.craftsmenlabs.gareth.validator.model.DateTimeDTO
import org.springframework.stereotype.Component
import java.time.*
import java.util.*

@Component
class DateTimeService : TimeService {

    override fun midnight(): LocalDate {
        return LocalDate.now()
    }

    override fun now(): LocalDateTime {
        return LocalDateTime.now()
    }

    override fun toDate(dateTime: LocalDateTime): Date {
        val instant = dateTime.toInstant(ZoneOffset.UTC)
        return Date.from(instant)
    }

    override fun fromDate(dateTime: Date): LocalDateTime {
        val instant = Instant.ofEpochMilli(dateTime.getTime())
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    }

    override fun toDate(dto: DateTimeDTO): LocalDateTime {
        return LocalDateTime.now().withYear(dto.year).withMonth(dto.month).withDayOfMonth(dto.day).withHour(dto.hour).withMinute(dto.minute).withSecond(dto.seconds)
    }


    override fun getSecondsUntil(inFuture: LocalDateTime): Long {
        val now = now()
        if (!inFuture.isAfter(now))
            return 0
        return Duration.between(now, inFuture).seconds
    }

}