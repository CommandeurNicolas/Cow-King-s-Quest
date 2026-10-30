package com.nicolascommandeur.cowkingsquest.model.hunt

enum class Relic(val title: String, val shortName: String, val zones: List<String>) {
    SHARD(
        "Bloody Wooden Shard",
        "Shard",
        listOf("Hawezar", "Kehjistan"),
    ),
    TOME(
        "Musty Tome",
        "Tome",
        listOf("Scosglen", "Fractured Peaks"),
    ),
    FRAGMENT(
        "Intricate Metallic Fragment",
        "Fragment",
        listOf("Dry Steppes"),
    );

    val zoneLabel: String get() = zones.joinToString(" / ")
    val zoneInstruction: String get() = zones.joinToString(" or ")
}
