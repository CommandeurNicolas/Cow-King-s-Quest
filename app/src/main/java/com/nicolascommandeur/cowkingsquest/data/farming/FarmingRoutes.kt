package com.nicolascommandeur.cowkingsquest.data.farming

import com.nicolascommandeur.cowkingsquest.R
import com.nicolascommandeur.cowkingsquest.model.farming.FarmingRoute

internal val farmingRoutes = listOf(
    FarmingRoute(
        "farobru",
        "Farobru",
        "Dry Steppes",
        "Up to 14 cows / lap",
        "Start at the waypoint. Head south and follow the yellow loop through the nearby farms, returning to town.",
        R.drawable.route_farobru,
        "Farobru map: blue waypoint at the upper right; yellow farming loop runs south and west before returning."
    ),
    FarmingRoute(
        "cerrigar",
        "Cerrigar",
        "Scosglen",
        "Up to 18 cows / lap",
        "Start at the waypoint. Check the farms along the yellow branches west and east of town; retrace your path between branches.",
        R.drawable.route_cerrigar,
        "Cerrigar map: blue waypoint near the center; yellow farming branches extend west and east."
    ),
    FarmingRoute(
        "zarbinzet",
        "Zarbinzet",
        "Hawezar",
        "Up to 9 cows / lap",
        "Leave the waypoint toward the southeast. Follow the yellow path through the farm areas south of town, then return.",
        R.drawable.route_zarbinzet,
        "Zarbinzet map: blue waypoint at the top; yellow path heads south into the farms and branches west."
    )
)
