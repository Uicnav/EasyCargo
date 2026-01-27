package com.vantechinformatics.easycargo.data

import io.ktor.client.engine.HttpClientEngine

expect class HttpClientEngineFactory() {
    fun getHttpEngine(): HttpClientEngine
}