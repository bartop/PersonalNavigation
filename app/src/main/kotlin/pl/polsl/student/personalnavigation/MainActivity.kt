package pl.polsl.student.personalnavigation

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.github.kittinunf.result.Result
import kotterknife.bindView
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import java.util.concurrent.ScheduledThreadPoolExecutor
import android.R.string.cancel
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java8.util.Optional


class MainActivity : Activity() {
    private val NAME_KEY = "name"
    private val boundingBoxTransform = ScalingBoundingBoxTransform(2.0f)
    private val threadPoolExecutor = ScheduledThreadPoolExecutor(8)
    private val markersSource: MarkersSource = BackendMarkersSource("http://10.0.2.2:8080/")
    private val asyncMarkersSource = DefaultAsyncMarkersSource(
            markersSource,
            threadPoolExecutor
    )
    private val handler = Handler()
    private val loginService by lazy {
        AsyncLoginService(
            BackendLoginService("http://10.0.2.2:8080", sharedPreferences()),
            threadPoolExecutor
        )
    }

    private val mapView: MapView by bindView(R.id.map)
    private val nameView: TextView by bindView(R.id.nameTextView)

    //Depends on `mapView` - use only after layout inflation
    private val markersConsumer: MarkersConsumer by lazy{
        Map(
                mapView,
                DefaultOverlayMarkersFactory(mapView)
        )
    }

    private val nameInputDialog by lazy{
        NameInputDialog(this, layoutInflater, this::onNameEntered)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_main)

        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        val preferences = sharedPreferences()

        if (!preferences.contains(NAME_KEY)) {
            showNameDialog(null)
        } else {
            nameView.text = preferences.getString(NAME_KEY, getString(R.string.your_name))
        }

        login()

    }


    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }

    private fun login() {
        loginService
                .login()
                .exceptionally {
                    //TODO: cast...
                    Optional.of(it as Exception)
                }
                .thenAccept {
                    it.ifPresentOrElse(
                            {
                                //try to relogin
                                handler.postDelayed(this::login, 1000L)
                                Log.e("Main activity", "Cannot login!", it)
                            },
                            this::updateMap
                    )
                }
    }

    private fun updateMap() {
        val delay = 250L
        asyncMarkersSource
                .getMarkersIn(
                        boundingBoxTransform(mapView.boundingBox)
                )
                .exceptionally {
                    Result.of { throw it }
                }
                .thenAccept {
                    markers -> runOnUiThread {
                        markersConsumer.consume(markers)
                        handler.postDelayed(this::updateMap, delay)
                    }
                }
    }

    fun showNameDialog(view: View?) {
        nameInputDialog.show()
    }

    private fun onNameEntered(name: String) {
        val textView = findViewById<TextView>(R.id.nameTextView)
        textView.text = name

        val preferences = sharedPreferences()

        preferences.edit().putString(NAME_KEY, name).apply()
    }

    private fun sharedPreferences(): SharedPreferences {
        return getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        )
    }

}