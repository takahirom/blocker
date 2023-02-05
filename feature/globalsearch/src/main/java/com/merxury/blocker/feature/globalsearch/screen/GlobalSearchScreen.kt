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

package com.merxury.blocker.feature.globalsearch.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.merxury.blocker.core.designsystem.bottomsheet.ModalBottomSheetValue
import com.merxury.blocker.core.designsystem.bottomsheet.ModalBottomSheetValue.Expanded
import com.merxury.blocker.core.designsystem.bottomsheet.ModalBottomSheetValue.HalfExpanded
import com.merxury.blocker.core.designsystem.bottomsheet.rememberModalBottomSheetState
import com.merxury.blocker.core.designsystem.component.BlockerLoadingWheel
import com.merxury.blocker.core.designsystem.component.BlockerModalBottomSheetLayout
import com.merxury.blocker.core.designsystem.component.BlockerScrollableTabRow
import com.merxury.blocker.core.designsystem.component.BlockerTab
import com.merxury.blocker.core.designsystem.icon.BlockerIcons
import com.merxury.blocker.core.designsystem.theme.BlockerTheme
import com.merxury.blocker.core.model.Application
import com.merxury.blocker.core.ui.data.ErrorMessage
import com.merxury.blocker.feature.globalsearch.R.string
import com.merxury.blocker.feature.globalsearch.component.AppListItem
import com.merxury.blocker.feature.globalsearch.component.SearchBar
import com.merxury.blocker.feature.globalsearch.component.SelectedAppTopBar
import com.merxury.blocker.feature.globalsearch.model.FilterAppItem
import com.merxury.blocker.feature.globalsearch.model.LocalSearchUiState
import com.merxury.blocker.feature.globalsearch.model.LocalSearchViewModel
import com.merxury.blocker.feature.globalsearch.model.SearchBoxUiState
import com.merxury.blocker.feature.globalsearch.model.SearchTabState
import kotlinx.coroutines.launch

