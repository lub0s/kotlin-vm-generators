package com.example.anvils

import androidx.lifecycle.SavedStateHandle
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@MergeComponent(AppScope::class)
interface AppComponent {
  //fun getSubcomponentFactory(): ViewModelComponent.Factory
  fun getViewModelMap(): ViewModelMap
}

// @MergeSubcomponent(ViewModelScope::class)
// interface ViewModelComponent {
//
//   fun getViewModelMap(): ViewModelMap
//
//   @Subcomponent.Factory
//   interface Factory {
//     fun create(@BindsInstance savedStateHandle: SavedStateHandle): ViewModelComponent
//   }
// }
