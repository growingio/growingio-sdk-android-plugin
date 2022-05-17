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

import android.view.KeyEvent
import android.view.MenuItem
import android.widget.*
import androidx.core.widget.CompoundButtonCompat
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import com.growingio.inject.annotation.BeforeSuper

/**
 * 暂未参与埋点的可交互控件
 */
interface InvalidInjector {

    @BeforeSuper(
        clazz = TimePicker.OnTimeChangedListener::class,
        method = "onTimeChanged",
        parameterTypes = [TimePicker::class, Int::class, Int::class],
    )
    fun timePickerTimeChanged(
        listener: TimePicker.OnTimeChangedListener,
        timePicker: TimePicker,
        hourOfDay: Int, minute: Int
    )

    @BeforeSuper(
        clazz = DatePicker.OnDateChangedListener::class,
        method = "onDateChanged",
        parameterTypes = [DatePicker::class, Int::class, Int::class, Int::class],
    )
    fun datePickerTimeChanged(
        listener: DatePicker.OnDateChangedListener,
        timePicker: DatePicker,
        year: Int, monthOfYear: Int, dayOfMonth: Int
    )

    @BeforeSuper(
        clazz = TextView.OnEditorActionListener::class,
        method = "onEditorAction",
        parameterTypes = [TextView::class, Int::class, KeyEvent::class],
        returnType = Boolean::class
    )
    fun textViewEditorAction(listener: TextView.OnEditorActionListener, actionId: Int, event: KeyEvent)


    @BeforeSuper(
        clazz = Slider.OnSliderTouchListener::class,
        method = "onStopTrackingTouch",
        parameterTypes = [Slider::class],
    )
    fun sliderTouch(slider: Slider)

    @BeforeSuper(
        clazz = SearchView.OnQueryTextListener::class,
        method = "onQueryTextSubmit",
        parameterTypes = [SearchView::class, String::class],
        returnType = Boolean::class
    )
    fun searchViewTextSubmit(listener: SearchView.OnQueryTextListener, query: String)

    @BeforeSuper(
        clazz = NavigationBarView.OnItemSelectedListener::class,
        method = "onNavigationItemSelected",
        parameterTypes = [MenuItem::class],
        returnType = Boolean::class
    )
    fun naviBarViewOnMenuItemClick(
        listener: NavigationBarView.OnItemSelectedListener,
        item: MenuItem
    )

    @BeforeSuper(
        clazz = TabLayout.OnTabSelectedListener::class,
        method = "onTabSelected",
        parameterTypes = [TabLayout.Tab::class],
    )
    fun tabLayoutSelected(tab: TabLayout.Tab)

}