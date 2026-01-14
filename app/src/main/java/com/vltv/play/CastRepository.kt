package com.vltv.play

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object CastRepository {
    private val client = OkHttpClient()
    private const val API_KEY = "9b73f5dd15b8165b1b57419be2f29128"

    fun carregarElenco(nome: String, isSeries: Boolean, callback: (List<CastMember>) -> Unit) {
        // Detecta se busca por 'tv' ou 'movie'
        val type = if (isSeries) "tv" else "movie"
        val url = "https://api.themoviedb.org/3/search/$type?api_key=$API_KEY&query=$nome&language=pt-BR"

        client.newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback(emptyList())
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string() ?: ""
                val id = JSONObject(body).optJSONArray("results")?.optJSONObject(0)?.optInt("id")
                
                if (id != null) {
                    // Busca os créditos (atores) usando o ID encontrado
                    val urlCredits = "https://api.themoviedb.org/3/$type/$id/credits?api_key=$API_KEY&language=pt-BR"
                    client.newCall(Request.Builder().url(urlCredits).build()).enqueue(object : Callback {
                        override fun onResponse(call: Call, r: Response) {
                            val cBody = r.body()?.string() ?: ""
                            val castArray = JSONObject(cBody).optJSONArray("cast") ?: return callback(emptyList())
                            val list = mutableListOf<CastMember>()
                            // Pega os 10 primeiros atores
                            for (i in 0 until minOf(castArray.length(), 10)) {
                                val obj = castArray.getJSONObject(i)
                                list.add(CastMember(obj.getString("name"), obj.optString("profile_path")))
                            }
                            callback(list)
                        }
                        override fun onFailure(call: Call, e: IOException) = callback(emptyList())
                    })
                } else {
                    callback(emptyList())
                }
            }
        })
    }
}
