package com.merxury.blocker.feature.search

import androidx.lifecycle.ViewModel
import com.merxury.blocker.core.database.generalrule.GeneralRuleEntity
import com.merxury.blocker.core.ui.TabState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val _tabState = MutableStateFlow(
        TabState(
            titles = listOf(R.string.local_search, R.string.online_rules),
            currentIndex = 0
        )
    )
    val tabState: StateFlow<TabState> = _tabState.asStateFlow()

    fun switchTab(newIndex: Int) {
        if (newIndex != tabState.value.currentIndex) {
            _tabState.update {
                it.copy(currentIndex = newIndex)
            }
        }
    }
}

sealed interface SearchUiState {
    object Loading : SearchUiState
    class OnlineRules(val list: List<GeneralRuleEntity>) : SearchUiState
    class LocalSearchResult(val filter: List<GeneralRuleEntity>) : SearchUiState
    class Error(val message: String) : SearchUiState
}