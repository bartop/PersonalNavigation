package pl.polsl.student.personalnavigation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.android.extension.jsonDeserializer
import org.osmdroid.util.GeoPoint


class MapViewModel(private val sharedPreferences: SharedPreferences): ViewModel() {
    private val ZOOM_KEY = "zoom"
    private val CENTER_LATITUDE_KEY = "center-latitude"
    private val CENTER_LONGITUDE_KEY = "center-latitude"

    private val mutableCenter: MutableLiveData<GeoPoint> = MutableLiveData()
    private val mutableZoom: MutableLiveData<Int> = MutableLiveData()
    private val mutableTrackMyself: MutableLiveData<Boolean> = MutableLiveData()

    init {
        mutableTrackMyself.value = true
        mutableZoom.value = sharedPreferences.getInt(ZOOM_KEY, 16)

        val latitude = sharedPreferences.getString(CENTER_LATITUDE_KEY, "55.0").toDouble()
        val longitude = sharedPreferences.getString(CENTER_LONGITUDE_KEY, "16.0").toDouble()
        mutableCenter.value = GeoPoint(latitude, longitude)
    }

    fun setZoom(zoom: Int) {
        if (zoom != mutableZoom.value) {
            mutableZoom.value = zoom
            sharedPreferences.edit().putInt(ZOOM_KEY, zoom).apply()
        }
    }

    fun setCenter(center: GeoPoint) {
        if (center != mutableCenter.value) {
            mutableCenter.value = center
            sharedPreferences
                    .edit()
                    .putString(
                            CENTER_LATITUDE_KEY,
                            center.latitude.toString()
                    )
                    .putString(
                            CENTER_LONGITUDE_KEY,
                            center.longitude.toString()
                    ).apply()
        }
    }

    fun setTrackMyself(track: Boolean) {
        mutableTrackMyself.value = track
    }

    val center: LiveData<GeoPoint> = mutableCenter
    val zoom: LiveData<Int> = mutableZoom
    val trackMyself: LiveData<Boolean> = mutableTrackMyself
}