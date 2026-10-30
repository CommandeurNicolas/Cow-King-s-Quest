package com.nicolascommandeur.cowkingsquest.model.guide

data class GuideSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val steps: List<GuideStep>,
    val description: String? = null,
    val reference: GuideReference? = null
)
