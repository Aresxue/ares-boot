package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: Ares
 * @time: 2022-07-18 18:02:29
 * @description: 保留指定小数
 * @description: Keep the specified decimal
 * @version: JDK 1.8
 */
public class BigDecimalScaleSerializer extends ToStringSerializerBase {

  private static final long serialVersionUID = 1405232268944934434L;

  private final Integer bigDecimalScale;

  public BigDecimalScaleSerializer(Integer bigDecimalScale) {
    super(BigDecimal.class);
    this.bigDecimalScale = bigDecimalScale;
  }

  @Override
  public final String valueToString(Object value) {
    BigDecimal bigDecimal = (BigDecimal) value;
    return bigDecimal.setScale(bigDecimalScale, RoundingMode.HALF_UP).toPlainString();
  }

}
