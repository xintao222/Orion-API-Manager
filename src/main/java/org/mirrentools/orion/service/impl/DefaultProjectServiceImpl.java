package org.mirrentools.orion.service.impl;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mirrentools.orion.common.ColumnsAPI;
import org.mirrentools.orion.common.ColumnsApiGroup;
import org.mirrentools.orion.common.ColumnsApiTemplate;
import org.mirrentools.orion.common.ColumnsProject;
import org.mirrentools.orion.common.ColumnsProjectShare;
import org.mirrentools.orion.common.LoginRole;
import org.mirrentools.orion.common.LoginSession;
import org.mirrentools.orion.common.OrionApiManager;
import org.mirrentools.orion.common.ResultCode;
import org.mirrentools.orion.common.ResultUtil;
import org.mirrentools.orion.common.SqlAssist;
import org.mirrentools.orion.common.StringUtil;
import org.mirrentools.orion.common.WebSocket;
import org.mirrentools.orion.entity.Project;
import org.mirrentools.orion.entity.ProjectApi;
import org.mirrentools.orion.entity.ProjectApiGroup;
import org.mirrentools.orion.entity.ProjectApiTemplate;
import org.mirrentools.orion.entity.ProjectInfo;
import org.mirrentools.orion.entity.ProjectShare;
import org.mirrentools.orion.entity.Users;
import org.mirrentools.orion.mapper.ProjectApiGroupMapper;
import org.mirrentools.orion.mapper.ProjectApiMapper;
import org.mirrentools.orion.mapper.ProjectApiTemplateMapper;
import org.mirrentools.orion.mapper.ProjectMapper;
import org.mirrentools.orion.mapper.ProjectShareMapper;
import org.mirrentools.orion.mapper.UsersMapper;
import org.mirrentools.orion.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目的服务接口的默认实现
 * 
 * @author <a href="https://mirrentools.org/">Mirren</a>
 *
 */
