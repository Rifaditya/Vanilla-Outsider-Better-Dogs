Add-Type -AssemblyName System.Drawing
try {
    $img1 = [System.Drawing.Image]::FromFile('Doc\Media\Gallery\wolve_is_ready_for_adoption_image.webp')
    $img1.Save('Doc\Media\Gallery\wolve_is_ready_for_adoption_image.png', [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Converted image 1"
} catch {
    Write-Host "Error converting image 1: $($_.Exception.Message)"
}

try {
    $img2 = [System.Drawing.Image]::FromFile('Doc\Media\Gallery\adoption_called_off_image.webp')
    $img2.Save('Doc\Media\Gallery\adoption_called_off_image.png', [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Converted image 2"
} catch {
    Write-Host "Error converting image 2: $($_.Exception.Message)"
}
