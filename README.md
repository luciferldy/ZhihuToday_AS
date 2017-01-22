### 知乎日报·今日 AS版

![IC](art/ic_launcher.jpg)

发现 知乎日报·今日的 Eclipse 版有了一些 star ，其他开发者编译代码时需要手动导入 v7 等 jar 包并建立依赖关系，不利于项目的使用，管理和维护。

现在主流做法是使用 Android Studio + Gradle 开发并管理依赖，因此便有了这个项目。

### 简介

![PP](art/intro.gif)

首页使用 Activity ，日报的详情页使用 Fragment 的 WebView 展示。每个界面独立的 ToolBar ，适配4.4的半透明状态栏和5.0的透明状态栏。

项目使用 MVP 的架构模式，使用 Retrofit + RxJava 获取日报数据。

逻辑操作均放在 Presenter 中执行， Activity 和 Fragment 只是单纯的展示界面。

所有图片展示使用 [Fresco](http://fresco-cn.org/) 。


### 依赖库

* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [Retrofit](https://github.com/square/retrofit)
* [Fresco](http://fresco-cn.org/)

### 参考

* [DecentBanner](https://github.com/chengdazhi/DecentBanner) 参考了其中 indicator 的实现方式，应用到 dot 中。
* [ZhihuDailyPurify](https://github.com/izzyleung/ZhihuDailyPurify/) 数据来源与 ZhihuDailyPurify 对知乎日报 API 的分析。
* [EasyRecycleAdapterHelper](https://github.com/HotBitmapGG/EasyRecycleAdapterHelper) 加载 RecycleView item 时的动画效果。
* More 还有很多优秀的 Retrofit ，RxJava 的文章。

### License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.