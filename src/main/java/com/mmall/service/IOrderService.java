package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by xyzzg on 2018/6/27.
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
}
