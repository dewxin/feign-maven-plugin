package fun.enou.maven.model;

import java.util.LinkedList;
import java.util.List;

public class DataHolder {
	
	private static DataHolder dataHolder = new DataHolder();

	public static DataHolder instance() {
		return dataHolder;
	}
	
	private String applicationName;
	
	private List<CtrlEntity> ctrlEntityList = new LinkedList<CtrlEntity>();

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public void addCtrlEntity(CtrlEntity ctrlEntity) {
		ctrlEntityList.add(ctrlEntity);
	}
	
	public List<CtrlEntity> getCtrlEntityList() {
		return ctrlEntityList;
	}
	
	
	

}
