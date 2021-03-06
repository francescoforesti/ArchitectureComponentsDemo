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

package it.codingjam.github.ui.user

import android.arch.lifecycle.LifecycleFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.codingjam.github.component
import it.codingjam.github.databinding.UserFragmentBinding
import it.codingjam.github.ui.common.DataBoundListAdapter
import it.codingjam.github.ui.common.StringFragmentCreator
import it.codingjam.github.util.ViewModels

class UserFragment : LifecycleFragment() {
    private val viewModel by ViewModels.delegate({
        component.userViewModel().also { it.load(getParam(this)) }
    })

    lateinit var binding: UserFragmentBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = UserFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel

        val adapter = DataBoundListAdapter { UserRepoViewHolder(it, viewModel) }
        binding.repoList.adapter = adapter

        viewModel.observe(this) {
            binding.state = it
            binding.executePendingBindings()
            adapter.replace(it.repos())
        }
    }

    companion object : StringFragmentCreator(::UserFragment)
}
