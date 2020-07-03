import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.titos.flygrocer.OrderDetailsItem
import com.titos.flygrocer.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.util.*

class OrderDetailsFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView =  inflater.inflate(R.layout.fragment_order_details, container, false)
        val orderNumber = arguments?.getString("orderNumber")!!
        val cityName = activity?.getSharedPreferences("userData", Context.MODE_PRIVATE)
            ?.getString("cityName", "Default City")!!.toUpperCase(Locale.ROOT)

        val rvProductList = layoutView.findViewById<RecyclerView>(R.id.rvOrderItemList)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        val orderRef = FirebaseDatabase.getInstance().reference.child("/allOrders")


        layoutView.findViewById<TextView>(R.id.tvOrderNumber).text = "#$orderNumber"
        rvProductList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        orderRef.child("$cityName/$orderNumber/orderedItems").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (uniqueKey in p0.children){
                    groupAdapter.add(OrderDetailsItem(uniqueKey.child("barcode").value.toString(), uniqueKey.child("qty").value.toString()))
                }
            }

        })

        return layoutView
    }
}