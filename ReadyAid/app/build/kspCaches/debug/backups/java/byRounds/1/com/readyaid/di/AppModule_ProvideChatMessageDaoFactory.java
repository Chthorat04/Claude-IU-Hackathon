package com.readyaid.di;

import com.readyaid.data.profile.AppDatabase;
import com.readyaid.data.profile.ChatMessageDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideChatMessageDaoFactory implements Factory<ChatMessageDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public AppModule_ProvideChatMessageDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public ChatMessageDao get() {
    return provideChatMessageDao(appDatabaseProvider.get());
  }

  public static AppModule_ProvideChatMessageDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new AppModule_ProvideChatMessageDaoFactory(appDatabaseProvider);
  }

  public static ChatMessageDao provideChatMessageDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideChatMessageDao(appDatabase));
  }
}
