package com.sanqi.sso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
*@author作者weilin
*@version 创建时间:2019年5月2日下午6:30:58
*类说明
*/
@Service
public class RedisService {
	
	@Autowired
	private ShardedJedisPool shardedJedisPool;
	
	private <T> T execute(Function<T, ShardedJedis> fun) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = shardedJedisPool.getResource();
			return fun.callback(shardedJedis);
		} 
		finally {
			if(shardedJedis != null) {
				shardedJedis.close();
			}
		}
	}
	
	/*
	 * 实现存操作
	 * */
	public String set(final String key, final String value) {
		return this.execute(new Function<String, ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.set(key, value);
			}
		});
	}
	
	
	/*
	 * 实现取操作
	 * */
	public String get(final String key) {
		return this.execute(new Function<String, ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.get(key);
			}
		});
	}
	
	
	
	/*
	 * 实现删除操作
	 * */
	public Long del(final String key) {
		return this.execute(new Function<Long, ShardedJedis>(){
			@Override
			public Long callback(ShardedJedis e) {
				return e.del(key);
			}
		});
	}
	
	
	/*
	 * 设置生存时间
	 * */
	public Long expire(final String key, final Integer seconds) {
		return this.execute(new Function<Long, ShardedJedis>(){
			@Override
			public Long callback(ShardedJedis e) {
				return e.expire(key, seconds);
			}
		});
	}
	
	/*
	 * set操作并设置生存时间
	 * */
	public String set(final String key, final String value,final Integer seconds) {
		return this.execute(new Function<String, ShardedJedis>(){

			@Override
			public String callback(ShardedJedis e) {
				String str = e.set(key, value);
				e.expire(key, seconds);
				return str;
			}
		});
	}
	
}
