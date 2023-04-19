package com.ds.drag.demo.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ds.drag.demo.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/20
 * desc   :
 * version: 1.0
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_addition.setOnClickListener {
            val intent = Intent(this, AdditionActivity::class.java)
            startActivity(intent)
        }

        btn_folder.setOnClickListener {
            val intent = Intent(this, FolderActivity2::class.java)
            startActivity(intent)

        }

        btn_multiplication.setOnClickListener {
            val intent = Intent(this, MultiplyActivity::class.java)
            startActivity(intent)
        }

        btn_not_handler.setOnClickListener {
            val intent = Intent(this, NotDragHandlerImplActivity::class.java)
            startActivity(intent)
        }

        btn_test.setOnClickListener {
            val intent = Intent(this, TestDragActivity::class.java)
            startActivity(intent)
        }

    }

}