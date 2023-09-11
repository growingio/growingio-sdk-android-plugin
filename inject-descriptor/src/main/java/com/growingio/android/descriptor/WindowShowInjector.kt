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

import android.app.Dialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.growingio.inject.annotation.AroundInject
import com.growingio.inject.annotation.Belong
import com.growingio.inject.annotation.Inject

/**
 * <p>
 * @author cpacm 2023/8/23
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.WindowShowInjector")
interface WindowShowInjector {

    @AroundInject(clazz = Toast::class, method = "show")
    fun toastShow(toast: Toast)

    @AroundInject(clazz = Dialog::class, method = "show")
    fun dialogShow(dialog: Dialog)

    @AroundInject(clazz = TimePickerDialog::class, method = "show")
    fun timePickerDialogShow(dialog: TimePickerDialog)

    @AroundInject(
        clazz = DialogFragment::class,
        method = "show",
        parameterTypes = [FragmentManager::class, String::class]
    )
    fun dialogFragmentShow(fragment: DialogFragment, fm: FragmentManager, tag: String)

    @AroundInject(
        clazz = DialogFragment::class,
        method = "show",
        parameterTypes = [FragmentTransaction::class, String::class],
        returnType = Int::class
    )
    fun dialogFragmentShowFt(fragment: DialogFragment, ft: FragmentTransaction, tag: String)


    @AroundInject(
        clazz = android.app.DialogFragment::class,
        method = "show",
        parameterTypes = [android.app.FragmentManager::class, String::class]
    )
    fun dialogFragmentSystemShow(fragment: android.app.DialogFragment, fm: android.app.FragmentManager, tag: String)

    @AroundInject(
        clazz = android.app.DialogFragment::class,
        method = "show",
        parameterTypes = [android.app.FragmentTransaction::class, String::class],
        returnType = Int::class
    )
    fun dialogFragmentSystemShowFt(
        fragment: android.app.DialogFragment,
        fm: android.app.FragmentTransaction,
        tag: String
    )


    @Inject(
        targetClazz = "android/support/v4/app/Fragment",
        targetMethod = "show",
        targetMethodDesc = "(Landroid/support/v4/app/FragmentManager;Ljava/lang/String;)V",
        injectMethod = "dialogFragmentV4Show",
        injectMethodDesc = "(Landroid/support/v4/app/DialogFragment;Landroid/support/v4/app/FragmentManager;Ljava/lang/String;)V",
        isAfter = true,
        type = 2
    )
    fun dialogFragmentV4Show()

    @Inject(
        targetClazz = "android/support/v4/app/Fragment",
        targetMethod = "show",
        targetMethodDesc = "(Landroid/support/v4/app/FragmentTransaction;Ljava/lang/String;)I",
        injectMethod = "dialogFragmentV4ShowFt",
        injectMethodDesc = "(Landroid/support/v4/app/DialogFragment;Landroid/support/v4/app/FragmentTransaction;Ljava/lang/String;)V",
        isAfter = true,
        type = 2
    )
    fun dialogFragmentV4ShowFt()


    @AroundInject(clazz = PopupMenu::class, method = "show")
    fun popupMenuShow(menu: PopupMenu)

    @AroundInject(
        clazz = PopupWindow::class,
        method = "showAsDropDown",
        parameterTypes = [View::class, Int::class, Int::class, Int::class]
    )
    fun popupWindowShowAsDropDown(popupWindow: PopupWindow, view: View, x: Int, y: Int, g: Int)

    @AroundInject(
        clazz = PopupWindow::class,
        method = "showAtLocation",
        parameterTypes = [View::class, Int::class, Int::class, Int::class]
    )
    fun popupWindowShowAtLocation(popupWindow: PopupWindow, view: View, g: Int, x: Int, y: Int)
}