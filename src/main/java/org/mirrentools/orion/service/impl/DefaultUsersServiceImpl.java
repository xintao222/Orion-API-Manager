package org.mirrentools.orion.service.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.mirrentools.orion.common.ColumnsProject;
import org.mirrentools.orion.common.ColumnsTags;
import org.mirrentools.orion.common.ColumnsUsers;
import org.mirrentools.orion.common.LoginRole;
import org.mirrentools.orion.common.LoginSession;
import org.mirrentools.orion.common.LoginSessionStore;
import org.mirrentools.orion.common.MD5Util;
import org.mirrentools.orion.common.ResultUtil;
import org.mirrentools.orion.common.SqlAssist;
import org.mirrentools.orion.common.SqlAssist.LimitResult;
import org.mirrentools.orion.common.StringUtil;
import org.mirrentools.orion.entity.Tags;
import org.mirrentools.orion.entity.Users;
import org.mirrentools.orion.mapper.ProjectMapper;
import org.mirrentools.orion.mapper.TagsMapper;
import org.mirrentools.orion.mapper.UsersMapper;
import org.mirrentools.orion.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户相关的服务接口默认实现
 * 
 * @author <a href="https://mirrentools.org/">Mirren</a>
 *
 */
@Service
public class DefaultUsersServiceImpl implements UsersService {
	private static final Logger LOG = LogManager.getLogger(DefaultUsersServiceImpl.class);
	/** 用户的SQL */
	private UsersMapper usersMapper;
	/** 标签的SQL */
	@Autowired
	private TagsMapper tagsMapper;
	/** 项目的SQL */
	private ProjectMapper projectMapper;

	@Override
	public Map<String, Object> login(String id, String pwd) {
		if (StringUtil.isNullOrEmpty(id, pwd)) {
			return ResultUtil.failed("登录失败,账号与密码不能为空!", 0);
		}
		if (id.startsWith("X-")) {
			try {
				byte[] bytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/config/user.json"));
				JSONObject users = new JSONObject(new String(bytes));
				if (users.has(id)) {
					JSONObject user = users.getJSONObject(id);
					if (user.has("pwd") && MD5Util.compare(MD5Util.encode(user.getString(pwd), 2), pwd)) {
						String sessionId = MD5Util.encode(UUID.randomUUID().toString(), 3);
						LoginSessionStore.save(sessionId, id, LoginRole.ROOT);
						Map<String, String> result = new HashMap<>();
						result.put("uid", id);
						result.put("sessionId", sessionId);
						result.put("role", LoginRole.ROOT.name());
						return ResultUtil.succeed(result);
					} else {
						return ResultUtil.failed("登录失败,账号或密码错误!", 0);
					}
				} else {
					return ResultUtil.failed("登录失败,账号或密码错误!", 0);
				}
			} catch (Exception e) {
				LOG.error("执行ROOT用户登录-->失败:" + e);
				return ResultUtil.failed(e.getMessage(), 0);
			}
		} else {
			try {
				Users users = usersMapper.selectById(id);
				if (users == null || users.getUid() == null) {
					return ResultUtil.failed("登录失败,账号或密码错误!", 0);
				} else {
					if (MD5Util.compare(users.getPwd(), MD5Util.encode(pwd))) {
						String sessionId = MD5Util.encode(UUID.randomUUID().toString(), 3);
						LoginSessionStore.save(sessionId, id, LoginRole.ROOT);
						Map<String, String> result = new HashMap<>();
						result.put("uid", id);
						result.put("sessionId", sessionId);
						result.put("role", LoginRole.valueOf(users.getRole()).name());
						// 更新最后登录时间
						Users user = new Users();
						user.setUid(id);
						user.setLasttime(System.currentTimeMillis());
						usersMapper.updateNotNullById(user);
						return ResultUtil.succeed(result);
					} else {
						return ResultUtil.failed("登录失败,账号或密码错误!", 0);
					}
				}
			} catch (Exception e) {
				LOG.error("执行用户登录-->失败:" + e);
				return ResultUtil.failed("登录失败,请稍后重试", 0);
			}
		}
	}

