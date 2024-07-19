package cn.ares.boot.starter.web.config;

import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.util.common.DateUtil;
import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.json.JsonUtil;
import cn.ares.boot.util.json.deserializer.CompositeLocalDateTimeDeSerializer;
import cn.ares.boot.util.json.deserializer.CompositeLocalTimeDeSerializer;
import cn.ares.boot.util.json.serializer.BigDecimalScaleSerializer;
import cn.ares.boot.util.json.serializer.BigDecimalTrailingZerosSerializer;
import cn.ares.boot.util.json.serializer.CompositeLocalDateTimeSerializer;
import cn.ares.boot.util.json.serializer.CompositeLocalTimeSerializer;
import cn.ares.boot.util.json.serializer.modifier.NullBeanSerializerModifier;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: Ares
 * @time: 2020-11-26 13:48:00
 * @description: 做了以下工作 1.解决js和java数字精度不对等(js为2 ^ 53, java为2 ^ 64) 2.Date类型转为字符串
 * 3.BigDecimal也转为string 4.LocalDateTime、LocalDate、LocalTime等jdk8时间格式的处理
 * @version: JDK 1.8
 */
@Configuration
@ConditionalOnMissingBean(WebMessageConverterConfiguration.class)
@ConditionalOnProperty(name = "ares.web.message-convert.enabled", havingValue = TRUE, matchIfMissing = true)
@Role(value = ROLE_INFRASTRUCTURE)
@Order(0)
public class WebMessageConverterConfiguration implements WebMvcConfigurer {

  @Value("${ares.web.message-convert.big-decimal.scale:}")
  private Integer bigDecimalScale;

  @Value("${ares.web.message-convert.date.format:yyyy-MM-dd HH:mm:ss.SSS}")
  private String dateFormat;

  @Value("${ares.web.message-convert.local-date.format:yyyy-MM-dd}")
  private String localDateFormat;

  @Value("${ares.web.message-convert.local-time.format:HH:mm:ss.SSS}")
  private String localTimeFormat;

  @Value("${ares.web.message-convert.local-date-time.format:yyyy-MM-dd HH:mm:ss.SSS}")
  private String localDateTimeFormat;

  @Value("${ares.web.message-convert.ignore-null:false}")
  private boolean notNull;

  @Value("${ares.web.message-convert.null2default:false}")
  private boolean nullToDefaultEnable;

  @Override
  public void configureMessageConverters(@Nullable List<HttpMessageConverter<?>> converterList) {
    if (null != converterList) {
      MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
          Jackson2ObjectMapperBuilder.json().build());

      ObjectMapper objectMapper = converter.getObjectMapper();
      if (notNull) {
        objectMapper.setSerializationInclusion(Include.NON_NULL);
      }

      // 加入null值设置默认值的序列化，开启后Boolean的null会变成false，Number的null变成0，字符串的null变成""，数组的null变成[]
      if (nullToDefaultEnable) {
        JsonUtil.withSerializerModifier(objectMapper, new NullBeanSerializerModifier());
      }

      objectMapper.setTimeZone(TimeZone.getDefault());

      SimpleDateFormat defaultDateFormat = DateUtil.getFormat(dateFormat);
      objectMapper.setDateFormat(defaultDateFormat);

      SimpleModule newModule = new SimpleModule();
      newModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
      if (null == bigDecimalScale) {
        newModule.addSerializer(BigDecimal.class, new BigDecimalTrailingZerosSerializer());
      } else {
        newModule.addSerializer(BigDecimal.class, new BigDecimalScaleSerializer(bigDecimalScale));
      }

      JsonUtil.disableIgnoreDuplicateModuleRegistrations(objectMapper);
      objectMapper.registerModule(newModule);

      // 处理LocalDateTime、LocalDate、LocalTime
//      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

      JavaTimeModule javaTimeModule = new JavaTimeModule();
      javaTimeModule.addSerializer(LocalDateTime.class,
          new CompositeLocalDateTimeSerializer(localDateTimeFormat));
      javaTimeModule.addDeserializer(LocalDateTime.class,
          new CompositeLocalDateTimeDeSerializer(localDateTimeFormat));

      javaTimeModule.addSerializer(LocalDate.class,
          new LocalDateSerializer(DateTimeFormatter.ofPattern(localDateFormat)));
      javaTimeModule.addDeserializer(LocalDate.class,
          new LocalDateDeserializer(DateTimeFormatter.ofPattern(localDateFormat)));

      javaTimeModule.addSerializer(LocalTime.class,
          new CompositeLocalTimeSerializer(localTimeFormat));
      javaTimeModule.addDeserializer(LocalTime.class,
          new CompositeLocalTimeDeSerializer(localTimeFormat));

      objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
      JsonUtil.enableIgnoreDuplicateModuleRegistrations(objectMapper);

      converter.setObjectMapper(objectMapper);

      // 插入到合适的位置
      for (int i = 0; i < converterList.size(); i++) {
        if (converterList.get(i) instanceof MappingJackson2HttpMessageConverter) {
          converterList.add(i, converter);
          return;
        }
      }
      // 如果没有找到MappingJackson2HttpMessageConverter则直接添加到最后
      converterList.add(converter);
    }
  }

  /**
   * @author: Ares
   * @time: 2022-11-26 14:40:00
   * @description: 主要作用于Get请求
   * @version: JDK 1.8
   */
  @ControllerAdvice
  public class RequestConvertConfiguration {

    private final DateTimeFormatter localDateTimeFormatter;
    private DateTimeFormatter removeZeroLocalDateTimeFormatter;
    private final DateTimeFormatter localTimeFormatter;
    private DateTimeFormatter removeZeroLocalTimeFormatter;
    private final DateTimeFormatter localDateFormatter;

    public RequestConvertConfiguration() {
      this.localDateTimeFormatter = DateTimeFormatter.ofPattern(localDateTimeFormat);
      List<String> formatList = StringUtil.listSplit(localDateTimeFormat, ".S");
      if (formatList.size() > 1) {
        removeZeroLocalDateTimeFormatter = DateTimeFormatter.ofPattern(formatList.get(0));
      }
      this.localTimeFormatter = DateTimeFormatter.ofPattern(localTimeFormat);
      formatList = StringUtil.listSplit(localTimeFormat, ".S");
      if (formatList.size() > 1) {
        removeZeroLocalTimeFormatter = DateTimeFormatter.ofPattern(formatList.get(0));
      }
      this.localDateFormatter = DateTimeFormatter.ofPattern(localDateFormat);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
      binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
          DateTimeFormatter formatter;
          if (null == removeZeroLocalDateTimeFormatter || text.contains(".")) {
            formatter = localDateTimeFormatter;
          } else {
            formatter = removeZeroLocalDateTimeFormatter;
          }
          setValue(LocalDateTime.parse(text, formatter));
        }
      });

      binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
          setValue(LocalDate.parse(text, localDateFormatter));
        }
      });

      binder.registerCustomEditor(LocalTime.class, new PropertyEditorSupport() {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
          DateTimeFormatter formatter;
          if (null == removeZeroLocalTimeFormatter || text.contains(".")) {
            formatter = localTimeFormatter;
          } else {
            formatter = removeZeroLocalTimeFormatter;
          }
          setValue(LocalTime.parse(text, formatter));
        }
      });
      binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
          ExceptionUtil.run(() -> setValue(DateUtil.getFormat(dateFormat).parse(text)));
        }
      });
    }
  }


}

