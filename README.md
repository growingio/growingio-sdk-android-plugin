# GrowingIO Android SDK Plugin

---------

该项目为 [growingio-sdk-android-autotracker](https://github.com/growingio/growingio-sdk-android-autotracker/) 的插件部分，主要用于实现无埋点的代码插入功能。

Growingio Sdk Gradle Plugin 4.0 具有以下的功能特性：
* 适配 AGP 8.0 Instrumentation API；
* 兼容 AGP4.2及其更早版本的 Transform API；
* 在插件中可以配置集成Giokit;

## 如何集成
这里只说明在 Android Gradle插件为7.0及以上版本时的集成方式。

### 添加 Maven 仓库
需要在 project 中的 `settings.gradle` 文件中添加Maven仓库

```groovy
// 当AGP版本为7.0及以上添加
pluginManagement {
    repositories {
        // 添加maven仓库
        gradlePluginPortal()
        //如果使用 SNAPSHOT 版本，则需要使用如下该仓库。
        maven { url "https://central.sonatype.com/repository/maven-snapshots/" }
        google()
      
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 添加maven仓库
        mavenCentral()
        //如果使用 SNAPSHOT 版本，则需要使用如下该仓库。
        maven { url "https://central.sonatype.com/repository/maven-snapshots/" }
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
    id 'com.growingio.android.autotracker' version '4.0.0' apply false
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
| logEnabled                 | _Boolean_       | 否      | `false`  | 编译时是否输出log日志          |  |
| skipDependencyCheck       | _Boolean_       | 否      | `false`  | 编译时检测当前project是否配置SDK依赖（模块中依赖时配置为true）          |  |
| includePackages            | _Array<String\>_ | 否      | `null`   | 需要额外包含编译的包名          |  |
| excludePackages            | _Array<String\>_ | 否      | `null`   | 需要跳过编译的包名             |  |
| giokit                     | _GiokitExtension_ | 否    | `null`   | 可以用来配置是否引入 Giokit | | 

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
    giokit {
        //...
    }
}


dependencies {
  ···
}
```

### Giokit 配置

| Extension                    | 参数类型         | 是否必填 | 默认值                 | 说明 |
| :-------------------------   | :------         | :----:  |:--------------------|:------|
| enabled                   | _Boolean_       | 否      | `false`             |  是否添加 Giokit        |
| trackerFinderEnabled      | _Boolean_       | 否      | `true`              | 查找App下调用App埋点接口的信息      |
| trackerFinderDomain        | _Array<String\>_ | 否      | 默认为应用 ApplicationId | 查找的范围  |
| trackerCalledMethod        | _Array<String\>_ | 否      | 默认为SDK相应接口          | 要查找的类和方法  |
| autoAttachEnabled          | _Boolean_       | 否      | `true`              |  GioKit 是否自动依附在Activity上，若设为false，需要自行调用api打开GioKit  |
| releaseEnabled             | _Boolean_       | 否      | `false`             |  **请不要打开**，否则会在 Release 打包中包含 GioKit 代码    |
| autoInstallVersion         | _String_        | 否      | `2.1.2`             |  自动依赖的GioKit版本号             |

现在SDK不用再额外引入 Giokit，只需要在插件中开启即可。示例如下：

```groovy
growingAutotracker {
    logEnabled true
    giokit {
        enabled false  //开启则会引入 GioKit
        trackerFinderEnabled true
        trackerFinderDomain "com.xxxx.yourapplication"
        trackerCalledMethod "com.growingio.android.tracker#trackCumtomEvent"
        autoAttachEnabled true
        releaseEnabled false
        autoInstallVersion "2.1.2"
    }
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