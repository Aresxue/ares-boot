package cn.ares.boot.util.common;

import static cn.ares.boot.util.common.StringUtil.isEmpty;
import static cn.ares.boot.util.common.constant.CommonConstant.FLOATING_POINT_NUMBER_PATTERN;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author: Ares
 * @time: 2022-05-19 10:39:00
 * @description: Provides extra functionality for Java Number classes.
 * @version: JDK 1.8
 */
public class NumberUtil {

  /**
   * Reusable Long cn.ares.boot.util.crypt.constant for zero.
   */
  public static final Long LONG_ZERO = 0L;
  /**
   * Reusable Long cn.ares.boot.util.crypt.constant for one.
   */
  public static final Long LONG_ONE = 1L;
  /**
   * Reusable Long cn.ares.boot.util.crypt.constant for minus one.
   */
  public static final Long LONG_MINUS_ONE = -1L;
  /**
   * Reusable Integer cn.ares.boot.util.crypt.constant for zero.
   */
  public static final Integer INTEGER_ZERO = 0;
  /**
   * Reusable Integer cn.ares.boot.util.crypt.constant for one.
   */
  public static final Integer INTEGER_ONE = 1;
  /**
   * Reusable Integer cn.ares.boot.util.crypt.constant for two
   */
  public static final Integer INTEGER_TWO = 2;
  /**
   * Reusable Integer cn.ares.boot.util.crypt.constant for minus one.
   */
  public static final Integer INTEGER_MINUS_ONE = -1;
  /**
   * Reusable Short cn.ares.boot.util.crypt.constant for zero.
   */
  public static final Short SHORT_ZERO = (short) 0;
  /**
   * Reusable Short cn.ares.boot.util.crypt.constant for one.
   */
  public static final Short SHORT_ONE = (short) 1;
  /**
   * Reusable Short cn.ares.boot.util.crypt.constant for minus one.
   */
  public static final Short SHORT_MINUS_ONE = (short) -1;
  /**
   * Reusable Byte cn.ares.boot.util.crypt.constant for zero.
   */
  public static final Byte BYTE_ZERO = (byte) 0;
  /**
   * Reusable Byte cn.ares.boot.util.crypt.constant for one.
   */
  public static final Byte BYTE_ONE = (byte) 1;
  /**
   * Reusable Byte cn.ares.boot.util.crypt.constant for minus one.
   */
  public static final Byte BYTE_MINUS_ONE = (byte) -1;
  /**
   * Reusable Double cn.ares.boot.util.crypt.constant for zero.
   */
  public static final Double DOUBLE_ZERO = 0.0d;
  /**
   * Reusable Double cn.ares.boot.util.crypt.constant for one.
   */
  public static final Double DOUBLE_ONE = 1.0d;
  /**
   * Reusable Double cn.ares.boot.util.crypt.constant for minus one.
   */
  public static final Double DOUBLE_MINUS_ONE = -1.0d;
  /**
   * Reusable Float cn.ares.boot.util.crypt.constant for zero.
   */
  public static final Float FLOAT_ZERO = 0.0f;
  /**
   * Reusable Float cn.ares.boot.util.crypt.constant for one.
   */
  public static final Float FLOAT_ONE = 1.0f;
  /**
   * Reusable Float cn.ares.boot.util.crypt.constant for minus one.
   */
  public static final Float FLOAT_MINUS_ONE = -1.0f;

  /**
   * {@link Integer#MAX_VALUE} as a {@link Long}.
   */
  public static final Long LONG_INT_MAX_VALUE = (long) Integer.MAX_VALUE;

  /**
   * {@link Integer#MIN_VALUE} as a {@link Long}.
   */
  public static final Long LONG_INT_MIN_VALUE = (long) Integer.MIN_VALUE;


  /**
   * <p>{@code NumberUtil} instances should NOT be constructed in standard programming.
   * Instead, the class should be used as {@code NumberUtil.toInt("6");}.</p>
   *
   * <p>This constructor is public to permit tools that require a JavaBean instance
   * to operate.</p>
   */
  public NumberUtil() {
  }

  //-----------------------------------------------------------------------

  /**
   * <p>Convert a {@code String} to an {@code int}, returning
   * {@code zero} if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, {@code zero} is returned.</p>
   *
   * <pre>
   *   NumberUtil.toInt(null) = 0
   *   NumberUtil.toInt("")   = 0
   *   NumberUtil.toInt("1")  = 1
   * </pre>
   *
   * @param str the string to convert, may be null
   * @return the int represented by the string, or {@code zero} if conversion fails
   */
  public static int toInt(final String str) {
    return toInt(str, 0);
  }