@Composable
fun GlobalSearchRoute(
    navigationToSearchedAppDetail: () -> Unit,
    viewModel: LocalSearchViewModel = hiltViewModel(),
) {
    val searchBoxUiState by viewModel.searchBoxUiState.collectAsStateWithLifecycle()
    val localSearchUiState by viewModel.localSearchUiState.collectAsStateWithLifecycle()
    val tabState by viewModel.tabState.collectAsStateWithLifecycle()

    GlobalSearchScreen(
        searchBoxUiState = searchBoxUiState,
        tabState = tabState,
        localSearchUiState = localSearchUiState,
        switchTab = viewModel::switchTab,
        onSearchTextChanged = viewModel::onSearchTextChanged,
        onClearClick = viewModel::onClearClick,
        onNavigationClick = { viewModel.switchSelectedMode(false) },
        onSelectAll = viewModel::onSelectAll,
        onBlockAll = viewModel::onBlockAll,
        onCheckAll = viewModel::onCheckAll,
        switchSelectedMode = viewModel::switchSelectedMode,
        onSelect = viewModel::onSelectItem,
        navigationToSearchedAppDetail = navigationToSearchedAppDetail,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun GlobalSearchScreen(
    modifier: Modifier = Modifier,
    tabState: SearchTabState,
    searchBoxUiState: SearchBoxUiState,
    localSearchUiState: LocalSearchUiState,
    switchTab: (Int) -> Unit,
    onSearchTextChanged: (TextFieldValue) -> Unit,
    onClearClick: () -> Unit,
    onNavigationClick: () -> Unit,
    onSelectAll: () -> Unit,
    onBlockAll: () -> Unit,
    onCheckAll: () -> Unit,
    switchSelectedMode: (Boolean) -> Unit,
    onSelect: (Boolean) -> Unit,
    navigationToSearchedAppDetail: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != HalfExpanded },
    )
    val coroutineScope = rememberCoroutineScope()

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }
    if (sheetState.targetValue == Expanded) {
        navigationToSearchedAppDetail()
        // TODO
    }
    BlockerModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            // TODO
        },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    localSearchUiState = localSearchUiState,
                    searchBoxUiState = searchBoxUiState,
                    onSearchTextChanged = onSearchTextChanged,
                    onClearClick = onClearClick,
                    onNavigationClick = onNavigationClick,
                    onSelectAll = onSelectAll,
                    onBlockAll = onBlockAll,
                    onCheckAll = onCheckAll,
                )
            },
        ) { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumedWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (localSearchUiState) {
                    LocalSearchUiState.NoSearch -> {
                        Column(
                            modifier = modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            NoSearchScreen()
                        }
                    }

                    LocalSearchUiState.Loading -> {
                        Column(
                            modifier = modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            BlockerLoadingWheel(
                                modifier = modifier,
                                contentDesc = stringResource(id = string.searching),
                            )
                        }
                    }

                    is LocalSearchUiState.LocalSearchResult -> {
                        SearchResultTabRow(tabState = tabState, switchTab = switchTab)
                        when (tabState.currentIndex) {
                            0 -> {
                                SearchResultContent(
                                    appList = localSearchUiState.filter,
                                    isSelectedMode = localSearchUiState.isSelectedMode,
                                    switchSelectedMode = switchSelectedMode,
                                    onSelect = onSelect,
                                    onClick = {
                                        coroutineScope.launch {
                                            if (sheetState.isVisible) {
                                                sheetState.hide()
                                            } else {
                                                sheetState.animateTo(HalfExpanded)
                                            }
                                        }
                                    },
                                )
                            }

                            1 -> {}
                            2 -> {}
                        }
                    }

                    is LocalSearchUiState.Error -> {
                        ErrorScreen(localSearchUiState.message)
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    localSearchUiState: LocalSearchUiState,
    searchBoxUiState: SearchBoxUiState,
    onSearchTextChanged: (TextFieldValue) -> Unit,
    onClearClick: () -> Unit,
    onNavigationClick: () -> Unit,
    onSelectAll: () -> Unit,
    onBlockAll: () -> Unit,
    onCheckAll: () -> Unit,
) {
    if (localSearchUiState is LocalSearchUiState.LocalSearchResult &&
        localSearchUiState.isSelectedMode
    ) {
        SelectedAppTopBar(
            localSearchUiState.selectedAppCount,
            onNavigationClick = onNavigationClick,
            onSelectAll = onSelectAll,
            onBlockAll = onBlockAll,
            onCheckAll = onCheckAll,
        )
    } else {
        SearchBar(
            modifier = modifier,
            uiState = searchBoxUiState,
            onSearchTextChanged = onSearchTextChanged,
            onClearClick = onClearClick,
        )
    }
}

@Composable
fun SearchResultTabRow(
    tabState: SearchTabState,
    switchTab: (Int) -> Unit,
) {
    BlockerScrollableTabRow(
        selectedTabIndex = tabState.currentIndex,
    ) {
        BlockerTab(
            selected = 0 == tabState.currentIndex,
            onClick = { switchTab(0) },
            text = {
                Text(
                    text = stringResource(
                        id = tabState.titles[0],
                        tabState.appCount,
                    ),
                )
            },
        )
        BlockerTab(
            selected = 1 == tabState.currentIndex,
            onClick = { switchTab(1) },
            text = {
                Text(
                    text = stringResource(
                        id = tabState.titles[1],
                        tabState.componentCount,
                    ),
                )
            },
        )
        BlockerTab(
            selected = 2 == tabState.currentIndex,
            onClick = { switchTab(2) },
            text = {
                Text(
                    text = stringResource(
                        id = tabState.titles[2],
                        tabState.rulesCount,
                    ),
                )
            },
        )
    }
}

@Composable
fun ErrorScreen(message: ErrorMessage) {
    Text(text = message.message)
}

@Composable
fun NoSearchScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = BlockerIcons.Inbox,
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = stringResource(id = string.no_search_result),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun SearchResultContent(
    modifier: Modifier = Modifier,
    appList: List<FilterAppItem>,
    isSelectedMode: Boolean,
    switchSelectedMode: (Boolean) -> Unit,
    onSelect: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    val listContent = remember { appList }
    val listState = rememberLazyListState()
    Box(modifier) {
        LazyColumn(
            modifier = modifier,
            state = listState,
        ) {
            items(listContent, key = { it.app.label }) {
                AppListItem(
                    filterAppItem = it,
                    isSelectedMode = isSelectedMode,
                    switchSelectedMode = switchSelectedMode,
                    onSelect = onSelect,
                    onClick = onClick,
                )
            }
        }
    }
}

@Composable
@Preview
fun GlobalSearchScreenEmptyPreview() {
    val searchBoxUiState = SearchBoxUiState()
    val localSearchUiState = LocalSearchUiState.NoSearch
    val tabState = SearchTabState(
        titles = listOf(
            string.application,
            string.component,
            string.online_rule,
        ),
        currentIndex = 0,
    )
    BlockerTheme {
        GlobalSearchScreen(
            searchBoxUiState = searchBoxUiState,
            localSearchUiState = localSearchUiState,
            onSearchTextChanged = {},
            onClearClick = {},
            tabState = tabState,
            switchTab = {},
            onNavigationClick = {},
            onSelectAll = {},
            onBlockAll = {},
            onCheckAll = {},
            switchSelectedMode = {},
            onSelect = {},
            navigationToSearchedAppDetail = {},
        )
    }
}

@Composable
@Preview
fun GlobalSearchScreenPreview() {
    val filterAppItem = FilterAppItem(
        app = Application(
            label = "Blocker",
        ),
        activityCount = 0,
        broadcastCount = 1,
        serviceCount = 0,
        contentProviderCount = 9,
    )
    val searchBoxUiState = SearchBoxUiState()
    val localSearchUiState = LocalSearchUiState.LocalSearchResult(
        filter = listOf(filterAppItem),
        isSelectedMode = false,
        selectedAppCount = 0,
    )
    val tabState = SearchTabState(
        titles = listOf(
            string.application,
            string.component,
            string.online_rule,
        ),
        currentIndex = 0,
    )
    BlockerTheme {
        GlobalSearchScreen(
            searchBoxUiState = searchBoxUiState,
            localSearchUiState = localSearchUiState,
            onSearchTextChanged = {},
            onClearClick = {},
            tabState = tabState,
            switchTab = {},
            onNavigationClick = {},
            onSelectAll = {},
            onBlockAll = {},
            onCheckAll = {},
            switchSelectedMode = {},
            onSelect = {},
            navigationToSearchedAppDetail = {},
        )
    }
}

@Composable
@Preview
fun GlobalSearchScreenSelectedPreview() {
    val filterAppItem = FilterAppItem(
        app = Application(
            label = "Blocker",
        ),
        isSelected = true,
        activityCount = 0,
        broadcastCount = 1,
        serviceCount = 0,
        contentProviderCount = 9,
    )
    val searchBoxUiState = SearchBoxUiState()
    val localSearchUiState = LocalSearchUiState.LocalSearchResult(
        filter = listOf(filterAppItem),
        isSelectedMode = true,
        selectedAppCount = 1,
    )
    val tabState = SearchTabState(
        titles = listOf(
            string.application,
            string.component,
            string.online_rule,
        ),
        currentIndex = 0,
    )
    BlockerTheme {
        GlobalSearchScreen(
            searchBoxUiState = searchBoxUiState,
            localSearchUiState = localSearchUiState,
            onSearchTextChanged = {},
            onClearClick = {},
            tabState = tabState,
            switchTab = {},
            onNavigationClick = {},
            onSelectAll = {},
            onBlockAll = {},
            onCheckAll = {},
            switchSelectedMode = {},
            onSelect = {},
            navigationToSearchedAppDetail = {},
        )
    }
}