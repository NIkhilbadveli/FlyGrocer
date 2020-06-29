import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.titos.flygrocer.OrderItem
import com.titos.flygrocer.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class OrdersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView = inflater.inflate(R.layout.fragment_orders, container, false)
        val rvOrderList = layoutView.findViewById<RecyclerView>(R.id.rvOrderList)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

        rvOrderList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        val openOrderDetails = { orderNumber: String ->
            val bundle = Bundle()
            bundle.putString("orderNumber", orderNumber)
            findNavController().navigate(R.id.action_ordersFragment_to_orderDetailsFragment, bundle)
        }

        userRef.child("orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (key in p0.children){
                    groupAdapter.add(OrderItem(key.value.toString(), openOrderDetails))
                }
            }
        })

        return layoutView
    }
}