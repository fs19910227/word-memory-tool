# word-memory-tool
联想词记忆辅助工具

三阶魔方盲拧一般都会通过字母联想的方式进行记忆。
一般为两个英文字母组成一组联想编码，记忆和整理编码均需要耗费大量的工作，故此打造工具辅助整理编码，记忆联想。
暂时只支持基于shell操作
### 使用到的技术
基于SpringShell + JPA + H2Database
### SUPPORTS
- 支持基于分组的隔离，可以基于分组录入多份联想词
- 支持二维联想词表格的导入
### quick start
first,use maven to package
>mvn package

second run run.bat
>./run.bat
### 可用命令
具体可以查看内部帮助信息，此处只给出简单的命令

- init 
  初始化数据

- use
  切换分组,不存在分组自动创建

- groups
  查询所有分组信息 
  
- query,q
  查询编码的联想词。前缀模糊查询。

- add,a
  新增联想词
  
- edit,e
  编辑联想词

- delete,d
  删除联想词,不可逆
  
- import
  导入联想词。
  
- drop
  清除当前分组所有数据，无确认，不可逆，谨慎操作。
  
- test 
	联想词记忆测试
### 配置方式
通过 db.location 配置数据库文件的路径


