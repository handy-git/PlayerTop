# PlayerTop

### 介绍

一款有点好用的玩家排行榜插件

### 使用配置指南

1. 在 config.yml的enable节点中 开关你想要的内部排行数据(必须)
2. 在 papi.yml中按照example示例写你需要获取的变量数据(可选)
3. 在format.yml中按照格式添加你在papi.yml中format节点添加写的变量

### 开始使用指南

1. 配置好后执行 /pt createHd [类型] 用来生成内部全息图,生成的配置在hologram.yml
2. 在papi.yml里配置的只需要执/pt reload后会获取数据并生成

### 变量使用指南

| 变量名                        | 显示内容               |
|----------------------------|--------------------|
| %playerTop_[类型]_[排行]%      | 对应类型排行的值           |
| %playerTop_[类型]_[排行]_name% | 对应类型排行的玩家名(1.1.6+) |
| %playerTop_[类型]_rank%      | 当前玩家的排行(1.1.6+)    |

例如:  %playerTop_vault_1% 就是金币排行1的数据
例如:  %playerTop_vault_1_name% 就是金币排行1的玩家名

如果你在papi.yml中配置了变量  
例如你配置了 %playerTitle_number% 那么变量就是 %playerTop_playerTitle_number_1%

[MCBBS](https://www.mcbbs.net/thread-1351130-1-1.html)

![](https://bstats.org/signatures/bukkit/PlayerTop.svg)

[WIKI](https://ricedoc.handyplus.cn/wiki/PlayerTop/)

### 爱发电购买

[爱发电](https://afdian.net/item?plan_id=3ccf4a54e3f611ec984c52540025c377)

### 如果你没有钱购买支持作者

请点个star支持下也可

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=handy-git/PlayerTop&type=Date)](https://star-history.com/#handy-git/PlayerTop&Date)
