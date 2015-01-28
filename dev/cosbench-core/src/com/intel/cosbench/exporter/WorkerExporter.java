package com.intel.cosbench.exporter;

import java.io.IOException;
import java.io.Writer;

public interface WorkerExporter {
	public void export(Writer writer) throws IOException;
}
