import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.CameraX.getContext
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.dnd.smartroute.helpers.ResponseData
import com.dnd.smartroute.helpers.RetrofitHelper
import com.dnd.smartroute.interfaces.Api
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resumeWithException


@Composable
fun CameraView(activityContext: Context) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                val BASE = ""
//                imageCapture.takePicture()

//                val photoFile = File(
//                    outputDirectory,
//                    SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
//                )
//                val file = File("/path/to/your/file")
                MainScope().launch{
                    uploadImage(activityContext, imageCapture)
                }

                Log.i("CAMERA", "ON CLICK")
            },
            content = {
                Icon(
                    imageVector = Icons.Sharp.Lens,
                    contentDescription = "Take picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

suspend fun imageCaptureToByteArray(imageCapture: ImageCapture): ByteArray = suspendCancellableCoroutine { cont ->
    val executor = Executors.newSingleThreadExecutor()
    val callback = object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(imageProxy: ImageProxy) {
            val buffer = imageProxy.planes[0].buffer
            val byteArray = ByteArray(buffer.remaining())
            buffer.get(byteArray)
            imageProxy.close()
            cont.resume(byteArray)
        }

        override fun onError(exception: ImageCaptureException) {
            cont.resumeWithException(exception)
        }
    }
    imageCapture.takePicture(executor, callback)
}

suspend fun uploadImage(context: Context, imageCapture: ImageCapture) {
    withContext(Dispatchers.IO) {
        val byteArray = imageCaptureToByteArray(imageCapture)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//        val imageRequestBody = outputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val mediaType = MediaType.parse("image/jpeg")
        val imageRequestBody = RequestBody.create(mediaType, outputStream.toByteArray())
        val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", imageRequestBody)
        val api = RetrofitHelper.getInstance().create(Api::class.java)
        val call = api.uploadImage(imagePart)
        val response = call.execute()

        if (response.isSuccessful) {
            println("Image uploaded successfully")
            Log.i("succ", response.toString())
            val a = response.body()?.string()
            Log.i("succ", "$a")

            var gson = Gson()


            val dt = gson.fromJson(a, ResponseData::class.java)
            println(dt.path)
            withContext(Dispatchers.Main) {
                Toast.makeText(context,"${dt.path}", Toast.LENGTH_LONG).show()
            }
        } else {
            println("Failed to upload image")
        }
    }
}

