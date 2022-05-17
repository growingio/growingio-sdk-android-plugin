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

import android.accounts.AccountAuthenticatorActivity
import android.app.*
import android.content.Intent
import android.preference.PreferenceActivity
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.growingio.inject.annotation.BeforeSuper
import com.growingio.inject.annotation.Belong

/**
 * <p>
 *     Activity 注入
 * @author cpacm 2022/5/7
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.ActivityInjector")
interface ActivityInjector {

    @BeforeSuper(clazz = Activity::class, method = "onNewIntent", parameterTypes = arrayOf(Intent::class))
    @BeforeSuper(clazz = AccountAuthenticatorActivity::class, method = "onNewIntent", parameterTypes = arrayOf(Intent::class))
    @BeforeSuper(clazz = ActivityGroup::class, method = "onNewIntent", parameterTypes = [Intent::class])
    @BeforeSuper(clazz = AliasActivity::class, method = "onNewIntent", parameterTypes = [Intent::class])
    @BeforeSuper(clazz = ExpandableListActivity::class, method = "onNewIntent", parameterTypes = [Intent::class])
    @BeforeSuper(clazz = LauncherActivity::class, method = "onNewIntent", parameterTypes = [Intent::class])
    @BeforeSuper(clazz = ListActivity::class, method = "onNewIntent", parameterTypes = [Intent::class])
    @BeforeSuper(clazz = NativeActivity::class, method = "onNewIntent", parameterTypes = [Intent::class])
    @BeforeSuper(clazz = TabActivity::class, method = "onNewIntent", parameterTypes = [Intent::class])
    @BeforeSuper(clazz = PreferenceActivity::class, method = "onNewIntent", parameterTypes = [Intent::class])
    fun onActivityNewIntent(activity: Activity, intent: Intent)


    @BeforeSuper(clazz = Activity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = AccountAuthenticatorActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = ActivityGroup::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = AliasActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = ExpandableListActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = LauncherActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = ListActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = NativeActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = TabActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    @BeforeSuper(clazz = PreferenceActivity::class, method = "onOptionsItemSelected", parameterTypes = [MenuItem::class], returnType = Boolean::class)
    fun menuItemOnOptionsItemSelected(activity: Activity, item: MenuItem)


    @BeforeSuper(
        clazz = ExpandableListActivity::class,
        method = "onChildClick",
        parameterTypes = [ExpandableListView::class, View::class, Int::class, Int::class, Long::class],
        returnType = Boolean::class
    )
    fun expandableListActivityOnChildClick(
        activity: ExpandableListActivity,
        parent: ExpandableListView,
        v: View,
        groupPosition: Int,
        childPosition: Int,
        id: Long
    )

    @BeforeSuper(
        clazz = ListActivity::class,
        method = "onListItemClick",
        parameterTypes = [ListView::class, View::class, Int::class, Long::class]
    )
    fun listActivityOnListItemClick(activity: ListActivity, listView: ListView, view: View, position: Int, id: Long)
}