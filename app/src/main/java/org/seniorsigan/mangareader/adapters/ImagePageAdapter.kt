package org.seniorsigan.mangareader.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import org.seniorsigan.mangareader.R
import uk.co.senab.photoview.PhotoViewAttacher

class ImagePageAdapter(
        val urls: List<String> = emptyList(),
        val context: Context
): PagerAdapter() {
    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == (obj as ImageViewItem).view
    }

    override fun getCount(): Int = urls.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(context).inflate(R.layout.image_view, container, false)
        container.addView(itemView)
        val item = ImageViewItem(itemView, context, urls[position])
        return item
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val item = obj as ImageViewItem
        item.dispose()
        container.removeView(item.view)
    }
}

class ImageViewItem(
        val view: View,
        private val context: Context,
        private val url: String
) {
    private var photoViewAttacher: PhotoViewAttacher? = null

    init {
        val imageView = view.find<ImageView>(R.id.image_view)
        updatePhotoView(url, imageView)
    }

    fun dispose() {
        photoViewAttacher?.cleanup()
        photoViewAttacher = null
    }

    private fun updatePhotoView(url: String, imageView: ImageView) {
        Picasso.with(context)
                .load(url)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        val progressBar = view.find<ProgressBar>(R.id.progressbar)
                        progressBar.visibility = View.GONE
                        updateAttach(imageView)
                    }

                    override fun onError() {
                        Log.e("ImageViewItem", "Can't load image by url $url")
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
}