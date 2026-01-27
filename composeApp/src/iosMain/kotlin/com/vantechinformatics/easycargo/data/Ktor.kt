package com.vantechinformatics.easycargo.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual class HttpClientEngineFactory {
    actual fun getHttpEngine(): HttpClientEngine {
        return Darwin.create()
    }
}