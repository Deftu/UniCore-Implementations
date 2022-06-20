package xyz.unifycraft.unicore.utils.text

import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
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
        val tokenizer = StringTokenizer(input)

        // Process each token in the string.
        var previousToken: String? = null
        val hasStartedTag = BasicState(false)
        val hasStartedLinkText = BasicState(false)
        val hasStartedLinkUrl = BasicState(false)
        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()
            handleTag(token, previousToken, hasStartedTag)
            handleLink(token, previousToken, hasStartedLinkText, hasStartedLinkUrl)
            println("Token: $token")
            previousToken = token
        }

        return builder
    }

    fun handleTag(token: String, previousToken: String?, hasStartedTag: State<Boolean>) {
        if (token.startsWith("<")) {
            // This is a color or object tag.
            hasStartedTag.set(true)
        }

        if (hasStartedTag.get() && token.endsWith("/>")) {
            // This is the end of a color or object tag.
            hasStartedTag.set(false)
        }
    }

    fun handleLink(token: String, previousToken: String?, hasStartedLinkText: State<Boolean>, hasStartedLinkUrl: State<Boolean>) {
        if (token.startsWith("[")) {
            // This is link text.
            hasStartedLinkText.set(true)
        }

        if (hasStartedLinkText.get() && token.endsWith("/>")) {
            // This is the end of link text.
            hasStartedLinkText.set(false)
        }

        if (previousToken != null && previousToken.endsWith("]") && token.startsWith("(")) {
            // This is the end of a link.
            hasStartedLinkUrl.set(true)
        } else hasStartedLinkText.set(false)

        if (hasStartedLinkUrl.get() && token.endsWith(")")) {
            // This is the end of a link.
            hasStartedLinkUrl.set(false)
        }
    }
}
