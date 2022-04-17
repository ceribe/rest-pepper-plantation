package com.ceribe

import com.ceribe.models.Pepper
import com.ceribe.models.Pot

object Database {
    val peppers = mutableListOf<Pepper>()
    val pots = mutableListOf<Pot>()
    var waterAmount = 0
    var soilAmount = 0
}