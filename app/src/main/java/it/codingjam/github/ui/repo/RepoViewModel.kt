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

package it.codingjam.github.ui.repo

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import it.codingjam.github.NavigationController
import it.codingjam.github.repository.RepoRepository
import it.codingjam.github.ui.common.RxViewModel
import it.codingjam.github.vo.RepoId
import it.codingjam.github.vo.Resource

class RepoViewModel(
        private val navigationController: NavigationController,
        private val repository: RepoRepository
) : RxViewModel<RepoViewState>(RepoViewState(Resource.Empty)) {

    private lateinit var repoId: RepoId

    fun retry() = reload()

    fun init(repoId: RepoId) {
        this.repoId = repoId
        reload()
    }

    fun reload() {
        state.update { copy(Resource.Loading) }

        repository.loadRepo(repoId.owner, repoId.name)
                .takeUntil(cleared)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        state.updateOnEvent { copy(Resource.Success(it)) },
                        state.updateOnEvent { copy(Resource.Error(it)) }
                )
    }

    fun openUserDetail(login: String) =
            uiActions.execute { navigationController.navigateToUser(it, login) }
}
