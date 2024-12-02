package cn.ares.boot.starter.spring.validation.config;

import static org.hibernate.validator.BaseHibernateValidatorConfiguration.FAIL_FAST;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.starter.spring.validation.util.ValidatorUtil;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

/**
 * @author: Ares
 * @time: 2024-12-02 11:40:33
 * @description: 校验配置（为了使spring-boot-starter-validation在业务层使用 在接口类上使用@Validated(Default.class), *
 * 在方法上使用校验注解(如@NotNull), 如果是实体想校验其内部的属性那么需要再加上@Valid, 实体中的实体也需要@Valid）
 * @description: Validation configuration
 * @version: JDK 1.8
 */
@Configuration
@Role(value = ROLE_INFRASTRUCTURE)
@Import({ValidatorUtil.class})
public class ValidationConfiguration {

  @Value("${ares.validation.fail-fast:false}")
  private String failFast;

  @Bean
  @Role(value = ROLE_INFRASTRUCTURE)
  public Validator validator() {
    try (ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
        .configure().addProperty(FAIL_FAST, failFast).buildValidatorFactory()) {
      return validatorFactory.getValidator();
    }
  }

}
