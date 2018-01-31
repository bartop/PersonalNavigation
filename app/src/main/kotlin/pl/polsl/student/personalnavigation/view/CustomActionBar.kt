package pl.polsl.student.personalnavigation.view

import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
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
    private val nameView by lazy { activity.find<TextView>(R.id.nameTextView) }
    private val cancelTrackButton by lazy { activity.find<ImageButton>(R.id.cancelTrackButton) }
    private val maneuverImage by lazy { activity.find<ImageView>(R.id.maneuverImage) }



    private val nameInputDialog by lazy {
        NameInputDialog(activity, activity.layoutInflater, this::onNameEntered)
    }

    private val nameViewModel by lazy { activity.getViewModel<NameViewModel>() }
    private val markersViewModel by lazy { activity.getViewModel<MarkersViewModel>() }
    private val roadViewModel by lazy { activity.getViewModel<RoadViewModel>() }
    private val maneuverIconProvider = ManeuverIconProvider(activity)

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
                                with (it.mNodes) {
                                    val currentNode = getOrElse(1) { first() }
                                    directionTextView.text = currentNode.mInstructions
                                    maneuverImage.setImageDrawable(maneuverIconProvider(currentNode))
                                }
                                durationTextView.text = it.getLengthDurationText(activity, 0)

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

        nameView.onClick {
            nameInputDialog.show()
        }

        cancelTrackButton.onClick { cancelTracking() }
    }

    private fun onNameEntered(name: String) {
        try {
            nameViewModel.setName(name)
        } catch (e: Exception) {
            activity.toast(e.message.toString())
        }
    }

    private fun cancelTracking() {
        markersViewModel.resetTrackedMarker()
    }

}
