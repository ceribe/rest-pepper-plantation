package com.ceribe.models

import kotlinx.serialization.Serializable

@Serializable
data class Pepper(var name: String, var potId: Int, var lastWatering: Long)

val Pepper?.etag: String
    get() = hashCode().toString()