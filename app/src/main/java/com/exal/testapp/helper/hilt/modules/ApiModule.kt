package com.exal.testapp.helper.hilt.modules

import com.exal.testapp.data.network.ApiConfig
import com.exal.testapp.data.network.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun provideApiConfig(): ApiConfig {
        return ApiConfig()
        //TODO: add sharedPreferenceToken in the future
    }

    @Provides
    fun provideApiServices(apiConfig: ApiConfig): ApiServices {
        return apiConfig.getApiService()
    }
}