package org.seniorsigan.mangareader.ui.widgets

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import uk.co.senab.photoview.PhotoViewAttacher

abstract class ImageViewFacade(
        protected val context: Context
) {
    protected lateinit var imageView: ImageView

    fun innerView() = imageView

    fun attach(view: View, id: Int): ImageViewFacade {
        imageView = view.findViewById(id) as ImageView
        return this
    }

    fun attach(view: Activity, id: Int): ImageViewFacade {
        imageView = view.findViewById(id) as ImageView
        return this
    }

    fun attach(fragment: Fragment, id: Int): ImageViewFacade {
        imageView = fragment.view.findViewById(id) as ImageView
        return this
    }

    open fun onDestroy() {
        imageView.destroyDrawingCache()
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

class SimpleImageViewFacade(context: Context) : ImageViewFacade(context) {
    override fun load(url: String?, callback: Callback) {
        Log.d("SimpleImageViewFacade", "load $url")
        Picasso.with(context).load(url).into(imageView, object: com.squareup.picasso.Callback {
            override fun onSuccess() {
                callback.onSuccess()
            }
            override fun onError() {
                callback.onError(null)
            }
        })
    }
}

class ZoomableImageViewFacade(context: Context): ImageViewFacade(context) {
    private var photoViewAttacher: PhotoViewAttacher? = null

    override fun load(url: String?, callback: Callback) {
        Log.d("ZoomableImageViewFacade", "load $url")
        Picasso.with(context).load(url).into(imageView, object: com.squareup.picasso.Callback {
            override fun onSuccess() {
                updateAttach(imageView)
                callback.onSuccess()
            }
            override fun onError() {
                callback.onError(null)
            }
        })
    }

    private fun updateAttach(imageView: ImageView) {
        if (photoViewAttacher == null) {
            photoViewAttacher = PhotoViewAttacher(imageView)
        } else {
            photoViewAttacher?.update()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        photoViewAttacher?.cleanup()
        photoViewAttacher = null
    }
}