package org.seniorsigan.mangareader.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.TAG

class FilterDialogFragment : DialogFragment() {
    private lateinit var listener: FilterDialogListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as FilterDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement NoticeDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val sources = App.mangaSourceRepository.sources()
        return with(AlertDialog.Builder(activity), {
            setTitle(R.string.pick_manga_source)
            setSingleChoiceItems(sources.toTypedArray(), App.mangaSourceRepository.current, { dialog, which ->
                App.mangaSourceRepository.current = which
                listener.onSelected(this@FilterDialogFragment)
                dismiss()
            })
        }).create()
    }

    interface FilterDialogListener {
        fun onSelected(dialogFragment: DialogFragment)
    }
}
