/*
 * Copyright 2023 Blocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.merxury.blocker.feature.appdetail.navigation

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.merxury.blocker.core.decoder.StringDecoder
import com.merxury.blocker.core.ui.AppDetailTabs
import com.merxury.blocker.feature.appdetail.AppDetailRoute

@VisibleForTesting
internal const val packageNameArg = "packageName"

@VisibleForTesting
internal const val tabArg = "tab"

@VisibleForTesting
internal const val searchKeywordArg = "searchKeyword"

internal class AppDetailArgs(
    val packageName: String,
    val tabs: AppDetailTabs = AppDetailTabs.Info,
    val searchKeyword: List<String> = listOf(),
) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
        this(
            stringDecoder.decodeString(checkNotNull(savedStateHandle[packageNameArg])),
            AppDetailTabs.fromName(savedStateHandle[tabArg]),
            stringDecoder.decodeString(checkNotNull(savedStateHandle[searchKeywordArg])).split(","),
        )
}

fun NavController.navigateToAppDetail(
    packageName: String,
    tab: AppDetailTabs = AppDetailTabs.Info,
    searchKeyword: List<String> = listOf(),
) {
    val encodedId = Uri.encode(packageName)
    val keywords = searchKeyword.joinToString(",")
    this.navigate("app_detail_route/$encodedId?screen=${tab.name}?searchKeyword=$keywords") {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
    }
}

fun NavGraphBuilder.detailScreen(onBackClick: () -> Unit) {
    composable(
        route = "app_detail_route/{$packageNameArg}?screen={$tabArg}?searchKeyword={$searchKeywordArg}",
        arguments = listOf(
            navArgument(packageNameArg) { type = NavType.StringType },
            navArgument(tabArg) { type = NavType.StringType },
            navArgument(searchKeywordArg) { type = NavType.StringType },
        ),
    ) {
        AppDetailRoute(onBackClick = onBackClick)
    }
}
