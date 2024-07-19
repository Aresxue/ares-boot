package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import java.math.BigDecimal;

/**
 * @author: Ares
 * @time: 2022-07-18 18:07:36
 * @description: 去除末尾多余的0
 * @description: Remove the extra zero at the end
 * @version: JDK 1.8
 */
public class BigDecimalTrailingZerosSerializer extends ToStringSerializerBase {

  private static final long serialVersionUID = 999919204236167357L;

  public BigDecimalTrailingZerosSerializer() {
    super(BigDecimal.class);
  }

  @Override
  public final String valueToString(Object value) {
    BigDecimal bigDecimal = (BigDecimal) value;
    return bigDecimal.stripTrailingZeros().toPlainString();
  }

}
