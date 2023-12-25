package cn.ares.boot.util.common;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author: Ares
 * @time: 2023-12-24 23:35:21
 * @description: MethodHandle util
 * @version: JDK 1.8
 */
class MethodHandleUtil {

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
  private static MethodHandles.Lookup TRUSTED_LOOKUP = null;
  private static MethodHandles.Lookup PRIVATE_LOOKUP = null;

  static {
    try {
      Method privateLookupIn = MethodHandles.class.getDeclaredMethod("privateLookupIn");
      boolean isAccessible = privateLookupIn.isAccessible();
      privateLookupIn.setAccessible(true);
      PRIVATE_LOOKUP = (MethodHandles.Lookup) privateLookupIn.invoke(null);
      privateLookupIn.setAccessible(isAccessible);
    } catch (Throwable throwable) {
      try {
        Field internal = Lookup.class.getDeclaredField("IMPL_LOOKUP");
        boolean isAccessible = internal.isAccessible();
        internal.setAccessible(true);
        TRUSTED_LOOKUP = (MethodHandles.Lookup) internal.get(null);
        internal.setAccessible(isAccessible);
      } catch (Throwable ignored) {
      }
    }
  }

  static Lookup getLookup(AccessibleObject accessibleObject, boolean forLambda) {
    // 避免重复获取实例 仅当accessibleObject不可访问时生成新的实例
    // Avoid duplicate instance acquisition A new instance is generated only when accessibleObject is unavailable
    Lookup lookup;
    if (forLambda) {
      // 兼容不同版本的jdk
      // Compatible with different jdk versions
      if (null == PRIVATE_LOOKUP) {
        if (accessibleObject instanceof Method) {
          Method method = (Method) accessibleObject;
          if (Modifier.isPublic(method.getModifiers())) {
            return LOOKUP;
          }
          method.setAccessible(true);
          lookup = TRUSTED_LOOKUP.in(method.getDeclaringClass());
        } else if (accessibleObject instanceof Constructor) {
          Constructor<?> constructor = (Constructor<?>) accessibleObject;
          if (Modifier.isPublic(constructor.getModifiers())) {
            return LOOKUP;
          }
          constructor.setAccessible(true);
          lookup = TRUSTED_LOOKUP.in((constructor.getDeclaringClass()));
        } else {
          lookup = TRUSTED_LOOKUP;
        }
        return lookup;
      } else {
        return PRIVATE_LOOKUP;
      }
    } else {
      if (accessibleObject.isAccessible()) {
        lookup = LOOKUP;
      } else {
        accessibleObject.setAccessible(true);
        return MethodHandles.lookup();
      }
    }
    return lookup;
  }

}
