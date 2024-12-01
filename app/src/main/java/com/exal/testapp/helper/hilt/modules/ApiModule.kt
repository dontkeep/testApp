package com.exal.testapp.helper.hilt.modules

import com.exal.testapp.data.network.ApiConfig
import com.exal.testapp.data.network.ApiServices
import com.exal.testapp.helper.hilt.MlApiService
import com.exal.testapp.helper.hilt.RegularApiService
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
    }

    @Provides
    @RegularApiService
    fun provideApiServices(apiConfig: ApiConfig): ApiServices {
        return apiConfig.getApiService()
    }

    @Provides
    @MlApiService
    fun provideApiServicesML(apiConfig: ApiConfig): ApiServices {
        return apiConfig.getMLApiService()
    }
}