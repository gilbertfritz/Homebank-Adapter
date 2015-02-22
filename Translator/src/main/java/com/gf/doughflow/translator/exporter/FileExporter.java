package com.gf.doughflow.translator.exporter;

import com.gf.doughflow.translator.model.Transaction;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class FileExporter {

    public static void exportFile(List<Transaction> transactions, IExporter exporter, String filename) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "UTF-8"));
            String header = exporter.createHeader();
            if (header != null) {
                writer.write(header);
            }
            for (Transaction t : transactions) {
                writer.write(exporter.export(t));
            }
            String trailer = exporter.createTrailer();
            if (trailer != null) {
                writer.write(trailer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}
