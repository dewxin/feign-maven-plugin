package com.github.dewxin.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.dewxin.model.CtrlEntity;
import com.github.dewxin.model.TypeNameEntity;

import edu.emory.mathcs.backport.java.util.Arrays;

public class DataCenter {

	private static DataCenter dataHolder = new DataCenter();

	public static DataCenter instance() {
		return dataHolder;
	}

	private String projectBaseDir;
	private String projectArtifactId;
	private String projectGroupId;
	private String projectVersion;

	private String applicationName;
	private List<CtrlEntity> ctrlEntityList = new LinkedList<>();
	private HashSet<String> pojoClassNameSet = new HashSet<>();
	private boolean hasAutoWrapMsg = false;

	public String getOriginAppName() {
		return applicationName;
	}

	public void setHasAutoWrapMsg(boolean hasWrap) {
		hasAutoWrapMsg = hasWrap;
	}
	
	public boolean hasAutoWarpMsg() {
		return hasAutoWrapMsg;
	}

	public String getFormattedAppName() {
		String tmpAppName = applicationName;
		
		tmpAppName = tmpAppName.replace("-", "").toLowerCase();
		tmpAppName = tmpAppName.replace("service", "");
		tmpAppName = tmpAppName.substring(0,1).toUpperCase() + tmpAppName.substring(1);
		return tmpAppName;
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

	public List<String> getPojoClassNameList() {
		return new LinkedList<>(pojoClassNameSet);
	}

	public void collectControllerAndPojo(List<Class<?>> entryClassList) {
		List<Class<?>> annotatedCtrlList = entryClassList.stream().filter(Filter::hasRestCtrlAnote)
				.collect(Collectors.toList());
		DebugOutput.annotatedClassAndMethod(annotatedCtrlList);

		for (Class<?> aClass : annotatedCtrlList) {
			CtrlEntity ctrlEntity = CtrlEntity.toEntity(aClass);
			addCtrlEntity(ctrlEntity);
		}

		collectPojo(annotatedCtrlList);

	}

	private void collectPojo(List<Class<?>> annotatedCtrlList) {
		List<Method> allMapAnnotatedMethodList = new LinkedList<>();
		for (Class<?> aClass : annotatedCtrlList) {
			List<Method> methodList = CtrlEntity.getMapAnnotatedMethod(aClass);
			allMapAnnotatedMethodList.addAll(methodList);
		}

		for (Method method : allMapAnnotatedMethodList) {
			String retTypeName = method.getGenericReturnType().getTypeName();
			if (retTypeName.contains("ResponseEntity")) {
				retTypeName = TypeNameEntity.getInnerGenriceType(retTypeName);
			}
			addGenericInnerTypeToGenerateRecusively(retTypeName, pojoClassNameSet);

			for (Parameter param : method.getParameters()) {

				String paramTypeName = param.getType().getName();
				addGenericInnerTypeToGenerateRecusively(paramTypeName, pojoClassNameSet);
			}
		}
		
		List<String> typeNameList = new ArrayList<>(pojoClassNameSet);
		for (String typeName : typeNameList) {
			addFieldClassToGenerateRecusively(typeName, pojoClassNameSet);
		}

	}

	private static void addGenericInnerTypeToGenerateRecusively(String typeName, HashSet<String> pojoNameSet) {
		String pojoName = TypeNameEntity.getFullName(typeName);

		addPojoNameToSet(pojoName, pojoNameSet);

		if (TypeNameEntity.hasInnerType(typeName)) {
			String subTypeStr = TypeNameEntity.getInnerGenriceType(typeName);
			addGenericInnerTypeToGenerateRecusively(subTypeStr, pojoNameSet);
		}
	}

	private static void addFieldClassToGenerateRecusively(String className, HashSet<String> pojoNameSet) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Class<?> aClass = Class.forName(className, true, classLoader);
			for(Field field : aClass.getDeclaredFields()) {
				String genericName = field.getGenericType().getTypeName();

				addGenericInnerTypeToGenerateRecusively(genericName, pojoNameSet);

				if(genericName.startsWith("java."))
					continue;

				addFieldClassToGenerateRecusively(genericName, pojoNameSet);
			}
		} catch (ClassNotFoundException e) {
			Logger.warn(Arrays.toString(e.getStackTrace()));
		}

	}

	private static boolean addPojoNameToSet(String classFullName, HashSet<String> pojoNameSet) {
		if(classFullName.equals("void"))
			return false;
		if(classFullName.startsWith("java."))
			return false;

		pojoNameSet.add(classFullName);
		Logger.debug("{0} is added to pojoNameSet set",classFullName);

		return true;

	}

	public String getProjectBaseDir() {
		return projectBaseDir;
	}

	public void setProjectBaseDir(String projectBaseDir) {
		this.projectBaseDir = projectBaseDir;
	}

	public String getProjectArtifactId() {
		return projectArtifactId;
	}

	public void setProjectArtifactId(String projectArtifactId) {
		this.projectArtifactId = projectArtifactId;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}

	public String getProjectGroupId() {
		return projectGroupId;
	}

	public void setProjectGroupId(String projectGroupId) {
		this.projectGroupId = projectGroupId;
	}

}
