package pl.polsl.student.personalnavigation.view

import android.content.Context
import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter

class TranslatedEnumSpinnerAdapter<E: Enum<E>>(
        val context: Context,
        ids: Map<Int, E>,
        val fromString: (String) -> E
): SpinnerAdapter {

    private val translations = ids.mapKeys { context.resources.getString(it.key) }

    private val adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item,
            translations.keys.toList()
    )

    override fun isEmpty() = adapter.isEmpty

    override fun getView(position: Int, view: View?, group: ViewGroup?) =
            adapter.getView(position, view, group)

    override fun registerDataSetObserver(observer: DataSetObserver?) =
            adapter.registerDataSetObserver(observer)

    override fun getItemViewType(position: Int) = adapter.getItemViewType(position)

    override fun getItem(position: Int): E? {
        val translated: String? = adapter.getItem(position)
        return translations[translated]
    }

    override fun getViewTypeCount() = adapter.viewTypeCount

    override fun getItemId(position: Int) = adapter.getItemId(position)

    override fun hasStableIds() = adapter.hasStableIds()

    override fun getDropDownView(position: Int, view: View?, viewGroup: ViewGroup?) =
            adapter.getDropDownView(position, view, viewGroup)

    override fun unregisterDataSetObserver(observer: DataSetObserver?) =
            adapter.unregisterDataSetObserver(observer)

    override fun getCount() = adapter.count
}