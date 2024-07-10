package cn.ares.boot.base.model.page;

import cn.ares.boot.base.model.Result;
import cn.ares.boot.util.common.CollectionUtil;
import cn.ares.boot.util.json.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2024-07-10 16:16:26
 * @description: Pageable test
 * @version: JDK 1.8
 */
public class PageableTest {


  @Test
  public void test() {
    Pageable<String> pageable = Pageable.of(10, 100, 1000);
    String columnName = "id";
    pageable.addSortField(SortField.of(columnName, true));
    pageable.setRecordList(CollectionUtil.asList("ares"));
    String pageableStr = JsonUtil.toJsonString(pageable);
    Pageable<String> restorePageable = JsonUtil.parseObject(pageableStr,
        new TypeReference<Pageable<String>>() {
        });

    assertPageable(restorePageable, columnName);

    Result<Pageable<String>> result = Result.success(pageable);
    String resultStr = JsonUtil.toJsonString(result);
    Result<Pageable<String>> restoreResult = JsonUtil.parseObject(resultStr,
        new TypeReference<Result<Pageable<String>>>() {
        });
    assertPageable(restoreResult.getData(), columnName);
  }

  private void assertPageable(Pageable<String> restorePageable, String columnName) {
    Assertions.assertNotNull(restorePageable);
    Assertions.assertEquals(10, (long) restorePageable.getCurrent());
    Assertions.assertEquals(100, (long) restorePageable.getSize());
    Assertions.assertEquals(1000, (long) restorePageable.getTotal());
    Assertions.assertTrue(CollectionUtil.isNotEmpty(restorePageable.getSortFieldList()));
    Assertions.assertEquals(columnName, restorePageable.getSortFieldList().get(0).getColumn());
    Assertions.assertTrue(restorePageable.getSortFieldList().get(0).isAsc());
    Assertions.assertTrue(CollectionUtil.isNotEmpty(restorePageable.getRecordList()));
    Assertions.assertEquals("ares", restorePageable.getRecordList().get(0));
  }

}
