package com.sanqi.sso.service;
/**
*@author作者weilin
*@version 创建时间:2019年5月2日下午6:43:33
*类说明
*/
public interface Function<T, E> {

	public T callback(E e);
}
