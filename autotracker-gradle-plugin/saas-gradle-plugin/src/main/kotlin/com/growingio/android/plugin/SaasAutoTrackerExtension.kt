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

package com.growingio.android.plugin

import org.gradle.internal.reflect.Instantiator

/**
 * <p>
 *
 * @author cpacm 2022/3/30
 */

open class SaasAutoTrackerExtension(var instantiator: Instantiator) {

    var logEnabled = false

    var skipDependencyCheck = false

    var enableRn = false

    var excludePackages: Array<String>? = null

    var includePackages: Array<String>? = null

    var injectClasses: Array<String>? = null
}