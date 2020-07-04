import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        val ratingBar = layoutView.findViewById<RatingBar>(R.id.orderRating)
        val etOrderReview = layoutView.findViewById<EditText>(R.id.etOrderReview)

        layoutView.findViewById<TextView>(R.id.tvOrderNumber).text = "#$orderNumber"
        rvProductList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        orderRef.child("$cityName/$orderNumber").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (uniqueKey in p0.child("orderedItems").children){
                    groupAdapter.add(OrderDetailsItem(uniqueKey.child("barcode").value.toString(), uniqueKey.child("qty").value.toString()))
                }
                if (p0.child("orderRating").exists())
                    ratingBar.rating = p0.child("orderRating").value.toString().toFloat()
                if (p0.child("orderReview").exists())
                    etOrderReview.setText(p0.child("orderReview").value.toString())
            }

        })


        layoutView.findViewById<Button>(R.id.submitButton).setOnClickListener {
            val orderRating = ratingBar.rating
            val orderReview = etOrderReview.text.toString()
            orderRef.child("$cityName/$orderNumber").child("orderRating").setValue(orderRating)
            orderRef.child("$cityName/$orderNumber").child("orderReview").setValue(orderReview)
            Toast.makeText(requireContext(),"Review added", Toast.LENGTH_SHORT).show()
            layoutView.findViewById<EditText>(R.id.etOrderReview).clearFocus()
        }

        return layoutView
    }
}