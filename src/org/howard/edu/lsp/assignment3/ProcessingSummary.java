package org.howard.edu.lsp.assignment3;

import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Stores and reports the results of one ETL run.
 */
final class ProcessingSummary {
    private final int rowsRead;
    private final int rowsTransformed;
    private final int rowsSkipped;
    private final Path outputPath;

    ProcessingSummary(int rowsRead, int rowsTransformed, int rowsSkipped, Path outputPath) {
        this.rowsRead = rowsRead;
        this.rowsTransformed = rowsTransformed;
        this.rowsSkipped = rowsSkipped;
        this.outputPath = outputPath;
    }

    void printTo(PrintStream output) {
        output.println("Rows read: " + rowsRead);
        output.println("Rows transformed: " + rowsTransformed);
        output.println("Rows skipped: " + rowsSkipped);
        output.println("Output file: " + outputPath);
    }
}
