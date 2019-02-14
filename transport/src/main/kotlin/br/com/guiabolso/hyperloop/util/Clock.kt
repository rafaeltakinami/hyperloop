package br.com.guiabolso.hyperloop.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Clock: DateIsoFormat{
    override fun dateNow(): String = LocalDateTime.now().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT)
}
