package com.exal.testapp.helper.hilt

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RegularApiService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MlApiService