package cn.ares.boot.demo.user.domain.mapper;

import cn.ares.boot.demo.user.domain.entity.UserDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<UserDo> {


}
