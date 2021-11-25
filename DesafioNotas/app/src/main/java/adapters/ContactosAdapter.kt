package adapters

import android.graphics.Color
import android.icu.lang.UCharacter
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
    var contactos: List<Contacto>
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

    override fun getItemCount(): Int = contactos.size

    fun getSelected(): Contacto {
        val contacto = contactos.get(seleccionado)
        seleccionado = -1
        return contacto
    }

    fun isSelected(): Boolean = seleccionado != -1

    class ViewHolder(view: View, ventana: AppCompatActivity) : RecyclerView.ViewHolder(view) {
        val txtNombre = view.findViewById<TextView>(R.id.txtContacto)
        val clItem = view.findViewById<ConstraintLayout>(R.id.lyContacto)
        val txtInicial = view.findViewById<TextView>(R.id.txtInicialContacto)

        fun bind(
            contacto: Contacto,
            context: AppCompatActivity,
            pos: Int,
            contactosAdapter: ContactosAdapter
        ) {
            txtNombre.text = contacto.nombre
            txtInicial.text = contacto.nombre.trim()[0].toString().uppercase()
            if (pos == seleccionado) {
                with(clItem) { setBackgroundResource(com.example.desafionotas.R.color.FondoNota) }
            } else {
                with(clItem) { setBackgroundColor(Color.TRANSPARENT) }
            }
            itemView.setOnClickListener(View.OnClickListener {
                marcarSeleccion(contactosAdapter, pos)
            })
        }

        private fun marcarSeleccion(contactosAdapter: ContactosAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) -1 else pos
            contactosAdapter.notifyDataSetChanged()
        }
    }

}