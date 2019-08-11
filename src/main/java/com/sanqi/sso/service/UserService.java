package com.sanqi.sso.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanqi.sso.mapper.UserMapper;
import com.sanqi.sso.pojo.User;

/**
*@author作者weilin
*@version 创建时间:2019年6月26日下午7:34:22
*类说明
*/
@Service
public class UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RedisService redisService;
	
	private static ObjectMapper mapper = new ObjectMapper();

	public Boolean check(String params, Integer type) {
		User record = new User();
		switch(type) {
		case 1:
			record.setUsername(params);
			break;
		case 2:
			record.setPhone(params);
			break;
		case 3:
			record.setEmail(params);
		default:
				return null;
		}
		
		return this.userMapper.selectOne(record)!=null;
	}

	public Boolean saveUser(User user) {
		user.setId(null);
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		//密码以MD5形式加密
		return this.userMapper.insert(user)==1;
	}

	public String doLogin(String username, String password) throws Exception {
		User record = new User();
		record.setUsername(username);
		User user = this.userMapper.selectOne(record);
		if(user==null) {
			return null;
		}
		//判断密码是否相等
		if(!user.getPassword().equals(password)) {
			return null;
		}
		//String token = DigestUtils.md5DigestAsHex(System.currentTimeMillis()+ username);
		String token = System.currentTimeMillis()+username;
		//将token保存至redis中
		this.redisService.set("TOKEN_"+token, mapper.writeValueAsString(user), 60*30);
		return token;
	}

	public User queryByToken(String token) {
		String key = "TOKEN_" + token;
		String jsonData = this.redisService.get(key);
		if(jsonData == null) {
			//登录超时
			return null;
		}else {
			try {
				//用户活跃,设置时间
				this.redisService.expire(key, 60 * 30);
				return mapper.readValue(jsonData, User.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	
}
