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

import com.growingio.inject.annotation.Belong
import com.growingio.inject.annotation.Inject

/**
 * <p>
 *     fragment support 注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.FragmentV4Injector")
interface FragmentSupportInjector {

    @Inject(
        targetClazz = "android/support/v4/app/Fragment",
        targetMethod = "onResume",
        targetMethodDesc = "()V",
        injectMethod = "v4FragmentOnResume",
        injectMethodDesc = "(Landroid/support/v4/app/Fragment;)V",
        isAfter = true,
        type = 1
    )
    fun v4FragmentOnResume()

    @Inject(
        targetClazz = "android/support/v4/app/Fragment",
        targetMethod = "setUserVisibleHint",
        targetMethodDesc = "(Z)V",
        injectMethod = "v4FragmentSetUserVisibleHint",
        injectMethodDesc = "(Landroid/support/v4/app/Fragment;Z)V",
        isAfter = true,
        type = 1
    )
    fun v4FragmentSetUserVisibleHint()

    @Inject(
        targetClazz = "android/support/v4/app/Fragment",
        targetMethod = "onHiddenChanged",
        targetMethodDesc = "(Z)V",
        injectMethod = "v4FragmentOnHiddenChanged",
        injectMethodDesc = "(Landroid/support/v4/app/Fragment;Z)V",
        isAfter = true,
        type = 1
    )
    fun v4FragmentOnHiddenChanged()

    @Inject(
        targetClazz = "android/support/v4/app/Fragment",
        targetMethod = "onDestroyView",
        targetMethodDesc = "()V",
        injectMethod = "v4FragmentOnDestroyView",
        injectMethodDesc = "(Landroid/support/v4/app/Fragment;)V",
        isAfter = true,
        type = 1
    )
    fun v4FragmentOnDestroyView()
}