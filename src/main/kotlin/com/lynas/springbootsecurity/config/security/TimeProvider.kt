package com.lynas.springbootsecurity.config.security

import org.springframework.stereotype.Component
import java.util.*

@Component
class TimeProvider {
    fun now(): Date {
        return Date()
    }
}