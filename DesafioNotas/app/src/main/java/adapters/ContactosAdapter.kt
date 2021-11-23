package adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.desafionotas.R
import model.Contacto

class ContactosAdapter(
    var context: AppCompatActivity,
    var contactos: ArrayList<Contacto>
) :
    RecyclerView.Adapter<ContactosAdapter.ViewHolder>() {

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.contacto_item, parent, false), context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contactos.get(position)
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return contactos.size
    }

    fun getSelected(): Contacto {
        return contactos.get(seleccionado)
    }

    fun isSelected(): Boolean = seleccionado != -1

    class ViewHolder(view: View, ventana: AppCompatActivity) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.txtContacto)
        val item = view.findViewById<ConstraintLayout>(R.id.lyContacto)
        fun bind(
            contacto: Contacto,
            context: AppCompatActivity,
            pos: Int,
            contactosAdapter: ContactosAdapter
        ) {
            nombre.text = contacto.nombre
            if (pos == seleccionado) {
                with(item) { setBackgroundResource(com.example.desafionotas.R.color.FondoNota) }
            } else {
                with(item) { setBackgroundColor(Color.TRANSPARENT) }
            }
            itemView.setOnClickListener(View.OnClickListener {
                marcarSeleccion(contactosAdapter, pos)
            })
        }

        private fun marcarSeleccion(contactosAdapter: ContactosAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) {
                -1
            } else {
                pos
            }
            contactosAdapter.notifyDataSetChanged()
        }
    }

}