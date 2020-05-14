/*
 *  Copyright 2020 Midhun Harikumar
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ae.apps.messagecounter.data.smsbackup

import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.InvalidParameterSpecException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

@Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
fun generateKey(password: String): SecretKey? {
    return SecretKeySpec(password.toByteArray(), "AES")
}

@Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, InvalidParameterSpecException::class, IllegalBlockSizeException::class, BadPaddingException::class, UnsupportedEncodingException::class)
fun encryptMsg(message: String, secret: SecretKey?): ByteArray? {
    /* Encrypt the message. */
    var cipher: Cipher? = null
    cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secret)
    return cipher.doFinal(message.toByteArray(charset("UTF-8")))
}

@Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class, InvalidParameterSpecException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class, UnsupportedEncodingException::class)
fun decryptMsg(cipherText: ByteArray?, secret: SecretKey?): String? {
    /* Decrypt the message, given derived encContentValues and initialization vector. */
    var cipher: Cipher? = null
    cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secret)
    return String(cipher.doFinal(cipherText), charset("UTF-8"))
}