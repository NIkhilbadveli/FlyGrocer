import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.titos.flygrocer.R

class EditAddressFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView = inflater.inflate(R.layout.fragment_edit_address, container, false)

        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
        val etFullName = layoutView.findViewById<EditText>(R.id.etFullName)
        val etMobileNumber = layoutView.findViewById<EditText>(R.id.etMobileNumber)
        val etLine1 = layoutView.findViewById<EditText>(R.id.etLine1)
        val etLine2 = layoutView.findViewById<EditText>(R.id.etLine2)
        val etPincode = layoutView.findViewById<EditText>(R.id.etPincode)
        val etLandMark = layoutView.findViewById<EditText>(R.id.etLandMark)
        val btn = layoutView.findViewById<Button>(R.id.addAddressButton)

        var addressId = 1
        userRef.child("addresses").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists())
                    addressId = p0.childrenCount.toInt() + 1
            }

        })

        //If edit button is clicked in any one of the already existing addresses
        val id = arguments?.getInt("addressId")
        var editing = false
        if (id != null){
            btn.text = "Save"
            editing = true
            userRef.child("addresses").child(id.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    etFullName.setText(p0.child("name").value.toString())
                    etMobileNumber.setText(p0.child("mobileNumber").value.toString())
                    etPincode.setText(p0.child("pincode").value.toString())
                    etLine1.setText(p0.child("line1").value.toString())
                    etLine2.setText(p0.child("line2").value.toString())
                    etLandMark.setText(p0.child("landmark").value.toString())
                }

            })
        }

        btn.setOnClickListener {
            if (etFullName.text.isNotEmpty() && etMobileNumber.text.isNotEmpty()
                && etLine1.text.isNotEmpty() && etPincode.text.isNotEmpty() && !editing){

                userRef.child("addresses/$addressId").child("name").setValue(etFullName.text.toString())
                userRef.child("addresses/$addressId").child("mobileNumber").setValue(etMobileNumber.text.toString())
                userRef.child("addresses/$addressId").child("pincode").setValue(etPincode.text.toString())
                userRef.child("addresses/$addressId").child("line1").setValue(etLine1.text.toString())
                userRef.child("addresses/$addressId").child("line2").setValue(etLine2.text.toString())
                userRef.child("addresses/$addressId").child("landmark").setValue(etLandMark.text.toString())
                Toast.makeText(requireContext(),"Address added in database", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            else if (etFullName.text.isNotEmpty() && etMobileNumber.text.isNotEmpty()
                && etLine1.text.isNotEmpty() && etPincode.text.isNotEmpty() && editing){

                userRef.child("addresses/$id").child("name").setValue(etFullName.text.toString())
                userRef.child("addresses/$id").child("mobileNumber").setValue(etMobileNumber.text.toString())
                userRef.child("addresses/$id").child("pincode").setValue(etPincode.text.toString())
                userRef.child("addresses/$id").child("line1").setValue(etLine1.text.toString())
                userRef.child("addresses/$id").child("line2").setValue(etLine2.text.toString())
                userRef.child("addresses/$id").child("landmark").setValue(etLandMark.text.toString())
                Toast.makeText(requireContext(),"Address updated in database", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            else
                Toast.makeText(requireContext(),"Please enter all the required fields", Toast.LENGTH_SHORT).show()
        }

        return layoutView
    }
}