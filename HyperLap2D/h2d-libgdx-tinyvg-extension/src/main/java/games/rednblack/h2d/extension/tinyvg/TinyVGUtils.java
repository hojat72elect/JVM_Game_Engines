package games.rednblack.h2d.extension.tinyvg;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LittleEndianInputStream;
import dev.lyze.gdxtinyvg.TinyVG;
import dev.lyze.gdxtinyvg.TinyVGHeader;
import dev.lyze.gdxtinyvg.commands.Command;
import dev.lyze.gdxtinyvg.enums.CommandType;
import dev.lyze.gdxtinyvg.enums.StyleType;

import java.io.IOException;
import java.io.InputStream;

public class TinyVGUtils {

    public static TinyVG load(FileHandle fileHandle) {
        return load(fileHandle.read());
    }

    public static TinyVG load(InputStream stream) {
        try {
            return loadInternal(new LittleEndianInputStream(stream));
        } catch (IOException e) {
            throw new GdxRuntimeException(e);
        }
    }

    public static TinyVG clone (TinyVG tinyVG) {
        TinyVG clone = new TinyVG(tinyVG.getHeader(), tinyVG.getColorTable());

        for (Command command : tinyVG.getCommands()) {
            clone.addCommand(command);
        }

        return clone;
    }

    private static TinyVG loadInternal(LittleEndianInputStream stream) throws IOException {
        TinyVGHeader header = new TinyVGHeader();
        header.read(stream);

        TinyVG tinyVg = new TinyVG(header, readColorTable(header, stream));

        Command command;
        do {
            command = readCommand(stream, tinyVg);
            if (command == null) {
                // todo change me once everything is implemented throw new
                // IllegalArgumentException("Unknown command in file");
                return tinyVg;
            }
            tinyVg.addCommand(command);
        } while (command.getType() != CommandType.END_OF_DOCUMENT);

        return tinyVg;
    }

    private static Command readCommand(LittleEndianInputStream stream, TinyVG tinyVG) throws IOException {
        int commandStyleByte = stream.readUnsignedByte();
        CommandType commandType = CommandType.valueOf(commandStyleByte & 0b0011_1111);
        StyleType styleType = StyleType.valueOf((commandStyleByte & 0b1100_0000) >> 6);

        return commandType.read(stream, styleType, tinyVG);
    }

    private static Color[] readColorTable(TinyVGHeader header, LittleEndianInputStream stream) throws IOException {
        Color[] table = new Color[header.getColorTableCount()];

        for (int i = 0; i < table.length; i++)
            table[i] = header.getColorEncoding().read(stream);

        return table;
    }
}
