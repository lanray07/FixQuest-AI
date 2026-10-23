param([string]$WorkFolder)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Speech
$work = (Resolve-Path -LiteralPath $WorkFolder).Path
$chapters = Get-Content -Raw -LiteralPath (Join-Path $work 'chapters.json') | ConvertFrom-Json
$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer
try {
    $synth.SelectVoice('Microsoft Hazel Desktop')
    $synth.Rate = 0
    for ($index = 0; $index -lt $chapters.chapters.Count; $index++) {
        $synth.SetOutputToWaveFile((Join-Path $work ('voice-{0:D2}.wav' -f $index)))
        $synth.Speak($chapters.chapters[$index].text)
        $synth.SetOutputToNull()
    }
} finally { $synth.Dispose() }
