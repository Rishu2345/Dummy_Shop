package com.assasement.dummyShop.navigation

import kotlin.reflect.KClass


sealed class NavCommand(
    val timestamp: Long = System.currentTimeMillis(),
) {
    class Navigate(
        val destination: AppRoute,
        val clearBackStack: Boolean = false,
        val popUpTo: AppRoute? = null,
        val popUpToClass: KClass<*>? = null,
        val popUpToInclusive: Boolean = false,
        timestamp: Long = System.currentTimeMillis(),
    ) : NavCommand(timestamp)

    class PopToRoute(
        val destination: AppRoute,
        val inclusive: Boolean,
        timestamp: Long = System.currentTimeMillis(),
    ): NavCommand(timestamp)

    class PopToRouteClass(
        val destination: KClass<*>,
        val inclusive: Boolean,
        timestamp: Long = System.currentTimeMillis(),
    ): NavCommand(timestamp)

    class PopUp(
        timestamp: Long = System.currentTimeMillis(),
    ): NavCommand(timestamp)


}