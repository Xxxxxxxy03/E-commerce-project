package com.offcn.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offcn.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/****
 * @Author:ujiuye
 * @Description:User的Dao
 * @Date 2021/2/1 14:19
 *****/
@Repository
public interface UserMapper extends BaseMapper<User> {

    /***
     * 增加用户积分
     * @param username
     * @param point
     * @return
     */
    @Update("UPDATE tb_user SET points=points+#{point} WHERE  username=#{username}")
    int addUserPoints(@Param("username") String username, @Param("point") Integer point);
}
