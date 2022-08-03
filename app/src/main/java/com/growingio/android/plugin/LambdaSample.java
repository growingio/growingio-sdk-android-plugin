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

package com.growingio.android.plugin;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toolbar;

/**
 * <p>
 *
 * @author cpacm 2022/4/19
 */
public class LambdaSample implements View.OnClickListener {

    private void methodClick(View v) {
        Log.d("LambdaUtil", "methodClick");
    }

    //正常调用
    public void print(View view) {
        view.setOnClickListener(this);
    }

    //lambda表达式
    public void print0(View view) {
        view.setOnClickListener(v -> {
            Log.d("LambdaUtil", "print2");
        });
    }

    //方法引用1
    public void print1(View view) {
        view.setOnClickListener(this::onClick);
    }

    //方法引用2
    public void print2(View view) {
        view.setOnClickListener(this::methodClick);
    }

    //方法引用重复1
    public void print3(Toolbar toolbar) {
        toolbar.setOnMenuItemClickListener(this::menuItemClick);
    }

    //方法引用重复2
    public void print4(PopupMenu popupMenu) {
        popupMenu.setOnMenuItemClickListener(this::menuItemClick);
    }

    public boolean menuItemClick(MenuItem item) {
        Log.d("LambdaUtil", "onMenuItemClick");
        return true;
    }

    // 外部类方法引用
    public void print5(View view) {
        view.setOnClickListener(ClickTestClass::onClick);
    }

    // 外部类方法引用
    public void print6(PopupMenu popupMenu) {
        popupMenu.setOnMenuItemClickListener(ClickTestClass::menuItemClick);
    }

    @Override
    public void onClick(View v) {
        Log.d("LambdaUtil", "onClick");
    }

    static class ClickTestClass {
        public static void onClick(View v) {
            Log.d("ClickTestClass", "onClick");
        }

        public static boolean menuItemClick(MenuItem item) {
            Log.d("ClickTestClass", "onMenuItemClick");
            return true;
        }
    }

    public void digitalClick(int i) {
        Log.d("LambdaUtil", "digitalClick");
    }

    public void print7(MenuItem item, int position) {
        float test2 = position * 1.0f;
        item.getActionView().setOnClickListener(v -> {
            digitalClick((int) test2);
        });
    }

    public void print8(MenuItem item, int position) {
        float test2 = position * 1.0f;
        item.getActionView().setOnClickListener(v -> {
            Log.d("LambdaUtil", test2+"cpacm");
        });
    }

}

