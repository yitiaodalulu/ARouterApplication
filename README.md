#### 1、新建[libmodule_a](libmodule_a)、[libmodule_b](libmodule_b)

#### 2、增加[config_mudule.properties](config_mudule.properties)组件文件配置
* 是否开启组件化编译全局开关，用于开启 （library） 或者（application）模式切换
* isAllModule=true
* 为每个组件设置独立开启组件功能，debug
* libmodule_a=false
* libmodule_b=true

#### 3、增加[config.gradle](config.gradle)配置，用来动态开启（library） 或者（application）

#### 4、[settings.gradle](settings.gradle)使用动态配置方式对模块进行include
* modules保存了所有的组件，新增组件只需要添加即可
* 