package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import assistant.TipoNota
import com.example.desafionotas.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import connection.Conexion
import model.Nota
import model.NotaTareas
import model.NotaTexto

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

    private fun delNota(nota: Nota) {
        notas.remove(nota)
    }

    class ViewHolder(view: View, ventana: AppCompatActivity) : RecyclerView.ViewHolder(view) {
        val txtAsunto = view.findViewById<TextView>(R.id.txtAsuntoNotaRecycler)
        val imgTipo = view.findViewById<ImageView>(R.id.imgIconTipo)
        val txtHora = view.findViewById<TextView>(R.id.txtHoraRecycler)
        val clItem = view.findViewById<ConstraintLayout>(R.id.lyNota)
        val txtContenido = view.findViewById<TextView>(R.id.txtContenido)
        val btnEditar = ventana.findViewById<FloatingActionButton>(R.id.btnEditarNota)

        fun bind(nota: Nota, context: AppCompatActivity, pos: Int, notasAdapter: NotasAdapter) {
            txtAsunto.text = nota.asunto
            if (nota.tipo == TipoNota.TEXTO) {
                imgTipo.setImageResource(R.drawable.text_icon)
                var cont = (nota as NotaTexto).texto
                txtContenido.text = if (cont.length > 50) cont.substring(0, 50)
                else cont
            } else {
                imgTipo.setImageResource(R.drawable.lista_icon)
                txtContenido.text = ""
            }
            txtHora.text = nota.hora
            if (pos == seleccionado) {
                with(clItem) { setBackgroundResource(R.color.FondoNotaSeleccionada) }
            } else {
                with(clItem) { setBackgroundResource(R.color.FondoNota) }
            }
            itemView.setOnClickListener(View.OnClickListener {
                marcarSeleccion(notasAdapter, pos)
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                marcarSeleccion(notasAdapter, pos)
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.strTituloBorrar))
                    .setMessage(context.getString(R.string.strMensajeBorrar))
                    .setPositiveButton(context.getString(R.string.strConfirmacion)) { view, _ ->
                        Conexion.delNota(context, nota)
                        notasAdapter.delNota(nota)
                        notasAdapter.deseleccionar()
                        habilitarBoton(false)
                        Toast.makeText(
                            context,
                            context.getString(R.string.strEliminando),
                            Toast.LENGTH_SHORT
                        ).show()
                        notasAdapter.notifyDataSetChanged()
                        view.dismiss()
                    }
                    .setNegativeButton(context.getString(R.string.strNegacion)) { view, _ ->
                        view.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
                true
            })
        }

        private fun habilitarBoton(opcion: Boolean) {
            btnEditar.isVisible = opcion
        }

        private fun marcarSeleccion(notasAdapter: NotasAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) {
                habilitarBoton(false)
                -1
            } else {
                habilitarBoton(true)
                pos
            }
            notasAdapter.notifyDataSetChanged()
        }
    }

}