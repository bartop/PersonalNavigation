package pl.polsl.student.personalnavigation.view

import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.architecture.ext.getViewModel
import pl.polsl.student.personalnavigation.R
import pl.polsl.student.personalnavigation.util.observeNotNull
import pl.polsl.student.personalnavigation.viewmodel.MarkersViewModel
import pl.polsl.student.personalnavigation.viewmodel.NameViewModel
import pl.polsl.student.personalnavigation.viewmodel.RoadViewModel

class CustomActionBar(private val activity: AppCompatActivity) {

    private val nameLayout by lazy { activity.find<View>(R.id.nameLayout) }
    private val directionsLayout by lazy { activity.find<View>(R.id.directionsLayout) }
    private val directionTextView by lazy { activity.find<TextView>(R.id.directionTextView) }
    private val durationTextView by lazy { activity.find<TextView>(R.id.durationTextView) }
    private val distanceTextView by lazy { activity.find<TextView>(R.id.distanceTextView) }
    private val menuButton by lazy { activity.find<ImageButton>(R.id.menuButton) }


    private val nameView by lazy { activity.find<TextView>(R.id.nameTextView) }
    private val cancelTrackButton by lazy { activity.find<ImageButton>(R.id.cancelTrackButton) }
    private val maneuverImage by lazy { activity.find<ImageView>(R.id.maneuverImage) }
    private val roadProgressBar by lazy { activity.find<ProgressBar>(R.id.roadProgressBar) }

    private val nameViewModel by lazy { activity.getViewModel<NameViewModel>() }
    private val markersViewModel by lazy { activity.getViewModel<MarkersViewModel>() }
    private val roadViewModel by lazy { activity.getViewModel<RoadViewModel>() }

    private val nameInputDialog by lazy {
        NameInputDialog(activity, nameViewModel)
    }

    init {
        with (activity.supportActionBar!!) {
            displayOptions = android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setCustomView(pl.polsl.student.personalnavigation.R.layout.custom_action_bar_layout)
        }

        roadViewModel
                .road
                .observeNotNull(activity) {
                    it.ifPresentOrElse(
                            {
                                nameLayout.visibility = View.INVISIBLE
                                directionsLayout.visibility = View.VISIBLE

                                val maneuverInfo = ManeuverInfo(activity, it)

                                directionTextView.text = maneuverInfo.instructions()

                                maneuverImage.setImageDrawable(
                                        maneuverInfo.icon()
                                )


                                distanceTextView.text = maneuverInfo.distanceText()
                                durationTextView.text = maneuverInfo.totalLengthDurationText()

                            },
                            {
                                nameLayout.visibility = View.VISIBLE
                                directionsLayout.visibility = View.INVISIBLE
                            }
                    )
                }

        nameViewModel.name.observeNotNull(activity) {
            nameInputDialog.setName(it)
            nameView.text = it
        }

        if (nameViewModel.name.value == null) {
            nameInputDialog.show()
        }

        markersViewModel
                .trackedMarker
                .observeNotNull(activity) {
                    it.ifPresentOrElse(
                            { roadProgressBar.visibility = View.VISIBLE },
                            { roadProgressBar.visibility = View.INVISIBLE }
                    )
                }

        nameView.onClick { showProfileDialog() }
        cancelTrackButton.onClick { cancelTracking() }

        menuButton.onClick {
            val popup = PopupMenu(activity, menuButton)

            // This activity implements OnMenuItemClickListener
            popup.setOnMenuItemClickListener{
                when (it.itemId) {
                    R.id.search_menu_item ->
                        activity.startActivity<SearchActivity>()
                    R.id.my_profile_menu_item ->
                        showProfileDialog()
                    R.id.filters_menu_item ->
                        activity.startActivity<FiltersSettingActivity>()
                }
                true
            }
            popup.inflate(R.menu.options_menu)
            popup.show()
        }
    }

    fun showProfileDialog() {
        nameInputDialog.show()
    }

    private fun cancelTracking() {
        markersViewModel.resetTrackedMarker()
    }
}
