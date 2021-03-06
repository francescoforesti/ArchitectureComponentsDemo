/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.codingjam.github.ui.search

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import it.codingjam.github.NavigationController
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.ui.common.RxViewModel
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource
import java.util.*
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val repoRepository: RepoRepository,
    private val navigationController: NavigationController
) : RxViewModel<SearchViewState>(SearchViewState()) {

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (input != state.value.query) {
            reloadData(input)
        }
    }

    private fun reloadData(input: String) {
        state.update { copy(input, Resource.Loading, false, null) }
        repoRepository.search(input)
                .takeUntil(cleared)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        state.updateOnEvent { copy(repos = Resource.Success(it.items), nextPage = it.nextPage) },
                        state.updateOnEvent { copy(repos = Resource.Error(it)) }
                )
    }

    fun loadNextPage() {
        val query = state.value.query
        val nextPage = state.value.nextPage
        if (!query.isEmpty() && nextPage != null && !state.value.loadingMore) {
            state.update { copy(loadingMore = true) }
            repoRepository.searchNextPage(query, nextPage)
                    .takeUntil(cleared)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            state.updateOnEvent { copy(repos = repos.map { v -> v + it.items }, nextPage = it.nextPage, loadingMore = false) },
                            { t ->
                                state.update { copy(loadingMore = false) }
                                uiActions.execute { navigationController.showError(it, t.message) }
                            }
                    )
        }
    }

    fun refresh() {
        val query = state.value.query
        if (!query.isEmpty()) {
            reloadData(query)
        }
    }

    fun openRepoDetail(id: RepoId) =
            uiActions.execute { navigationController.navigateToRepo(it, id) }
}
