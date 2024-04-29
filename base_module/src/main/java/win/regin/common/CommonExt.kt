package win.regin.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType


/**
 *         功能描述:日志扩展类
 */


/**
 * 转换String
 */
fun Any?.toJsonString(): String {
    return Gson().toJson(this)
}

/**
 * 转换String带格式化
 */
fun Any?.toJsonFormatterString(): String {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    val je: JsonElement = JsonParser.parseString(toJsonString())
    return gson.toJson(je)
}

/**
 * json String格式化
 */
fun String?.parseString(): String {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    val jsonElement = JsonParser.parseString(this)
    return gson.toJson(jsonElement)
}

/**
 * 转换成对象
 */
inline fun <reified T> String.toJsonObject(): T {
    return if (T::class.java.isArray) {
        Gson().fromJson(this, object : TypeToken<T>() {}.type) as T
    } else {
        val type = object : TypeToken<T>() {}.type
        val rawType = (type as? ParameterizedType)?.rawType

        if (rawType == List::class.java) {
            Gson().fromJson(this, type) as T
        } else {
            Gson().fromJson(this, T::class.java)
        }
    }
}