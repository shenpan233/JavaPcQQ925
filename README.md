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
# ``由于java代码安全性极差，已经放弃本项目``
