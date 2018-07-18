package br.com.guiabolso.hyperloop.cryptography

class NoOpCryptographyEngine : CryptographyEngine {
    override fun decrypt(data: EncryptedData) = data.data.toString(Charsets.UTF_8)

    override fun encrypt(string: String) = EncryptedData(string.toByteArray())
}