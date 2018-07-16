package br.com.guiabolso.hyperloop

import java.nio.charset.Charset
import java.security.MessageDigest

fun String.md5(charset: Charset = Charsets.UTF_8): String {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(this.toByteArray()).toString(charset)
}