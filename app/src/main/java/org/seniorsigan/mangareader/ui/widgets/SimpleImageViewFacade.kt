package org.seniorsigan.mangareader.ui.widgets

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.async
import org.jetbrains.anko.onUiThread
import org.seniorsigan.mangareader.App

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
    private val view = imageView as SubsamplingScaleImageView

    override fun load(url: String?, callback: Callback) {
        if (url == null) {
            callback.onError(null)
            return
        }
        context.async() {
            val uri = App.transport.load(url)
            if (uri == null) {
                callback.onError(null)
            } else {
                context.onUiThread {
                    try {
                        view.setImage(ImageSource.uri(uri), ImageViewState(0f, PointF(0f, 0f), 0))
                        view.setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener {
                            override fun onImageLoaded() {
                            }

                            override fun onTileLoadError(p0: Exception?) {
                            }

                            override fun onImageLoadError(p0: Exception?) {
                            }

                            override fun onPreviewLoadError(p0: Exception?) {
                            }

                            override fun onReady() {
                                view.setScaleAndCenter(view.scaleToWidth(), PointF(0f, 0f))
                            }
                        })
                        callback.onSuccess()
                    } catch (e: Exception) {
                        Log.w("Zoom", "UPS $e", e)
                    }
                }
            }
        }
    }
}

fun SubsamplingScaleImageView.scaleToWidth(): Float {
    val hPadding = paddingLeft + paddingRight
    val vPadding = paddingTop + paddingBottom
    return if (sWidth > sHeight) {
        (height - vPadding).toFloat() / sHeight.toFloat()
    } else {
        (width - hPadding).toFloat() / sWidth.toFloat()
    }
}