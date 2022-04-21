package com.ceribe

import com.ceribe.models.Pepper
import com.ceribe.models.Pot

object Database {
    private val peppersMap = mutableMapOf<Int, Pepper>()
    private val potsMap = mutableMapOf<Int, Pot>()
    var waterAmount = 0
    var soilAmount = 0

    val peppers: List<Pepper>
        get() = peppersMap.values.toList()

    val pots: List<Pot>
        get() = potsMap.values.toList()

    fun addPepper(pepper: Pepper) {
        val newId = peppersMap.keys.maxOfOrNull { it }?.plus(1) ?: 1
        peppersMap[newId] = pepper
    }

    fun updatePepper(pepper: Pepper, id: Int) {
        peppersMap[id] = pepper
    }

    fun addPot(pot: Pot) {
        val newId = potsMap.keys.maxOfOrNull { it }?.plus(1) ?: 1
        potsMap[newId] = pot
    }

    fun updatePot(pot: Pot, id: Int) {
        potsMap[id] = pot
    }
}