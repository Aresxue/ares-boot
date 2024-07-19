package cn.ares.business.demo.user.domain.mapper;

import cn.ares.business.demo.user.domain.entity.UserDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

  UserDo queryUserById(@Param("id") Long id);

}
