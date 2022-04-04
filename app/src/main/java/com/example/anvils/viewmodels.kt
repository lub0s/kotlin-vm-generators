package com.example.anvils

import androidx.lifecycle.ViewModel
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Provider

typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>

//@ContributesViewModel(AppComponent::class)
@ContributesMultibinding(AppScope::class)
@ViewModelKey(MainViewModel::class)
class MainViewModel @Inject constructor(
  val provider: StringsProvider
): ViewModel()

// @AssistedFactory
// interface MainViewModelFactory : ViewModelFactory<MainViewModel>

// interface ViewModelFactory<VM : ViewModel> {
//   fun create(): VM
// }
