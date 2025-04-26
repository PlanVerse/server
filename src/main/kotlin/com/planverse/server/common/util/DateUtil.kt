package com.planverse.server.common.util

import java.time.Duration
import java.util.Date

object DateUtil {
    fun yearsToMilliseconds(years: Int): Long = years * Duration.ofDays(1).toMillis() * 365
    fun monthsToMilliseconds(months: Int): Long = months * Duration.ofDays(1).toMillis() * 30
    fun daysToMilliseconds(days: Int): Long = Duration.ofDays(days.toLong()).toMillis()
    fun hoursToMilliseconds(hours: Int): Long = Duration.ofHours(hours.toLong()).toMillis()

    fun diffFromNowInMilliseconds(target: Date): Long = System.currentTimeMillis() - target.time
}