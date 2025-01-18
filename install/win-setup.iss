[Setup]
AppName=Ani Together
AppVersion=0.4.2
DefaultDirName={pf}\AniTogether
DefaultGroupName=AniTogether
OutputDir=dist
OutputBaseFilename=ani-together-installer
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin

[Files]
Source: "..\app\build\install\ani-together\bin\*"; DestDir: "{app}\bin"; Flags: ignoreversion

Source: "..\app\build\install\ani-together\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion

Source: "win-path.ps1"; DestDir: "{app}\scripts"; Flags: ignoreversion

[Icons]
Name: "{commondesktop}\Ani Together"; Filename: "{app}\bin\ani-together.bat"

[Run]
Filename: "powershell.exe"; Parameters: "-ExecutionPolicy Bypass -File ""{app}\scripts\win-path.ps1"""; Flags: runhidden nowait postinstall
