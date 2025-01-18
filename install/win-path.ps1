$programFilesX86 = "C:\Program Files (x86)"
$appPath = "$programFilesX86\AniTogether\bin"

# Get the current PATH environment variable for the user
$currentPath = [System.Environment]::GetEnvironmentVariable("PATH", [System.EnvironmentVariableTarget]::User)

# Check if the appPath already exists in the PATH
if ($currentPath -notmatch [regex]::Escape($appPath)) {
    # Add the path if it doesn't exist
    [System.Environment]::SetEnvironmentVariable("PATH", "$currentPath;$appPath", [System.EnvironmentVariableTarget]::User)
    Write-Host "Path added to environment variable."
} else {
    Write-Host "Path already exists in the environment variable."
}
