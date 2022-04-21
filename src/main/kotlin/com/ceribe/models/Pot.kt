package com.ceribe.models

import kotlinx.serialization.Serializable

@Serializable
data class Pot(var name: String, var count: Int)