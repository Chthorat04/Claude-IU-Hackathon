package com.readyaid.ui.onboarding;

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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<UserProfileDao> userDaoProvider;

  public OnboardingViewModel_Factory(Provider<UserProfileDao> userDaoProvider) {
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(userDaoProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<UserProfileDao> userDaoProvider) {
    return new OnboardingViewModel_Factory(userDaoProvider);
  }

  public static OnboardingViewModel newInstance(UserProfileDao userDao) {
    return new OnboardingViewModel(userDao);
  }
}
