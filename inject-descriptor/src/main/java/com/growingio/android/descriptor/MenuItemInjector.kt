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

import android.view.MenuItem
import android.widget.*
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import com.growingio.inject.annotation.BeforeSuper
import com.growingio.inject.annotation.Belong
import com.growingio.inject.annotation.Inject

@Belong(className = "com.growingio.android.sdk.autotrack.inject.MenuItemInjector")
interface MenuItemInjector {
    @BeforeSuper(
        clazz = Toolbar.OnMenuItemClickListener::class,
        method = "onMenuItemClick",
        parameterTypes = [MenuItem::class],
        returnType = Boolean::class
    )
    fun toolbarOnMenuItemClick(listener: Toolbar.OnMenuItemClickListener, item: MenuItem)
/*
    // 支持库中的 toolbar 由 View.OnClickListener 转发
    @BeforeSuper(
        clazz = androidx.appcompat.widget.Toolbar.OnMenuItemClickListener::class,
        method = "onMenuItemClick",
        parameterTypes = [MenuItem::class],
        returnType = Boolean::class
    )
    fun toolbarXOnMenuItemClick(listener: androidx.appcompat.widget.Toolbar.OnMenuItemClickListener, item: MenuItem)

    @Inject(
        targetClazz = "android/support/v7/widget/Toolbar\$OnMenuItemClickListener",
        targetMethod = "onMenuItemClick",
        targetMethodDesc = "(Landroid/view/MenuItem;)Z",
        injectMethod = "toolbarSupportOnMenuItemClick",
        injectMethodDesc = "(Landroid/support/v7/widget/Toolbar\$OnMenuItemClickListener;Landroid/view/MenuItem;)V",
        isAfter = false,
        isSuper = true
    )
    fun toolbarSupportOnMenuItemClick()
*/

    /**
     * 只处理android.jar上的ActionMenuView.OnMenuItemClickListener，不会造成多次调用 Toolbar.OnMenuItemClickListener.
     * 不处理appcompat和support包上的ActionMenuView.OnMenuItemClickListener，避免重复调用 Toolbar.OnMenuItemClickListener.
     */
    @BeforeSuper(
        clazz = ActionMenuView.OnMenuItemClickListener::class,
        method = "onMenuItemClick",
        parameterTypes = [MenuItem::class],
        returnType = Boolean::class
    )
    fun actionMenuViewOnMenuItemClick(listener: ActionMenuView.OnMenuItemClickListener, item: MenuItem)


    @BeforeSuper(
        clazz = PopupMenu.OnMenuItemClickListener::class,
        method = "onMenuItemClick",
        parameterTypes = [MenuItem::class],
        returnType = Boolean::class
    )
    fun popupMenuOnMenuItemClick(listener: PopupMenu.OnMenuItemClickListener, item: MenuItem)

/*
    @BeforeSuper(
        clazz = androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener::class,
        method = "onMenuItemClick",
        parameterTypes = [MenuItem::class],
        returnType = Boolean::class
    )
    fun popupMenuXOnMenuItemClick(
        listener: androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener,
        item: MenuItem
    )

    @Inject(
        targetClazz = "android/support/v7/widget/PopupMenu\$OnMenuItemClickListener",
        targetMethod = "onMenuItemClick",
        targetMethodDesc = "(Landroid/view/MenuItem;)Z",
        injectMethod = "popupMenuSupportOnMenuItemClick",
        injectMethodDesc = "(Landroid/support/v7/widget/PopupMenu\$OnMenuItemClickListener;Landroid/view/MenuItem;)V",
        isAfter = false,
        isSuper = true
    )
    fun popupMenuSupportOnMenuItemClick()
*/

}