@Service
public class DefaultProjectServiceImpl implements ProjectService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultProjectServiceImpl.class);

	/** 项目相关的数据库操作 */
	@Autowired
	private ProjectMapper projectMapper;
	/** 项目分享相关的数据库操作 */
	@Autowired
	private ProjectShareMapper shareMapper;
	/** 接口分组相关的数据库操作 */
	@Autowired
	private ProjectApiGroupMapper groupMapper;
	/** 接口相关的数据库操作 */
	@Autowired
	private ProjectApiMapper apiMapper;
	/** 接口模板的数据库操作 */
	@Autowired
	private ProjectApiTemplateMapper templateMapper;
	/** 用户相关的数据库操作 */
	@Autowired
	private UsersMapper usersMapper;

	@Override
	public Map<String, Object> getProjectList(LoginSession loginSession) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			SqlAssist assist = new SqlAssist();
			assist.setResultColumn(String.format("%s,%s,%s,%s,%s,%s", ColumnsProject.KEY, ColumnsProject.OWNER, ColumnsProject.NAME,
					ColumnsProject.VERSIONS, ColumnsProject.LAST_TIME, ColumnsProject.SORTS));
			assist.setOrderBy(String.format("%s asc, %s desc", ColumnsProject.SORTS, ColumnsProject.LAST_TIME));
			List<Project> all;
			if (loginSession.getRole() == LoginRole.ROOT) {
				all = projectMapper.selectAll(assist);
			} else {
				if (loginSession.getTags() != null && !loginSession.getTags().isEmpty()) {
					for (String tag : loginSession.getTags()) {
						assist.orLike(ColumnsProject.OWNERS, "%" + tag + "%");
					}
				} else if (loginSession.getRole() == LoginRole.CLIENT) {
					return ResultUtil.format200(new ArrayList<>());
				}
				if (loginSession.getRole() == LoginRole.SERVER) {
					assist.orEq(ColumnsProject.OWNER, loginSession.getUid());
				}
				assist.orEq(ColumnsProject.OWNERS, "[]");
				all = projectMapper.selectAll(assist);
			}
			if (all == null) {
				return ResultUtil.format200(new ArrayList<>());
			}
			List<ProjectInfo> result = new ArrayList<ProjectInfo>();
			for (int i = 0; i < all.size(); i++) {
				Project project = all.get(i);
				ProjectInfo projectInfo = new ProjectInfo();
				projectInfo.setKey(project.getKey());
				projectInfo.setOwner(project.getOwner());
				projectInfo.setName(project.getName());
				projectInfo.setVersion(project.getVersions());
				projectInfo.setTime(project.getLastTime());
				projectInfo.setSorts(project.getSorts());
				result.add(projectInfo);
			}
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行获取项目列表-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getProject(LoginSession loginSession, String id) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			Project project = projectMapper.selectById(id);
			if (project != null) {
				if (loginSession.getRole() != LoginRole.ROOT) {
					if (!isProjectOwners(loginSession, project)) {
						return ResultUtil.format403();
					}
				}
				Users users = usersMapper.selectById(project.getOwner());
				if (users != null) {
					Users info = new Users();
					info.setUid(users.getUid());
					info.setNickname(users.getNickname());
					info.setContact(users.getContact());
					info.setSummary(users.getSummary());
					info.setLasttime(users.getLasttime());
					project.setOwnerInfo(info);
				}
			}
			return ResultUtil.format200(project);
		} catch (Throwable e) {
			LOG.error("执行获取项目->" + id + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}

	}

	@Override
	public Map<String, Object> saveProject(LoginSession loginSession, Project project) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(project.getServers(), project.getName())) {
				return ResultUtil.format(ResultCode.R412, "存在空值,name与host都为必填");
			}
			if (StringUtil.isNullOrEmpty(project.getKey())) {
				project.setKey(UUID.randomUUID().toString());
			}
			if (project.getSorts() == null) {
				project.setSorts(0);
			}
			if (loginSession.getRole() != LoginRole.ROOT) {
				project.setOwner(loginSession.getUid());
			}
			project.setLastTime(System.currentTimeMillis());
			int result = projectMapper.insertNotNull(project);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行保存项目->\n" + project + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> saveProjectfromJson(LoginSession loginSession, String json) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(json)) {
				return ResultUtil.format(ResultCode.R412, "project不能为空,应为项目的json字符串!");
			}
			JSONObject data = new JSONObject(json);
			if (StringUtil.isNullOrEmpty(data.getString("name"), data.getString("servers"))) {
				return ResultUtil.format(ResultCode.R412, "项目名称,服务集不能为空!");
			}
			Project project = convertProject(data);
			if (StringUtil.isNullOrEmpty(project.getKey())) {
				project.setKey(UUID.randomUUID().toString());
			}
			if (project.getSorts() == null) {
				project.setSorts(0);
			}
			if (loginSession.getRole() != LoginRole.ROOT) {
				project.setOwner(loginSession.getUid());
			}
			project.setLastTime(System.currentTimeMillis());
			int saveProject = projectMapper.insertNotNull(project);
			if (saveProject > 0) {
				if (data.has("content")) {
					JSONArray array = data.getJSONArray("content");
					for (int i = 0; i < array.length(); i++) {
						JSONObject gdata = array.getJSONObject(i);
						ProjectApiGroup group = convertGroup(project.getKey(), gdata);
						int saveGroup = groupMapper.insertNotNull(group);
						if (saveGroup > 0 && gdata.has("apis")) {
							JSONArray apis = gdata.getJSONArray("apis");
							for (int j = 0; j < apis.length(); j++) {
								JSONObject adata = apis.getJSONObject(j);
								ProjectApi api = convertApi(group.getGroupId(), adata);
								apiMapper.insertNotNull(api);
							}
						}
					}
				}
				return ResultUtil.format200(1);
			} else {
				return ResultUtil.format(ResultCode.R202, "操作成功,但需要你检查是否完成");
			}
		} catch (Exception e) {
			LOG.error("执行通过导入json并保存项目->\n" + json + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public void saveProjectfromJsonWebSocket(LoginSession loginSession, String json, Session session) {
		Async remote = session.getAsyncRemote();

		try {
			if (StringUtil.isNullOrEmpty(json)) {
				remote.sendText(WebSocket.failed(WebSocket.CHECK_PROJECT_JSON));
				return;
			}
			if (checkSession(loginSession)) {
				remote.sendText(WebSocket.failed401());
				return;
			}
			JSONObject data = new JSONObject(json);
			if (StringUtil.isNullOrEmpty(data.getString("name"), data.getString("servers"))) {
				if (session != null && session.isOpen()) {
					remote.sendText(WebSocket.failed(WebSocket.CHECK_PROJECT_NAME_SERVERS));
				}
				return;
			}
			Project project = convertProject(data);
			if (session != null && session.isOpen()) {
				remote.sendText(WebSocket.success(WebSocket.PROJECT_SAVEING, project.getName()));
			}
			if (StringUtil.isNullOrEmpty(project.getKey())) {
				project.setKey(UUID.randomUUID().toString());
			}
			if (project.getSorts() == null) {
				project.setSorts(0);
			}
			if (loginSession.getRole() != LoginRole.ROOT) {
				project.setOwner(loginSession.getUid());
			}
			project.setLastTime(System.currentTimeMillis());
			int saveProject = projectMapper.insertNotNull(project);
			if (saveProject > 0) {
				if (session != null && session.isOpen()) {
					remote.sendText(WebSocket.success(WebSocket.PROJECT_SAVED, project.getName()));
				}
				if (data.has("content")) {
					JSONArray groups = data.getJSONArray("content");
					for (int i = 0; i < groups.length(); i++) {
						JSONObject gdata = groups.getJSONObject(i);
						ProjectApiGroup group = convertGroup(project.getKey(), gdata);
						int saveGroup = groupMapper.insertNotNull(group);
						if (session != null && session.isOpen()) {
							remote.sendText(
									WebSocket.success(WebSocket.GROUP_SAVED, WebSocket.progressModel(group.getName(), (i + 1), groups.length(), saveGroup)));
						}
						if (saveGroup > 0 && gdata.has("apis")) {
							JSONArray apis = gdata.getJSONArray("apis");
							for (int j = 0; j < apis.length(); j++) {
								JSONObject adata = apis.getJSONObject(j);
								ProjectApi api = convertApi(group.getGroupId(), adata);
								int saveApi = apiMapper.insertNotNull(api);
								if (session != null && session.isOpen()) {
									remote.sendText(
											WebSocket.success(WebSocket.API_SAVED, WebSocket.progressModel(api.getTitle(), (j + 1), apis.length(), saveApi)));
								}
							}
						}
					}
				}
			} else {
				if (session != null && session.isOpen()) {
					remote.sendText(WebSocket.success(WebSocket.PROJECT_SAVE_EXCEPTION, project.getName()));
				}
			}
		} catch (Exception e) {
			LOG.error("执行通过Websocket导入json并保存项目->\n" + json + "\n-->失败:", e);
			if (session != null && session.isOpen()) {
				remote.sendText(WebSocket.failed(e.getMessage()));
			}
		}
		try {
			synchronized (remote) {
				if (session != null && session.isOpen()) {
					remote.sendText(WebSocket.end());
				}
			}
		} catch (Exception e) {
			synchronized (remote) {
				if (session != null && session.isOpen()) {
					remote.sendText(WebSocket.end());
				}
			}
		}
	}

	@Override
	public Map<String, Object> copyProject(LoginSession loginSession, String key) {
		try {
			if (StringUtil.isNullOrEmpty(key)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目的id为必填");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			Project project = projectMapper.selectById(key);
			if (project == null) {
				return ResultUtil.format404();
			}
			if (loginSession.getRole() != LoginRole.ROOT && !loginSession.getUid().equals(project.getOwner())) {
				return ResultUtil.format403();
			}
			project.setKey(UUID.randomUUID().toString());
			project.setName(project.getName() + "_副本");
			if (project.getSorts() == null) {
				project.setSorts(0);
			}
			project.setLastTime(System.currentTimeMillis());
			List<ProjectApiGroup> groups = groupMapper.selectAll(new SqlAssist().andEq(ColumnsApiGroup.PROJECT_ID, key));
			if (groups != null && !groups.isEmpty()) {
				for (ProjectApiGroup g : groups) {
					String gid = new String(g.getGroupId());
					g.setProjectId(project.getKey());
					g.setGroupId(UUID.randomUUID().toString());
					groupMapper.insertNotNull(g);
					List<ProjectApi> apiList = apiMapper.selectAll(new SqlAssist().andEq(ColumnsAPI.GROUP_ID, gid));
					if (apiList != null && !apiList.isEmpty()) {
						for (ProjectApi api : apiList) {
							api.setGroupId(g.getGroupId());
							api.setApiId(UUID.randomUUID().toString());
							apiMapper.insertNotNull(api);
						}
					}
				}
			}
			int result = projectMapper.insertNotNull(project);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行复制项目->\n" + key + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> updateProject(LoginSession loginSession, Project project) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(project.getKey(), project.getServers(), project.getName())) {
				return ResultUtil.format(ResultCode.R412, "存在空值,host,name,key都为必填");
			}
			if (!isProjectOwner(loginSession, project.getKey())) {
				return ResultUtil.format403();
			}
			if (project.getSorts() == null) {
				project.setSorts(0);
			}
			project.setLastTime(System.currentTimeMillis());
			int result = projectMapper.updateNotNullById(project);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行更新项目->\n" + project + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> projectMoveUp(String key) {
		try {
			if (StringUtil.isNullOrEmpty(key)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目的id不能为空");
			}
			int result = projectMapper.updateProjectMoveUp(key);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行将项目上移动->" + key + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> projectMoveDown(String key) {
		try {
			if (StringUtil.isNullOrEmpty(key)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目的id不能为空");
			}
			int result = projectMapper.updateProjectMoveDown(key);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行将项目下移动->" + key + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> deleteProject(LoginSession loginSession, String key) {
		try {
			if (StringUtil.isNullOrEmpty(key)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目的id不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (!isProjectOwner(loginSession, key)) {
				return ResultUtil.format403();
			}
			List<ProjectApiGroup> gids = groupMapper
					.selectAll(new SqlAssist().setResultColumn(ColumnsApiGroup.GROUP_ID).andEq(ColumnsApiGroup.PROJECT_ID, key));
			if (gids != null) {
				for (ProjectApiGroup g : gids) {
					// 删除API
					apiMapper.deleteByAssist(new SqlAssist().andEq(ColumnsAPI.GROUP_ID, g.getGroupId()));
					// 删除分组
					groupMapper.deleteById(g.getGroupId());
				}
			}
			// 删除分享记录
			shareMapper.deleteByAssist(new SqlAssist().andEq(ColumnsProjectShare.PID, key));
			// 删除项目
			int result = projectMapper.deleteById(key);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行删除项目->" + key + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> findProjectShare(LoginSession loginSession, String key) {
		try {
			if (StringUtil.isNullOrEmpty(key)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目的id不能为空");
			}
			SqlAssist assist = new SqlAssist().andEq(ColumnsProjectShare.PID, key);
			assist.setOrderBy(ColumnsProjectShare.SHARE_TIME + " DESC ");
			List<ProjectShare> result = shareMapper.selectAll(assist);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行获取项目分享记录->" + key + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> saveProjectShare(LoginSession loginSession, ProjectShare share) {
		try {
			if (share == null || StringUtil.isNullOrEmpty(share.getPid(), share.getPwd())) {
				return ResultUtil.format(ResultCode.R412, "存在空值,pid,pwd不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (!isProjectOwner(loginSession, share.getPid())) {
				return ResultUtil.format403();
			}
			String pwd = share.getPwd();
			if (pwd.length() < 4 || pwd.length() > 32) {
				return ResultUtil.format(ResultCode.R413, "无效参数,分享密码长度无效");
			}
			String sid = System.currentTimeMillis() + StringUtil.randomString(4);
			share.setSid(sid);
			share.setShareTime(System.currentTimeMillis());
			int result = shareMapper.insertNotNull(share);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行新增项目分享->" + share + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> updateProjectShare(LoginSession loginSession, ProjectShare share) {
		try {
			if (share == null || StringUtil.isNullOrEmpty(share.getSid(), share.getPwd())) {
				return ResultUtil.format(ResultCode.R412, "存在空值,sid,pid,pwd不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (!isProjectOwner(loginSession, share.getPid())) {
				return ResultUtil.format403();
			}
			String pwd = share.getPwd();
			if (pwd.length() < 4 || pwd.length() > 32) {
				return ResultUtil.format(ResultCode.R413, "无效参数,分享密码长度无效");
			}
			share.setShareTime(null);
			int result = shareMapper.updateNotNullById(share);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行修改项目分享->" + share + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> deleteProjectShare(LoginSession loginSession, String sid) {
		try {
			if (StringUtil.isNullOrEmpty(sid)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目分享的id不能为空");
			}
			int result = shareMapper.deleteById(sid);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行删除项目分享->" + sid + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}

	}

	@Override
	public Map<String, Object> getApiGroupList(LoginSession loginSession, String projectId) {
		try {
			if (StringUtil.isNullOrEmpty(projectId)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目的id不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			Project project = projectMapper.selectById(projectId);
			if (project == null) {
				return ResultUtil.format404();
			}
			if (!isProjectOwners(loginSession, project)) {
				return ResultUtil.format403();
			}
			List<ProjectApiGroup> result = getProjectApiGroupList(projectId, false);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行获取接口分组列表包含分组信息->项目:" + projectId + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getApiGroup(LoginSession loginSession, String groupId) {
		try {
			if (StringUtil.isNullOrEmpty(groupId)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,项目的id不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			ProjectApiGroup result = groupMapper.selectById(groupId);
			if (result != null) {
				Project project = projectMapper.selectById(result.getProjectId());
				if (project == null) {
					return ResultUtil.format404();
				}
				if (!isProjectOwners(loginSession, project)) {
					return ResultUtil.format403();
				}
			}
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行获取指定接口分组信息->" + groupId + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> saveApiGroup(LoginSession loginSession, ProjectApiGroup group) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(group.getProjectId(), group.getName(), group.getSummary())) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组的名称与简介不能为空");
			}
			if (!isProjectOwner(loginSession, group.getProjectId())) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(group.getGroupId())) {
				group.setGroupId(UUID.randomUUID().toString());
			}
			if (group.getSorts() == null) {
				group.setSorts(0);
			}
			int result = groupMapper.insertNotNull(group);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行保存接口分组->\n" + group + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> updateApiGroup(LoginSession loginSession, ProjectApiGroup group) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(group.getGroupId(), group.getProjectId())) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组与项目的id不能为空");
			}
			if (!isProjectOwner(loginSession, group.getProjectId())) {
				return ResultUtil.format403();
			}
			if (group.getSorts() == null) {
				group.setSorts(0);
			}
			int result = groupMapper.updateNotNullById(group);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行修改接口分组->\n" + group + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> moveUpApiGroup(String id) {
		try {
			if (StringUtil.isNullOrEmpty(id)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组的id不能为空");
			}
			int result = groupMapper.updateProjectApiGroupMoveUp(id);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行将接口分组下移动->" + id + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> moveDownApiGroup(String id) {
		try {
			if (StringUtil.isNullOrEmpty(id)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组的id不能为空");
			}
			int result = groupMapper.updateProjectApiGroupMoveDown(id);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行将接口分组上移动->" + id + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> deleteApiGroup(LoginSession loginSession, String groupId) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(groupId)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组的id不能为空");
			}
			ProjectApiGroup group = groupMapper.selectById(groupId);
			if (group == null || group.getProjectId() == null) {
				return ResultUtil.format404();
			}
			if (!isProjectOwner(loginSession, group.getProjectId())) {
				return ResultUtil.format403();
			}
			apiMapper.deleteByAssist(new SqlAssist().andEq(ColumnsAPI.GROUP_ID, groupId));
			int result = groupMapper.deleteById(groupId);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行删除接口分组->" + groupId + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> saveApi(LoginSession loginSession, ProjectApi api) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(api.getGroupId(), api.getMethod(), api.getPath(), api.getTitle())) {
				return ResultUtil.format(ResultCode.R412, "存在空值");
			}
			ProjectApiGroup group = groupMapper.selectById(api.getGroupId());
			if (group == null) {
				return ResultUtil.format(ResultCode.R412, "分组的id无效");
			}
			if (!isProjectOwner(loginSession, group.getProjectId())) {
				return ResultUtil.format403();
			}
			if (api.getDeprecated() == null) {
				api.setDeprecated(Boolean.toString(false));
			}
			if (StringUtil.isNullOrEmpty(api.getApiId())) {
				api.setApiId(UUID.randomUUID().toString());
			}
			if (api.getSorts() == null) {
				api.setSorts(0);
			}
			int result = apiMapper.insertNotNull(api);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行新增接口->\n" + api + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> findApis(LoginSession loginSession, String groupId) {
		try {
			if (StringUtil.isNullOrEmpty(groupId)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组id不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (loginSession.getRole() != LoginRole.ROOT) {
				ProjectApiGroup group = groupMapper.selectById(groupId);
				if (group == null || group.getProjectId() == null) {
					return ResultUtil.format404();
				}
				Project project = projectMapper.selectById(group.getProjectId());
				if (project == null) {
					return ResultUtil.format404();
				}
				if (!isProjectOwners(loginSession, project)) {
					return ResultUtil.format403();
				}
			}
			SqlAssist assist = new SqlAssist();
			assist.andEq(ColumnsAPI.GROUP_ID, groupId);
			assist.setOrderBy(ColumnsAPI.SORTS);
			List<ProjectApi> result = apiMapper.selectAll(assist);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行获取指定分组接口列表->" + groupId + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getApi(LoginSession loginSession, String apiId) {
		try {

			if (StringUtil.isNullOrEmpty(apiId)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,id不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			Project project = getProjectByApiId(apiId);
			if (project == null) {
				return ResultUtil.format404();
			}
			if (!isProjectOwners(loginSession, project)) {
				return ResultUtil.format403();
			}
			ProjectApi result = apiMapper.selectById(apiId);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行获取指定接口->" + apiId + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> copyApi(LoginSession loginSession, String apiId) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(apiId)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,id不能为空");
			}
			ProjectApi api = apiMapper.selectById(apiId);
			if (api == null || api.getGroupId() == null) {
				return ResultUtil.format(ResultCode.R404);
			}
			ProjectApiGroup group = groupMapper.selectById(api.getGroupId());
			if (group == null || group.getProjectId() == null) {
				return ResultUtil.format404();
			}
			if (!isProjectOwner(loginSession, group.getProjectId())) {
				return ResultUtil.format403();
			}
			api.setApiId(UUID.randomUUID().toString());
			String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd hh:mm:ss"));
			api.setTitle(api.getTitle() + "-copy-" + now);
			if (api.getSorts() == null) {
				api.setSorts(0);
			}
			int result = apiMapper.insertNotNull(api);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行复制接口->\n" + apiId + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> updateApi(LoginSession loginSession, ProjectApi api) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(api.getApiId())) {
				return ResultUtil.format(ResultCode.R412, "存在空值,id不能为空");
			}
			ProjectApiGroup group = groupMapper.selectById(api.getGroupId());
			if (group == null || group.getProjectId() == null) {
				return ResultUtil.format404();
			}
			if (!isProjectOwner(loginSession, group.getProjectId())) {
				return ResultUtil.format403();
			}
			int result = apiMapper.updateNotNullById(api);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行修改接口->\n" + api + "\n-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> moveUpApi(String id) {
		try {
			if (StringUtil.isNullOrEmpty(id)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组的id不能为空");
			}
			int result = apiMapper.updateProjectApiMoveUp(id);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行将接口上移动->" + id + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> moveDownApi(String id) {
		try {
			if (StringUtil.isNullOrEmpty(id)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,分组的id不能为空");
			}
			int result = apiMapper.updateProjectApiMoveDown(id);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行将接口下移动->" + id + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> deleteApi(LoginSession loginSession, String apiId) {
		try {
			if (StringUtil.isNullOrEmpty(apiId)) {
				return ResultUtil.format(ResultCode.R412, "存在空值,id不能为空");
			}
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (loginSession.getRole() != LoginRole.ROOT) {
				Project project = getProjectByApiId(apiId);
				if (project == null) {
					return ResultUtil.format403();
				}
				if (!loginSession.getUid().equals(project.getOwner())) {
					return ResultUtil.format403();
				}
			}
			int result = apiMapper.deleteById(apiId);
			return ResultUtil.format200(result);
		} catch (Throwable e) {
			LOG.error("执行删除接口->" + apiId + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> findApiTemplateList(LoginSession loginSession) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			SqlAssist assist = new SqlAssist();
			assist.andEq(ColumnsApiTemplate.UID, loginSession.getUid());
			assist.setOrderBy(ColumnsApiTemplate.CTIME + " desc");
			assist.setResultColumn(ColumnsApiTemplate.TID + "," + ColumnsApiTemplate.NAME);
			List<ProjectApiTemplate> result = templateMapper.selectAll(assist);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行获取接口模板列表-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getApiTemplate(LoginSession loginSession, String tid) {
		try {
			if (StringUtil.isNullOrEmpty(tid)) {
				return ResultUtil.format(ResultCode.R412);
			}
			ProjectApiTemplate result = templateMapper.selectById(tid);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行获取接口模板->" + tid + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> postApiTemplate(LoginSession loginSession, ProjectApiTemplate template) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (template == null || StringUtil.isNullOrEmpty(template.getName())) {
				return ResultUtil.format(ResultCode.R412);
			}
			template.setTid(System.currentTimeMillis() + "-" + new Random().nextInt(1000));
			template.setUid(loginSession.getUid());
			template.setCtime(System.currentTimeMillis());
			int result = templateMapper.insertNotNull(template);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行新增接口模板->" + template + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> deleteApiTemplate(LoginSession loginSession, String tid) {
		try {
			if (checkSession(loginSession)) {
				return ResultUtil.format403();
			}
			if (StringUtil.isNullOrEmpty(tid)) {
				return ResultUtil.format(ResultCode.R412);
			}
			SqlAssist assist = new SqlAssist().andEq(ColumnsApiTemplate.UID, loginSession.getUid()).andEq(ColumnsApiTemplate.TID, tid);
			System.out.println(assist);
			int result = templateMapper.deleteByAssist(assist);
			return ResultUtil.format200(result);
		} catch (Exception e) {
			LOG.error("执行删除接口模板->" + tid + "-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getJsonByShare(String sid, String pwd) {
		try {
			ProjectShare share = shareMapper.selectById(sid);
			if (share == null || StringUtil.isNullOrEmpty(share.getPid())) {
				return ResultUtil.format(ResultCode.R404);
			}
			if (!Objects.equals(pwd, share.getPwd())) {
				return ResultUtil.format(ResultCode.R405);
			}
			Project project = projectMapper.selectById(share.getPid());
			if (project == null) {
				return ResultUtil.format(ResultCode.R404);
			}
			JSONObject result = convertProjectToJson(project);
			return ResultUtil.format200(result.toString());
		} catch (Throwable e) {
			LOG.error("执行获取项目分享记录-->失败:", e);
			return ResultUtil.format(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public String getJson(LoginSession loginSession, String projectId) {
		try {
			if (checkSession(loginSession) || StringUtil.isNullOrEmpty(projectId)) {
				return ResultUtil.formatAsString(ResultCode.R412);
			}
			Project project = projectMapper.selectById(projectId);
			if (project == null) {
				return ResultUtil.formatAsString(ResultCode.R404);
			}
			if (!Objects.equals("[]", project.getOwners())) {
				if (!isProjectOwners(loginSession, project)) {
					return ResultUtil.formatAsString(ResultCode.R403);
				}
			}
			JSONObject result = convertProjectToJson(project);
			return result.toString(2);
		} catch (Throwable e) {
			LOG.error("执行获取项目并转化为JSON->" + projectId + "-->失败:", e);
			return ResultUtil.formatAsString(ResultCode.R555, e.getMessage());
		}
	}

	@Override
	public void downJson(HttpServletResponse response, LoginSession loginSession, String projectId) {
		try {
			String result = getJson(loginSession, projectId);
			response.setContentType("application/force-download;charset=UTF-8");
			String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss"));
			String fileName = "API-DATA-" + time + ".json";
			response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
			try (PrintWriter writer = response.getWriter()) {
				writer.write(result);
			}
		} catch (Throwable e) {
			LOG.error("执行下载JSON文件->" + projectId + "-->失败:", e);
		}
	}

	/**
	 * 获取指定项目的所有分组
	 * 
	 * @param projectId
	 * @param getApis
	 *          是否包括分组的接口,true包括,false不包括
	 * @return
	 */
	private List<ProjectApiGroup> getProjectApiGroupList(String projectId, boolean getApis) {
		SqlAssist gAssist = new SqlAssist().andEq(ColumnsApiGroup.PROJECT_ID, projectId).setOrderBy(ColumnsApiGroup.SORTS);
		List<ProjectApiGroup> groups = groupMapper.selectAll(gAssist);
		if (groups != null) {
			for (int i = 0; i < groups.size(); i++) {
				ProjectApiGroup group = groups.get(i);
				if (getApis) {
					String gid = group.getGroupId();
					SqlAssist assist = new SqlAssist().andEq(ColumnsAPI.GROUP_ID, gid).setOrderBy(ColumnsAPI.SORTS);
					List<ProjectApi> apiList = apiMapper.selectAll(assist);
					group.setApis(apiList);
				}
			}
		}
		return groups;
	}

	/**
	 * 转换项目信息
	 * 
	 * @param data
	 * @return
	 */
	private Project convertProject(JSONObject data) {
		Project project = new Project();
		project.setKey(UUID.randomUUID().toString());
		project.setName(data.getString("name"));
		project.setServers(data.getString("servers"));
		project.setSorts(data.has("sorts") ? data.getInt("sorts") : 0);
		project.setVersions(data.has("versions") ? data.getString("versions") : null);
		project.setDescription(data.has("description") ? data.getString("description") : null);
		project.setExternalDocs(data.has("externalDocs") ? data.getString("externalDocs") : null);
		project.setContactName(data.has("contactName") ? data.getString("contactName") : null);
		project.setContactInfo(data.has("contactInfo") ? data.getString("contactInfo") : null);
		project.setLastTime(System.currentTimeMillis());
		return project;
	}

	/**
	 * 将项目转换为JSON
	 * 
	 * @param project
	 * @return
	 */
	private JSONObject convertProjectToJson(Project project) {
		JSONObject result = new JSONObject();
		result.put("orionApi", OrionApiManager.VERSION);
		result.put("key", project.getKey());
		result.put("name", project.getName());
		result.put("versions", project.getVersions());
		result.put("description", project.getDescription());
		try {
			result.put("servers", new JSONArray(project.getServers()));
		} catch (Exception e) {
			result.put("servers", project.getServers());
		}
		result.put("contactName", project.getContactName());
		result.put("contactInfo", project.getContactInfo());
		if (project.getLastTime() != null) {
			result.put("lastTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(project.getLastTime())));
		}
		if (project.getExternalDocs() != null) {
			try {
				result.put("externalDocs", new JSONObject(project.getExternalDocs()));
			} catch (Exception e) {
				result.put("externalDocs", project.getExternalDocs());
				e.printStackTrace();
			}
		}
		if (project.getExtensions() != null) {
			try {
				result.put("extensions", new JSONObject(project.getExtensions()));
			} catch (Exception e) {
				try {
					result.put("extensions", new JSONArray(project.getExtensions()));
				} catch (Exception e1) {
					result.put("extensions", project.getExtensions());
				}
			}
		}
		List<ProjectApiGroup> list = getProjectApiGroupList(project.getKey(), true);
		if (list != null && !list.isEmpty()) {
			JSONArray content = new JSONArray();
			for (ProjectApiGroup pag : list) {
				JSONObject group = new JSONObject();
				group.put("groupId", pag.getGroupId());
				group.put("projectId", pag.getProjectId());
				group.put("name", pag.getName());
				group.put("summary", pag.getSummary());
				group.put("description", pag.getDescription());
				if (pag.getExternalDocs() != null) {
					try {
						group.put("externalDocs", new JSONObject(pag.getExternalDocs()));
					} catch (Exception e) {
						group.put("externalDocs", pag.getExternalDocs());
						e.printStackTrace();
					}
				}
				if (pag.getExtensions() != null) {
					try {
						group.put("extensions", new JSONObject(pag.getExtensions()));
					} catch (Exception e) {
						group.put("extensions", pag.getExtensions());
						e.printStackTrace();
					}
				}
				JSONArray apis = new JSONArray();
				if (pag.getApis() != null) {
					for (ProjectApi pa : pag.getApis()) {
						JSONObject api = new JSONObject();
						api.put("apiId", pa.getApiId());
						api.put("groupId", pa.getGroupId());
						api.put("title", pa.getTitle());
						api.put("method", pa.getMethod());
						api.put("path", pa.getPath());
						api.put("description", pa.getDescription());
						api.put("deprecated", "true".equals(pa.getDeprecated()) ? true : false);
						if (pa.getConsumes() != null) {
							try {
								api.put("consumes", new JSONArray(pa.getConsumes()));
							} catch (Exception e) {
								api.put("consumes", pa.getConsumes());
								e.printStackTrace();
							}
						}
						if (pa.getParameters() != null) {
							try {
								api.put("parameters", new JSONArray(pa.getParameters()));
							} catch (Exception e) {
								api.put("parameters", pa.getParameters());
								e.printStackTrace();
							}
						}
						api.put("body", pa.getBody());
						if (pa.getProduces() != null) {
							try {
								api.put("produces", new JSONArray(pa.getProduces()));
							} catch (Exception e) {
								api.put("produces", pa.getProduces());
								e.printStackTrace();
							}
						}

						if (pa.getResponses() != null) {
							try {
								api.put("responses", new JSONArray(pa.getResponses()));
							} catch (Exception e) {
								api.put("responses", pa.getResponses());
								e.printStackTrace();
							}
						}
						if (pa.getAdditional() != null) {
							try {
								api.put("additional", new JSONArray(pa.getAdditional()));
							} catch (Exception e) {
								api.put("additional", pa.getAdditional());
								e.printStackTrace();
							}
						}
						if (pa.getExternalDocs() != null) {
							try {
								api.put("externalDocs", new JSONObject(pa.getExternalDocs()));
							} catch (Exception e) {
								api.put("externalDocs", pa.getExternalDocs());
								e.printStackTrace();
							}
						}
						if (pa.getExtensions() != null) {
							try {
								api.put("extensions", new JSONObject(pa.getExtensions()));
							} catch (Exception e) {
								try {
									api.put("extensions", new JSONArray(pa.getExtensions()));
								} catch (Exception e1) {
									api.put("extensions", pa.getExtensions());
									e1.printStackTrace();
								}
							}
						}
						apis.put(api);
					}
				}
				group.put("apis", apis);
				content.put(group);
			}
			result.put("content", content);
		}
		return result;
	}

	/**
	 * 转换分组信息
	 * 
	 * @param projectId
	 * @param gdata
	 * @return
	 */
	private ProjectApiGroup convertGroup(String projectId, JSONObject gdata) {
		ProjectApiGroup group = new ProjectApiGroup();
		group.setProjectId(projectId);
		group.setGroupId(UUID.randomUUID().toString());
		group.setSorts(gdata.has("sort") ? gdata.getInt("sort") : 0);
		group.setName(gdata.has("name") ? gdata.getString("name") : "group");
		group.setSummary(gdata.has("summary") ? gdata.getString("summary") : "summary");
		group.setDescription(gdata.has("description") ? gdata.getString("description") : null);
		group.setExternalDocs(gdata.has("externalDocs") ? gdata.getString("externalDocs") : null);
		return group;
	}

	/**
	 * 转换API信息
	 * 
	 * @param groupId
	 * @param adata
	 * @return
	 */
	private ProjectApi convertApi(String groupId, JSONObject adata) {
		ProjectApi api = new ProjectApi();
		api.setApiId(adata.has("apiId") ? adata.getString("apiId") : UUID.randomUUID().toString());
		api.setGroupId(groupId);
		api.setMethod(adata.has("method") ? adata.getString("method") : "get");
		api.setPath(adata.has("path") ? adata.getString("path") : "path");
		api.setTitle(adata.has("title") ? adata.getString("title") : "title");
		api.setDescription(adata.has("description") ? adata.getString("description") : null);
		api.setConsumes(adata.has("consumes") ? adata.getString("consumes") : null);
		api.setParameters(adata.has("parameters") ? adata.getString("parameters") : null);
		api.setBody(adata.has("body")?adata.getString("body"):null);
		api.setProduces(adata.has("produces") ? adata.getString("produces") : null);
		api.setResponses(adata.has("responses") ? adata.getString("responses") : null);
		api.setDeprecated(adata.has("deprecated") ? adata.getString("deprecated") : null);
		api.setExternalDocs(adata.has("externalDocs") ? adata.getString("externalDocs") : null);
		api.setExtensions(adata.has("extensions") ? adata.getString("extensions") : null);
		api.setSorts(adata.has("sorts") ? adata.getInt("sorts") : 0);
		return api;
	}

	/**
	 * 检查用户是否不满足条件
	 * 
	 * @param loginSession
	 * @return
	 */
	private boolean checkSession(LoginSession loginSession) {
		return loginSession == null || loginSession.getUid() == null || loginSession.getRole() == null;
	}

	/**
	 * 通过API的id获取项目信息
	 * 
	 * @param aid
	 * @return
	 */
	private Project getProjectByApiId(String aid) {
		SqlAssist assist = new SqlAssist();
		String in = String.format(" %s IN (SELECT g.%s FROM %s g INNER JOIN %s a ON g.%s=a.%s WHERE a.%s=", ColumnsProject.KEY,
				ColumnsApiGroup.PROJECT_ID, ColumnsApiGroup.TABLE_NAME, ColumnsAPI.TABLE_NAME, ColumnsApiGroup.GROUP_ID, ColumnsAPI.GROUP_ID,
				ColumnsAPI.API_ID);
		assist.setCondition(SqlAssist.whereCondition(in, aid, ")"));
		List<Project> all = projectMapper.selectAll(assist);
		if (all != null && all.size() >= 1) {
			return all.get(0);
		}
		return null;
	}

	/**
	 * 检查用户是否是项目的负责人
	 * 
	 * @param loginSession
	 * @param projectId
	 * @return
	 */
	private boolean isProjectOwner(LoginSession loginSession, String projectId) {
		if (loginSession.getRole() == LoginRole.ROOT) {
			return true;
		}
		Project project = projectMapper.selectById(projectId);
		if (project == null || project.getOwner() == null) {
			return false;
		}
		return project.getOwner().equals(loginSession.getUid());
	}

	/**
	 * 判断用户是否是可以访问项目的成员
	 * 
	 * @param loginSession
	 * @param project
	 * @return
	 */
	private boolean isProjectOwners(LoginSession loginSession, Project project) {
		if (loginSession.getRole() == LoginRole.ROOT) {
			return true;
		}
		if (loginSession.getUid().equals(project.getOwner())) {
			return true;
		}
		if (project.getOwners() == null || (loginSession.getTags() == null || loginSession.getTags().isEmpty())) {
			return false;
		}
		JSONArray array = new JSONArray(project.getOwners());
		for (int i = 0; i < array.length(); i++) {
			String tag = array.getString(i);
			if (loginSession.getTags().contains(tag)) {
				return true;
			}
		}
		return false;
	}

}
