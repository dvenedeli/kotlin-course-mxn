# Downloads JAR dependencies listed in deps.list into lib/
param(
    [string]$ProjectRoot = $PSScriptRoot
)

$ErrorActionPreference = 'Stop'
$lib = Join-Path $ProjectRoot 'lib'
$depsFile = Join-Path $ProjectRoot 'deps.list'
$mavenBase = 'https://repo1.maven.org/maven2'
$kotlinVersion = '1.9.24'
$kotlinZipUrl = "https://github.com/JetBrains/kotlin/releases/download/v$kotlinVersion/kotlin-compiler-$kotlinVersion.zip"

New-Item -ItemType Directory -Force -Path $lib | Out-Null

if (-not (Test-Path $depsFile)) {
    Write-Error "deps.list not found in $ProjectRoot"
}

function Get-ZipEntryBytes {
    param([string]$ZipPath, [string]$EntryName)
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zip = [System.IO.Compression.ZipFile]::OpenRead($ZipPath)
    try {
        $entry = $zip.Entries | Where-Object { $_.FullName -replace '\\', '/' -eq $EntryName } | Select-Object -First 1
        if (-not $entry) { return $null }
        $stream = $entry.Open()
        try {
            $ms = New-Object System.IO.MemoryStream
            $stream.CopyTo($ms)
            return $ms.ToArray()
        } finally {
            $stream.Dispose()
        }
    } finally {
        $zip.Dispose()
    }
}

function Ensure-KotlinCompilerZip {
    $tools = Join-Path $ProjectRoot 'tools'
    $zipPath = Join-Path $tools "kotlin-compiler-$kotlinVersion.zip"
    New-Item -ItemType Directory -Force -Path $tools | Out-Null
    if (-not (Test-Path $zipPath)) {
        Write-Host "Downloading Kotlin compiler $kotlinVersion..."
        Invoke-WebRequest -Uri $kotlinZipUrl -OutFile $zipPath -UseBasicParsing
    }
    return $zipPath
}

$lines = Get-Content $depsFile -Encoding UTF8
foreach ($line in $lines) {
    $line = $line.Trim()
    if (-not $line -or $line.StartsWith('#')) { continue }

    if ($line.StartsWith('KOTLIN_DIST:')) {
        $parts = $line.Substring('KOTLIN_DIST:'.Length).Split(':')
        $entryInZip = $parts[0].TrimStart('/')
        $destName = $parts[1]
        $dest = Join-Path $lib $destName
        if (Test-Path $dest) {
            Write-Host "OK $destName"
            continue
        }
        $zipPath = Ensure-KotlinCompilerZip
        Write-Host "Extracting $destName from Kotlin compiler..."
        $bytes = Get-ZipEntryBytes -ZipPath $zipPath -EntryName $entryInZip
        if (-not $bytes) {
            Write-Error "Entry not found in zip: $entryInZip"
        }
        [IO.File]::WriteAllBytes($dest, $bytes)
        continue
    }

    $name = Split-Path $line -Leaf
    $dest = Join-Path $lib $name
    if (Test-Path $dest) {
        Write-Host "OK $name"
        continue
    }
    $url = "$mavenBase/$line"
    Write-Host "Downloading $name..."
    Invoke-WebRequest -Uri $url -OutFile $dest -UseBasicParsing
}

$kotlinHome = Join-Path (Join-Path $ProjectRoot 'tools') "kotlin-$kotlinVersion"
if (-not (Test-Path (Join-Path $kotlinHome 'bin\kotlinc.bat'))) {
    Write-Host "Extracting Kotlin compiler to tools/kotlin-$kotlinVersion ..."
    $zipPath = Ensure-KotlinCompilerZip
    $tools = Join-Path $ProjectRoot 'tools'
    Expand-Archive -Path $zipPath -DestinationPath $tools -Force
    $extracted = Join-Path $tools 'kotlinc'
    if (Test-Path $extracted) {
        if (Test-Path $kotlinHome) { Remove-Item -Recurse -Force $kotlinHome }
        Rename-Item $extracted "kotlin-$kotlinVersion"
    }
}

Write-Host "Dependencies ready in lib/"
