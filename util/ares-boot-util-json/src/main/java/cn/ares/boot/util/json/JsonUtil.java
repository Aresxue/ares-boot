package cn.ares.boot.util.json;

import static cn.ares.boot.util.common.DateUtil.DATE_FORMAT_TIME_MILLIS;
import static cn.ares.boot.util.common.DateUtil.DATE_FORMAT_WHIFFLETREE_DAY;
import static cn.ares.boot.util.common.DateUtil.DATE_FORMAT_WHIFFLETREE_MILLIS;
import static com.fasterxml.jackson.databind.MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.ClassUtil;
import cn.ares.boot.util.common.DateUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.json.exception.JsonException;
import cn.ares.boot.util.json.serializer.modifier.IgnoreParentPropertiesBeanSerializerModifier;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2021-04-06 20:39:00
 * @description: Json工具类
 * @description: Json util
 * @version: JDK 1.8
 */
public class JsonUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
  private static final JsonMapper DEFAULT_JSON_MAPPER = getDefaultJsonMapper();

  private static final TypeReference<HashMap<String, String>> STRING_MAP_TYPE_REFERENCE = new TypeReference<HashMap<String, String>>() {
  };


  /**
   * @author: Ares
   * @description: java对象转成json字符串
   * @description: Java object convert to json string
   * @time: 2021-12-03 15:57:00
   * @params: [object] java object
   * @return: java.lang.String json string
   */
  public static String toJsonString(Object object) {
    return toJsonString(object, false);
  }

  /**
   * @author: Ares
   * @description: java对象转成json字符串（可指定是否使用美化和失败时是否直接使用toString方法）
   * @description: Java object convert to json string(You can specify whether to use beautification
   * and whether to use the toString method directly when it fails)
   * @time: 2021-05-14 15:57:00
   * @params: [object, pretty, useToString] 对象, 是否美化, 失败时是否使用ToString方法
   * @return: java.lang.String json string
   */
  public static String toJsonString(Object object, boolean pretty, boolean useToString) {
    return toJsonString(DEFAULT_JSON_MAPPER, object, pretty, useToString);
  }

  /**
   * @author: Ares
   * @description: java对象转成json字符串（可指定是否美化）
   * @description: Java object convert to json string(You can specify whether to use
   * beautification)
   * @time: 2021-05-14 15:57:00
   * @params: [object, pretty] 对象, 是否美化
   * @return: java.lang.String json string
   */
  public static String toJsonString(Object object, boolean pretty) {
    return toJsonString(DEFAULT_JSON_MAPPER, object, pretty);
  }

  /**
   * @author: Ares
   * @description: 使用指定objectMapper将java对象转成json字符串
   * @description: Java object convert to json string use the specified objectMapper
   * @time: 2021-05-14 15:57:00
   * @params: [objectMapper, object] objectMapper, java object
   * @return: java.lang.String json string
   */
  public static String toJsonString(ObjectMapper objectMapper, Object object) {
    return toJsonString(objectMapper, object, false);
  }

  /**
   * @author: Ares
   * @description: java对象转成json字符串（可指定objectMapper和是否美化）
   * @description: Java object convert to json string(You can specify objectMapper and whether to
   * use beautification)
   * @time: 2021-05-14 15:57:00
   * @params: [objectMapper, object, pretty] objectMapper, 对象, 是否美化
   * @return: java.lang.String json string
   */
  public static String toJsonString(ObjectMapper objectMapper, Object object, boolean pretty) {
    return toJsonString(objectMapper, object, pretty, false);
  }

  /**
   * @author: Ares
   * @description: java对象转成json字符串（可指定objectMapper、是否美化和失败时是否直接使用toString方法）
   * @description: Java object convert to json string(You can specify objectMapper, whether to use
   * beautification and whether to use the toString method directly when it fails)
   * @time: 2021-05-14 15:57:00
   * @params: [objectMapper, object, pretty, useToString] objectMapper, 对象, 是否美化,
   * 失败时是否直接使用toString方法
   * @return: java.lang.String json string
   */
  public static String toJsonString(ObjectMapper objectMapper, Object object, boolean pretty,
      boolean useToString) {
    if (null == object) {
      return null;
    }
    if (ClassUtil.isPrimitiveOrWrapOrString(object.getClass())) {
      return String.valueOf(object);
    }

    String result;
    try {
      return pretty ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
          : objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      LOGGER.warn("java object convert to json string exception: ", e);
      if (useToString) {
        result = object.toString();
      } else {
        throw new JsonException("Java object convert to json string exception", e);
      }
    }

    return result;
  }

  /**
   * @author: Ares
   * @description: 把字符串转成java对象(指定java类型)
   * @description: Convert string to java object(Specify the java type)
   * @time: 2021-06-10 10:46:00
   * @params: [json, valueType] json string, java type
   * @return: T java object
   */
  public static <T> T toJavaObject(String json, JavaType valueType) {
    try {
      return DEFAULT_JSON_MAPPER.readValue(json, valueType);
    } catch (JsonProcessingException e) {
      throw new JsonException("Json string can not convert to java object", e);
    }
  }

  /**
   * @author: Ares
   * @description: 使用type把字符串转成java对象（指定类型）
   * @description: Convert string to java object use type
   * @time: 2022-06-06 16:46:00
   * @params: [json, valueType] json string, java type
   * @return: T java object
   */
  public static <T> T toJavaObject(String json, Type type) {
    try {
      return DEFAULT_JSON_MAPPER.readValue(json, DEFAULT_JSON_MAPPER.constructType(type));
    } catch (JsonProcessingException e) {
      throw new JsonException("Json string can not convert to java object", e);
    }
  }

  /**
   * @author: Ares
   * @description: 字符串转为java对象
   * @description: Convert string to java object
   * @time: 2021-12-23 23:13:02
   * @params: [json, valueTypeRef] json string, typeReference
   * @return: T java object
   */
  public static <T> T toJavaObject(String json, TypeReference<T> valueTypeRef) {
    try {
      return DEFAULT_JSON_MAPPER.readValue(json, valueTypeRef);
    } catch (JsonProcessingException e) {
      throw new JsonException("Json string can not convert to java object", e);
    }
  }

  /**
   * @author: Ares
   * @description: 字符串转为java对象（指定容器及其泛型）
   * @description: Convert string to java object(Specify the container and its generics)
   * @time: 2021-12-23 23:13:02
   * @params: [json, rawType, ownerType] json string, raw class, owner class
   * @return: T java object
   */
  @SuppressWarnings("unchecked")
  public static <OWNER, RAW> OWNER toJavaObject(String json, Class<OWNER> rawType,
      Class<RAW> ownerType) {
    JavaType javaType = DEFAULT_JSON_MAPPER.getTypeFactory()
        .constructParametricType(rawType, ownerType);
    try {
      return DEFAULT_JSON_MAPPER.readValue(json, javaType);
    } catch (JsonProcessingException e) {
      throw new JsonException("Json string can not convert to java object", e);
    }
  }


  /**
   * @author: Ares
   * @description: 根据容器及其泛型构建java类型
   * @description: Build javaType from containers and their generics
   * @time: 2021-12-23 23:36:35
   * @params: [ownerClass, rawClass] owner class, raw class
   * @return: com.fasterxml.jackson.databind.JavaType javaType
   */
  public static <OWNER, RAW> JavaType buildJavaType(Class<OWNER> ownerClass, Class<RAW> rawClass) {
    TypeFactory typeFactory = DEFAULT_JSON_MAPPER.getTypeFactory();
    return typeFactory.constructParametricType(ownerClass, rawClass);
  }

  /**
   * @author: Ares
   * @description: 使用容器类型和java类型构建java类型
   * @description: Build javaType with rawType and javaType
   * @time: 2022-11-30 20:29:51
   * @params: [rawType, javaType] raw class, javaType
   * @return: com.fasterxml.jackson.databind.JavaType javaType
   */
  public static JavaType buildJavaType(Class<?> rawType, JavaType javaType) {
    TypeFactory typeFactory = DEFAULT_JSON_MAPPER.getTypeFactory();
    return typeFactory.constructParametricType(rawType, javaType);
  }

  /**
   * @author: Ares
   * @description: 使用单泛型的class数组构建javaType
   * @description: Build javaType with single generic class
   * @description:
   * @time: 2022-11-30 20:30:43
   * @params: [classes] class数组
   * @return: com.fasterxml.jackson.databind.JavaType out 出参
   */
  public static JavaType buildJavaTypeWithSingleGenericType(Class<?> rawType, Class<?>... classes) {
    if (ArrayUtil.isEmpty(classes)) {
      return null;
    }
    TypeFactory typeFactory = DEFAULT_JSON_MAPPER.getTypeFactory();
    int arrLength = classes.length;
    if (arrLength == 1) {
      return typeFactory.constructParametricType(rawType, classes[0]);
    }

    JavaType javaType = typeFactory.constructParametricType(classes[arrLength - 2],
        classes[arrLength - 1]);
    for (int i = arrLength - 3; i >= 0; i--) {
      javaType = typeFactory.constructParametricType(classes[i], javaType);
    }

    return typeFactory.constructParametricType(rawType, javaType);
  }


  /**
   * @author: Ares
   * @description: 解析json字符串为json节点
   * @description: Parse json string to json node
   * @time: 2021-08-19 20:02:00
   * @params: [content] json content
   * @return: com.fasterxml.jackson.databind.JsonNode json node
   */
  public static JsonNode parseObject(String json) {
    try {
      return DEFAULT_JSON_MAPPER.readTree(json);
    } catch (JsonProcessingException e) {
      throw new JsonException("Json string can not convert to java node", e);
    }
  }

  /**
   * @author: Ares
   * @description: 解析对象（以指定类）
   * @description: Parse object(use specify class)
   * @time: 2021-12-23 22:58:09
   * @params: [object, clazz] java object, class
   * @return: T java object
   */
  public static <T> T parseObject(Object object, Class<T> clazz) {
    return toJavaObject(toJsonString(object), clazz);
  }

  /**
   * @author: Ares
   * @description: 使用typeReference解析对象
   * @time: 2022-11-19 21:01:02
   * @params: [object, typeReference]
   * @return: T java object
   */
  public static <T> T parseObject(Object object, TypeReference<T> typeReference) {
    return toJavaObject(toJsonString(object), typeReference);
  }

  /**
   * @author: Ares
   * @description: 解析对象(以指定java类型)
   * @description: Parse object(use javaType)
   * @time: 2021-12-23 22:58:09
   * @params: [object, javaType] java object, javaType
   * @return: T java object
   */
  public static <T> T parseObject(Object object, JavaType javaType) {
    return toJavaObject(toJsonString(object), javaType);
  }

  /**
   * @author: Ares
   * @description: 解析对象(以指定类型)
   * @description: Parse object(use type)
   * @time: 2022-06-06 16:46:00
   * @params: [object, type] java object, type
   * @return: T java object
   */
  public static <T> T parseObject(Object object, Type type) {
    return toJavaObject(toJsonString(object), type);
  }


  /**
   * @author: Ares
   * @description: 解析对象为json节点
   * @description: Parse object to json node
   * @time: 2021-10-21 19:38:00
   * @params: [object] object
   * @return: com.fasterxml.jackson.databind.JsonNode json node
   */
  public static <T> JsonNode parseObject(T object) {
    try {
      return DEFAULT_JSON_MAPPER.readTree(toJsonString(object));
    } catch (JsonProcessingException e) {
      throw new JsonException("Entity can not convert to java node", e);
    }
  }

  /**
   * @author: Ares
   * @description: 字符串转为java对象（以指定类）
   * @description: Convert string to java object(use specify class)
   * @time: 2021-06-10 10:46:00
   * @params: [json, valueType] json string, class
   * @return: T java object
   */
  public static <T> T toJavaObject(String json, Class<T> valueType) {
    try {
      return DEFAULT_JSON_MAPPER.readValue(json, valueType);
    } catch (JsonProcessingException e) {
      throw new JsonException("Json string can not convert to java object", e);
    }
  }

  /**
   * @author: Ares
   * @description: 将字节数组转为java对象
   * @description: Convert byte array to java object
   * @time: 2021-12-01 23:17:00
   * @params: [bytes, valueType] byte array, class
   * @return: T java object
   */
  public static <T> T toJavaObject(byte[] bytes, Class<T> type) {
    try {
      return DEFAULT_JSON_MAPPER.readValue(bytes, type);
    } catch (Exception e) {
      throw new JsonException("Byte array can not convert to java object", e);
    }
  }

  /**
   * @author: Ares
   * @description: 将java独享转为字节数组（失败时使用toString）
   * @description: Java object convert to byte(when fail use toString and getBytes)
   * @time: 2021-12-01 23:12:00
   * @params: [object] java object
   * @return: byte[] byte array
   */
  public static byte[] toBytes(Object object) {
    try {
      return DEFAULT_JSON_MAPPER.writeValueAsBytes(object);
    } catch (JsonProcessingException e) {
      LOGGER.warn("java object convert to byte array exception: ", e);
      return String.valueOf(object).getBytes(Charset.defaultCharset());
    }
  }

  /**
   * @author: Ares
   * @description: 解析json数组为链表
   * @description: Parse json array convert to list
   * @time: 2021-10-25 14:10:00
   * @params: [json, valueType] json string, raw class
   * @return: java.util.List<T> list of java entity
   */
  public static <T> List<T> parseArray(String json, Class<T> valueType) {
    try {
      JavaType javaType = DEFAULT_JSON_MAPPER.getTypeFactory()
          .constructParametricType(List.class, valueType);
      return DEFAULT_JSON_MAPPER.readValue(json, javaType);
    } catch (JsonProcessingException e) {
      throw new JsonException("Json array string can not convert to list of java object", e);
    }
  }


  /**
   * @author: Ares
   * @description: json字符串转为Map
   * @description: Convert json string to string map
   * @time: 2021-12-23 23:03:46
   * @params: [json] json string
   * @return: java.util.Map<java.lang.String, java.lang.String>
   */
  public static Map<String, String> stringMapFromJsonString(String json) {
    if (null == json) {
      return null;
    }
    try {
      return DEFAULT_JSON_MAPPER.readValue(json, STRING_MAP_TYPE_REFERENCE);
    } catch (IOException e) {
      throw new JsonException("Json string can not convert to string map", e);
    }
  }


  /**
   * @author: Ares
   * @description: 解析字节数组转为链表
   * @description: Parse byte array to list
   * @time: 2021-12-01 23:20:00
   * @params: [bytes, valueType] byte array, raw type
   * @return: java.util.List<T> list of java entity
   */
  public static <T> List<T> parseArray(byte[] bytes, Class<T> valueType) {
    try {
      JavaType javaType = DEFAULT_JSON_MAPPER.getTypeFactory()
          .constructCollectionType(List.class, valueType);
      return DEFAULT_JSON_MAPPER.readValue(bytes, javaType);
    } catch (IOException e) {
      throw new JsonException("Byte array can not convert to list of java object", e);
    }
  }

  /**
   * @author: Ares
   * @description: 获取设置了一些默认选项和时间序列化策略的ObjectMapper(不忽略null值)
   * @description: Get object mapper with default configure and time serialization strategy(Do not
   * ignore null values)
   * @time: 2021-12-23 22:58:30
   * @params: []
   * @return: com.fasterxml.jackson.databind.json.JsonMapper
   */
  public static JsonMapper getJsonMapper() {
    return getJsonMapper(false);
  }

  /**
   * @author: Ares
   * @description: 获取设置了一些默认选项和时间序列化策略的ObjectMapper
   * @description: Get object mapper with default configure and time serialization strategy
   * @time: 2021-12-23 22:58:30
   * @params: [nonNull] 是否忽略null值
   * @return: com.fasterxml.jackson.databind.json.JsonMapper
   */
  public static JsonMapper getJsonMapper(boolean nonNull) {
    return getJsonMapper(nonNull, false);
  }

  /**
   * @author: Ares
   * @description: 获取设置了一些默认选项和时间序列化策略的ObjectMapper
   * @description: Get object mapper with default configure and time serialization strategy
   * @time: 2021-12-23 22:58:30
   * @params: [nonNull, onlyUseField] 是否忽略null值，只使用field做序列化
   * @return: com.fasterxml.jackson.databind.json.JsonMapper
   */
  public static JsonMapper getJsonMapper(boolean nonNull, boolean onlyUseField) {
    JsonMapper jsonMapper = new JsonMapper();
    // 忽略null值
    //  ignore null
    if (nonNull) {
      jsonMapper.setSerializationInclusion(Include.NON_NULL);
    }

    jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    // 只使用field做序列化（避免将get、set方法序列化为原先不存在的属性）
    if (onlyUseField) {
      jsonMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
      jsonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    }

    configTime(jsonMapper, DATE_FORMAT_WHIFFLETREE_MILLIS, DATE_FORMAT_WHIFFLETREE_DAY,
        DATE_FORMAT_TIME_MILLIS, DATE_FORMAT_WHIFFLETREE_MILLIS);

    // 加入自定义bean序列化修改
    jsonMapper.setSerializerFactory(jsonMapper.getSerializerFactory()
        .withSerializerModifier(new IgnoreParentPropertiesBeanSerializerModifier()));

//    SimpleFilterProvider filterProvider = new SimpleFilterProvider();
//    filterProvider.addFilter("table",
//        SimpleBeanPropertyFilter.filterOutAllExcept(Sets.newHashSet("field")));
//    jsonMapper.setFilterProvider(filterProvider);
    disableIgnoreDuplicateModuleRegistrations(jsonMapper);
    jsonMapper.registerModule(new Jdk8Module());
    jsonMapper.registerModule(new ParameterNamesModule());
    enableIgnoreDuplicateModuleRegistrations(jsonMapper);

    return jsonMapper;
  }

  /**
   * @author: Ares
   * @description: 获取设置了一些默认选项和时间序列化策略的ObjectMapper（不忽略null值）
   * @description: Get object mapper with default configure and time serialization strategy(Do not
   * ignore null values)
   * @time: 2021-12-23 22:58:30
   * @params: []
   * @return: com.fasterxml.jackson.databind.json.JsonMapper
   */
  public static JsonMapper getDefaultJsonMapper() {
    return getJsonMapper(false);
  }

  public static void configInclude(String include) {
    configInclude(DEFAULT_JSON_MAPPER, include);
  }

  public static void configInclude(ObjectMapper objectMapper, String include) {
    Arrays.stream(Include.values()).filter(value -> include.equalsIgnoreCase(value.name()))
        .findFirst().ifPresent(objectMapper::setSerializationInclusion);
  }

  public static void configTime(ObjectMapper objectMapper, String dateFormat) {
    configTime(objectMapper, dateFormat, null, null, null);
  }

  public static void configTime(String dateFormat) {
    configTime(DEFAULT_JSON_MAPPER, dateFormat);
  }

  public static void configTime(ObjectMapper objectMapper, String localDateFormat,
      String localTimeFormat, String localDateTimeFormat) {
    configTime(objectMapper, null, localDateFormat, localTimeFormat, localDateTimeFormat);
  }

  public static void configTime(String localDateFormat, String localTimeFormat,
      String localDateTimeFormat) {
    configTime(DEFAULT_JSON_MAPPER, localDateFormat, localTimeFormat, localDateTimeFormat);
  }

  /**
   * @author: Ares
   * @description: 配置时间序列化
   * @description: Config time serializer
   * @time: 2021-11-27 05:40:00
   * @params: [objectMapper, dateFormat, localDateFormat, localTimeFormat, localDateTimeFormat]
   * objectMapper, dateFormat, localDateFormat, localTimeFormat, localDateTimeFormat
   * @return: void
   */
  public static void configTime(ObjectMapper objectMapper, String dateFormat,
      String localDateFormat, String localTimeFormat, String localDateTimeFormat) {
    if (StringUtil.isNotEmpty(dateFormat)) {
      objectMapper.setDateFormat(DateUtil.getFormat(dateFormat));
    }

    boolean localDateIsNotEmpty = StringUtil.isNotEmpty(localDateFormat);
    boolean localTimeIsNotEmpty = StringUtil.isNotEmpty(localTimeFormat);
    boolean localDateTimeIsNotEmpty = StringUtil.isNotEmpty(localDateTimeFormat);
    if (localDateIsNotEmpty || localTimeIsNotEmpty || localDateTimeIsNotEmpty) {
      JavaTimeModule javaTimeModule = new JavaTimeModule();
      if (localDateIsNotEmpty) {
        javaTimeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern(localDateFormat)));
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern(localDateFormat)));
      }
      if (localTimeIsNotEmpty) {
        javaTimeModule.addSerializer(LocalTime.class,
            new LocalTimeSerializer(DateTimeFormatter.ofPattern(localTimeFormat)));
        javaTimeModule.addDeserializer(LocalTime.class,
            new LocalTimeDeserializer(DateTimeFormatter.ofPattern(localTimeFormat)));
      }
      if (localDateTimeIsNotEmpty) {
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(localDateTimeFormat)));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(localDateTimeFormat)));
      }
      disableIgnoreDuplicateModuleRegistrations(objectMapper);
      objectMapper.registerModule(javaTimeModule);
      enableIgnoreDuplicateModuleRegistrations(objectMapper);
    }
  }

  /**
   * @author: Ares
   * @description: 禁用objectMapper忽略模块重复注册的功能
   * @time: 2023-05-08 15:27:31
   * @params: [objectMapper] objectMapper
   * @return: void
   */
  public static void disableIgnoreDuplicateModuleRegistrations(ObjectMapper objectMapper) {
    objectMapper.disable(IGNORE_DUPLICATE_MODULE_REGISTRATIONS);
  }

  /**
   * @author: Ares
   * @description: 启用objectMapper忽略模块重复注册的功能
   * @time: 2023-05-08 15:27:31
   * @params: [objectMapper] objectMapper
   * @return: void
   */
  public static void enableIgnoreDuplicateModuleRegistrations(ObjectMapper objectMapper) {
    objectMapper.enable(IGNORE_DUPLICATE_MODULE_REGISTRATIONS);
  }

  public static void withSerializerModifier(BeanSerializerModifier modifier) {
    withSerializerModifier(DEFAULT_JSON_MAPPER, modifier);
  }

  public static void withSerializerModifier(ObjectMapper objectMapper,
      BeanSerializerModifier modifier) {
    objectMapper.setSerializerFactory(
        objectMapper.getSerializerFactory().withSerializerModifier(modifier));
  }

}
