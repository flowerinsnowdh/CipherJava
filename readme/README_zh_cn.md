# CipherJava
一个使用原生 Java 和原生算法进行加解密的对称和非对称加密工具

# 环境
请使用 Java 17

# 用法
<details open="open">

|       参数名       | 简写 |                 参数类型                  |               参数说明               |
|:---------------:|:--:|:-------------------------------------:|:--------------------------------:|
|    --encrypt    | -e |             [Void](#Void)             |               指定加密               |
|    --decrypt    | -d |             [Void](#Void)             |               指定解密               |
|    --newkey     | -n |             [Void](#Void)             |             指定随机生成密钥             |
|     --seed      |    |           [String](#String)           |      指定生成密钥的种子；若不指定，将完全随机。       |
|    --length     | -l |          [Integer](#Integer)          |       指定生成密钥长度[\[1\]](#1)        |
|    --string     | -s |           [String](#String)           |      指定字符串作为[\[输入源\]](#输入源)      |
|     --file      | -f |             [Path](#Path)             |      指定文件作为[\[输入源\]](#输入源)       |
|   --algorithm   | -a |  [Enum&lt;Algorithm&gt;](#Algorithm)  |        指定加密算法。若不指定，默认AES         |
|   --keybase64   |    |           [Base64](#Base64)           |      指定格式为Base64的密钥，必须是合法密钥      |
|    --keyhex     |    |              [HEX](#HEX)              |     指定格式为十六进制字符串的密钥，必须是合法密钥      |
|      --key      | -k |           [String](#String)           |        指定字符串密钥[\[2\]](#2)        |
| --keyfilebase64 |    |             [Path](#Path)             |     指定格式为Base64的密钥文件，必须是合法密钥     |
|  --keyfilehex   |    |             [Path](#Path)             |    指定格式为十六进制字符串的密钥文件，必须是合法密钥     |
|    --keyfile    |    |             [Path](#Path)             |      指定格式为二进制的密钥文件，必须是合法密钥       |
|    --output     | -o |             [Path](#Path)             | 指定文件作为输出路径[\[3\]](#3)[\[6\]](#6) |
| --publicoutput  |    |             [Path](#Path)             |     指定文件作为公钥输出路径[\[6\]](#6)      |
|  --outputtype   | -t | [Enum&lt;OutputType&gt;](#OutputType) |       指定输出格式。若不指定，默认base64       |
| --inputcharset  |    |           [String](#String)           | 指定[\[输入源\]](#输入源)编码。若不指定，默认UTF-8 |
| --outputcharset |    |           [String](#String)           |       指定输出编码。若不指定，默认UTF-8        |
|    --cipher     | -c |           [String](#String)           |        指定加密算法。[\[4\]](#4)        |
|   --warpping    | -w |             [Void](#Void)             |       指定采用密钥封装法[\[5\]](#5)       |

## 1
单位：字节。**注意不是位**

| 密钥算法 | 默认长度 |     合法长度     |
|:----:|:----:|:------------:|
| AES  |  16  |  16, 24, 32  |
| DES  |  8   |      8       |
| RSA  | 256  | \[64, 2048\] |
|  EC  |  32  |    32, 48    |

## 2
可以为任意字符串，一般作为密码。

将把密码作为随机数种子生成密钥。

建议仅应用于对称加密，非对称加密使用随机生成的密钥。

## 3
若输出对象为私钥，则为私钥输出位置

## 4
| 密钥算法 |        默认加密算法        |
|:----:|:--------------------:|
| AES  | AES/ECB/PKCS5Padding |
| DES  | DES/ECB/PKCS5Padding |
| RSA  |         RSA          |
|  EC  |        ECIES         |

## 5
密钥封装法是指：先将对称密钥用非对称密钥加密，再用对称密钥加密数据，并将其一起放入文件中

非对称加密很有可能有上限数据长度，例如 RSA 的长度上限即为

上限数据长度 = RSA 密钥长度（以位为单位） / 8 - 填充字节长度

所以建议配合密钥封装法

## 6
文件的写入会遵循以下原则

- 当文件不存在时，将创建该文件
- 当文件存在时，会清空重写该文件
- 当文件无法被创建和写入时，会出现错误并退出程序。见[退出代码](#退出代码)

## 输入源
指要加密的内容

## Void
表示这是一个独立的参数，后面不需要跟任何参数

## String
表示这是一个字符串参数，可以跟合法任意字符串

<details open="open">

```shell
--param "parameter"
```

```shell
-p "parameter"
```

</details>

## Path
表示这是一个文件目录，和[String](#String)用法差不多

<details open="open">

```shell
--input "/path/to/file.zip"
```

```shell
-i "/path/to/file.zip"
```

</details>

## Integer
需为有符号32位整数，范围为 -2147483648~2147483647

<details open="open">

```shell
--length 32
```

```shell
-l 32
```

</details>

## Base64
需为合法 Base64 字符串

<details open="open">

```shell
--keybase64 "MmMyNmI0NmI2OGZmYzY4ZmY5OWI0NTNjMWQzMDQxMzQxMzQyMmQ3MDY0ODNiZmEwZjk4YTVlODg2MjY2ZTdhZQ=="
```

</details>

## HEX
需为合法的十六进制字符串

<details open="open">

```shell
--keyhex "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae"
```

</details>

## 枚举
只能使用指定的值

<details open="open">

```shell
--outputtype base64
```

```shell
-t base64
```

</details>

### Algorithm
- aes
- des
- rsa
- ec

### OutputType
- base64
- hex
- bin

</details>

# 控制台消息
控制台消息可以在 `language.conf` 文件中自定义，变量使用`%s`代替而且只能使用`%s`代替，不能使用其他格式化例如`%d`

同时支持颜色代码

|        代码        | 颜色 | 前景色/背景色 |
|:----------------:|:--:|:-------:|
| &lt;c:reset&gt;  | 重置 |   所有    |
| &lt;f:black&gt;  | 黑色 |   前景色   |
|  &lt;f:red&gt;   | 红色 |   前景色   |
| &lt;f:green&gt;  | 绿色 |   前景色   |
| &lt;f:yellow&gt; | 黄色 |   前景色   |
|  &lt;f:blue&gt;  | 蓝色 |   前景色   |
| &lt;f:purple&gt; | 紫色 |   前景色   |
|  &lt;f:cyan&gt;  | 青色 |   前景色   |
| &lt;f:white&gt;  | 白色 |   前景色   |
| &lt;b:black&gt;  | 黑色 |   背景色   |
|  &lt;b:red&gt;   | 红色 |   背景色   |
| &lt;b:green&gt;  | 绿色 |   背景色   |
| &lt;b:yellow&gt; | 黄色 |   背景色   |
|  &lt;b:blue&gt;  | 蓝色 |   背景色   |
| &lt;b:purple&gt; | 紫色 |   背景色   |
|  &lt;b:cyan&gt;  | 青色 |   背景色   |
| &lt;b:white&gt;  | 白色 |   背景色   |

# 退出代码
|     代码     | 十进制 |      造成原因      |
|:----------:|:---:|:--------------:|
| 0x00000000 |  0  |      一切成功      |
| 0x80000001 | -1  |    语言文件读取失败    |
| 0x80000002 | -2  |      用法错误      |
| 0x80000003 | -3  |      参数错误      |
| 0x80000004 | -4  | 读取密钥时出现了 IO 异常 |
| 0x80000005 | -5  | 读取源时出现了 IO 异常  |
| 0x80000006 | -6  | 写入输出时出现了 IO 异常 |
| 0x80000006 | -7  | 写入公钥时出现了 IO 异常 |
| 0x80000007 | -8  |      密钥无效      |

# 第三方开源引用
[EasyConfiguration](https://github.com/CarmJos/EasyConfiguration) By [CarmJos](https://github.com/CarmJos) ([LGPL-3.0 license](https://github.com/CarmJos/EasyConfiguration/blob/master/LICENSE))

# 后记
~~果然我还是讨厌写前端，估计四分之三的时间都拿去写命令解析了~~
