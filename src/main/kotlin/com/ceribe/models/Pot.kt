package com.ceribe.models

import kotlinx.serialization.Serializable

@Serializable
data class Pot(val id: Int, val name: String, val count: Int)