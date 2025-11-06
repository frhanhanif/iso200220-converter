package org.example;

import com.prowidesoftware.swift.utils.Lib;
import com.prowidesoftware.swift.model.mx.MxCamt05300108;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String input = "C:\\iso20022-converter\\testfile-cbpr\\kasikorn.xml";
        String outputPath = "result.csv";
        //read xml file from path
        MxCamt05300108 camt053 = MxCamt05300108.parse(Lib.readFile(input));

        // Extract data
        Camt053Data data = new Camt053DataExtractor().extract(camt053);
        // Generate CSV
        List<String> csvLines = new Camt053CsvGenerator().generate(data);

        // Write to file
        Files.write(Paths.get(outputPath), csvLines);
    }
}