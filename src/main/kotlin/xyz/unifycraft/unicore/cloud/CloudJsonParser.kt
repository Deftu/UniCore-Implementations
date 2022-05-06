package xyz.unifycraft.unicore.cloud

import com.google.gson.stream.JsonReader
import xyz.deftu.quicksocket.common.utils.AbstractJsonParser
import java.io.Reader

class CloudJsonParser : AbstractJsonParser {
    //#if MC>=11202
    private val parser = com.google.gson.JsonParser()
    //#endif

    override fun isValidJson(input: String) = try {
        parse(input)
        true
    } catch (e: Exception) {
        false
    }

    override fun parse(input: JsonReader) =
        //#if MC>=11202
        parser.parse(input)
        //#else
        //$$ JsonParser.parseReader(reader)
        //#endif
    override fun parse(input: Reader) =
            //#if MC>=11202
            parser.parse(input)
            //#else
            //$$ JsonParser.parseReader(reader)
            //#endif
    override fun parse(input: String) =
            //#if MC>=11202
            parser.parse(input)
            //#else
            //$$ JsonParser.parseString(reader)
            //#endif
}