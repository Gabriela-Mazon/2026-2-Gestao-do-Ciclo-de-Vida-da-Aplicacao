param(
    [Parameter(Mandatory = $true)]
    [ValidateRange(0, 7)]
    [int]$Etapa,
    [Parameter(Mandatory = $true)]
    [string]$JdkDirectory
)

$ErrorActionPreference = 'Stop'
$projectDirectory = Split-Path -Parent $PSScriptRoot
$javacExecutable = Join-Path $JdkDirectory 'bin\javac.exe'
$javaExecutable = Join-Path $JdkDirectory 'bin\java.exe'
$stepName = $Etapa.ToString('00')
$runDirectory = Join-Path $projectDirectory ('build\etapa-' + $stepName + '-' + [guid]::NewGuid().ToString('N'))
$classesDirectory = Join-Path $runDirectory 'classes'
$evidenceDirectory = Join-Path $projectDirectory 'evidencias'
if ($Etapa -eq 0 -and (Test-Path -LiteralPath (Join-Path $evidenceDirectory 'etapa-00-saida.txt'))) {
    throw 'A referencia original ja esta registrada. Preserve as evidencias da etapa 0.'
}
New-Item -ItemType Directory -Force -Path $classesDirectory, $evidenceDirectory | Out-Null
$sourceFiles = @(Get-ChildItem -LiteralPath (Join-Path $projectDirectory 'src\projetoprincipiosdesign') -Filter '*.java' | Sort-Object Name | Select-Object -ExpandProperty FullName)

& $javacExecutable --release 17 -encoding UTF-8 -Xlint:all -Werror -d $classesDirectory @sourceFiles
if ($LASTEXITCODE -ne 0) { throw "Falha de compilacao na etapa $Etapa." }

Push-Location $runDirectory
try {
    $programOutput = @(& $javaExecutable '-Dfile.encoding=UTF-8' '-Duser.language=pt' '-Duser.country=BR' -cp $classesDirectory projetoprincipiosdesign.Main 2>&1)
    if ($LASTEXITCODE -ne 0) { throw "Falha de execucao na etapa $Etapa." }
    $ordersText = [System.IO.File]::ReadAllText((Join-Path $runDirectory 'pedidos.txt'))
} finally {
    Pop-Location
}

$outputText = ($programOutput -join [Environment]::NewLine) + [Environment]::NewLine
$utf8Encoding = [System.Text.UTF8Encoding]::new($false)
if ($Etapa -gt 0) {
    $originalOutput = [System.IO.File]::ReadAllText((Join-Path $evidenceDirectory 'etapa-00-saida.txt'))
    $originalOrders = [System.IO.File]::ReadAllText((Join-Path $evidenceDirectory 'etapa-00-pedidos.txt'))
    if ($outputText -cne $originalOutput) { throw "A saida do Main divergiu do original na etapa $Etapa." }
    if ($ordersText -cne $originalOrders) { throw "A persistencia do Main divergiu do original na etapa $Etapa." }
}
[System.IO.File]::WriteAllText((Join-Path $evidenceDirectory "etapa-$stepName-saida.txt"), $outputText, $utf8Encoding)
[System.IO.File]::WriteAllText((Join-Path $evidenceDirectory "etapa-$stepName-pedidos.txt"), $ordersText, $utf8Encoding)
$sourceHashes = @($sourceFiles | ForEach-Object { [PSCustomObject]@{Arquivo = Split-Path -Leaf $_; SHA256 = (Get-FileHash -LiteralPath $_ -Algorithm SHA256).Hash} })
$verification = [PSCustomObject]@{
    Etapa = $Etapa
    Data = (Get-Date).ToString('o')
    Java = ((& $javaExecutable -version 2>&1) -join [Environment]::NewLine)
    Compilacao = 'javac --release 17 -encoding UTF-8 -Xlint:all -Werror'
    Resultado = 'APROVADO'
    ComparacaoComOriginal = if ($Etapa -eq 0) { 'Referencia original registrada' } else { 'Saida e pedidos.txt identicos ao original' }
    Fontes = $sourceHashes
}
[System.IO.File]::WriteAllText((Join-Path $evidenceDirectory "etapa-$stepName-verificacao.json"), ($verification | ConvertTo-Json -Depth 4), $utf8Encoding)
Write-Output "ETAPA $Etapa APROVADA: compilacao e Main; pedido salvo: $($ordersText.Trim())."
