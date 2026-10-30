package com.nicolascommandeur.cowkingsquest.model.guide

data class GuideStep(
    val id: String,
    val title: String,
    val detail: String,
    val groupTitle: String? = null,
    val optional: Boolean = false,
    val images: List<GuideImage> = emptyList(),
    val checkable: Boolean = true
)
