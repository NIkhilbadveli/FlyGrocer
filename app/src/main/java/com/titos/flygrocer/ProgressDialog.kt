package com.titos.flygrocer

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView

class ProgressDialog {
        companion object {
            fun progressDialog(context: Context): Dialog {
                val dialog = Dialog(context)
                val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
                inflate.findViewById<TextView>(R.id.login_tv_dialog).text = "Logging in ..."

                dialog.setContentView(inflate)
                dialog.setCancelable(false)
                dialog.window!!.setBackgroundDrawable(
                        ColorDrawable(Color.TRANSPARENT)
                )
                return dialog
            }
        }
    }