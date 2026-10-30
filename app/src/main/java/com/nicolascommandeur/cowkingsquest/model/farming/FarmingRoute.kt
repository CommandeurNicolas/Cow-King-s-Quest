package com.nicolascommandeur.cowkingsquest.model.farming

import androidx.annotation.DrawableRes

internal data class FarmingRoute(
    val id: String,
    val town: String,
    val region: String,
    val estimate: String,
    val directions: String,
    @param:DrawableRes val map: Int,
    val mapDescription: String
)
