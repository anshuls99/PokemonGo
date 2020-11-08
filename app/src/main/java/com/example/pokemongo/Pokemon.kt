package com.example.pokemongo

import android.location.Location

class Pokemon(name: String, des: String, image: Int, power: Double, lat: Double, log: Double) {

    var name: String? = name
    var des: String? = des
    var image: Int? = image
    var power: Double? = power
    var location: Location? = null
    var isCatch: Boolean? = false

    init {
        this.isCatch = false
        this.location = Location(name)
        this.location!!.latitude = lat
        this.location!!.longitude = log
    }
}