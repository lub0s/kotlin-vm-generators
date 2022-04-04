package com.example.annotations

import kotlin.reflect.KClass

// import androidx.lifecycle.ViewModel
// import dagger.MapKey
// import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
annotation class ContributesViewModel(
  val scope: KClass<out Any>
)

//
// @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
// @Retention(AnnotationRetention.RUNTIME)
// @MapKey
// annotation class ViewModelKey(val value: KClass<out ViewModel>)
