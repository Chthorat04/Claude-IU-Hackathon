package com.readyaid.core.navigation;

import com.readyaid.data.profile.UserProfileDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<UserProfileDao> userProfileDaoProvider;

  public MainViewModel_Factory(Provider<UserProfileDao> userProfileDaoProvider) {
    this.userProfileDaoProvider = userProfileDaoProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(userProfileDaoProvider.get());
  }

  public static MainViewModel_Factory create(Provider<UserProfileDao> userProfileDaoProvider) {
    return new MainViewModel_Factory(userProfileDaoProvider);
  }

  public static MainViewModel newInstance(UserProfileDao userProfileDao) {
    return new MainViewModel(userProfileDao);
  }
}
