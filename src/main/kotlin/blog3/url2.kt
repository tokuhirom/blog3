package blog3

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun decodeURL(src: String): String {
    return URLDecoder.decode(
        src,
        StandardCharsets.UTF_8
    )!!
}

fun encodeURL(src: String) : String {
    return URLEncoder.encode(
        src,
        StandardCharsets.UTF_8
    )
}
