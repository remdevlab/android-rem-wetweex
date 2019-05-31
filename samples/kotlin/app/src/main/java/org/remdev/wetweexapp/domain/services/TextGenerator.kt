package org.remdev.wetweexapp.domain.services

import org.remdev.wetweexapp.domain.model.GeneratedText
import kotlin.random.Random

class TextGenerator {

    fun generateText(millis: Long): GeneratedText {
        val list = mutableListOf<Byte>()
        val end = System.currentTimeMillis() + millis
        while (System.currentTimeMillis() < end) {
            list.add(Random.nextInt(0, 127).toByte())
            Thread.sleep(10)
        }
        return GeneratedText(String(list.toByteArray()))
    }
}