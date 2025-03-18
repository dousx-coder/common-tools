# 常用工具类

## 1.编译

```shell
mvn clean package install -Dmaven.test.skip=true
```

`powershell`部分命令需要加`单引号`

```powershell
mvn clean package install '-Dmaven.test.skip=true'
```

## 2. 引入

1. `maven`工程

```xml

<dependency>
    <groupId>cn.cruder.dousx</groupId>
    <artifactId>common-tools</artifactId>
    <version>1.1.20250318-17</version>
</dependency>
```

2. `gradle`工程

```groovy
 implementation group: 'cn.cruder.dousx', name: 'common-tools', version: '1.1.20250318-17'
```

