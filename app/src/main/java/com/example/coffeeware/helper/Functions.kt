package com.example.coffeeware.helper

import android.app.AlertDialog
import android.content.Context
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import com.example.coffeeware.R
import java.text.DecimalFormat

class Functions(private val context: Context) {

    fun exibirCaixaDialogo(titulo: String, mensagem: String, acaoPositiva: () -> Unit, acaoNegativa: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
        builder.setMessage(mensagem)

        builder.setPositiveButton(R.string.yes) { dialog, which ->
            acaoPositiva()
        }

        builder.setNegativeButton(R.string.no) { dialog, which ->
            acaoNegativa()
        }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(getColor(context, R.color.default_color))

            // Definir a cor do botão "Não" programaticamente
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(getColor(context, R.color.default_color))
        }

        dialog.show()
    }

}