/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.growingio.android.descriptor

import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.tabs.TabLayout
import com.growingio.inject.annotation.SuperInject
import com.growingio.inject.annotation.Belong

/**
 * <p>
 *     控件点击注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.ViewClickInjector")
interface ViewClickInjector {

    @SuperInject(clazz = View.OnClickListener::class, method = "onClick", parameterTypes = [View::class])
    fun viewOnClick(listener: View.OnClickListener, view: View)

    @SuperInject(
        clazz = RadioGroup.OnCheckedChangeListener::class,
        method = "onCheckedChanged",
        parameterTypes = [RadioGroup::class, Int::class]
    )
    fun radioGroupOnChecked(listener: RadioGroup.OnCheckedChangeListener, radioGroup: RadioGroup, i: Int)

    @SuperInject(
        clazz = MaterialButtonToggleGroup.OnButtonCheckedListener::class,
        method = "onButtonChecked",
        parameterTypes = [MaterialButtonToggleGroup::class, Int::class, Boolean::class]
    )
    fun materialButtonToggleGroupOnButtonChecked(
        listener: MaterialButtonToggleGroup.OnButtonCheckedListener,
        materialButtonToggleGroup: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean
    )

    @SuperInject(
        clazz = CompoundButton.OnCheckedChangeListener::class,
        method = "onCheckedChanged",
        parameterTypes = [CompoundButton::class, Boolean::class]
    )
    fun compoundButtonOnChecked(
        listener: CompoundButton.OnCheckedChangeListener,
        button: CompoundButton,
        checked: Boolean
    )

    @SuperInject(
        clazz = OnItemClickListener::class,
        method = "onItemClick",
        parameterTypes = [AdapterView::class, View::class, Int::class, Long::class]
    )
    fun adapterViewOnItemClick(
        listener: OnItemClickListener,
        adapterView: AdapterView<*>,
        view: View?,
        position: Int,
        id: Long
    )

    @SuperInject(
        clazz = AdapterView.OnItemSelectedListener::class,
        method = "onItemSelected",
        parameterTypes = [AdapterView::class, View::class, Int::class, Long::class]
    )
    fun adapterViewOnItemSelected(
        listener: AdapterView.OnItemSelectedListener,
        adapterView: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    )

    @SuperInject(
        clazz = OnGroupClickListener::class,
        method = "onGroupClick",
        parameterTypes = [ExpandableListView::class, View::class, Int::class, Long::class],
        returnType = Boolean::class
    )
    fun expandableListViewOnGroupClick(
        listener: OnGroupClickListener,
        parent: ExpandableListView,
        v: View,
        groupPosition: Int,
        id: Long
    )

    @SuperInject(
        clazz = OnChildClickListener::class,
        method = "onChildClick",
        parameterTypes = [ExpandableListView::class, View::class, Int::class, Int::class, Long::class],
        returnType = Boolean::class
    )
    fun expandableListViewOnChildClick(
        listener: OnChildClickListener,
        parent: ExpandableListView,
        v: View,
        groupPosition: Int,
        childPosition: Int,
        id: Long
    )

    @SuperInject(
        clazz = TabLayout.OnTabSelectedListener::class,
        method = "onTabSelected",
        parameterTypes = [TabLayout.Tab::class],
    )
    fun tabLayoutOnTabSelected(listener: TabLayout.OnTabSelectedListener, tab: TabLayout.Tab)
}