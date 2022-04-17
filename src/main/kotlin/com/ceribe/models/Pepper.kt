package com.ceribe.models

import kotlinx.serialization.Serializable

@Serializable
data class Pepper(val id: Int, val name: String, val pot: String, val lastWatering: String)

@Serializable
data class Pot(val id: Int, val name: String, val count: Int)