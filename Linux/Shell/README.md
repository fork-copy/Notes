
# 找到当前运行脚本的目录，并设为环境变量

```
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
```