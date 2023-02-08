package com.daharansemar.adminresto.util

import androidx.appcompat.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.daharansemar.adminresto.R
import com.daharansemar.adminresto.databinding.LoadingDialogBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import android.text.Html
import androidx.navigation.NavController
import com.bumptech.glide.Glide


object Utilization {


    fun formatRupiah(number: Double?): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)


        var string = format.format(number)

        if(string.contains(',')){
            string = string.substring(0, string.indexOf(','))
        }

        return if (number!! < 0.0) {
            "-${string.substring(3, string.length)}"
        } else {
            string.substring(2, string.length)
        }


    }

    fun initLabelFragment(navController: NavController, supportActionBar: ActionBar?):Int {

        var current = 0

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        navController.addOnDestinationChangedListener { _, destination, _ ->
            current = destination.id
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

        return current
    }


    fun simpleDateFormat(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())


        return simpleDateFormat.format(date)
    }

    fun simpleDateFormatUntilMonth(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


        return simpleDateFormat.format(date)
    }

    fun untilMonth(date: Date): Long {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return sdf.parse(sdf.format(date))!!.time

    }


    fun customDialog(context: Context, view: View): AlertDialog {

        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setView(view)

        val dialog = dialogBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog.setCanceledOnTouchOutside(false)
        return dialog


    }

    fun loadingDialog(activity: Activity): AlertDialog {

        val dialogBuilder = AlertDialog.Builder(activity)

        val layout = LoadingDialogBinding.inflate(LayoutInflater.from(activity))

        Glide.with(activity).load(R.raw.ayamloading).into(layout.giffer)


        dialogBuilder.setView(layout.root)

        val dialog = dialogBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog


    }

    fun disableTemp(view: View) {
        view.isEnabled = false

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            view.isEnabled = true
        }, 1500)
    }

    fun toastMaker(context: Context, pesan: String): Toast {

        return Toast.makeText(context, pesan, Toast.LENGTH_SHORT)
    }

    fun hideAndShowToogle(view: View, show: Boolean) {

        if (show) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    fun selectImage(
        resultLaunch: ActivityResultLauncher<Intent>,
        context: Context,
        code: (Int) -> Unit
    ) {


        val items = arrayOf<String>("Ambil foto", "Pilih dari galeri", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Upload foto menu")



        builder.setItems(items) { dialog, item ->
            when (item) {
                0 -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    code.invoke(10)


                    resultLaunch.launch(intent)


                }
                1 -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"

                    code.invoke(20)


                    resultLaunch.launch(intent)


                }
                else -> {
                    dialog.dismiss()
                }
            }


        }
        builder.show()

    }


}