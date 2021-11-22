package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.desafionotas.DetalleNotasActivity
import com.example.desafionotas.R
import connection.Conexion
import model.Tarea

class TareasAdapter(
    var context: DetalleNotasActivity,
    var tareas: ArrayList<Tarea>
) :
    RecyclerView.Adapter<TareasAdapter.ViewHolder>() {

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.tareas_list, parent, false), context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tareas.get(position)
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return tareas.size
    }

    private fun delTarea(tarea: Tarea) {
        tareas.remove(tarea)
    }

    class ViewHolder(view: View, ventana: DetalleNotasActivity) : RecyclerView.ViewHolder(view) {
        val imagen = view.findViewById<ImageButton>(R.id.imgTarea)
        val descTarea = view.findViewById<TextView>(R.id.txtTarea)
        val checked = view.findViewById<ImageView>(R.id.imgCheck)
        val ventana = ventana

        fun bind(tarea: Tarea, context: AppCompatActivity, pos: Int, tareasAdapter: TareasAdapter) {
            descTarea.text = tarea.tarea
            checked.isVisible = tarea.realizada
            imagen.setOnClickListener(View.OnClickListener {
                val img = changeImg()
            })
            itemView.setOnClickListener(View.OnClickListener {
                checked.isVisible = !checked.isVisible
                tarea.realizada = true
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                marcarSeleccion(tareasAdapter, pos)
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.strTituloBorrar))
                    .setMessage(context.getString(R.string.strMensajeBorrar))
                    .setPositiveButton(context.getString(R.string.strConfirmacion)) { view, _ ->
                        tareasAdapter.delTarea(tarea)
                        Toast.makeText(
                            context,
                            context.getString(R.string.strEliminando),
                            Toast.LENGTH_SHORT
                        ).show()
                        tareasAdapter.notifyDataSetChanged()
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

        private fun changeImg(): Any {
            return "imagen"
        }

        private fun marcarSeleccion(tareasAdapter: TareasAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) -1
            else pos
            tareasAdapter.notifyDataSetChanged()
        }
    }
}