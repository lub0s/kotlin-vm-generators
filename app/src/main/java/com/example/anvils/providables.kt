package com.example.anvils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringsProvider @Inject constructor() {
  val a: String = "A"
  val b: String = "B"
  val c: String = "C"
}
