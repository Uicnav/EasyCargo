package com.vantechinformatics.easycargo.data

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual class HttpClientEngineFactory {
    actual fun getHttpEngine(): HttpClientEngine {
        return CIO.create()
    }
}