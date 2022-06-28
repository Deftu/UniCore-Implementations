package xyz.unifycraft.unicore.utils.text

import java.util.StringTokenizer

/**
 * This class is used to process color and
 * object tags and links in text. It uses
 * tokenization to do such things and is
 * very customizable and powerful.
 */
object TextProcessor {
    fun process(input: String, text: TextImpl): StringBuilder {
        val builder = StringBuilder(input)
        val tokenizer = StringTokenizer(input, "", true)

        return builder
    }
}
