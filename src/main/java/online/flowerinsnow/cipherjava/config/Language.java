package online.flowerinsnow.cipherjava.config;

import cc.carm.lib.configuration.core.ConfigurationRoot;
import cc.carm.lib.configuration.core.value.type.ConfiguredValue;
import online.flowerinsnow.cipherjava.object.ASNIColour;

import java.io.PrintStream;

public class Language extends ConfigurationRoot {
    public static class NewKey extends ConfigurationRoot {
        public static final ConfiguredValue<String> SYMMETRICAL = ConfiguredValue.of(String.class, "Key: ");
        public static final ConfiguredValue<String> PRIVATE = ConfiguredValue.of(String.class, "Private key: ");
        public static final ConfiguredValue<String> PUBLIC = ConfiguredValue.of(String.class, "Public key: ");
    }

    public static class Encrypt extends ConfigurationRoot {
        public static final ConfiguredValue<String> RESULT = ConfiguredValue.of(String.class, "Result: %s");
    }

    public static class Exit extends ConfigurationRoot {
        public static final ConfiguredValue<String> ZERO = ConfiguredValue.of(String.class, "<f:green>SUCCESS in <f:white>%s<f:green>ms.<c:reset>");
        public static final ConfiguredValue<String> NON_ZERO = ConfiguredValue.of(String.class, "<f:red>FAILED in <f:yellow>%s<f:red>ms with exit code <f:yellow>%s<f:red>.<c:reset>");
    }

    public static class Error extends ConfigurationRoot {
        public static class Parameters extends ConfigurationRoot {
            public static final ConfiguredValue<String> NUMBER_FORMAT = ConfiguredValue.of(String.class, "<f:red>Failed to format number: <f:yellow>%s<c:reset>");
            public static final ConfiguredValue<String> VALUE_MISSING = ConfiguredValue.of(String.class, "<f:red>Parameter value missing: <f:yellow>%s<c:reset>");
            public static final ConfiguredValue<String> NO_SUCH_ENUM = ConfiguredValue.of(String.class, "<f:red>Parameter <f:yellow>'%s'<f:red> has no corresponding enum <f:yellow>'%s'<f:red>. It must be one of the following list:<c:reset>");
            public static final ConfiguredValue<String> RUN_MODE_COUNT = ConfiguredValue.of(String.class, "<f:red>Parameter '--encrypt' '--decrypt' '--newkey' must specify one and at most one.<c:reset>");
            public static final ConfiguredValue<String> KEYS_COUNT = ConfiguredValue.of(String.class, "<f:red>Parameter '--key' '--keybase64' '--keyhex' '--keyfile' '--keyfilebase64' '--keyfilehex' must specify at most one.<c:reset>");
            public static final ConfiguredValue<String> SOURCE_COUNT = ConfiguredValue.of(String.class, "<f:red>Parameter '--string' '--string' must specify one and at most one.<c:reset>");
            public static final ConfiguredValue<String> INVALID_BASE64 = ConfiguredValue.of(String.class, "<f:red>Base64 <f:yellow>'%s'<f:red> is invalid.<c:reset>");
            public static final ConfiguredValue<String> INVALID_HEX_STRING = ConfiguredValue.of(String.class, "<f:red>HEX string <f:yellow>'%s'<f:red> is invalid.<c:reset>");
            public static final ConfiguredValue<String> INVALID_KEY_LENGTH = ConfiguredValue.of(String.class, "<f:red>The key length of the <f:yellow>'%s'<f:red> algorithm cannot be <f:yellow>%s<f:red> (bytes).<c:reset>");
            public static final ConfiguredValue<String> UNSUPPORTED_CIPHER = ConfiguredValue.of(String.class, "<f:red>The cipher <f:yellow>'%s'<f:red> is unsupported.<c:reset>");
            public static final ConfiguredValue<String> UNSUPPORTED_CHARSET = ConfiguredValue.of(String.class, "<f:red>The charset <f:yellow>'%s'<f:red> is unsupported.<c:reset>");
            public static final ConfiguredValue<String> OUTPUT_IS_REQUIRED_IN_FILE_MODE = ConfiguredValue.of(String.class, "<f:red>'--output' is required in file mode.<c:reset>");
            public static final ConfiguredValue<String> INVALID_CIPHER = ConfiguredValue.of(String.class, "<f:red>Invalid input.<c:reset>");
        }

        public static class IO extends ConfigurationRoot {
            public static final ConfiguredValue<String> UNABLE_READ_FILE = ConfiguredValue.of(String.class, "<f:red>An error occurred while reading file <f:yellow>%s<f:red>: <f:yellow>%s<c:reset>");
            public static final ConfiguredValue<String> UNABLE_WRITE_FILE = ConfiguredValue.of(String.class, "<f:red>An error occurred while writing to file <f:yellow>%s<f:red>: <f:yellow>%s<c:reset>");
        }

        public static class Key extends ConfigurationRoot {
            public static final ConfiguredValue<String> KEY_INVALID = ConfiguredValue.of(String.class, "<f:red>Key is invalid.<c:reset>");
            public static final ConfiguredValue<String> CANNOT_DECRYPT = ConfiguredValue.of(String.class, "<f:red>The current key or cipher cannot decrypt this content.<c:reset>");
        }
    }

    public static void printf(PrintStream stream, ConfiguredValue<String> field, Object... params) {
        stream.printf(ASNIColour.format(field.getNotNull()
                .replace("%", "%%")
                .replace("%%s", "%s")
        ), params);
        stream.println();
    }
}
