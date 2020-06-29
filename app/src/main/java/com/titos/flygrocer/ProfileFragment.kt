package com.titos.flygrocer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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

        rvProfile.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        groupAdapter.add(ProfileItem(R.drawable.ic_baseline_camera_alt_24,"My Orders"))
        groupAdapter.add(ProfileItem(R.drawable.ic_baseline_camera_alt_24,"My Addresses"))

        groupAdapter.setOnItemClickListener { _, view ->
            when (rvProfile.getChildAdapterPosition(view)) {
                0 -> findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
                1 -> findNavController().navigate(R.id.action_profileFragment_to_addressFragment)
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