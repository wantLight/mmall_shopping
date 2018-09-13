package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;

import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * Created by xyzzg on 2018/6/4.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUserName(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在！");
        }
        //密码登录MD5
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

    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse = this.checkValue(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题是空的！");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            //说明答案是这个用户的，并且正确
            String forgetToken = UUID.randomUUID().toString();
            //本地缓存
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，需要值！");
        }
        ServerResponse validResponse = this.checkValue(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }else {
                return ServerResponse.createByErrorMessage("请重新获取重置密码的token");
            }
        }
        return ServerResponse.createBySuccessMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，检验这个用户的旧密码（指定这个用户）。
        //因为我们会查询一个count（1），若不指定id，那么结果就是true count>0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createBySuccessMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user){
        //username不能被更新
        //email需要交验是否存在？相同不能是当前用户
        int resultMap = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultMap>0){
            return ServerResponse.createByErrorMessage("电子邮箱已存在！");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            return ServerResponse.createBySuccess("跟新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败！");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend
    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
