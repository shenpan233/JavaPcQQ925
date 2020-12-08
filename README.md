# JavaPcQQ925
This is a protocol Robot for PcQQ9.2.5,but it's not perfect.  
这是一个PcQQ9.2.5的协议机器人，但还只是个**半成品**  

## 已知的bug(懒得修):  
1. 对于非常用IP的账号无法登录(其实是我懒得写后续)  


## 目录文件说明
  - PCQQ
    - bin\lib=>lib库文件
    - src    =>代码目录
      - Mysql =>Mysql操作，留着不用
      - Pack =>协议组包工具
      - Plugin =>插件系统 下面细讲使用方法
      - UDP  =>udpSocket组件，用于和服务器通讯
      - info =>账号信息，token，cookies，qqtea集合
      - lib  => 。。。啊这多了一个可删
      - main =>主程序
        - test.java =>用于测试的
        - _Tlv.java =>tlv类型数据组包
        - qq.java   =>主程序main函数
      - tools =>工具类，用于Tea加密和字节数组操作  
#### 其他的**看名字结合翻译工具**就能懂了不细讲！

## 插件系统
  **默认端口2333**，协议方式为**UDP**，通讯方式**JSON**  
  你也可以到**Plugin/PluginServer**下修改**类变量port**的参数来修改端口  
  
  目前只有3个**可用**API，我还是太菜了,直接上JSON数据，自己看着组吧QWQ  
  
  以下加粗为变量，根据自己需求改变  
  初始化：{"cmd":"Init","Name":**插件名称**,"Writer":**作者的大名**}  
  发好友消息：{"cmd":"SendFriendMsg","FriendQQ":**好友QQ**,"Msg",**消息内容**}  
  发群消息：{"cmd":"GroupUin","FriendQQ":**群号**,"Msg",**消息内容**}  
    - 消息变量说明：群里艾特：\[TR:at=**QQ号**\] 图片变量：\[TR:pic=**GUID**\](Guid可通过发消息给机器人日志中获取！)  
  
  
## 发行版使用方法  
对于不会java的小白，你可以打开**cmd**，输入**java -jar 下载jar包路径**来使用  
# ``由于java代码安全性极差，已经放弃本项目``
