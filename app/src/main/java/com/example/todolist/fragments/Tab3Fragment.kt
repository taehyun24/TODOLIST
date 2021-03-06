package com.example.todolist.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.databinding.FragmentTab3Binding
import com.example.todolist.model.Profile
import com.example.todolist.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class Tab3Fragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    var binding: FragmentTab3Binding? = null
    var auth: FirebaseAuth? = null
    var profileList: ArrayList<Profile> = arrayListOf()
    var list: ArrayList<Profile> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTab3Binding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        profileViewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        profileViewModel.profilecurrentValue.observe(requireActivity(), Observer {
            binding?.tabRecyclerView?.adapter = CustomAdapter()
            binding?.tabRecyclerView?.layoutManager = LinearLayoutManager(activity)
        })

        binding?.searchEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                val text: String = binding?.searchEdit?.text.toString()
                search(text)
            }
        })

        return binding!!.root
    }

    fun search(text: String) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        profileList.clear()

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (text.isEmpty()) {
            profileList.addAll(list)
        }

        // 문자 입력을 할때
        else {
            for (i: Int in 0 until list.size) {
                if (list[i].email?.contains(text)!!) {
                    profileList.add(list[i])
                }
            }
        }
        binding?.tabRecyclerView?.adapter?.notifyDataSetChanged()
    }

    inner class CustomAdapter : RecyclerView.Adapter<CustomAdapter.ItemViewHolder>() {

        init {
            profileList.clear()
            for (i: Int in 0 until profileViewModel.profileList.size) {
                //다른사람의 팔로우에 내계정이 포함되어있다 = 그 사람이 내 팔로워이다.
                if (profileViewModel.profileList[i].followings.containsKey(auth?.currentUser?.uid)) {
                    profileList.add(profileViewModel.profileList[i])
                }
            }
            list.addAll(profileList)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.tab_recycler, parent, false)

            return ItemViewHolder(view)
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nickName_tv = itemView.findViewById<TextView>(R.id.nickName_tv)
            private val email_tv = itemView.findViewById<TextView>(R.id.email_tv)
            val home_btn = itemView.findViewById<Button>(R.id.home_btn)
            fun bind(profile: Profile) {
                nickName_tv.text = profile.nickName
                email_tv.text = profile.email
            }
        }

        override fun getItemCount(): Int {
            Log.d("숫자", profileList.size.toString())
            return profileList.size
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(profileList[position])
            holder.home_btn.setOnClickListener {

                var bundle = Bundle()
                bundle.putString("uid", profileList[position].uid)
                bundle.putString("email", profileList[position].email)
                Navigation.findNavController(binding!!.root)
                    .navigate(R.id.action_socialFragment_to_homeFragment, bundle)
            }
        }
    }
}