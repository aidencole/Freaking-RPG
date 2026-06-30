# Freaking RPG — one-shot dev environment setup for Windows
# This repo is private, so the Breach-style `irm ... | iex` one-liner does not work.
#
# Run in PowerShell (after cloning, or use the one-liner in README.md):
#   .\scripts\setup-dev.ps1

# Native commands like `java -version` write to stderr; don't treat that as fatal.
$ErrorActionPreference = "Continue"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$RepoUrl   = "https://github.com/aidencole/Freaking-RPG.git"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$RepoRoot  = Split-Path -Parent $ScriptDir
$ClonePath = Join-Path $env:USERPROFILE "Projects\Freaking-RPG"

# If this script was run from an existing checkout, use that folder.
if ((Test-Path (Join-Path $RepoRoot ".git")) -and (Test-Path (Join-Path $RepoRoot "build.gradle.kts"))) {
    $ClonePath = $RepoRoot
}
$GitName   = "aidencole"
$GitEmail  = "aidencole@users.noreply.github.com"

function Refresh-Path {
    $env:Path = @(
        "C:\Program Files\Git\bin",
        "C:\Program Files\GitHub CLI",
        $env:Path
    ) -join ";"
}

function Ensure-Command($name, $wingetId) {
    Refresh-Path
    if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
        Write-Host "Installing $name..." -ForegroundColor Yellow
        winget install --id $wingetId -e --accept-source-agreements --accept-package-agreements
        Refresh-Path
    }
}

function Get-Jdk25Path {
    @(
        "C:\Program Files\Microsoft\jdk-25*",
        "C:\Program Files\Eclipse Adoptium\jdk-25*"
    ) | ForEach-Object {
        Get-ChildItem $_ -ErrorAction SilentlyContinue
    } | Sort-Object Name -Descending | Select-Object -First 1
}

function Test-GitHubLogin {
    Refresh-Path
    if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
        Write-Host "GitHub CLI (gh) not installed. Cloning still works; install gh to push/pull easily." -ForegroundColor Yellow
        Write-Host "  winget install GitHub.cli" -ForegroundColor DarkGray
        return
    }

    $status = gh auth status 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0) {
        Write-Host "GitHub login: OK" -ForegroundColor Green
        ($status -split "`n" | Select-Object -First 2) | ForEach-Object { Write-Host "  $_" -ForegroundColor DarkGray }
    } else {
        Write-Host "GitHub login: NOT logged in on this PC" -ForegroundColor Yellow
        Write-Host "  Run this, then re-run setup if needed:" -ForegroundColor Yellow
        Write-Host "  gh auth login" -ForegroundColor Cyan
        Write-Host "  (Pick GitHub.com -> HTTPS -> Login with browser)" -ForegroundColor DarkGray
    }
}

Write-Host "`n=== Freaking RPG dev setup ===" -ForegroundColor Cyan

Ensure-Command git "Git.Git"

$jdk = Get-Jdk25Path
if (-not $jdk) {
    Write-Host "Installing JDK 25..." -ForegroundColor Yellow
    winget install --id Microsoft.OpenJDK.25 -e --accept-source-agreements --accept-package-agreements
    $jdk = Get-Jdk25Path
}
if (-not $jdk) {
    Write-Host "ERROR: JDK 25 not found. Restart PowerShell and run this script again." -ForegroundColor Red
    exit 1
}

$env:JAVA_HOME = $jdk.FullName
$env:Path = "$($jdk.FullName)\bin;" + $env:Path
Write-Host "Using JAVA_HOME=$($jdk.FullName)" -ForegroundColor DarkGray

if (-not (git config --global user.name 2>$null)) {
    git config --global user.name $GitName
    Write-Host "Set git user.name to $GitName" -ForegroundColor DarkGray
}
if (-not (git config --global user.email 2>$null)) {
    git config --global user.email $GitEmail
    Write-Host "Set git user.email to $GitEmail" -ForegroundColor DarkGray
}

Test-GitHubLogin

if (Test-Path $ClonePath) {
    if (-not (Test-Path (Join-Path $ClonePath ".git"))) {
        Write-Host "ERROR: $ClonePath exists but is not a git repo." -ForegroundColor Red
        Write-Host "Remove or rename that folder, then run this script again." -ForegroundColor Yellow
        exit 1
    }

    Write-Host "`nUpdating existing repo at $ClonePath" -ForegroundColor Green
    Set-Location $ClonePath
    git pull origin main
    if ($LASTEXITCODE -ne 0) {
        Write-Host "git pull failed - if this is an auth issue, run: gh auth login" -ForegroundColor Yellow
    }
} else {
    Write-Host "`nCloning Freaking RPG to $ClonePath" -ForegroundColor Green
    New-Item -ItemType Directory -Force -Path (Split-Path $ClonePath) | Out-Null
    git clone $RepoUrl $ClonePath
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: git clone failed." -ForegroundColor Red
        exit 1
    }
    Set-Location $ClonePath
}

Write-Host "`nDownloading Paper API + Gradle dependencies (first run takes a minute)..." -ForegroundColor Yellow
& .\gradlew.bat build --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Gradle failed. Check JAVA_HOME and internet connection." -ForegroundColor Red
    exit 1
}

$ideaInstalled = @(
    (Get-ChildItem "$env:ProgramFiles\JetBrains\IntelliJ IDEA*" -ErrorAction SilentlyContinue),
    (Get-ChildItem "$env:LOCALAPPDATA\Programs\IntelliJ IDEA*" -ErrorAction SilentlyContinue)
) | Where-Object { $_ } | Select-Object -First 1

if (-not $ideaInstalled) {
    Write-Host "`nInstalling IntelliJ IDEA Community..." -ForegroundColor Yellow
    winget install --id JetBrains.IntelliJIDEA.Community -e --accept-source-agreements --accept-package-agreements
}

Write-Host "`n=== Setup complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:"
Write-Host "  1. Open IntelliJ IDEA"
Write-Host "  2. File -> Open -> $ClonePath"
Write-Host "  3. Trust project, wait for Gradle sync"
Write-Host "  4. File -> Project Structure -> Project SDK -> JDK 25"
Write-Host "  5. Run runServer from the Gradle tool window (or top-right run config)"
Write-Host ""
Write-Host "Or without IntelliJ:"
Write-Host "  cd $ClonePath"
Write-Host "  .\gradlew.bat runServer"
Write-Host ""
Write-Host "GitHub push/pull on this PC:"
Write-Host "  gh auth login"
Write-Host ""
