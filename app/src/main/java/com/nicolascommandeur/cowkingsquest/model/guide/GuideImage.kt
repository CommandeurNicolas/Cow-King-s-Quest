package com.nicolascommandeur.cowkingsquest.model.guide

import androidx.annotation.DrawableRes

data class GuideImage(
    val id: String,
    val title: String,
    val caption: String,
    @param:DrawableRes val resource: Int,
    val source: String
) {
    val sourceCredit: String
        get() = "Image: $source · Diablo IV © Blizzard"
}
