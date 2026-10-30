package com.nicolascommandeur.cowkingsquest.view.components.image

import androidx.annotation.DrawableRes

/** Presentation data shared by guide references and farming maps. */
internal data class ViewerImage(
    val id: String,
    val title: String,
    @param:DrawableRes val resource: Int,
    val contentDescription: String,
    val caption: String,
    val sourceCredit: String
)
