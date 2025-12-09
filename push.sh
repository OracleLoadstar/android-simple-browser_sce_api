#!/bin/bash

# 获取版本号参数
if [ -z "$1" ]; then
    echo "请提供版本号，例如: ./push.sh v1.0.0"
    exit 1
fi

VERSION=$1

# 检查是否在git仓库中
if [ ! -d ".git" ]; then
    echo "错误: 当前目录不是git仓库"
    exit 1
fi

# 添加所有更改
git add .

# 提交更改
git commit -m "Release $VERSION"

# 创建标签
git tag $VERSION

# 推送到远程仓库
git push origin main
git push origin $VERSION

echo "已成功创建并推送标签 $VERSION"%