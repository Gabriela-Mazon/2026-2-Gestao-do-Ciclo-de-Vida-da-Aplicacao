param(
    [ValidateSet('executar', 'testar', 'compilar')]
    [string]$Modo = 'executar',
    [string]$JdkDirectory
)

$ErrorActionPreference = 'Stop'
if (-not $JdkDirectory -and $env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\javac.exe'))) {
    $JdkDirectory = $env:JAVA_HOME
}
if (-not $JdkDirectory) {
    $compilerCommand = Get-Command javac -ErrorAction SilentlyContinue
    if ($compilerCommand) { $JdkDirectory = Split-Path -Parent (Split-Path -Parent $compilerCommand.Source) }
}
if (-not $JdkDirectory) {
    $jdkCache = Join-Path ([Environment]::GetFolderPath('UserProfile')) '.cache\codex-tools\java'
    if (Test-Path -LiteralPath $jdkCache) {
        $cachedCompiler = Get-ChildItem -LiteralPath $jdkCache -Filter 'javac.exe' -File -Recurse | Sort-Object FullName | Select-Object -First 1
        if ($cachedCompiler) { $JdkDirectory = Split-Path -Parent $cachedCompiler.DirectoryName }
    }
}
if (-not $JdkDirectory -or -not (Test-Path -LiteralPath (Join-Path $JdkDirectory 'bin\javac.exe'))) {
    throw 'Informe um JDK 17+ com -JdkDirectory, configure JAVA_HOME ou disponibilize javac no PATH.'
}

$javaExecutable = Join-Path $JdkDirectory 'bin\java.exe'
$javacExecutable = Join-Path $JdkDirectory 'bin\javac.exe'
$classesDirectory = Join-Path $PSScriptRoot ('build\' + $Modo + '-' + [guid]::NewGuid().ToString('N') + '\classes')
New-Item -ItemType Directory -Force -Path $classesDirectory | Out-Null
$sourceFiles = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'src\projetoprincipiosdesign') -Filter '*.java' | Select-Object -ExpandProperty FullName)
if ($Modo -eq 'testar') {
    $sourceFiles += @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'tests\projetoprincipiosdesign') -Filter '*.java' | Select-Object -ExpandProperty FullName)
}
& $javacExecutable --release 17 -encoding UTF-8 -Xlint:all -Werror -d $classesDirectory @sourceFiles
if ($LASTEXITCODE -ne 0) { throw 'Falha de compilacao.' }

if ($Modo -eq 'compilar') {
    Write-Output "Compilado em: $classesDirectory"
    return
}

$mainClass = if ($Modo -eq 'testar') { 'projetoprincipiosdesign.RefatoracaoTest' } else { 'projetoprincipiosdesign.Main' }
Push-Location $PSScriptRoot
try {
    $programOutput = @(& $javaExecutable '-Dfile.encoding=UTF-8' '-Duser.language=pt' '-Duser.country=BR' -cp $classesDirectory $mainClass 2>&1)
    $programExit = $LASTEXITCODE
    if ($Modo -eq 'testar') {
        $evidenceDirectory = Join-Path $PSScriptRoot 'evidencias'
        New-Item -ItemType Directory -Force -Path $evidenceDirectory | Out-Null
        [System.IO.File]::WriteAllText((Join-Path $evidenceDirectory 'testes.txt'), (($programOutput -join [Environment]::NewLine) + [Environment]::NewLine), [System.Text.UTF8Encoding]::new($false))
    }
    $programOutput | ForEach-Object { Write-Output $_ }
    if ($programExit -ne 0) { throw "Execucao falhou com codigo $programExit." }
} finally {
    Pop-Location
}
