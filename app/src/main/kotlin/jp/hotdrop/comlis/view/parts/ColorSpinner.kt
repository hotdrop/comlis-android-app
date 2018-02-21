package jp.hotdrop.comlis.view.parts

import android.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import jp.hotdrop.comlis.util.ColorUtil

class ColorSpinner(
        private val spinner: Spinner,
        private val context: Context?
) {

    private val adapter by lazy {
        Adapter(context, R.layout.simple_dropdown_item_1line, ColorUtil.getNames()).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
    }

    init {
        spinner.adapter = adapter
    }

    fun getSelection(): String = spinner.selectedItem as String

    fun setSelection(name: String) {
        val position = adapter.getPosition(name)
        spinner.setSelection(position)
    }

    private inner class Adapter(context: Context?, textViewResourceId: Int, var colorNames: List<String>)
        : ArrayAdapter<String>(context, textViewResourceId, colorNames) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = convertView ?: View.inflate(context, android.R.layout.simple_dropdown_item_1line, null)
            return getTextViewWithSetColor(v as TextView, position)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = convertView ?: View.inflate(context, android.R.layout.simple_dropdown_item_1line, null)
            return getTextViewWithSetColor(v as TextView, position)
        }

        private fun getTextViewWithSetColor(v: TextView, position: Int): View =
            (v.findViewById<TextView>(android.R.id.text1)).apply {
                setTextColor(ColorUtil.getResDark(colorNames[position], context))
                text = colorNames[position]
                textSize = 20.toFloat()
        }
    }
}