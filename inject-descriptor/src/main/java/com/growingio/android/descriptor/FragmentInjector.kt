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

@file:Suppress("DEPRECATION")

package com.growingio.android.descriptor

import android.app.DialogFragment
import android.app.Fragment
import android.app.ListFragment
import android.preference.PreferenceFragment
import android.webkit.WebViewFragment
import com.growingio.inject.annotation.SuperInject
import com.growingio.inject.annotation.Belong

/**
 * <p>
 *     fragment 注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.FragmentInjector")
interface FragmentInjector {

    @SuperInject(clazz = Fragment::class, method = "onResume", isAfter = true)
    @SuperInject(clazz = DialogFragment::class, method = "onResume", isAfter = true)
    @SuperInject(clazz = ListFragment::class, method = "onResume", isAfter = true)
    @SuperInject(clazz = PreferenceFragment::class, method = "onResume", isAfter = true)
    @SuperInject(clazz = WebViewFragment::class, method = "onResume", isAfter = true)
    fun systemFragmentOnResume(fragment: Fragment)

    @SuperInject(
        clazz = Fragment::class,
        method = "setUserVisibleHint",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    @SuperInject(
        clazz = DialogFragment::class,
        method = "setUserVisibleHint",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    @SuperInject(
        clazz = ListFragment::class,
        method = "setUserVisibleHint",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    @SuperInject(
        clazz = PreferenceFragment::class,
        method = "setUserVisibleHint",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    @SuperInject(
        clazz = WebViewFragment::class,
        method = "setUserVisibleHint",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    fun systemFragmentSetUserVisibleHint(fragment: Fragment, isVisibleToUser: Boolean)

    @SuperInject(clazz = Fragment::class, method = "onHiddenChanged", parameterTypes = [Boolean::class], isAfter = true)
    @SuperInject(
        clazz = DialogFragment::class,
        method = "onHiddenChanged",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    @SuperInject(
        clazz = ListFragment::class,
        method = "onHiddenChanged",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    @SuperInject(
        clazz = PreferenceFragment::class,
        method = "onHiddenChanged",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    @SuperInject(
        clazz = WebViewFragment::class,
        method = "onHiddenChanged",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    fun systemFragmentOnHiddenChanged(fragment: Fragment, hidden: Boolean)

    @SuperInject(clazz = Fragment::class, method = "onDestroyView", isAfter = true)
    @SuperInject(clazz = DialogFragment::class, method = "onDestroyView", isAfter = true)
    @SuperInject(clazz = ListFragment::class, method = "onDestroyView", isAfter = true)
    @SuperInject(clazz = PreferenceFragment::class, method = "onDestroyView", isAfter = true)
    @SuperInject(clazz = WebViewFragment::class, method = "onDestroyView", isAfter = true)
    fun systemFragmentOnDestroyView(fragment: Fragment)

    @SuperInject(clazz = androidx.fragment.app.Fragment::class, method = "onResume", isAfter = true)
    fun androidxFragmentOnResume(fragment: androidx.fragment.app.Fragment?)

    @Deprecated("新版本的AndroidX Fragment setUserVisibleHint 将通过 FragmentTransaction setMaxLifecycle 来控制生命周期实现")
    @SuperInject(
        clazz = androidx.fragment.app.Fragment::class,
        method = "setUserVisibleHint",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    fun androidxFragmentSetUserVisibleHint(fragment: androidx.fragment.app.Fragment, isVisibleToUser: Boolean)

    @SuperInject(
        clazz = androidx.fragment.app.Fragment::class,
        method = "onHiddenChanged",
        parameterTypes = [Boolean::class],
        isAfter = true
    )
    fun androidxFragmentOnHiddenChanged(fragment: androidx.fragment.app.Fragment, hidden: Boolean)

    @SuperInject(clazz = androidx.fragment.app.Fragment::class, method = "onDestroyView", isAfter = true)
    fun androidxFragmentOnDestroyView(fragment: androidx.fragment.app.Fragment)
}