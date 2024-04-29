## 学习使用，还没有代码，只是结构提交，有哪里不对的地方感谢指正！
#### 1、新建[libmodule_a](libmodule_a)、[libmodule_b](libmodule_b)
### 分别在组件[build.gradle](libmodule_a%2Fbuild.gradle)
    apply from: '../config.gradle' 

    android {}

#### 2、增加[config_mudule.properties](config_mudule.properties)组件文件配置
* 是否开启组件化编译全局开关，用于开启 （library） 或者（application）模式切换
* isAllModule=true
* 为每个组件设置独立开启组件功能
* libmodule_a=false
* libmodule_b=true

#### 3、增加[config.gradle](config.gradle)配置，用来动态开启（library） 或者（application）

#### 4、[settings.gradle](settings.gradle)使用动态配置方式对模块进行include
* modules保存了所有的组件，新增组件只需要添加即可

QQ：672277407 
验证写 github，以防被略过。
