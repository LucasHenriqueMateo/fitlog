package com.fitlog.app.data.remote

import com.fitlog.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class AnthropicService @Inject constructor(private val client: OkHttpClient) {

    private val apiKey = BuildConfig.ANTHROPIC_API_KEY

    suspend fun generateWorkout(userPrompt: String): String = withContext(Dispatchers.IO) {
        val systemPrompt = """
            Você é um personal trainer especialista. Quando o usuário descrever um objetivo ou perfil,
            responda com um treino estruturado em formato limpo, sem markdown excessivo.
            Use este formato:

            TREINO: [nome]
            Frequência: [X dias/semana]

            EXERCÍCIOS:
            1. [Nome] — [X séries] × [Y reps] | [Peso sugerido ou "Peso livre"]
               Obs: [dica técnica breve]
            ...

            DICA DO DIA: [motivação ou dica de nutrição/recuperação]
        """.trimIndent()

        val body = JSONObject().apply {
            put("model", "claude-opus-4-5")
            put("max_tokens", 1024)
            put("system", systemPrompt)
            put(
                "messages", org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userPrompt)
                    })
                }
            )
        }.toString()

        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: throw Exception("Empty response"))
        json.getJSONArray("content").getJSONObject(0).getString("text")
    }
}
