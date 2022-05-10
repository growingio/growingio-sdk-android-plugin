# GrowingIO Android SDK Plugin
====
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
