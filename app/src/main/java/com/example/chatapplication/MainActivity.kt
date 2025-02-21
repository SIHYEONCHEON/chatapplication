package com.example.chatapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        // 인증 초기화
        mAuth = Firebase.auth

        // DB 초기화
        mDbRef = Firebase.database.reference

        // 리스트 초기화
        userList = ArrayList()

        adapter = UserAdapter(this, userList)

        binding.userRecycelrView.layoutManager = LinearLayoutManager(this)
        binding.userRecycelrView.adapter = adapter

        // 사용자 정보 가져오기
        mDbRef.child("user").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children) {
                    // 유저 정보
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid != currentUser?.uId) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 실패 시 실행
            }

        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}