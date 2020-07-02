package com.titos.flygrocer


import agency.tango.android.avatarview.views.AvatarView
import agency.tango.android.avatarviewglide.GlideLoader
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView = inflater.inflate(R.layout.fragment_profile, container, false)
        val rvProfile = layoutView.findViewById<RecyclerView>(R.id.rvProfile)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        val sharedPref = activity?.getSharedPreferences("userData", Context.MODE_PRIVATE)!!
        val userName = sharedPref.getString("userName", "User Name")
        layoutView.findViewById<TextView>(R.id.tvUserName).text = userName
        layoutView.findViewById<TextView>(R.id.tvCityName).text = sharedPref.getString("cityName", "Default City")
        layoutView.findViewById<TextView>(R.id.tvMobileNumber).text = "+91 "+ sharedPref.getString("phoneNumber", "+91 1111122222")

        val avatarView = layoutView.findViewById<AvatarView>(R.id.profileAvatar)
        GlideLoader().loadImage(avatarView,"https://dummy",userName)

        rvProfile.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        groupAdapter.add(ProfileItem(R.drawable.ic_baseline_insert_invitation_24,"My Orders"))
        groupAdapter.add(ProfileItem(R.drawable.ic_baseline_map_24,"My Addresses"))
        groupAdapter.add(ProfileItem(R.drawable.ic_baseline_card_giftcard_24,"My Rewards"))

        groupAdapter.setOnItemClickListener { _, view ->
            when (rvProfile.getChildAdapterPosition(view)) {
                0 -> findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
                1 -> findNavController().navigate(R.id.action_profileFragment_to_addressFragment)
                2 -> findNavController().navigate(R.id.action_profileFragment_to_rewardsFragment)
            }
        }

        layoutView.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity,LoginActivity::class.java))
            activity?.finish()
        }

        return layoutView
    }
}