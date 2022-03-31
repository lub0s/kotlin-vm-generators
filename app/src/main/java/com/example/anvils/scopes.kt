package com.example.anvils

import com.squareup.anvil.annotations.MergeComponent
import javax.inject.Singleton

abstract class AppScope private constructor()

@Singleton
@MergeComponent(AppScope::class)
interface AppComponent

