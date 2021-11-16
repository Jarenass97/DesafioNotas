package adapters

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.desafionotas.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import model.Nota

class NotasAdapter(
    var context: AppCompatActivity,
    var notas: ArrayList<Nota>
) :
    RecyclerView.Adapter<NotasAdapter.ViewHolder>() {

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.notas_list, parent, false), context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notas.get(position)
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return notas.size
    }

    fun getSelected(): Nota {
        return notas.get(seleccionado)
    }

    fun deseleccionar() {
        if (seleccionado != -1) {
            seleccionado = -1
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View, ventana: AppCompatActivity) : RecyclerView.ViewHolder(view) {
        val asunto = view.findViewById<TextView>(R.id.txtAsuntoNotaRecycler)
        val hora = view.findViewById<TextView>(R.id.txtHoraRecycler)
        val item = view.findViewById<ConstraintLayout>(R.id.lyNota)
        val btnEditar = ventana.findViewById<FloatingActionButton>(R.id.btnEditarNota)

        fun bind(nota: Nota, context: AppCompatActivity, pos: Int, notasAdapter: NotasAdapter) {
            asunto.text = nota.asunto
            hora.text = nota.hora
            if (pos == seleccionado) {
                with(item) { setBackgroundResource(R.color.FondoNotaSeleccionada) }
            } else {
                with(item) { setBackgroundResource(R.color.FondoNota) }
            }
            itemView.setOnClickListener(View.OnClickListener {
                marcarSeleccion(notasAdapter, pos)
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                marcarSeleccion(notasAdapter, pos)
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.strTituloBorrar))
                    .setMessage(context.getString(R.string.strMensajeBorrar))
                    .setPositiveButton(context.getString(R.string.strConfirmacionBorrar)) { view, _ ->
                        //eliminar nota
                        Toast.makeText(
                            context,
                            context.getString(R.string.strEliminando),
                            Toast.LENGTH_SHORT
                        ).show()
                        notasAdapter.notifyDataSetChanged()
                        view.dismiss()
                    }
                    .setNegativeButton(context.getString(R.string.strNegacionBorrar)) { view, _ ->
                        view.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
                true
            })
        }

        private fun marcarSeleccion(notasAdapter: NotasAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) {
                btnEditar.isVisible = false
                -1
            } else {
                btnEditar.isVisible = true
                pos
            }
            notasAdapter.notifyDataSetChanged()
        }
    }
}