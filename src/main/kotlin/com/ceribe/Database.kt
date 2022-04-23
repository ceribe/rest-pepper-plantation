package com.ceribe

import com.ceribe.models.Pepper
import com.ceribe.models.Pot

object Database {
    private val peppersMap = mutableMapOf<Int, Pepper>()
    private val potsMap = mutableMapOf<Int, Pot>()
    var waterAmount = 10
    var soilAmount = 10

    private val peppersETagMap = mutableMapOf<Int, Int>()
    private val potsETagMap = mutableMapOf<Int, Int>()

    private val peppers: List<Pepper>
        get() = peppersMap.values.toList()

    val pots: List<Pot>
        get() = potsMap.values.toList()

    fun getFilteredPeppers(filter: String?) =
        if (filter == null) {
            peppers
        } else {
            peppers.filter { it.name.contains(filter, true) }
        }

    private fun addPepper(pepper: Pepper): Int {
        val newId = peppersMap.keys.maxOfOrNull { it }?.plus(1) ?: 1
        peppersMap[newId] = pepper
        peppersETagMap[newId] = 1
        return newId
    }

    private fun addPot(pot: Pot): Int {
        val newId = potsMap.keys.maxOfOrNull { it }?.plus(1) ?: 1
        potsMap[newId] = pot
        potsETagMap[newId] = 1
        return newId
    }

    fun addDummyPepper() = addPepper(Pepper("", 0, System.currentTimeMillis()))

    fun addDummyPot() = addPot(Pot("", 0))

    fun getPepper(id: Int) = peppersMap[id]

    fun getPot(id: Int) = potsMap[id]

    fun doesPepperExist(id: Int) = peppersMap.containsKey(id)

    fun doesPotExist(id: Int) = potsMap.containsKey(id)

    fun deletePepper(id: Int) = peppersMap.remove(id) != null

    fun deletePot(id: Int) = potsMap.remove(id) != null

    fun updatePepper(id: Int, pepper: Pepper) {
        peppersMap[id] = pepper
        peppersETagMap[id] = peppersETagMap[id]!! + 1
    }

    fun updatePot(id: Int, pot: Pot) {
        potsMap[id] = pot
        potsETagMap[id] = potsETagMap[id]!! + 1
    }

    fun waterPepper(id: Int) {
        val pepper = peppersMap[id]!!
        pepper.lastWatering = System.currentTimeMillis()
        peppersETagMap[id] = peppersETagMap[id]!! + 1
    }

    fun repotPepper(pepperId: Int, newPotId: Int): Boolean {
        val pepper = peppersMap[pepperId]!!
        if (soilAmount < 1)
            return false
        val oldPotId = pepper.potId
        pots[oldPotId].count++
        pots[newPotId].count--
        pepper.potId = newPotId
        soilAmount--
        potsETagMap[oldPotId] = potsETagMap[oldPotId]!! + 1
        potsETagMap[newPotId] = potsETagMap[newPotId]!! + 1
        peppersETagMap[pepperId] = peppersETagMap[pepperId]!! + 1
        return true
    }

    fun getPotCount(id: Int) = potsMap[id]?.count ?: 0

    fun canWaterAllPeppers() = waterAmount > peppers.size

    private fun updateAllPeppersETag() {
        peppersETagMap.forEach { peppersETagMap[it.key] = peppersETagMap[it.key]!! + 1 }
    }

    fun waterAllPeppers() {
        waterAmount -= peppers.size
        peppers.forEach { it.lastWatering = System.currentTimeMillis() }
        updateAllPeppersETag()
    }

    fun getPeppersETag(id: Int) = (peppersETagMap[id] ?: 0).toString()

    fun getPotsETag(id: Int) = (potsETagMap[id] ?: 0).toString()
}