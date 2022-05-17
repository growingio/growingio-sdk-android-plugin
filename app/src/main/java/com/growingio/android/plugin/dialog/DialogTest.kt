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

package com.growingio.android.plugin.dialog

import android.app.AlertDialog
import android.content.Context
import android.util.Log

/**
 * <p>
 *
 * @author cpacm 2022/5/11
 */
object DialogTest {

    fun createAppDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("app.AlertDialog.Test")
            .setPositiveButton("OK") { dialog, witch ->
                Log.d("Dialog", dialog.toString() + witch)
            }
            .setNegativeButton("cancle") { dialog, witch ->
                Log.d("Dialog", dialog.toString() + witch)
            }
            .create()
        alertDialog.show()
    }

    fun createDialogX(context: Context) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("app.AlertDialog.Test")
            .setPositiveButton("OK") { dialog, witch ->
                Log.d("Dialog", dialog.toString() + witch)
            }
            .create()
        alertDialog.show()
    }
}