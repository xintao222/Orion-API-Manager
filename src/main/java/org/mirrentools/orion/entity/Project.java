package org.mirrentools.orion.entity;

/**
 * 项目表
 * 
 * @author
 */
public class Project {
	/** 项目的id */
	private String key;
	/** 名称 */
	private String name;
	/** 版本号 */
	private String versions;
	/** 描述(支持HTML) */
	private String description;
	/** 服务集(JsonArray(JsonObject)):
	 * 	[{
	 *    url(String):服务地址,
	 *    description(String):描述简介
	 *  }]
	 */
	private String servers;
	/** 附加文档(JsonObject):<br>
	 * description(String):附加文档说明<br>
	 * url(String): 附加文档路径
	 */
	private String externalDocs;
	/** 联系人 */
	private String contactName;
	/** 联系人信息 */
	private String contactInfo;
	/** 项目的负责人,为空时则为超级管理员 */
	private String owner;
	/** 用户的信息 */
	private Users ownerInfo;
	/** 可以查看该项目的人 */
	private String owners;
	/** 最后操作时间 */
	private Long lastTime;
	/** 项目排序 */
	private Integer sorts;
	/** 拓展信息String|String(JsonArray)|String(JsonObject) */
	private String extensions;

	/**
	 * 获取项目的id
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置项目的id
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public String getVersions() {
		return versions;
	}

	/**
	 * 设置版本号
	 * 
	 * @param versions
	 */
	public void setVersions(String versions) {
		this.versions = versions;
	}

	/**
	 * 获取描述
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置描述
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取服务集
	 * 
	 * @return
	 */
	public String getServers() {
		return servers;
	}

	/**
	 * 设置服务集
	 * 
	 * @param servers
	 */
	public void setServers(String servers) {
		this.servers = servers;
	}

	/**
	 * 获取附加文档
	 * 
	 * @return
	 */
	public String getExternalDocs() {
		return externalDocs;
	}

	/**
	 * 设置附加文档
	 * 
	 * @param externalDocs
	 */
	public void setExternalDocs(String externalDocs) {
		this.externalDocs = externalDocs;
	}

	/**
	 * 获取联系人
	 * 
	 * @return
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * 设置联系人
	 * 
	 * @param contactName
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * 获取联系人信息
	 * 
	 * @return
	 */
	public String getContactInfo() {
		return contactInfo;
	}

	/**
	 * 设置联系人信息
	 * 
	 * @param contactInfo
	 */
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	/**
	 * 获取拓展信息
	 * 
	 * @return
	 */
	public String getExtensions() {
		return extensions;
	}

	/**
	 * 设置拓展信息
	 * 
	 * @param extensions
	 */
	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	/**
	 * 获取项目负责人
	 * 
	 * @return
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * 设置创建该项目的负责人
	 * 
	 * @param owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * 获取负责人信息
	 * 
	 * @return
	 */
	public Users getOwnerInfo() {
		return ownerInfo;
	}

	/**
	 * 设置负责人信息
	 * 
	 * @param ownerInfo
	 */
	public void setOwnerInfo(Users ownerInfo) {
		this.ownerInfo = ownerInfo;
	}

	/**
	 * 获取可以查看该项目的人
	 * 
	 * @return
	 */
	public String getOwners() {
		return owners;
	}

	/**
	 * 设置可以查看该项目的人
	 * 
	 * @param owners
	 */
	public void setOwners(String owners) {
		this.owners = owners;
	}

	/**
	 * 获取最后操作时间
	 * 
	 * @return
	 */
	public Long getLastTime() {
		return lastTime;
	}

	/**
	 * 设置最后操作时间
	 * 
	 * @param lastTime
	 */
	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}

	/**
	 * 获取项目排序
	 * 
	 * @return
	 */
	public Integer getSorts() {
		return sorts;
	}

	/**
	 * 设置项目排序
	 * 
	 * @param sorts
	 */
	public void setSorts(Integer sorts) {
		this.sorts = sorts;
	}

	@Override
	public String toString() {
		return "Project [key=" + key + ", name=" + name + ", versions=" + versions + ", description=" + description
				+ ", servers=" + servers + ", externalDocs=" + externalDocs + ", contactName=" + contactName + ", contactInfo="
				+ contactInfo + ", extensions=" + extensions + ", owner=" + owner + ", ownerInfo=" + ownerInfo + ", owners="
				+ owners + ", lastTime=" + lastTime + ", sorts=" + sorts + "]";
	}

	
}
