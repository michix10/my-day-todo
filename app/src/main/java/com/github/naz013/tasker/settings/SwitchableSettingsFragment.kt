package com.github.naz013.tasker.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.settings.groups.GroupsViewModel
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_extra_settings.*

/**
 * Copyright 2018 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class SwitchableSettingsFragment : BaseFragment() {

    private var mTitle: String? = null
    private var mKey: String? = null
    private var mKeyList: String? = null
    private lateinit var mAdapter: CheckableGroupsListAdapter
    private lateinit var viewModel: GroupsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val safeArgs = SwitchableSettingsFragmentArgs.fromBundle(it)
            mTitle = safeArgs.argTitle
            mKey = safeArgs.argKey
            mKeyList = safeArgs.argKeyList
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_extra_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView.text = mTitle
        fab.onClick { findNavController().navigateUp() }

        groupsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mAdapter = CheckableGroupsListAdapter()
        mAdapter.ids = Prefs.getInstance(context!!).getStringList(mKeyList!!)
        groupsList.adapter = mAdapter

        switchButton.setOnSwitchListener { position, _ ->
            groupsList.isEnabled = position == Prefs.CUSTOM
            mAdapter.isClickable = position == Prefs.CUSTOM
        }
        switchButton.selectedTab = Prefs.getInstance(context!!).getInt(mKey!!, Prefs.DISABLED)

        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupsViewModel::class.java)
        viewModel.data.observe(this, Observer { data -> if (data != null) mAdapter.setData(data) })
    }

    override fun onStop() {
        super.onStop()
        val prefs = Prefs.getInstance(context!!)
        val sel = switchButton.selectedTab
        prefs.putInt(mKey!!, sel)
        if (sel == Prefs.CUSTOM) {
            prefs.putStringList(mKeyList!!, mAdapter.getCheckedIds())
        }
    }
}