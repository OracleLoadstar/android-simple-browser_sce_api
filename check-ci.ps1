# 查询最新CI运行状态，每60秒轮询一次
$repo = "xingguangcuican6666/android-simple-browser"
while ($true) {
    $result = gh run list --repo $repo --limit 1
    Write-Host "最新CI状态："
    Write-Host $result
    Start-Sleep -Seconds 60
}