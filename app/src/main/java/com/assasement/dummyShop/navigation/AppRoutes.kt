package com.assasement.dummyShop.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed interface AppRoute

@Serializable
data object HomeScreenRoute : AppRoute

