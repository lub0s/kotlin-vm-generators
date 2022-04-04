package com.example.anvils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.SavedStateHandle

private const val tag = "ta-ga-gagagaa"

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val vm: MainViewModel =
      Appka.appComponent.getSubcomponentFactory().create(SavedStateHandle()).getViewModelMap()[MainViewModel::class.java]!!.get() as MainViewModel

    findViewById<TextView>(R.id.text).text = vm.provider.a
  }
}
