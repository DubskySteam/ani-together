$programFilesX86 = "C:\Program Files (x86)"

$appPath = "$programFilesX86\AniTogether\bin"

if ($env:PATH -notcontains $appPath) {
    [System.Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";$appPath", [System.EnvironmentVariableTarget]::User)
}
