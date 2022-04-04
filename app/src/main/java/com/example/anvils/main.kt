package com.example.anvils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

private const val tag = "taga-ga-ga-ga"

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val vm: MainViewModel =
      Appka.appComponent.getViewModelMap()[MainViewModel::class.java]!!.get() as MainViewModel

    findViewById<TextView>(R.id.text).text = vm.provider.a
  }
}
