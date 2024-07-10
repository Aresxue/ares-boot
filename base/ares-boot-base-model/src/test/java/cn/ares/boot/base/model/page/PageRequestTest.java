package cn.ares.boot.base.model.page;

import cn.ares.boot.util.common.CollectionUtil;
import cn.ares.boot.util.json.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2024-07-10 16:16:26
 * @description: PageRequest test
 * @version: JDK 1.8
 */
public class PageRequestTest {


  @Test
  public void test() {
    PageRequest pageRequest = PageRequest.of(10, 100);
    String columnName = "id";
    pageRequest.addSortField(SortField.of(columnName, true));
    String pageRequestStr = JsonUtil.toJsonString(pageRequest);
    PageRequest restorePageRequest = JsonUtil.parseObject(pageRequestStr, PageRequest.class);

    Assertions.assertNotNull(restorePageRequest);
    Assertions.assertEquals(10, (long) restorePageRequest.getCurrent());
    Assertions.assertEquals(100, (long) restorePageRequest.getSize());
    Assertions.assertTrue(CollectionUtil.isNotEmpty(restorePageRequest.getSortFieldList()));
    Assertions.assertEquals(columnName, restorePageRequest.getSortFieldList().get(0).getColumn());
    Assertions.assertTrue(restorePageRequest.getSortFieldList().get(0).isAsc());
  }

}
