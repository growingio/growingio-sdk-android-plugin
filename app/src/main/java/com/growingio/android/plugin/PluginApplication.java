/*
 * Copyright (C) 2020 Beijing Yishu Technology Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.growingio.android.plugin;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.growingio.android.sdk.autotrack.CdpAutotrackConfiguration;
import com.growingio.android.sdk.autotrack.GrowingAutotracker;
import com.growingio.android.sdk.track.log.Logger;

import java.util.List;

public class PluginApplication extends Application {
    private static final String TAG = "PluginApplication";

    private static CdpAutotrackConfiguration sConfiguration;

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();

        if (sConfiguration == null) {
            sConfiguration = new CdpAutotrackConfiguration("ac04dfef4a1b6ec7", "growing.6b963145e9509ad0")
                    .setDataSourceId("ae1a372733eabfa8")
                    .setDataCollectionServerHost("https://collector-opdemo.growingio.com")
                    .setDebugEnabled(true)
                    .setDataCollectionEnabled(true);
                    //.setRequireAppProcessesEnabled(true)
        }

        enableStrictMode();

        long startTime = System.currentTimeMillis();
        GrowingAutotracker.startWithConfiguration(this, sConfiguration);
        Log.d(TAG, "start time: " + (System.currentTimeMillis() - startTime));
    }

    private boolean isMainProcess() {
        @SuppressLint("WrongConstant") ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();

        if (processInfos == null) {
            Logger.e(TAG, "isMainProcess: RunningAppProcessInfo list is NULL");
            return false;
        }
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void enableStrictMode() {
        StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
                .detectNetwork()
                .detectCustomSlowCalls()
                .permitDiskReads()
                .permitDiskWrites()
                .penaltyLog();
        StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectActivityLeaks()
                .penaltyLog();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            threadPolicyBuilder.detectResourceMismatches();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            threadPolicyBuilder.detectUnbufferedIo();
            vmPolicyBuilder.detectContentUriWithoutPermission();
        }

        StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        StrictMode.setVmPolicy(vmPolicyBuilder.build());
    }
}
