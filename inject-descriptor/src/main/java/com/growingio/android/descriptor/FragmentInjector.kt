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
import com.growingio.inject.annotation.AfterSuper
import com.growingio.inject.annotation.Belong

/**
 * <p>
 *     fragment 注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.FragmentInjector")
interface FragmentInjector {

    @AfterSuper(clazz = Fragment::class, method = "onResume")
    @AfterSuper(clazz = DialogFragment::class, method = "onResume")
    @AfterSuper(clazz = ListFragment::class, method = "onResume")
    @AfterSuper(clazz = PreferenceFragment::class, method = "onResume")
    @AfterSuper(clazz = WebViewFragment::class, method = "onResume")
    fun systemFragmentOnResume(fragment: Fragment)

    @AfterSuper(clazz = Fragment::class, method = "setUserVisibleHint", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = DialogFragment::class, method = "setUserVisibleHint", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = ListFragment::class, method = "setUserVisibleHint", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = PreferenceFragment::class, method = "setUserVisibleHint", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = WebViewFragment::class, method = "setUserVisibleHint", parameterTypes = [Boolean::class])
    fun systemFragmentSetUserVisibleHint(fragment: Fragment, isVisibleToUser: Boolean)

    @AfterSuper(clazz = Fragment::class, method = "onHiddenChanged", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = DialogFragment::class, method = "onHiddenChanged", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = ListFragment::class, method = "onHiddenChanged", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = PreferenceFragment::class, method = "onHiddenChanged", parameterTypes = [Boolean::class])
    @AfterSuper(clazz = WebViewFragment::class, method = "onHiddenChanged", parameterTypes = [Boolean::class])
    fun systemFragmentOnHiddenChanged(fragment: Fragment, hidden: Boolean)

    @AfterSuper(clazz = Fragment::class, method = "onDestroyView")
    @AfterSuper(clazz = DialogFragment::class, method = "onDestroyView")
    @AfterSuper(clazz = ListFragment::class, method = "onDestroyView")
    @AfterSuper(clazz = PreferenceFragment::class, method = "onDestroyView")
    @AfterSuper(clazz = WebViewFragment::class, method = "onDestroyView")
    fun systemFragmentOnDestroyView(fragment: Fragment)

    @AfterSuper(clazz = androidx.fragment.app.Fragment::class, method = "onResume")
    fun androidxFragmentOnResume(fragment: androidx.fragment.app.Fragment?)

    @Deprecated("新版本的AndroidX Fragment setUserVisibleHint 将通过 FragmentTransaction setMaxLifecycle 来控制生命周期实现")
    @AfterSuper(
        clazz = androidx.fragment.app.Fragment::class,
        method = "setUserVisibleHint",
        parameterTypes = [Boolean::class]
    )
    fun androidxFragmentSetUserVisibleHint(fragment: androidx.fragment.app.Fragment, isVisibleToUser: Boolean)

    @AfterSuper(
        clazz = androidx.fragment.app.Fragment::class,
        method = "onHiddenChanged",
        parameterTypes = [Boolean::class]
    )
    fun androidxFragmentOnHiddenChanged(fragment: androidx.fragment.app.Fragment, hidden: Boolean)

    @AfterSuper(clazz = androidx.fragment.app.Fragment::class, method = "onDestroyView")
    fun androidxFragmentOnDestroyView(fragment: androidx.fragment.app.Fragment)
}