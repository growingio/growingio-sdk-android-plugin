# GrowingIO Android SDK Plugin

---------

该项目为 [growingio-sdk-android-autotracker](https://github.com/growingio/growingio-sdk-android-autotracker/) 的插件部分，主要用于实现无埋点的代码插入功能。

项目主要模块为二个部分：

1. autotracker-gradle-plugin 插件代码实现部分，主要功能为：
  * 适配 AGP 8.0  Instrumentation API；
  * 兼容 AGP4.2及其更早版本的 Transform API；
  * 优化插件对脱糖的处理；
  * 兼容 AGP 7.0 及其以上 `pluginManagement` 的依赖方式；
  * 提供了完整的单元测试。

2. inject-annotation 无埋点代码Hook注解及其处理器, 用于生成无埋点Hook配置类以便插件进行无埋点代码插入
  * 提供两种注解方式，一是指向对应的类二是自己填入需要注解的类的描述符；
  * 使用 KSP kotlin 注解处理器生成 kotlin 代码，比传统的APT和KAPT效率更高；
  * inject-descriptor 为注解的描述文件，全部由接口文件组成；

## 如何集成
这里只说明在 Android Gradle插件为7.0及以上版本时的集成方式，若是AGP7以下则按照 [插件集成](https://growingio.github.io/growingio-sdk-docs/docs/android/base/Introduce#添加插件) 集成即可

### 添加 Maven 仓库
需要在 project 中的 `settings.gradle` 文件中添加Maven仓库

```groovy
// 当AGP版本为7.0及以上添加
pluginManagement {
    repositories {
        // 添加maven仓库
        mavenCentral()
        //如果使用 SNAPSHOT 版本，则需要使用如下该仓库。
        maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
        gradlePluginPortal()
        google()
      
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 添加maven仓库
        mavenCentral()
        //如果使用 SNAPSHOT 版本，则需要使用如下该仓库。
        maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
        google()
    }
}
```

### 添加插件路径
需要在 project 下的 `build.gradle` 下添加 Growingio 插件

```groony
plugins {
    id 'com.android.application' version '7.2.0' apply false

    ···
    // 添加GrowingIO 无埋点 SDK 插件
    id 'com.growingio.android.autotracker' version '3.4.0' apply false
}
```

### 使用插件
在 app 级别的 `build.gradle` 文件中添加 `com.growingio.android.autotracker` 插件
```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    // 使用 GrowingIO 无埋点 SDK 插件
    id 'com.growingio.android.autotracker'
}
```

## 插件配置说明

为了满足插件在不同项目环境下能够正常使用，Growingio 插件提供了以下配置。（非必需情况下可以不用添加）

| Extension                    | 参数类型         | 是否必填 | 默认值 | 说明 |  版本 |
| :-------------------------   | :------         | :----:  |:------  |:------| --------------------------   |
| `logEnabled`                 | `Boolean`       | 否      | `false`  | 编译时是否输出log日志          |  |
| `includePackages`            | `Array<String>` | 否      | `null`   | 需要额外包含编译的包名          |  |
| `excludePackages`            | `Array<String>` | 否      | `null`   | 需要跳过编译的包名             |  |
| `analyticsAdapter`           | `Extension`     | 否      | -        | 用于配置是否适配第三方分析数据   |  |
|     - `firebaseAnalytics`    | `Boolean`       | 否      | `false`  | 用于配置是否适配Firebase分析SDK |  |
|     - `googleAnalytics`      | `Boolean`       | 否      | `false`  | 用于配置是否适配GoogleAnalyticsSDK |  |


配置代码示例
```groony
plugins {
    ···
    // 使用 GrowingIO 无埋点 SDK 插件
    id 'com.growingio.android.autotracker'
}

growingAutotracker {
    logEnabled false
    includePackages "com.growingio.xxx1","com.growingio.xxx2"
    excludePackages "com.cpacm.xxx1"
    analyticsAdapter {
        firebaseAnalytics = false
        googleAnalytics = false
    }
}


dependencies {
  ···
}
```


## License
```
Copyright (C) 2022 Beijing Yishu Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```