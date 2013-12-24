package com.intel.cosbench.controller.loader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.intel.cosbench.config.Stage;
import com.intel.cosbench.config.WorkloadResolver;
import com.intel.cosbench.config.XmlConfig;
import com.intel.cosbench.config.castor.CastorConfigTools;
import com.intel.cosbench.controller.model.StageContext;
import com.intel.cosbench.controller.model.StageRegistry;
import com.intel.cosbench.controller.model.WorkloadContext;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.model.StageInfo;
import com.intel.cosbench.model.StageState;
import com.intel.cosbench.model.WorkloadInfo;
import com.intel.cosbench.service.WorkloadLoader;

public class SimpleWorkloadLoader implements WorkloadLoader {
	private static final Logger LOGGER = LogFactory.getSystemLogger();

	private static File ARCHIVE_DIR = new File("archive");

//	static {
//		if (!ROOT_DIR.exists())
//			ROOT_DIR.mkdirs();
//		String path = ROOT_DIR.getAbsolutePath();
//		LOGGER.info("using {} for loading workload archives", path);
//	}

    public SimpleWorkloadLoader() {
    	this("archive");
    }
    
    public SimpleWorkloadLoader(final String archive) {
    	ARCHIVE_DIR = new File(archive);
    	
        if (!ARCHIVE_DIR.exists())
        	ARCHIVE_DIR.mkdirs();
        String path = ARCHIVE_DIR.getAbsolutePath();
        LOGGER.info("loading workload archives from {}", path);
    }

	private static String getRunDirName(WorkloadInfo info) {
		String name = info.getId();
		name += '-' + info.getWorkload().getName();
		return name;
	}

	@Override
	public List<WorkloadInfo> loadWorkloadRun() throws IOException {
		File file = new File(ARCHIVE_DIR, "run-history.csv");
		if (!file.exists())
			return null;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		RunLoader loader = Loaders.newRunExporter(reader);
		List<WorkloadInfo> workloads = new ArrayList<WorkloadInfo>();
		workloads = loader.load();
		if (reader != null)
			reader.close();
		return workloads;
	}

	@Override
	public void loadWorkloadPageInfo(WorkloadInfo workloadContext)
			throws IOException {
		loadWorkloadConfig(workloadContext);
		loadWorkloadFile(workloadContext);
	}

	private void loadWorkloadConfig(WorkloadInfo workloadContext)
			throws FileNotFoundException {
		XmlConfig config = getWorkloadConfg(workloadContext);
		if(config != null) {
			WorkloadResolver resolver = CastorConfigTools.getWorkloadResolver();
			workloadContext.setWorkload(resolver.toWorkload(config));
			createStages(workloadContext);
		}else {
			((WorkloadContext) workloadContext).setStageRegistry(new StageRegistry());
		}		
	}

	public static XmlConfig getWorkloadConfg(WorkloadInfo workloadContext)
			throws FileNotFoundException {
		File file = new File(
				new File(ARCHIVE_DIR, getRunDirName(workloadContext)),
				"workload-config.xml");
		if (!file.exists())
			return null;
		XmlConfig config = new XmlConfig(new FileInputStream(file));
		return config;
	}

	private void createStages(WorkloadInfo workloadContext) {
		StageRegistry registry = new StageRegistry();
		int index = 1;
		for (Stage stage : workloadContext.getWorkload().getWorkflow()) {
			String id = "s" + index++;
			registry.addStage(createStageContext(id, stage));
		}
		((WorkloadContext) workloadContext).setStageRegistry(registry);
	}

	private static StageContext createStageContext(String id, Stage stage) {
		StageContext context = new StageContext();
		context.setId(id);
		context.setStage(stage);
		context.setState(StageState.COMPLETED, true);
		return context;
	}

	private static String getWorkloadFileName(WorkloadInfo info) {
		String name = info.getId();
		name += "-" + info.getWorkload().getName();
		return name;
	}

	private void loadWorkloadFile(WorkloadInfo workloadContext)
			throws IOException {
		File file = new File(
				new File(ARCHIVE_DIR, getRunDirName(workloadContext)),
				getWorkloadFileName(workloadContext) + ".csv");
		if (!file.exists())
			return;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		WorkloadFileLoader loader = Loaders.newWorkloadLoader(reader,
				workloadContext);
		loader.load();
	}

	private static String getStageFileName(StageInfo info) {
		String name = info.getId();
		name += "-" + info.getStage().getName();
		return name;
	}

	@Override
	public void loadStagePageInfo(WorkloadInfo workloadContext, String stageId)
			throws IOException {
		File file = new File(
				new File(ARCHIVE_DIR, getRunDirName(workloadContext)),
				getStageFileName(workloadContext.getStageInfo(stageId))
						+ ".csv");
		if (!file.exists())
			return;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		SnapshotLoader loader = Loaders.newSnapshotLoader(reader,
				workloadContext, stageId);
		loader.load();
	}

}
