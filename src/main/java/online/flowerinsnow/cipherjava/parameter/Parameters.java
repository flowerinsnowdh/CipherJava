package online.flowerinsnow.cipherjava.parameter;

import online.flowerinsnow.cipherjava.parameter.type.Algorithm;
import online.flowerinsnow.cipherjava.parameter.type.DataType;
import online.flowerinsnow.cipherjava.parameter.type.Unit;

import java.nio.file.Path;

public interface Parameters {
    Parameter<Unit> ENCRYPT = new Parameter<>();
    Parameter<Unit> DECRYPT = new Parameter<>();
    Parameter<Unit> NEW_KEY = new Parameter<>();

    Parameter<Integer> LENGTH = new Parameter<>();
    Parameter<String> SEED = new Parameter<>();

    Parameter<String> STRING = new Parameter<>();

    Parameter<Path> FILE = new Parameter<>();

    Parameter<Algorithm> ALGORITHM = new Parameter<>(Algorithm.AES);

    Parameter<String> KEY_BASE64 = new Parameter<>();
    Parameter<String> KEY_HEX = new Parameter<>();
    Parameter<String> KEY = new Parameter<>();

    Parameter<Path> KEY_FILE_BASE64 = new Parameter<>();
    Parameter<Path> KEY_FILE_HEX = new Parameter<>();
    Parameter<Path> KEY_FILE = new Parameter<>();

    Parameter<Path> OUTPUT = new Parameter<>();
    Parameter<Path> PUBLIC_OUTPUT = new Parameter<>();
    Parameter<DataType> OUTPUT_TYPE = new Parameter<>(DataType.BASE64);

    Parameter<String> INPUT_CHARSET = new Parameter<>("UTF-8");
    Parameter<String> OUTPUT_CHARSET = new Parameter<>("UTF-8");

    Parameter<String> CIPHER = new Parameter<>("CIPHER");

    Parameter<Unit> WRAPPING = new Parameter<>();
}
