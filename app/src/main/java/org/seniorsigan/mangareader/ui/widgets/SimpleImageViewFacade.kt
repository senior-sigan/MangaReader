package org.seniorsigan.mangareader.ui.widgets

import android.content.Context
import android.graphics.PointF
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Okio
import org.jetbrains.anko.onUiThread
import org.seniorsigan.mangareader.App
import java.io.File
import java.io.IOException

abstract class ImageViewFacade(
        protected val context: Context,
        protected val imageView: View?
) {

    open fun onDestroy() {
        imageView?.destroyDrawingCache()
    }

    abstract fun load(url: String?, callback: Callback = EmptyCallback())

    interface Callback {
        fun onSuccess()
        fun onError(e: Throwable?)
    }

    class EmptyCallback: Callback {
        override fun onSuccess() {}
        override fun onError(e: Throwable?) {}
    }
}

class SimpleImageViewFacade(context: Context, imageView: View?) : ImageViewFacade(context, imageView) {
    val view = imageView as ImageView

    override fun load(url: String?, callback: Callback) {
        Picasso.with(context).load(url).into(view, object: com.squareup.picasso.Callback {
            override fun onSuccess() {
                callback.onSuccess()
            }
            override fun onError() {
                callback.onError(null)
            }
        })
    }
}

class ZoomableImageViewFacade(context: Context, imageView: View?) : ImageViewFacade(context, imageView) {
    private val MANGA_CACHE = "manga-cache"
    private val view = imageView as SubsamplingScaleImageView

    override fun load(url: String?, callback: Callback) {
        if (url == null) {
            callback.onError(null)
            return
        }
        val client = OkHttpClient()
        val req = Request.Builder().url(url).build()
        client.newCall(req).enqueue(object: okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.onError(e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response == null) return
                val body = response.body()
                val file = File(createDefaultCacheDir(), generateName(url, body.contentType().subtype()))
                Log.d("Zoom", "Will save file into ${file.absolutePath}")
                val source = body.source()
                val sink = Okio.sink(file)
                source.readAll(sink)
                source.close()
                sink.close()
                context.onUiThread {
                    try {
                        view.setImage(ImageSource.uri(Uri.fromFile(file)), ImageViewState(0f, PointF(0f, 0f), 0))
                        callback.onSuccess()
                    } catch (e: Exception) {
                        Log.w("Zoom", "UPS $e", e)
                    }
                }
            }
        })
    }

    fun createDefaultCacheDir(): File {
        val cache = File(context.applicationContext.cacheDir, MANGA_CACHE)
        if (!cache.exists()) {
            cache.mkdirs()
        }
        return cache
    }

    private fun generateName(url: String, subtype: String?): String {
        val name = App.digest.digest(url)
        return "$name.$subtype"
    }
}