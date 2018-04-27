package com.intel.cosbench.exporter;

import java.io.IOException;
import java.io.Writer;

public interface TaskExporter {
	  public void export(Writer writer) throws IOException;
}
