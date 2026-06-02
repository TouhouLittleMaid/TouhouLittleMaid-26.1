package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;

import static net.minecraft.resources.Identifier.isValidNamespace;
import static net.minecraft.resources.Identifier.isValidPath;

public class IdentifierUtil {
    public static boolean isValid(String str) {
        String[] decompose = decompose(str);
        String first = decompose[0];
        String second = decompose[1];
        String namespace = StringUtils.isEmpty(first) ? "minecraft" : first;
        return isValidNamespace(namespace) && isValidPath(second);
    }

    private static String[] decompose(String str) {
        String[] strings = new String[]{"minecraft", str};
        int i = str.indexOf(':');
        if (i >= 0) {
            strings[1] = str.substring(i + 1);
            if (i >= 1) {
                strings[0] = str.substring(0, i);
            }
        }
        return strings;
    }

    public static Identifier modLoc(String str) {
        return Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, str);
    }
}
