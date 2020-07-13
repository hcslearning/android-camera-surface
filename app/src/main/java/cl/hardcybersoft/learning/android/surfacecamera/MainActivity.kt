package cl.hardcybersoft.learning.android.surfacecamera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.IOException

class MainActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {

    val REQUEST_CAMERA: Int = 50
    var textureView: TextureView? = null
    var camera: Camera? = null
    var surfaceTexture: SurfaceTexture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView = findViewById<TextureView>(R.id.textureView)
        textureView?.surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        this.surfaceTexture = surfaceTexture
        showCamera()
    }

    public fun showCamera() {
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ){
            showCameraPreview()
        } else {
            // si es igual o mayor a Marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if( shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ) {
                    Toast.makeText(this, "La cámara es necesaria para escanear los códigos de barra.", Toast.LENGTH_LONG).show()
                }

                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            }
        }
    }

    public fun showCameraPreview() {
        camera = Camera.open()
        try {
            camera?.setPreviewTexture(surfaceTexture)
            camera?.startPreview()
        } catch (ioe: IOException) {
            // algo malo paso
        }
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
        // Ignorado, Camera hace todo por nosotros
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
        camera?.stopPreview()
        camera?.release()
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
        // invocado cada vez que hay un nuevo frame de previsualización
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if( requestCode == REQUEST_CAMERA ) {
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                showCameraPreview();
            } else {
                Toast.makeText(this, "El permiso no fue concedido, no se podrán escanear los códigos de barra.", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}