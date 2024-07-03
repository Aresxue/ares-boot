package cn.ares.boot.util.common.constant;

import static cn.ares.boot.util.common.constant.StringConstant.FLOATING_POINT_NUMBER_FORMAT;
import static cn.ares.boot.util.common.constant.StringConstant.FORMAT_SPECIFIER;
import static cn.ares.boot.util.common.constant.StringConstant.NOT_ALPHABET_AND_NUMBER_REGEX;

import java.util.regex.Pattern;

/**
 * @author: Ares
 * @time: 2021-12-21 17:36:59
 * @description: Common cn.ares.boot.util.crypt.constant
 * @version: JDK 1.8
 */
public interface CommonConstant {

  Pattern FORMAT_PATTERN = Pattern.compile(FORMAT_SPECIFIER);

  Pattern FLOATING_POINT_NUMBER_PATTERN = Pattern.compile(FLOATING_POINT_NUMBER_FORMAT);

  Pattern NOT_ALPHABET_AND_NUMBER_PATTERN = Pattern.compile(NOT_ALPHABET_AND_NUMBER_REGEX);

  /**
   * false with byte type
   */
  byte BYTE_FALSE = (byte) 0;
  /**
   * true with byte type
   */
  byte BYTE_TRUE = (byte) 1;

  byte FALSE = 0;
  byte TRUE = 1;

  /**
   * Unicode 基本汉字编码范围0x4e00~0x9fa5 共 20902个
   */
  int CHINESE_CHARACTER_LENGTH = 20902;

  /**
   * 汉字起始值
   */
  int CHINESE_CHARACTER_START = 0x4e00;

}
