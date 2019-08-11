package com.sanqi.sso.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sanqi.sso.pojo.User;
import com.sanqi.sso.service.UserService;

/**
*@author作者weilin
*@version 创建时间:2019年6月24日下午8:35:24
*类说明
*/
@RequestMapping("user")
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;

	@RequestMapping(value="register",method=RequestMethod.GET)
	public String toRegister() {
			return "register";
	}
	
	@RequestMapping(value="check",method=RequestMethod.GET)
	public ResponseEntity<Boolean> check(@PathVariable("params")String params,
			@PathVariable("type")Integer type){
		try {
			Boolean b = this.userService.check(params, type);
			if (b == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			return ResponseEntity.ok(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	@RequestMapping(value ="doRegister",method = RequestMethod.POST)
	public Map<String,Object> doRegister(User user){
		Map<String,Object> result = new HashMap<String, Object>();
		try {
			Boolean b = this.userService.saveUser(user);
			if (b) {
				return (Map<String, Object>) result.put("status", "200");
			} else {
			     result.put("status", "500");
			     result.put("data", "haha");
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="doLogin",method=RequestMethod.POST)
	public Map<String,Object> doLogin(@RequestParam("username") String username,
			@RequestParam("password")String password,HttpServletRequest request,
			HttpServletResponse response){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			String token = this.userService.doLogin(username, password);
			if (token == null) {
				return (Map<String, Object>) result.put("status", 400);
			} else {
				return (Map<String, Object>) result.put("status", 200);
				//CookieUtils.setCookie(request,response,"COOKIE_NAME",token);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
			return (Map<String, Object>) result.put("status",500);
	}
	
	@RequestMapping(value = "{token}",method =RequestMethod.GET)
	public ResponseEntity<User> queryByToken(@PathVariable("token")String token){
		User user = this.userService.queryByToken(token);
		try {
			if (user == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
