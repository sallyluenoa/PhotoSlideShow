package org.fog_rock.photo_slideshow.app.module

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class AppDialogFragment : DialogFragment() {

    private val TAG = AppDialogFragment::class.java.simpleName

    interface Callback {

        fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent)
    }

    companion object {

        val BUTTON_POSITIVE = DialogInterface.BUTTON_POSITIVE
        val BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE
        val BUTTON_CANCEL = 0

        private const val ARGS_REQUEST_CODE = "request_code"
        private const val ARGS_TITLE = "title"
        private const val ARGS_MESSAGE = "message"
        private const val ARGS_POSITIVE_LABEL = "positive_label"
        private const val ARGS_NEGATIVE_LABEL = "negative_label"
    }

    class Builder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var positiveLabel: String? = null
        private var negativeLabel: String? = null
        private var cancelable = true

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }
        fun setTitle(title: Int): Builder =
            if (title != 0) setTitle(context.getString(title))
            else this

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }
        fun setMessage(message: Int): Builder =
            if (message != 0) setMessage(context.getString(message))
            else this

        fun setPositiveLabel(positiveLabel: String): Builder {
            this.positiveLabel = positiveLabel
            return this
        }
        fun setPositiveLabel(positiveLabel: Int): Builder =
            if (positiveLabel != 0) setPositiveLabel(context.getString(positiveLabel))
            else this

        fun setNegativeLabel(negativeLabel: String): Builder {
            this.negativeLabel = negativeLabel
            return this
        }
        fun setNegativeLabel(negativeLabel: Int): Builder =
            if (negativeLabel != 0) setNegativeLabel(context.getString(negativeLabel))
            else this

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        /**
         * AppDialogFragmentを生成.
         * @param fragmentManager Activity#getSupportFragmentManager() or Fragment#getChildFragmentManager()
         * @param requestCode リクエストコード
         */
        fun show(fragmentManager: FragmentManager, requestCode: Int) {
            val args = Bundle().apply {
                putInt(ARGS_REQUEST_CODE, requestCode)
                putString(ARGS_TITLE, title)
                putString(ARGS_MESSAGE, message)
                putString(ARGS_POSITIVE_LABEL, positiveLabel)
                putString(ARGS_NEGATIVE_LABEL, negativeLabel)
            }
            val fragment = AppDialogFragment().apply {
                arguments = args
                isCancelable = cancelable
            }
            fragment.show(fragmentManager, null)
        }
    }

    private val args: Bundle by lazy {
        arguments ?: run {
            Log.e(TAG, "Not found arguments.")
            Bundle()
        }
    }
    private val requestCode: Int by lazy { args.getInt(ARGS_REQUEST_CODE) }
    private val title: String? by lazy { args.getString(ARGS_TITLE) }
    private val message: String? by lazy { args.getString(ARGS_MESSAGE) }
    private val positiveLabel: String? by lazy { args.getString(ARGS_POSITIVE_LABEL) }
    private val negativeLabel: String? by lazy { args.getString(ARGS_NEGATIVE_LABEL) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(title)
            setMessage(message)
        }
        if (!positiveLabel.isNullOrEmpty()) {
            builder.setPositiveButton(positiveLabel) { dialog, which ->
                dismiss()
                callDialogResult(which)
            }
        }
        if (!negativeLabel.isNullOrEmpty()) {
            builder.setNegativeButton(negativeLabel) { dialog, which ->
                dismiss()
                callDialogResult(which)
            }
        }
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        callDialogResult(BUTTON_CANCEL)
    }

    private fun callDialogResult(which: Int) {
        val intent = Intent().apply { putExtras(args) }
        val fragment = parentFragment
        if (fragment != null && fragment is Callback) {
            fragment.onDialogResult(requestCode, which, intent)
            return
        }
        val activity = requireActivity()
        if (activity is Callback) {
            activity.onDialogResult(requestCode, which, intent)
            return
        }
        Log.w(TAG, "Not implemented callback.")
    }
}