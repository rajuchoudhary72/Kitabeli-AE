package com.kitabeli.ae.ui

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.ActivityMainBinding
import com.kitabeli.ae.ui.common.BaseActivity
import com.kitabeli.ae.utils.Constants
import com.kitabeli.ae.utils.ext.toast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : BaseActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var latestVersion: String
    private lateinit var requestSinglePermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)

        requestSinglePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == false) {
                    toast("External storage permission is required")
                } else {
                    downloadFile(
                        "AE-App-${latestVersion}",
                        "Latest AE App",
                        Constants.S3_APP_LINK
                    )
                }
            }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        checkForMaintenance()
        checkForUpdate()

    }

    private fun askPermissions() {
        requestSinglePermissionLauncher.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private fun checkForMaintenance() {

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val isUnderMaintenance = remoteConfig.getBoolean("ae_under_maintenance")
        if (isUnderMaintenance) {
            navController.navigate(R.id.fragment_maintenance)
        }
    }

    private fun checkForUpdate() {

        val appVersion: String = getAppVersion(this)
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        latestVersion = remoteConfig.getString("ae_latest_app_version")
        val minVersion = remoteConfig.getString("ae_min_app_version")


        if (latestVersion.isEmpty() || minVersion.isEmpty()) {
            return
        }

        if (getAppVersionWithoutAlphaNumeric(appVersion).toInt() < getAppVersionWithoutAlphaNumeric(
                latestVersion
            ).toInt()
        ) {
//            if (checkMandateVersionApplicable(
//                    getAppVersionWithoutAlphaNumeric(minVersion),
//                    getAppVersionWithoutAlphaNumeric(appVersion)
//                )
//            ) {
//                onUpdateNeeded(true)
//            } else {
//                onUpdateNeeded(false)
//            }
            onUpdateNeeded(true)
        }
    }

    private fun checkMandateVersionApplicable(
        minVersion: String,
        appVersion: String,
    ): Boolean {
        return try {
            val minVersionInt = minVersion.toInt()
            val appVersionInt = appVersion.toInt()
            minVersionInt > appVersionInt
        } catch (exp: NumberFormatException) {
            false
        }
    }

    private fun getAppVersion(context: Context): String {
        var result: String? = ""
        try {
            result = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: PackageManager.NameNotFoundException) {
        }
        return result ?: ""
    }

    private fun getAppVersionWithoutAlphaNumeric(result: String): String {
        val versionStr: String = result.replace(".", "")
        return versionStr.trim()
    }

    private fun onUpdateNeeded(isMandatoryUpdate: Boolean) {
        val dialogBuilder =
            AlertDialog.Builder(this).setTitle("Yuk Update Aplikasimu").setCancelable(false)
                .setMessage("Ada beberapa pengembangan di aplikasi yang baru. Kamu bisa unduh lalu dan hapus aplikasi yang lama ya.")
                .setPositiveButton("Unduh Aplikasi Baru") { _, _ ->
                    downloadAPK()
                }

        if (!isMandatoryUpdate) {
            dialogBuilder.setNegativeButton("Kemudian") { dialog, _ ->

                dialog?.dismiss()
            }.create()
        }
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun downloadAPK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadFile(
                "AE-App-${latestVersion}",
                "Latest AE App",
                Constants.S3_APP_LINK
            )
        } else {
            askPermissions()
        }

    }

    private fun installAPK() {
        val promptInstall = Intent(Intent.ACTION_VIEW);
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/myApp.apk"
        )
        Log.e("path", file.absolutePath)
        Log.e("path", file.exists().toString())
        promptInstall.setDataAndType(
            FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                file
            ),
            "application/vnd.android.package-archive"
        );
        promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //promptInstall.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            startActivity(promptInstall)
        } catch (e: Exception) {
            Log.e("error", e.localizedMessage)
        }

    }

    private fun downloadFile(fileName: String, desc: String, url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setMimeType("application/vnd.android.package-archive")
            .setTitle(fileName)
            .setDescription(desc)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${fileName}.apk")
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
    }

}