package adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafionotas.R
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
        seleccionado = -1
        notifyDataSetChanged()
    }

    class ViewHolder(view: View, ventana: AppCompatActivity) : RecyclerView.ViewHolder(view) {
        val asunto = view.findViewById<TextView>(R.id.txtAsuntoNotaRecycler)
        val hora = view.findViewById<TextView>(R.id.txtHoraRecycler)
        val item = view.findViewById<LinearLayout>(R.id.lyNota)

        fun bind(nota: Nota, context: Context, pos: Int, notasAdapter: NotasAdapter) {
            asunto.text = nota.asunto
            hora.text = nota.hora
            if (pos == seleccionado) with(item) { setBackgroundResource(R.color.FondoNotaSeleccionada) }
            else with(item) { setBackgroundResource(R.color.FondoNota) }
            itemView.setOnClickListener(View.OnClickListener {
                seleccionado = if (pos == seleccionado) {
                    habilitarBotones(false)
                    -1
                } else {
                    habilitarBotones(true)
                    pos
                }
                notasAdapter.notifyDataSetChanged()
            })
        }

        private fun habilitarBotones(option: Boolean) {

        }


    }


}