package com.kit.moddingtools.app;

import java.util.List;

import com.google.gson.JsonObject;
import com.kit.moddingtools.reader.Parser;
import com.kit.moddingtools.reader.Reader;

public class App 
{
    public static void main( String[] args )
    {
        List<String> list_of_files = Reader.listFiles("../anbennar-eu4-fork-public-build/common/disasters/");

        for (String file : list_of_files) {
            Parser parser = new Parser(file);
            JsonObject json = parser.getOutput();
            System.out.println(json);
        }
    }
}
