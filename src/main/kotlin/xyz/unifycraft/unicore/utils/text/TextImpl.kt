package xyz.unifycraft.unicore.utils.text

import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.universal.ChatColor
import xyz.unifycraft.unicore.api.utils.text.Text
import java.awt.Color

class TextImpl(
    text: String
) : Text {
    private var content: StringBuilder
    internal val links = mutableMapOf<String, String>()

    init {
        content = TextProcessor.process(text, this)
    }

    override fun append(text: String) = apply {
        content.append(text)
    }

    override fun append(text: Text) = apply {
        content.append(text.asFormattedString())
    }

    override fun asElementaText(shadow: Boolean, shadowColor: Color?) = UIText(content.toString(), shadow, shadowColor)
    override fun asElementaWrappedText(
        shadow: Boolean,
        shadowColor: Color?,
        centered: Boolean,
        trimText: Boolean,
        lineSpacing: Float,
        trimmedTextSuffix: String
    ) = UIWrappedText(content.toString(), shadow, shadowColor, centered, trimText, lineSpacing, trimmedTextSuffix)

    override fun asFormattedString() = content.toString()
    override fun asString() = content.toString().replace(COLOR_CODE_PATTERN, "")

    override fun asTruncated(length: Int) = Text.create(content.toString().take(length))
    override fun asTruncatedString(length: Int) = content.toString().take(length)
    override fun copy() = Text.create(content.toString())

    override fun format(vararg formatting: ChatColor) = apply {
        for (format in formatting) {
            prepend(format.toString())
        }
    }

    override fun iterator(): Iterator<Text> {
        return content.toString().map {
            Text.create(it.toString())
        }.iterator()
    }

    override fun prepend(text: String) = apply {
        content = StringBuilder(text + content.toString())
    }

    override fun prepend(text: Text) = apply {
        content = StringBuilder(text.asFormattedString() + content.toString())
    }

    override fun replace(key: String, value: String, modify: Boolean): Text {
        return if (modify) {
            content = StringBuilder(content.toString().replace(key, value))
            this
        } else {
            Text.create(content.toString().replace(key, value))
        }
    }

    override fun replace(key: String, value: Text, modify: Boolean): Text {
        return if (modify) {
            content = StringBuilder(content.toString().replace(key, value.asFormattedString()))
            this
        } else {
            Text.create(content.toString().replace(key, value.asFormattedString()))
        }
    }

    override fun replace(key: Text, value: String, modify: Boolean): Text {
        return if (modify) {
            content = StringBuilder(content.toString().replace(key.asFormattedString(), value))
            this
        } else {
            Text.create(content.toString().replace(key.asFormattedString(), value))
        }
    }

    override fun replace(key: Text, value: Text, modify: Boolean): Text {
        return if (modify) {
            content = StringBuilder(content.toString().replace(key.asFormattedString(), value.asFormattedString()))
            this
        } else {
            Text.create(content.toString().replace(key.asFormattedString(), value.asFormattedString()))
        }
    }

    override fun set(text: String) = apply {
        content = StringBuilder(text)
    }

    override fun set(text: Text) = apply {
        content = StringBuilder(text.asFormattedString())
    }

    companion object {
        val COLOR_CODE_PATTERN = "(?i)§[0-9A-FK-OR]".toRegex()
    }
}
