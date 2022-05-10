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

import android.app.AlertDialog
import android.content.DialogInterface
import com.growingio.inject.annotation.After
import com.growingio.inject.annotation.BeforeSuper
import com.growingio.inject.annotation.Belong

/**
 * <p>
 *
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.DialogInjector")
interface DialogInjector {

    @After(clazz = AlertDialog::class, method = "show")
    fun alertDialogShow(alertDialog: AlertDialog)

    @BeforeSuper(
        clazz = DialogInterface.OnClickListener::class,
        method = "onClick",
        parameterTypes = [DialogInterface::class, Int::class]
    )
    fun dialogOnClick(listener: DialogInterface.OnClickListener, dialogInterface: DialogInterface, which: Int)
}