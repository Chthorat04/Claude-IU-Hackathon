package com.readyaid.di;

import com.readyaid.data.rag.RagClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideRagClientFactory implements Factory<RagClient> {
  @Override
  public RagClient get() {
    return provideRagClient();
  }

  public static AppModule_ProvideRagClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RagClient provideRagClient() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRagClient());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideRagClientFactory INSTANCE = new AppModule_ProvideRagClientFactory();
  }
}
