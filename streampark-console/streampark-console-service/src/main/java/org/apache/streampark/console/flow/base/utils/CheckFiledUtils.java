package org.apache.streampark.console.flow.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class CheckFiledUtils {

  /**
   * Determine whether the attribute value in the object has a null value
   *
   * @param object
   * @return
   */
  public static boolean checkObjAnyFieldsIsNull(Object object) {
    if (null != object) {
      try {
        for (Field f : object.getClass().getDeclaredFields()) {
          f.setAccessible(true);
          if (f.get(object) == null || StringUtils.isBlank(f.get(object).toString())) {
            return true;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return true;
    }
    return false;
  }

  /**
   * Determine if the specified attribute value in the object is null
   *
   * @param object
   * @return
   */
  public static boolean checkObjSpecifiedFieldsIsNull(Object object, String[] arrStr) {
    if (null != object) {
      try {
        if (null != arrStr && arrStr.length > 0) {
          for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            String name = f.getName();
            if (Arrays.asList(arrStr).contains(name)) {
              if (f.get(object) == null || StringUtils.isBlank(f.get(object).toString())) {
                return true;
              }
            }
          }
        } else {
          for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(object) == null || StringUtils.isBlank(f.get(object).toString())) {
              return true;
            }
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return true;
    }
    return false;
  }
}
