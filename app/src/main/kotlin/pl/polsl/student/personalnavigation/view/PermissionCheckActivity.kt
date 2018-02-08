package pl.polsl.student.personalnavigation.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import pl.polsl.student.personalnavigation.R

class PermissionCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_check)

        AndPermission
                .with(this)
                .permission(
                        Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.ACCESS_FINE_LOCATION
                )
                .onGranted(this::onGranted)
                .onDenied(this::onDenied)
                .start()
    }

    private fun onDenied(permissions: List<String>) {
        if (permissions.contains(Permission.ACCESS_FINE_LOCATION)) {
            alert(R.string.location_permission_required) {
                okButton {
                    if (AndPermission.hasAlwaysDeniedPermission(
                            this@PermissionCheckActivity,
                            Permission.ACCESS_FINE_LOCATION
                    )) {
                        val settingService = AndPermission
                                .permissionSetting(this@PermissionCheckActivity)
                        settingService.execute()
                        settingService.cancel()
                    }
                    finishAndRemoveTask()
                }
            }.show()
        }
    }

    private fun onGranted(permissions: List<String>) {
        if (permissions.contains(Permission.ACCESS_FINE_LOCATION)) {
            finish()
            startActivity<MainActivity>()
        }
    }
}
