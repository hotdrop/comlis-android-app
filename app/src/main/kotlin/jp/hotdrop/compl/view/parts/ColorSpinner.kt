package jp.hotdrop.compl.view.parts

import android.R
import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import jp.hotdrop.compl.util.ColorDataUtil

class ColorSpinner(private val spinner: Spinner, private val activity: Activity) {

    private val adapter by lazy {
        Adapter(activity, R.layout.simple_dropdown_item_1line, ColorDataUtil.getNames())
    }

    init {
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun getSelection(): String {
        return spinner.selectedItem as String
    }

    fun setSelection(name: String) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }

    private inner class Adapter(context: Context?, textViewResourceId: Int, var colorNames: List<String>)
        : ArrayAdapter<String>(context, textViewResourceId, colorNames) {

        private val ITEM_TEXT_SIZE = 20.toFloat()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = convertView ?: View.inflate(context, android.R.layout.simple_dropdown_item_1line, null)
            val textView = ((v as TextView).findViewById(android.R.id.text1) as TextView).apply {
                setTextColor(ContextCompat.getColor(context, ColorDataUtil.getColorNormal(colorNames[position])))
                text = colorNames[position]
                textSize = ITEM_TEXT_SIZE
            }
            return textView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = convertView ?: View.inflate(context, android.R.layout.simple_dropdown_item_1line, null)
            val textView = ((v as TextView).findViewById(android.R.id.text1) as TextView).apply {
                setTextColor(ContextCompat.getColor(context, ColorDataUtil.getColorNormal(colorNames[position])))
                text = colorNames[position]
                textSize = ITEM_TEXT_SIZE
            }
            return textView
        }
    }
}