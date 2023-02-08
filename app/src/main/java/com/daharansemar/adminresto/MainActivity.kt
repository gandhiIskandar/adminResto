package com.daharansemar.adminresto

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.daharansemar.adminresto.repository.DataStoreRepository
import com.daharansemar.adminresto.util.Utilization.initLabelFragment
import com.daharansemar.adminresto.util.Utilization.toastMaker
import com.daharansemar.adminresto.viewModel.MainViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private var currentFragment = 0

    private lateinit var navHostFragment: NavHostFragment

    companion object {
        var keselAmat = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)



        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)



        initLabelFragment()



        if (keselAmat) {
            navController.navigate(R.id.fragmentListPesanan)
            keselAmat = false
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    fun initLabelFragment() {


        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentFragment = destination.id

            Log.d("currentFragment get", currentFragment.toString())
            Log.d("currentFragment get", R.id.fragmentTambahMenuNew.toString())


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back1)
                supportActionBar?.setTitle(
                    Html.fromHtml(
                        "<font color=\"#332C2B\" ><b>" + destination.label.toString() + "</b></font>",
                        Html.FROM_HTML_MODE_LEGACY
                    )
                )
            } else {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back1)


                supportActionBar?.setTitle(Html.fromHtml("<font color=\"#332C2B\" ><b>" + destination.label.toString() + "</b></font>"))
            }


        }


    }

    override fun onBackPressed() {

        if (navHostFragment.childFragmentManager.backStackEntryCount != 0) {

            if (currentFragment == R.id.fragmentTambahMenuNew) {
                dialogKonfirmasi()

            } else {
                navController.popBackStack()


            }


        } else {


            super.onBackPressed()
        }


    }

    private fun dialogKonfirmasi() {
        val alertBuilder = AlertDialog.Builder(this)

        alertBuilder.setMessage("ingin kembali ke halaman sebelumnya ?")
            .setPositiveButton("ya") { dialog, _ ->
                navController.popBackStack()
                dialog.dismiss()

            }
            .setNegativeButton("tidak") { dialog, _ ->

                dialog.dismiss()

            }
            .setCancelable(false)

        val alertDialog = alertBuilder.create()

        alertDialog.show()
    }


    override fun onStart() {
        super.onStart()


    }


}