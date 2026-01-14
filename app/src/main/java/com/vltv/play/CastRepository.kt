package com.vltv.play

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object CastRepository {
    private val client = OkHttpClient()
    private const val API_KEY = "9b73f5dd15b8165b1b57419be2f29128"

    fun carregarElenco(nomeFilme: String, callback: (List<CastMember>) -> Unit) {
        val urlBusca = "https://api.themoviedb.org/3/search/movie?api_key=$API_KEY&query=$nomeFilme"
        
        client.newCall(Request.Builder().url(urlBusca).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback(emptyList())
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string() ?: return
                val id = JSONObject(body).getJSONArray("results").optJSONObject(0)?.optInt("id")
                
                if (id != null) {
                    val urlCreditos = "https://api.themoviedb.org/3/movie/$id/credits?api_key=$API_KEY"
                    client.newCall(Request.Builder().url(urlCreditos).build()).enqueue(object : Callback {
                        override fun onResponse(call: Call, resp: Response) {
                            val castBody = resp.body()?.string() ?: return
                            val castArray = JSONObject(castBody).getJSONArray("cast")
                            val lista = mutableListOf<CastMember>()
                            for (i in 0 until minOf(castArray.length(), 10)) {
                                val obj = castArray.getJSONObject(i)
                                lista.add(CastMember(obj.getString("name"), obj.optString("profile_path", null)))
                            }
                            callback(lista)
                        }
                        override fun onFailure(call: Call, e: IOException) = callback(emptyList())
                    })
                }
            }
        })
    }
}
