package com.pwc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pwc.wiki.domain.User;
import com.pwc.wiki.domain.UserExample;
import com.pwc.wiki.exception.BusinessException;
import com.pwc.wiki.exception.BusinessExceptionCode;
import com.pwc.wiki.mapper.UserMapper;
import com.pwc.wiki.req.UserLoginReq;
import com.pwc.wiki.req.UserQueryReq;
import com.pwc.wiki.req.UserResetPasswordReq;
import com.pwc.wiki.req.UserSaveReq;
import com.pwc.wiki.resp.UserLoginResp;
import com.pwc.wiki.resp.UserQueryResp;
import com.pwc.wiki.resp.PageResp;
import com.pwc.wiki.util.CopyUtil;
import com.pwc.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getLoginName())){
            //模糊查询
            criteria.andLoginNameLike("%" + req.getLoginName() + "%");
        }

        PageHelper.startPage(req.getPage(),req.getSize());
        List<User>  userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}",pageInfo.getTotal());

        List<UserQueryResp> userQueryRespList = CopyUtil.copyList(userList,UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setList(userQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp ;
    }

    //登陆
    public UserLoginResp login(UserLoginReq req){
        //用用户名去数据库里查询用户
        User user = selectByLoginName(req.getLoginName());
        if(user==null){
            //用户名不存在
            LOG.info("用户名不存在, {}", req.getLoginName());//后端日志里面可以显示准确信息
            //该异常信息会返回给前端，故只能提示"用户名不存在或密码错误"
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }else{
            if(user.getPassword().equals(req.getPassword())){
                //登陆成功
                UserLoginResp userLoginResp = CopyUtil.copy(user,UserLoginResp.class);
                return userLoginResp;
            }else{
                //密码不正确
                LOG.info("密码不对, 输入密码：{}, 数据库密码：{}", req.getPassword(), user.getPassword());//后端日志里面可以显示准确信息
                //该异常信息会返回给前端，故只能提示"用户名不存在或密码错误"
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }


    //保存：修改+新增
    public void save(UserSaveReq req){
        User user = CopyUtil.copy(req,User.class);
        if(ObjectUtils.isEmpty(req.getId())){
            if(selectByLoginName(req.getLoginName())==null){
                //新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else{
                //用户名重复
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }

        }
        else{
            //修改
            //编辑用户时不修改用户名和密码
            user.setLoginName(null);
            user.setPassword(null);
            //"Selective"表示user里的属性有值才更新
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    //重置密码
    public void resetPassword(UserResetPasswordReq req){
        //复制出来的user对象只有id和password值
        User user = CopyUtil.copy(req,User.class);
        //"Selective"表示user里的属性有值才更新
        userMapper.updateByPrimaryKeySelective(user);
    }

    //删除
    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }


    public User selectByLoginName(String loginName){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        //MyBatis查出来的都是list
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList.isEmpty()){
            return null;
        }else{
            return userList.get(0);
        }

    }
}
