package com.mmall.service;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;

import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by xyzzg on 2018/6/4.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;


    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUserName(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在！");
        }
        //todo 密码登录MD5
        String md5Pass = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Pass);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValue(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess() ){
            return  validResponse;
        }
        validResponse = this.checkValue(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess() ){
            return  validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createByErrorMessage("注册成功！");
    }

    public ServerResponse<String> checkValue(String str,String type){
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(str)){
            //开始校验
            int resultCount = userMapper.checkUserName(str);
            if (resultCount > 0){
                return ServerResponse.createByErrorMessage("用户名已存在");
            }
            resultCount = userMapper.checkEmail(str);
            if (resultCount > 0){
                return ServerResponse.createByErrorMessage("邮箱已存在");
            }

        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成果");

    }
}