  /**
   * <p>Convert a {@code String} to an {@code int}, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, the default value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toInt(null, 1) = 1
   *   NumberUtil.toInt("", 1)   = 1
   *   NumberUtil.toInt("1", 0)  = 1
   * </pre>
   *
   * @param str          the string to convert, may be null
   * @param defaultValue the default value
   * @return the int represented by the string, or the default if conversion fails
   */
  public static int toInt(final String str, final int defaultValue) {
    if (str == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a {@code String} to a {@code long}, returning
   * {@code zero} if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, {@code zero} is returned.</p>
   *
   * <pre>
   *   NumberUtil.toLong(null) = 0L
   *   NumberUtil.toLong("")   = 0L
   *   NumberUtil.toLong("1")  = 1L
   * </pre>
   *
   * @param str the string to convert, may be null
   * @return the long represented by the string, or {@code 0} if conversion fails
   */
  public static long toLong(final String str) {
    return toLong(str, 0L);
  }

  /**
   * <p>Convert a {@code String} to a {@code long}, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, the default value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toLong(null, 1L) = 1L
   *   NumberUtil.toLong("", 1L)   = 1L
   *   NumberUtil.toLong("1", 0L)  = 1L
   * </pre>
   *
   * @param str          the string to convert, may be null
   * @param defaultValue the default value
   * @return the long represented by the string, or the default if conversion fails
   */
  public static long toLong(final String str, final long defaultValue) {
    if (str == null) {
      return defaultValue;
    }
    try {
      return Long.parseLong(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a {@code String} to a {@code float}, returning
   * {@code 0.0f} if the conversion fails.</p>
   *
   * <p>If the string {@code str} is {@code null},
   * {@code 0.0f} is returned.</p>
   *
   * <pre>
   *   NumberUtil.toFloat(null)   = 0.0f
   *   NumberUtil.toFloat("")     = 0.0f
   *   NumberUtil.toFloat("1.5")  = 1.5f
   * </pre>
   *
   * @param str the string to convert, may be {@code null}
   * @return the float represented by the string, or {@code 0.0f} if conversion fails
   */
  public static float toFloat(final String str) {
    return toFloat(str, 0.0f);
  }

  /**
   * <p>Convert a {@code String} to a {@code float}, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string {@code str} is {@code null}, the default
   * value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toFloat(null, 1.1f)   = 1.0f
   *   NumberUtil.toFloat("", 1.1f)     = 1.1f
   *   NumberUtil.toFloat("1.5", 0.0f)  = 1.5f
   * </pre>
   *
   * @param str          the string to convert, may be {@code null}
   * @param defaultValue the default value
   * @return the float represented by the string, or defaultValue if conversion fails
   */
  public static float toFloat(final String str, final float defaultValue) {
    if (str == null) {
      return defaultValue;
    }
    try {
      return Float.parseFloat(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a {@code String} to a {@code double}, returning
   * {@code 0.0d} if the conversion fails.</p>
   *
   * <p>If the string {@code str} is {@code null},
   * {@code 0.0d} is returned.</p>
   *
   * <pre>
   *   NumberUtil.toDouble(null)   = 0.0d
   *   NumberUtil.toDouble("")     = 0.0d
   *   NumberUtil.toDouble("1.5")  = 1.5d
   * </pre>
   *
   * @param str the string to convert, may be {@code null}
   * @return the double represented by the string, or {@code 0.0d} if conversion fails
   */
  public static double toDouble(final String str) {
    return toDouble(str, 0.0d);
  }

  /**
   * <p>Convert a {@code String} to a {@code double}, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string {@code str} is {@code null}, the default
   * value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toDouble(null, 1.1d)   = 1.1d
   *   NumberUtil.toDouble("", 1.1d)     = 1.1d
   *   NumberUtil.toDouble("1.5", 0.0d)  = 1.5d
   * </pre>
   *
   * @param str          the string to convert, may be {@code null}
   * @param defaultValue the default value
   * @return the double represented by the string, or defaultValue if conversion fails
   */
  public static double toDouble(final String str, final double defaultValue) {
    if (str == null) {
      return defaultValue;
    }
    try {
      return Double.parseDouble(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a {@code BigDecimal} to a {@code double}.</p>
   *
   * <p>If the {@code BigDecimal} {@code value} is
   * {@code null}, then the specified default value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toDouble(null)                     = 0.0d
   *   NumberUtil.toDouble(BigDecimal.valueOf(8.5d)) = 8.5d
   * </pre>
   *
   * @param value the {@code BigDecimal} to convert, may be {@code null}.
   * @return the double represented by the {@code BigDecimal} or {@code 0.0d} if the {@code
   * BigDecimal} is {@code null}.
   */
  public static double toDouble(final BigDecimal value) {
    return toDouble(value, 0.0d);
  }

  /**
   * <p>Convert a {@code BigDecimal} to a {@code double}.</p>
   *
   * <p>If the {@code BigDecimal} {@code value} is
   * {@code null}, then the specified default value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toDouble(null, 1.1d)                     = 1.1d
   *   NumberUtil.toDouble(BigDecimal.valueOf(8.5d), 1.1d) = 8.5d
   * </pre>
   *
   * @param value        the {@code BigDecimal} to convert, may be {@code null}.
   * @param defaultValue the default value
   * @return the double represented by the {@code BigDecimal} or the defaultValue if the {@code
   * BigDecimal} is {@code null}.
   */
  public static double toDouble(final BigDecimal value, final double defaultValue) {
    return value == null ? defaultValue : value.doubleValue();
  }

  //-----------------------------------------------------------------------

  /**
   * <p>Convert a {@code String} to a {@code byte}, returning
   * {@code zero} if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, {@code zero} is returned.</p>
   *
   * <pre>
   *   NumberUtil.toByte(null) = 0
   *   NumberUtil.toByte("")   = 0
   *   NumberUtil.toByte("1")  = 1
   * </pre>
   *
   * @param str the string to convert, may be null
   * @return the byte represented by the string, or {@code zero} if conversion fails
   */
  public static byte toByte(final String str) {
    return toByte(str, (byte) 0);
  }

  /**
   * <p>Convert a {@code String} to a {@code byte}, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, the default value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toByte(null, 1) = 1
   *   NumberUtil.toByte("", 1)   = 1
   *   NumberUtil.toByte("1", 0)  = 1
   * </pre>
   *
   * @param str          the string to convert, may be null
   * @param defaultValue the default value
   * @return the byte represented by the string, or the default if conversion fails
   */
  public static byte toByte(final String str, final byte defaultValue) {
    if (str == null) {
      return defaultValue;
    }
    try {
      return Byte.parseByte(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * <p>Convert a {@code String} to a {@code short}, returning
   * {@code zero} if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, {@code zero} is returned.</p>
   *
   * <pre>
   *   NumberUtil.toShort(null) = 0
   *   NumberUtil.toShort("")   = 0
   *   NumberUtil.toShort("1")  = 1
   * </pre>
   *
   * @param str the string to convert, may be null
   * @return the short represented by the string, or {@code zero} if conversion fails
   */
  public static short toShort(final String str) {
    return toShort(str, (short) 0);
  }

  /**
   * <p>Convert a {@code String} to an {@code short}, returning a
   * default value if the conversion fails.</p>
   *
   * <p>If the string is {@code null}, the default value is returned.</p>
   *
   * <pre>
   *   NumberUtil.toShort(null, 1) = 1
   *   NumberUtil.toShort("", 1)   = 1
   *   NumberUtil.toShort("1", 0)  = 1
   * </pre>
   *
   * @param str          the string to convert, may be null
   * @param defaultValue the default value
   * @return the short represented by the string, or the default if conversion fails
   */
  public static short toShort(final String str, final short defaultValue) {
    if (str == null) {
      return defaultValue;
    }
    try {
      return Short.parseShort(str);
    } catch (final NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   * Convert a {@code BigDecimal} to a {@code BigDecimal} with a scale of two that has been rounded
   * using {@code RoundingMode.HALF_EVEN}. If the supplied {@code value} is null, then {@code
   * BigDecimal.ZERO} is returned.
   *
   * <p>Note, the scale of a {@code BigDecimal} is the number of digits to the right of the
   * decimal point.</p>
   *
   * @param value the {@code BigDecimal} to convert, may be null.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final BigDecimal value) {
    return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
  }

  /**
   * Convert a {@code BigDecimal} to a {@code BigDecimal} whose scale is the specified value with a
   * {@code RoundingMode} applied. If the input {@code value} is {@code null}, we simply return
   * {@code BigDecimal.ZERO}.
   *
   * @param value        the {@code BigDecimal} to convert, may be null.
   * @param scale        the number of digits to the right of the decimal point.
   * @param roundingMode a rounding behavior for numerical operations capable of discarding
   *                     precision.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final BigDecimal value, final int scale,
      final RoundingMode roundingMode) {
    if (value == null) {
      return BigDecimal.ZERO;
    }
    return value.setScale(scale, (roundingMode == null) ? RoundingMode.HALF_EVEN : roundingMode
    );
  }

  /**
   * Convert a {@code Float} to a {@code BigDecimal} with a scale of two that has been rounded using
   * {@code RoundingMode.HALF_EVEN}. If the supplied {@code value} is null, then {@code
   * BigDecimal.ZERO} is returned.
   *
   * <p>Note, the scale of a {@code BigDecimal} is the number of digits to the right of the
   * decimal point.</p>
   *
   * @param value the {@code Float} to convert, may be null.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final Float value) {
    return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
  }

  /**
   * Convert a {@code Float} to a {@code BigDecimal} whose scale is the specified value with a
   * {@code RoundingMode} applied. If the input {@code value} is {@code null}, we simply return
   * {@code BigDecimal.ZERO}.
   *
   * @param value        the {@code Float} to convert, may be null.
   * @param scale        the number of digits to the right of the decimal point.
   * @param roundingMode a rounding behavior for numerical operations capable of discarding
   *                     precision.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final Float value, final int scale,
      final RoundingMode roundingMode) {
    if (value == null) {
      return BigDecimal.ZERO;
    }
    return toScaledBigDecimal(BigDecimal.valueOf(value), scale, roundingMode);
  }

  /**
   * Convert a {@code Double} to a {@code BigDecimal} with a scale of two that has been rounded
   * using {@code RoundingMode.HALF_EVEN}. If the supplied {@code value} is null, then {@code
   * BigDecimal.ZERO} is returned.
   *
   * <p>Note, the scale of a {@code BigDecimal} is the number of digits to the right of the
   * decimal point.</p>
   *
   * @param value the {@code Double} to convert, may be null.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final Double value) {
    return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
  }

  /**
   * Convert a {@code Double} to a {@code BigDecimal} whose scale is the specified value with a
   * {@code RoundingMode} applied. If the input {@code value} is {@code null}, we simply return
   * {@code BigDecimal.ZERO}.
   *
   * @param value        the {@code Double} to convert, may be null.
   * @param scale        the number of digits to the right of the decimal point.
   * @param roundingMode a rounding behavior for numerical operations capable of discarding
   *                     precision.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final Double value, final int scale,
      final RoundingMode roundingMode) {
    if (value == null) {
      return BigDecimal.ZERO;
    }
    return toScaledBigDecimal(
        BigDecimal.valueOf(value),
        scale,
        roundingMode
    );
  }

  /**
   * Convert a {@code String} to a {@code BigDecimal} with a scale of two that has been rounded
   * using {@code RoundingMode.HALF_EVEN}. If the supplied {@code value} is null, then {@code
   * BigDecimal.ZERO} is returned.
   *
   * <p>Note, the scale of a {@code BigDecimal} is the number of digits to the right of the
   * decimal point.</p>
   *
   * @param value the {@code String} to convert, may be null.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final String value) {
    return toScaledBigDecimal(value, INTEGER_TWO, RoundingMode.HALF_EVEN);
  }

  /**
   * Convert a {@code String} to a {@code BigDecimal} whose scale is the specified value with a
   * {@code RoundingMode} applied. If the input {@code value} is {@code null}, we simply return
   * {@code BigDecimal.ZERO}.
   *
   * @param value        the {@code String} to convert, may be null.
   * @param scale        the number of digits to the right of the decimal point.
   * @param roundingMode a rounding behavior for numerical operations capable of discarding
   *                     precision.
   * @return the scaled, with appropriate rounding, {@code BigDecimal}.
   */
  public static BigDecimal toScaledBigDecimal(final String value, final int scale,
      final RoundingMode roundingMode) {
    if (value == null) {
      return BigDecimal.ZERO;
    }
    return toScaledBigDecimal(
        createBigDecimal(value),
        scale,
        roundingMode
    );
  }

  //-----------------------------------------------------------------------
  // must handle Long, Float, Integer, Float, Short,
  //                  BigDecimal, BigInteger and Byte
  // useful methods:
  // Byte.decode(String)
  // Byte.valueOf(String, int radix)
  // Byte.valueOf(String)
  // Double.valueOf(String)
  // Float.valueOf(String)
  // Float.valueOf(String)
  // Integer.valueOf(String, int radix)
  // Integer.valueOf(String)
  // Integer.decode(String)
  // Integer.getInteger(String)
  // Integer.getInteger(String, int val)
  // Integer.getInteger(String, Integer val)
  // Integer.valueOf(String)
  // Double.valueOf(String)
  // new Byte(String)
  // Long.valueOf(String)
  // Long.getLong(String)
  // Long.getLong(String, int)
  // Long.getLong(String, Integer)
  // Long.valueOf(String, int)
  // Long.valueOf(String)
  // Short.valueOf(String)
  // Short.decode(String)
  // Short.valueOf(String, int)
  // Short.valueOf(String)
  // new BigDecimal(String)
  // new BigInteger(String)
  // new BigInteger(String, int radix)
  // Possible inputs:
  // 45 45.5 45E7 4.5E7 Hex Oct Binary xxxF xxxD xxxf xxxd
  // plus minus everything. Probably more. A lot are not separable.

  /**
   * <p>Turns a string value into a java.lang.Number.</p>
   *
   * <p>If the string starts with {@code 0x} or {@code -0x} (lower or upper case) or {@code #} or
   * {@code -#}, it will be interpreted as a hexadecimal Integer - or Long, if the number of digits
   * after the prefix is more than 8 - or BigInteger if there are more than 16 digits.
   * </p>
   * <p>Then, the value is examined for a type qualifier on the end, i.e. one of
   * {@code 'f', 'F', 'd', 'D', 'l', 'L'}.  If it is found, it starts trying to create successively
   * larger types from the type specified until one is found that can represent the value.</p>
   *
   * <p>If a type specifier is not found, it will check for a decimal point
   * and then try successively larger types from {@code Integer} to {@code BigInteger} and from
   * {@code Float} to {@code BigDecimal}.</p>
   *
   * <p>
   * Integral values with a leading {@code 0} will be interpreted as octal; the returned number will
   * be Integer, Long or BigDecimal as appropriate.
   * </p>
   *
   * <p>Returns {@code null} if the string is {@code null}.</p>
   *
   * <p>This method does not trim the input string, i.e., strings with leading
   * or trailing spaces will generate NumberFormatExceptions.</p>
   *
   * @param str String containing a number, may be null
   * @return Number created from the string (or null if the input is null)
   * @throws NumberFormatException if the value cannot be converted
   */
  public static Number createNumber(final String str) {
    if (str == null) {
      return null;
    }
    if (StringUtil.isBlank(str)) {
      throw new NumberFormatException("A blank string is not a valid number");
    }
    // need to deal with all possible hex prefixes here
    final String[] hexPrefixes = {"0x", "0X", "-0x", "-0X", "#", "-#"};
    final int length = str.length();
    int pfxLen = 0;
    for (final String pfx : hexPrefixes) {
      if (str.startsWith(pfx)) {
        pfxLen += pfx.length();
        break;
      }
    }
    // we have a hex number
    if (pfxLen > 0) {
      // strip leading zeroes
      char firstSigDigit = 0;
      for (int i = pfxLen; i < length; i++) {
        firstSigDigit = str.charAt(i);
        // count leading zeroes
        if (firstSigDigit == '0') {
          pfxLen++;
        } else {
          break;
        }
      }
      final int hexDigits = length - pfxLen;
      // too many for Long
      if (hexDigits > 16 || hexDigits == 16 && firstSigDigit > '7') {
        return createBigInteger(str);
      }
      // too many for an int
      if (hexDigits > 8 || hexDigits == 8 && firstSigDigit > '7') {
        return createLong(str);
      }
      return createInteger(str);
    }
    final char lastChar = str.charAt(length - 1);
    final String man;
    final String dec;
    final String exp;
    final int decPos = str.indexOf('.');
    // assumes both not present
    final int expPos = str.indexOf('e') + str.indexOf('E') + 1;
    // if both e and E are present, this is caught by the checks on expPos (which prevent IOOBE)
    // and the parsing which will detect if e or E appear in a number due to using the wrong offset

    // there is a decimal point
    if (decPos > -1) {
      // there is an exponent
      if (expPos > -1) {
        // prevents double exponent causing IOOBE
        if (expPos < decPos || expPos > length) {
          throw new NumberFormatException(str + " is not a valid number.");
        }
        dec = str.substring(decPos + 1, expPos);
      } else {
        dec = str.substring(decPos + 1);
      }
      man = getMantissa(str, decPos);
    } else {
      if (expPos > -1) {
        // prevents double exponent causing IOOBE
        if (expPos > length) {
          throw new NumberFormatException(str + " is not a valid number.");
        }
        man = getMantissa(str, expPos);
      } else {
        man = getMantissa(str);
      }
      dec = null;
    }
    if (!Character.isDigit(lastChar) && lastChar != '.') {
      if (expPos > -1 && expPos < length - 1) {
        exp = str.substring(expPos + 1, length - 1);
      } else {
        exp = null;
      }
      // Requesting a specific type
      final String numeric = str.substring(0, length - 1);
      final boolean allZeros = isAllZeros(man) && isAllZeros(exp);
      switch (lastChar) {
        case 'l':
        case 'L':
          if (dec == null && exp == null && (
              !numeric.isEmpty() && numeric.charAt(0) == '-' && isDigits(numeric.substring(1))
                  || isDigits(numeric))) {
            try {
              return createLong(numeric);
            } catch (final NumberFormatException nfe) {
              // Too big for a long
            }
            return createBigInteger(numeric);

          }
          throw new NumberFormatException(str + " is not a valid number.");
        case 'f':
        case 'F':
          try {
            final Float f = createFloat(str);
            if (!(f.isInfinite() || f == 0.0F && !allZeros)) {
              // If it's too big for a float or the float value = 0 and the string
              // has non-zeros in it, then float does not have the precision we want
              return f;
            }

          } catch (final NumberFormatException nfe) {
            // ignore the bad number
          }
          // $FALL-THROUGH$
        case 'd':
        case 'D':
          try {
            final Double d = createDouble(str);
            if (!(d.isInfinite() || d == 0.0D && !allZeros)) {
              return d;
            }
          } catch (final NumberFormatException nfe) {
            // ignore the bad number
          }
          try {
            return createBigDecimal(numeric);
          } catch (final NumberFormatException e) {
            // ignore the bad number
          }
          // $FALL-THROUGH$
        default:
          throw new NumberFormatException(str + " is not a valid number.");

      }
    }
    // ser doesn't have a preference on the return type, so let's start
    // small and go from there...
    if (expPos > -1 && expPos < length - 1) {
      exp = str.substring(expPos + 1);
    } else {
      exp = null;
    }
    if (dec == null && exp == null) {
      // no decimal point and no exponent
      // Must be an Integer, Long, Biginteger
      try {
        return createInteger(str);
      } catch (final NumberFormatException nfe) {
        // ignore the bad number
      }
      try {
        return createLong(str);
      } catch (final NumberFormatException nfe) {
        // ignore the bad number
      }
      return createBigInteger(str);
    }

    // must be a Float, Double, BigDecimal
    final boolean allZeros = isAllZeros(man) && isAllZeros(exp);
    try {
      final Float f = createFloat(str);
      final Double d = createDouble(str);
      if (!f.isInfinite()
          && !(f == 0.0F && !allZeros)
          && f.toString().equals(d.toString())) {
        return f;
      }
      if (!d.isInfinite() && !(d == 0.0D && !allZeros)) {
        final BigDecimal b = createBigDecimal(str);
        if (b.compareTo(BigDecimal.valueOf(d)) == 0) {
          return d;
        }
        return b;
      }
    } catch (final NumberFormatException nfe) {
      // ignore the bad number
    }
    return createBigDecimal(str);
  }

  /**
   * <p>Utility method for {@link #createNumber(java.lang.String)}.</p>
   *
   * <p>Returns mantissa of the given number.</p>
   *
   * @param str the string representation of the number
   * @return mantissa of the given number
   */
  private static String getMantissa(final String str) {
    return getMantissa(str, str.length());
  }

  /**
   * <p>Utility method for {@link #createNumber(java.lang.String)}.</p>
   *
   * <p>Returns mantissa of the given number.</p>
   *
   * @param str     the string representation of the number
   * @param stopPos the position of the exponent or decimal point
   * @return mantissa of the given number
   */
  private static String getMantissa(final String str, final int stopPos) {
    final char firstChar = str.charAt(0);
    final boolean hasSign = firstChar == '-' || firstChar == '+';

    return hasSign ? str.substring(1, stopPos) : str.substring(0, stopPos);
  }

  /**
   * <p>Utility method for {@link #createNumber(java.lang.String)}.</p>
   *
   * <p>Returns {@code true} if s is {@code null}.</p>
   *
   * @param str the String to check
   * @return if it is all zeros or {@code null}
   */
  private static boolean isAllZeros(final String str) {
    if (str == null) {
      return true;
    }
    for (int i = str.length() - 1; i >= 0; i--) {
      if (str.charAt(i) != '0') {
        return false;
      }
    }
    return !str.isEmpty();
  }

  //-----------------------------------------------------------------------

  /**
   * <p>Convert a {@code String} to a {@code Float}.</p>
   *
   * <p>Returns {@code null} if the string is {@code null}.</p>
   *
   * @param str a {@code String} to convert, may be null
   * @return converted {@code Float} (or null if the input is null)
   * @throws NumberFormatException if the value cannot be converted
   */
  public static Float createFloat(final String str) {
    if (str == null) {
      return null;
    }
    return Float.valueOf(str);
  }

  /**
   * <p>Convert a {@code String} to a {@code Double}.</p>
   *
   * <p>Returns {@code null} if the string is {@code null}.</p>
   *
   * @param str a {@code String} to convert, may be null
   * @return converted {@code Double} (or null if the input is null)
   * @throws NumberFormatException if the value cannot be converted
   */
  public static Double createDouble(final String str) {
    if (str == null) {
      return null;
    }
    return Double.valueOf(str);
  }

  /**
   * <p>Convert a {@code String} to a {@code Integer}, handling
   * hex (0xhhhh) and octal (0dddd) notations. N.B. a leading zero means octal; spaces are not
   * trimmed.</p>
   *
   * <p>Returns {@code null} if the string is {@code null}.</p>
   *
   * @param str a {@code String} to convert, may be null
   * @return converted {@code Integer} (or null if the input is null)
   * @throws NumberFormatException if the value cannot be converted
   */
  public static Integer createInteger(final String str) {
    if (str == null) {
      return null;
    }
    // decode() handles 0xAABD and 0777 (hex and octal) as well.
    return Integer.decode(str);
  }

  /**
   * <p>Convert a {@code String} to a {@code Long};
   * since 3.1 it handles hex (0xhhhh) and octal (0ddd) notations. N.B. a leading zero means octal;
   * spaces are not trimmed.</p>
   *
   * <p>Returns {@code null} if the string is {@code null}.</p>
   *
   * @param str a {@code String} to convert, may be null
   * @return converted {@code Long} (or null if the input is null)
   * @throws NumberFormatException if the value cannot be converted
   */
  public static Long createLong(final String str) {
    if (str == null) {
      return null;
    }
    return Long.decode(str);
  }

  /**
   * <p>Convert a {@code String} to a {@code BigInteger};
   * since 3.2 it handles hex (0x or #) and octal (0) notations.</p>
   *
   * <p>Returns {@code null} if the string is {@code null}.</p>
   *
   * @param str a {@code String} to convert, may be null
   * @return converted {@code BigInteger} (or null if the input is null)
   * @throws NumberFormatException if the value cannot be converted
   */
  public static BigInteger createBigInteger(final String str) {
    if (str == null) {
      return null;
    }
    // offset within string
    int pos = 0;
    int radix = 10;
    // need to negate later
    boolean negate = false;
    if (str.startsWith("-")) {
      negate = true;
      pos = 1;
    }
    // hex
    if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) {
      radix = 16;
      pos += 2;
    }
    // alternative hex (allowed by Long/Integer)
    else if (str.startsWith("#", pos)) {
      radix = 16;
      pos++;
    }
    // octal; so long as there are additional digits
    else if (str.startsWith("0", pos) && str.length() > pos + 1) {
      radix = 8;
      pos++;
      // default is to treat as decimal
    }

    final BigInteger value = new BigInteger(str.substring(pos), radix);
    return negate ? value.negate() : value;
  }

  /**
   * <p>Convert a {@code String} to a {@code BigDecimal}.</p>
   *
   * <p>Returns {@code null} if the string is {@code null}.</p>
   *
   * @param str a {@code String} to convert, may be null
   * @return converted {@code BigDecimal} (or null if the input is null)
   * @throws NumberFormatException if the value cannot be converted
   */
  public static BigDecimal createBigDecimal(final String str) {
    if (str == null) {
      return null;
    }
    // handle JDK1.3.1 bug where "" throws IndexOutOfBoundsException
    if (StringUtil.isBlank(str)) {
      throw new NumberFormatException("A blank string is not a valid number");
    }
    return new BigDecimal(str);
  }

  // Min in array
  //--------------------------------------------------------------------

  /**
   * <p>Returns the minimum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the minimum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static long min(final long... array) {
    // Validates input
    validateArray(array);

    // Finds and returns min
    long min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    }

    return min;
  }

  /**
   * <p>Returns the minimum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the minimum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static int min(final int... array) {
    // Validates input
    validateArray(array);

    // Finds and returns min
    int min = array[0];
    for (int j = 1; j < array.length; j++) {
      if (array[j] < min) {
        min = array[j];
      }
    }

    return min;
  }

  /**
   * <p>Returns the minimum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the minimum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static short min(final short... array) {
    // Validates input
    validateArray(array);

    // Finds and returns min
    short min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    }

    return min;
  }

  /**
   * <p>Returns the minimum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the minimum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static byte min(final byte... array) {
    // Validates input
    validateArray(array);

    // Finds and returns min
    byte min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    }

    return min;
  }

  /**
   * <p>Returns the minimum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the minimum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty differently
   */
  public static double min(final double... array) {
    // Validates input
    validateArray(array);

    // Finds and returns min
    double min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (Double.isNaN(array[i])) {
        return Double.NaN;
      }
      if (array[i] < min) {
        min = array[i];
      }
    }

    return min;
  }

  /**
   * <p>Returns the minimum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the minimum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty differently
   */
  public static float min(final float... array) {
    // Validates input
    validateArray(array);

    // Finds and returns min
    float min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (Float.isNaN(array[i])) {
        return Float.NaN;
      }
      if (array[i] < min) {
        min = array[i];
      }
    }

    return min;
  }

  // Max in array
  //--------------------------------------------------------------------

  /**
   * <p>Returns the maximum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the maximum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static long max(final long... array) {
    // Validates input
    validateArray(array);

    // Finds and returns max
    long max = array[0];
    for (int j = 1; j < array.length; j++) {
      if (array[j] > max) {
        max = array[j];
      }
    }

    return max;
  }

  /**
   * <p>Returns the maximum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the maximum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static int max(final int... array) {
    // Validates input
    validateArray(array);

    // Finds and returns max
    int max = array[0];
    for (int j = 1; j < array.length; j++) {
      if (array[j] > max) {
        max = array[j];
      }
    }

    return max;
  }

  /**
   * <p>Returns the maximum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the maximum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static short max(final short... array) {
    // Validates input
    validateArray(array);

    // Finds and returns max
    short max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    }

    return max;
  }

  /**
   * <p>Returns the maximum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the maximum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static byte max(final byte... array) {
    // Validates input
    validateArray(array);

    // Finds and returns max
    byte max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    }

    return max;
  }

  /**
   * <p>Returns the maximum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the maximum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty differently
   */
  public static double max(final double... array) {
    // Validates input
    validateArray(array);

    // Finds and returns max
    double max = array[0];
    for (int j = 1; j < array.length; j++) {
      if (Double.isNaN(array[j])) {
        return Double.NaN;
      }
      if (array[j] > max) {
        max = array[j];
      }
    }

    return max;
  }

  /**
   * <p>Returns the maximum value in an array.</p>
   *
   * @param array an array, must not be null or empty
   * @return the maximum value in the array
   * @throws IllegalArgumentException if {@code array} is {@code null}
   * @throws IllegalArgumentException if {@code array} is empty differently
   */
  public static float max(final float... array) {
    // Validates input
    validateArray(array);

    // Finds and returns max
    float max = array[0];
    for (int j = 1; j < array.length; j++) {
      if (Float.isNaN(array[j])) {
        return Float.NaN;
      }
      if (array[j] > max) {
        max = array[j];
      }
    }

    return max;
  }

  /**
   * Checks if the specified array is neither null nor empty.
   *
   * @param array the array to check
   * @throws IllegalArgumentException if {@code array} is either {@code null} or empty
   */
  private static void validateArray(final Object array) {
    notNull(array);
    isTrue(Array.getLength(array) != 0);
  }

  // 3 param min
  //-----------------------------------------------------------------------

  /**
   * <p>Gets the minimum of three {@code long} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the smallest of the values
   */
  public static long min(long a, final long b, final long c) {
    if (b < a) {
      a = b;
    }
    if (c < a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the minimum of three {@code int} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the smallest of the values
   */
  public static int min(int a, final int b, final int c) {
    if (b < a) {
      a = b;
    }
    if (c < a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the minimum of three {@code short} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the smallest of the values
   */
  public static short min(short a, final short b, final short c) {
    if (b < a) {
      a = b;
    }
    if (c < a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the minimum of three {@code byte} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the smallest of the values
   */
  public static byte min(byte a, final byte b, final byte c) {
    if (b < a) {
      a = b;
    }
    if (c < a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the minimum of three {@code double} values.</p>
   *
   * <p>If any value is {@code NaN}, {@code NaN} is
   * returned. Infinity is handled.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the smallest of the values differently
   */
  public static double min(final double a, final double b, final double c) {
    return Math.min(Math.min(a, b), c);
  }

  /**
   * <p>Gets the minimum of three {@code float} values.</p>
   *
   * <p>If any value is {@code NaN}, {@code NaN} is
   * returned. Infinity is handled.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the smallest of the values differently
   */
  public static float min(final float a, final float b, final float c) {
    return Math.min(Math.min(a, b), c);
  }

  // 3 param max
  //-----------------------------------------------------------------------

  /**
   * <p>Gets the maximum of three {@code long} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the largest of the values
   */
  public static long max(long a, final long b, final long c) {
    if (b > a) {
      a = b;
    }
    if (c > a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the maximum of three {@code int} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the largest of the values
   */
  public static int max(int a, final int b, final int c) {
    if (b > a) {
      a = b;
    }
    if (c > a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the maximum of three {@code short} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the largest of the values
   */
  public static short max(short a, final short b, final short c) {
    if (b > a) {
      a = b;
    }
    if (c > a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the maximum of three {@code byte} values.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the largest of the values
   */
  public static byte max(byte a, final byte b, final byte c) {
    if (b > a) {
      a = b;
    }
    if (c > a) {
      a = c;
    }
    return a;
  }

  /**
   * <p>Gets the maximum of three {@code double} values.</p>
   *
   * <p>If any value is {@code NaN}, {@code NaN} is
   * returned. Infinity is handled.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the largest of the values differently
   */
  public static double max(final double a, final double b, final double c) {
    return Math.max(Math.max(a, b), c);
  }

  /**
   * <p>Gets the maximum of three {@code float} values.</p>
   *
   * <p>If any value is {@code NaN}, {@code NaN} is
   * returned. Infinity is handled.</p>
   *
   * @param a value 1
   * @param b value 2
   * @param c value 3
   * @return the largest of the values differently
   */
  public static float max(final float a, final float b, final float c) {
    return Math.max(Math.max(a, b), c);
  }

  //-----------------------------------------------------------------------

  /**
   * <p>Checks whether the {@code String} contains only
   * digit characters.</p>
   *
   * <p>{@code Null} and empty String will return
   * {@code false}.</p>
   *
   * @param str the {@code String} to check
   * @return {@code true} if str contains only Unicode numeric
   */
  public static boolean isDigits(final String str) {
    return StringUtil.isNumeric(str);
  }

  /**
   * <p>Checks whether the String a valid Java number.</p>
   *
   * <p>Valid numbers include hexadecimal marked with the {@code 0x} or
   * {@code 0X} qualifier, octal numbers, scientific notation and numbers marked with a type
   * qualifier (e.g. 123L).</p>
   *
   * <p>Non-hexadecimal strings beginning with a leading zero are
   * treated as octal values. Thus the string {@code 09} will return {@code false}, since {@code 9}
   * is not a valid octal value. However, numbers beginning with {@code 0.} are treated as
   * decimal.</p>
   *
   * <p>{@code null} and empty/blank {@code String} will return
   * {@code false}.</p>
   *
   * <p>Note, {@link #createNumber(String)} should return a number for every
   * input resulting in {@code true}.</p>
   *
   * @param str the {@code String} to check
   * @return {@code true} if the string is a correctly formatted number
   * @since 3.5
   */
  public static boolean isCreatable(final String str) {
    if (isEmpty(str)) {
      return false;
    }
    final char[] chars = str.toCharArray();
    int sz = chars.length;
    boolean hasExp = false;
    boolean hasDecPoint = false;
    boolean allowSigns = false;
    boolean foundDigit = false;
    // deal with any possible sign up front
    final int start = chars[0] == '-' || chars[0] == '+' ? 1 : 0;
    // leading 0, skip if is a decimal number
    if (sz > start + 1 && chars[start] == '0' && !StringUtil.contains(str, '.')) {
      if (chars[start + 1] == 'x' || chars[start + 1] == 'X') { // leading 0x/0X
        int i = start + 2;
        if (i == sz) {
          return false; // str == "0x"
        }
        // checking hex (it can't be anything else)
        for (; i < chars.length; i++) {
          if ((chars[i] < '0' || chars[i] > '9')
              && (chars[i] < 'a' || chars[i] > 'f')
              && (chars[i] < 'A' || chars[i] > 'F')) {
            return false;
          }
        }
        return true;
      } else if (Character.isDigit(chars[start + 1])) {
        // leading 0, but not hex, must be octal
        int i = start + 1;
        for (; i < chars.length; i++) {
          if (chars[i] < '0' || chars[i] > '7') {
            return false;
          }
        }
        return true;
      }
    }
    sz--; // don't want to loop to the last char, check it afterwords
    // for type qualifiers
    int i = start;
    // loop to the next to last char or to the last char if we need another digit to
    // make a valid number (e.g. chars[0..5] = "1234E")
    while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
      if (chars[i] >= '0' && chars[i] <= '9') {
        foundDigit = true;
        allowSigns = false;

      } else if (chars[i] == '.') {
        if (hasDecPoint || hasExp) {
          // two decimal points or dec in exponent
          return false;
        }
        hasDecPoint = true;
      } else if (chars[i] == 'e' || chars[i] == 'E') {
        // we've already taken care of hex.
        if (hasExp) {
          // two E's
          return false;
        }
        if (!foundDigit) {
          return false;
        }
        hasExp = true;
        allowSigns = true;
      } else if (chars[i] == '+' || chars[i] == '-') {
        if (!allowSigns) {
          return false;
        }
        allowSigns = false;
        foundDigit = false; // we need a digit after the E
      } else {
        return false;
      }
      i++;
    }
    if (i < chars.length) {
      if (chars[i] >= '0' && chars[i] <= '9') {
        // no type qualifier, OK
        return true;
      }
      if (chars[i] == 'e' || chars[i] == 'E') {
        // can't have an E at the last byte
        return false;
      }
      if (chars[i] == '.') {
        if (hasDecPoint || hasExp) {
          // two decimal points or dec in exponent
          return false;
        }
        // single trailing decimal point after non-exponent is ok
        return foundDigit;
      }
      if (!allowSigns
          && (chars[i] == 'd'
          || chars[i] == 'D'
          || chars[i] == 'f'
          || chars[i] == 'F')) {
        return foundDigit;
      }
      if (chars[i] == 'l'
          || chars[i] == 'L') {
        // not allowing L with an exponent or decimal point
        return foundDigit && !hasExp && !hasDecPoint;
      }
      // last character is illegal
      return false;
    }
    // allowSigns is true iff the val ends in 'E'
    // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
    return !allowSigns && foundDigit;
  }

  /**
   * <p>Checks whether the given String is a parsable number.</p>
   *
   * <p>Parsable numbers include those Strings understood by {@link Integer#parseInt(String)},
   * {@link Long#parseLong(String)}, {@link Float#parseFloat(String)} or {@link
   * Double#parseDouble(String)}. This method can be used instead of catching {@link
   * java.text.ParseException} when calling one of those methods.</p>
   *
   * <p>Hexadecimal and scientific notations are <strong>not</strong> considered parsable.
   * See {@link #isCreatable(String)} on those cases.</p>
   *
   * <p>{@code Null} and empty String will return {@code false}.</p>
   *
   * @param str the String to check.
   * @return {@code true} if the string is a parsable number.
   */
  public static boolean isParsable(final String str) {
    if (isEmpty(str)) {
      return false;
    }
    if (str.charAt(str.length() - 1) == '.') {
      return false;
    }
    if (str.charAt(0) == '-') {
      if (str.length() == 1) {
        return false;
      }
      return withDecimalsParsing(str, 1);
    }
    return withDecimalsParsing(str, 0);
  }

  private static boolean withDecimalsParsing(final String str, final int beginIdx) {
    int decimalPoints = 0;
    for (int i = beginIdx; i < str.length(); i++) {
      final boolean isDecimalPoint = str.charAt(i) == '.';
      if (isDecimalPoint) {
        decimalPoints++;
      }
      if (decimalPoints > 1) {
        return false;
      }
      if (!isDecimalPoint && !Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * <p>Compares two {@code int} values numerically. This is the same functionality as provided in
   * Java 7.</p>
   *
   * @param x the first {@code int} to compare
   * @param y the second {@code int} to compare
   * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y};
   * and a value greater than {@code 0} if {@code x > y}
   */
  public static int compare(final int x, final int y) {
    if (x == y) {
      return 0;
    }
    return x < y ? -1 : 1;
  }

  /**
   * <p>Compares to {@code long} values numerically. This is the same functionality as provided in
   * Java 7.</p>
   *
   * @param x the first {@code long} to compare
   * @param y the second {@code long} to compare
   * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y};
   * and a value greater than {@code 0} if {@code x > y}
   */
  public static int compare(final long x, final long y) {
    if (x == y) {
      return 0;
    }
    return x < y ? -1 : 1;
  }

  /**
   * <p>Compares to {@code short} values numerically. This is the same functionality as provided in
   * Java 7.</p>
   *
   * @param x the first {@code short} to compare
   * @param y the second {@code short} to compare
   * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y};
   * and a value greater than {@code 0} if {@code x > y}
   */
  public static int compare(final short x, final short y) {
    if (x == y) {
      return 0;
    }
    return x < y ? -1 : 1;
  }

  /**
   * <p>Compares two {@code byte} values numerically. This is the same functionality as provided in
   * Java 7.</p>
   *
   * @param x the first {@code byte} to compare
   * @param y the second {@code byte} to compare
   * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y};
   * and a value greater than {@code 0} if {@code x > y}
   */
  public static int compare(final byte x, final byte y) {
    return x - y;
  }

  private static <T> void notNull(final T object, final Object... values) {
    Objects.requireNonNull(object, () -> String.format("array", values));
  }

  private static void isTrue(final boolean expression, final Object... values) {
    if (!expression) {
      throw new IllegalArgumentException(String.format("Array cannot be empty.", values));
    }
  }

  public static boolean isFloatingPointNumber(String str) {
    return FLOATING_POINT_NUMBER_PATTERN.matcher(str).matches();
  }

  /**
   * @author: Ares
   * @description: 是否为偶数
   * @time: 2024-04-29 12:25:14
   * @params: [number] 数字
   * @return: boolean 是否为偶数
   */
  public static boolean isEven(int number) {
    return number % 2 == 0;
  }

  /**
   * @author: Ares
   * @description: 是否为奇数
   * @time: 2024-04-29 12:25:14
   * @params: [number] 数字
   * @return: boolean 是否为奇数
   */
  public static boolean isOdd(int number) {
    return number % 2 != 0;
  }

}
