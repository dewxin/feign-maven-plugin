package com.github.dewxin.model;


public class TypeNameEntity {
	

	//including the generic type org.springframework.http.ResponseEntity<java.lang.String> 
	private String fullGenericName; 
	//org.springframework.http.ResponseEntity
	private String fullName;
	//ResponseEntity
	private String simpleName;
	private TypeNameEntity innerGenericType;
	
	public static String getInnerGenriceType(String type) {
		int leftArrowIndex = type.indexOf("<");
		int rightArrowIndex = type.lastIndexOf(">");
		
		return type.substring(leftArrowIndex+1, rightArrowIndex);
	}
	
	public static boolean hasInnerType(String fullGenericName) {
		return fullGenericName.indexOf("<") != -1;
	}
	
	public static String getFullName(String fullGenericName) {
		if(!hasInnerType(fullGenericName)) {
			return fullGenericName;
		}
		
		int leftArrowIndex = fullGenericName.indexOf("<");
		return  fullGenericName.substring(0, leftArrowIndex);
	}
	
	public static String getSimpleName(String fullName) {
		if(fullName.indexOf(".") == -1) {
			return fullName;
		}
		
		//Logger.debug("fullname is "+ fullName);
		
		String[] nameArray = fullName.split("\\.");
		
		//Arrays.asList(nameArray).forEach( name -> Logger.debug(fullName));
		//Logger.debug("length is " + nameArray.length );

		
		return nameArray[nameArray.length-1];
		
	}
	
	public static TypeNameEntity parse(String typeName) {
		
		TypeNameEntity typeEntity = new TypeNameEntity();
		typeEntity.fullGenericName = typeName;
		typeEntity.fullName = getFullName(typeName);
		typeEntity.simpleName = getSimpleName(typeEntity.fullName);
		
		// extract the code  todo
		// OldPojoEntity.addPojoToGenerate(typeEntity.fullName);
		
		if(hasInnerType(typeName)) {
			String subTypeStr = getInnerGenriceType(typeName);
			TypeNameEntity subType = TypeNameEntity.parse(subTypeStr);
			typeEntity.innerGenericType = subType;
		}
		
		return typeEntity;
	}
	
	public String getSelfDefSimpleName() {
		
		String currentName = fullName.startsWith("java.") ? fullName : simpleName;
		if(innerGenericType != null) {
			currentName += "<" + innerGenericType.getSelfDefSimpleName() +">";
		}
		
		return currentName;
	}

	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getFullGenericName() {
		return fullGenericName;
	}
	public void setFullGenericName(String fullGenericName) {
		this.fullGenericName = fullGenericName;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

}