	@Override
	public Map<String, Object> logout(String sessionId) {
		try {
			LoginSessionStore.remove(sessionId);
			return ResultUtil.succeed(1);
		} catch (Exception e) {
			LOG.error("执行退出登录-->失败:" + e);
			return ResultUtil.succeed(1);
		}
	}

	@Override
	public Map<String, Object> findUsers(String keywords, String tid, Integer page, Integer size) {
		try {
			SqlAssist assist = new SqlAssist();
			if (!StringUtil.isNullOrEmpty(keywords)) {
				assist.andEq(ColumnsUsers.NICKNAME, "%" + keywords + "%");
				assist.andEq(ColumnsUsers.SUMMARY, "%" + keywords + "%");
			}
			if (!StringUtil.isNullOrEmpty(tid)) {
				assist.andEq(ColumnsUsers.TAGS, "%\"" + tid + "\"%");
			}
			assist.setPage(page).setRowSize(size);
			LimitResult<Users> result = usersMapper.limitAll(assist);
			return ResultUtil.succeed(result);
		} catch (Exception e) {
			LOG.error("执行获取用户列表-->失败:" + e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getUser(String uid) {
		try {
			if (StringUtil.isNullOrEmpty(uid)) {
				return ResultUtil.failed("用户的id不能为空");
			}
			Users users = usersMapper.selectById(uid);
			if (users != null) {
				users.setPwd(null);
			}
			return ResultUtil.succeed(users);
		} catch (Exception e) {
			LOG.error("执行获取指定用户-->失败:" + e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> postUser(String sessionId, Users user) {
		try {
			if (user == null || StringUtil.isNullOrEmpty(user.getUid(), user.getRole(), user.getPwd(), user.getNickname(), user.getContact())) {
				return ResultUtil.failed("请按要求填写所有必填项");
			}
			LoginRole userRole = getUserRole(sessionId);
			LoginRole role = LoginRole.valueOf(user.getRole());
			if (role != LoginRole.CLIENT) {
				if (role == LoginRole.SERVER) {
					if (userRole != LoginRole.ROOT) {
						return ResultUtil.failed("普通管理员无法创建管理员角色的用户!");
					}
				} else {
					return ResultUtil.failed("无效的角色!");
				}
			}
			Users checkUser = usersMapper.selectById(user.getUid());
			if (checkUser != null && checkUser.getUid() != null) {
				return ResultUtil.failed("该用户的账号已存在!");
			}
			user.setPwd(MD5Util.encode(user.getPwd()));
			int result = usersMapper.insertNotNull(user);
			return ResultUtil.succeed(result);
		} catch (Exception e) {
			LOG.error("执行新增用户->\n" + user + "\n-->失败:", e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> putUser(String sessionId, Users user) {
		try {
			if (user == null || StringUtil.isNullOrEmpty(user.getUid(), user.getRole(), user.getNickname(), user.getContact())) {
				return ResultUtil.failed("请按要求填写所有必填项");
			}
			LoginRole userRole = getUserRole(sessionId);
			LoginRole role = LoginRole.valueOf(user.getRole());
			if (role != LoginRole.CLIENT) {
				if (role == LoginRole.SERVER) {
					if (userRole != LoginRole.ROOT) {
						return ResultUtil.failed("普通管理员无法创建管理员角色的用户!");
					}
				} else {
					return ResultUtil.failed("无效的角色!");
				}
			}
			Users checkUser = usersMapper.selectById(user.getUid());
			if (checkUser != null && checkUser.getUid() != null) {
				return ResultUtil.failed("该用户的账号已存在!");
			}
			if (!StringUtil.isNullOrEmpty(user.getPwd())) {
				user.setPwd(MD5Util.encode(user.getPwd()));
			}
			int result = usersMapper.updateNotNullById(user);
			return ResultUtil.succeed(result);
		} catch (Exception e) {
			LOG.error("执行修改用户->\n" + user + "\n-->失败:", e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}

	}

	@Override
	public Map<String, Object> deleteUser(String sessionId, String uid) {
		try {
			LoginRole userRole = getUserRole(sessionId);
			if (userRole == LoginRole.ROOT) {
				int result = usersMapper.deleteById(uid);
				return ResultUtil.succeed(result);
			} else if (userRole == LoginRole.SERVER) {
				SqlAssist assist = new SqlAssist();
				assist.andEq(ColumnsUsers.ROLE, LoginRole.CLIENT.name());
				assist.andEq(ColumnsUsers.UID, uid);
				int result = usersMapper.deleteByAssist(assist);
				if (result == 0) {
					return ResultUtil.failed("删除失败,管理员只能删除普通用户");
				}
				return ResultUtil.succeed(result);
			} else {
				return ResultUtil.failed("只有管理员可以删除用户");
			}
		} catch (Exception e) {
			LOG.error("执行删除用户->" + uid + "-->失败:", e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> findTags() {
		try {
			SqlAssist assist = new SqlAssist().setOrderBy(String.format("%s desc,%s desc", ColumnsTags.SORTS, ColumnsTags.CTIME));
			List<Tags> all = tagsMapper.selectAll(assist);
			return ResultUtil.succeed(all);
		} catch (Exception e) {
			LOG.error("执行获取所有标签-->失败:" + e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getTag(String tid) {
		try {
			if (StringUtil.isNullOrEmpty(tid)) {
				return ResultUtil.failed("标签的id不能为空");
			}
			Tags result = tagsMapper.selectById(tid);
			return ResultUtil.succeed(result);
		} catch (Exception e) {
			LOG.error("执行获取指定标签->" + tid + "-->失败:" + e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> postTag(Tags tag) {
		try {
			if (tag == null || StringUtil.isNullOrEmpty(tag.getTname())) {
				return ResultUtil.failed("标签的名称不能为空");
			}
			if (tag.getTid() == null) {
				tag.setTid(UUID.randomUUID().toString());
			}
			if (tag.getSorts() == null) {
				tag.setSorts(0);
			}
			tag.setCtime(System.currentTimeMillis());
			int result = tagsMapper.insertNotNull(tag);
			return ResultUtil.succeed(result);
		} catch (Exception e) {
			LOG.error("执行新增标签->\n" + tag + "\n-->失败:", e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> putTag(Tags tag) {
		try {
			if (tag == null || StringUtil.isNullOrEmpty(tag.getTid())) {
				return ResultUtil.failed("标签的id不能为空");
			}
			tag.setCtime(System.currentTimeMillis());
			int result = tagsMapper.updateNotNullById(tag);
			return ResultUtil.succeed(result);
		} catch (Exception e) {
			LOG.error("执行修改标签->\n" + tag + "\n-->失败:", e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	@Override
	public Map<String, Object> deleteTag(String tid) {
		try {
			SqlAssist pAssist = new SqlAssist();
			pAssist.andLike(ColumnsProject.OWNERS, "%\"" + tid + "\"%");
			long ps = projectMapper.getCount(pAssist);
			if (ps > 0) {
				return ResultUtil.failed("该标签已被项目引用,无法进行删除");
			}
			SqlAssist uAssist = new SqlAssist();
			uAssist.andLike(ColumnsUsers.TAGS, "%\"" + tid + "\"%");
			long us = projectMapper.getCount(uAssist);
			if (us > 0) {
				return ResultUtil.failed("该标签下有用户,无法进行删除");
			}
			int result = tagsMapper.deleteById(tid);
			return ResultUtil.succeed(result);
		} catch (Exception e) {
			LOG.error("执行删除标签->" + tid + "-->失败:", e);
			return ResultUtil.failed(e == null ? "" : e.getMessage());
		}
	}

	/**
	 * 获取用户的角色
	 * 
	 * @param sessionId
	 * @return
	 */
	private LoginRole getUserRole(String sessionId) {
		LoginSession session = LoginSessionStore.get(sessionId);
		if (session == null) {
			return null;
		}
		return session.getRole();
	}

}
