package cn.ares.boot.starter.search.engine.extension;

import cn.ares.boot.starter.search.engine.util.BulkOptionsHolder;
import java.util.List;
import java.util.Optional;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

/**
 * @author: Ares
 * @time: 2024-09-10 15:14:47
 * @description: ElasticsearchRestTemplate扩展
 * @description: ElasticsearchRestTemplate extension
 * @version: JDK 1.8
 */
public class ExtensionElasticsearchRestTemplate extends ElasticsearchRestTemplate {

  public ExtensionElasticsearchRestTemplate(RestHighLevelClient client) {
    super(client);
  }

  public ExtensionElasticsearchRestTemplate(RestHighLevelClient client,
      ElasticsearchConverter elasticsearchConverter) {
    super(client, elasticsearchConverter);
  }

  @NotNull
  @Override
  public List<IndexedObjectInformation> bulkIndex(@NotNull List<IndexQuery> queries,
      @NotNull IndexCoordinates index) {
    BulkOptions bulkOptions = BulkOptionsHolder.getBulkOptions();
    if (null == bulkOptions) {
      bulkOptions = BulkOptions.defaultOptions();
    }
    return super.bulkIndex(queries, bulkOptions, index);
  }

  @NotNull
  @Override
  protected <R extends WriteRequest<R>> R prepareWriteRequest(@NotNull R request) {
    R res = super.prepareWriteRequest(request);
    Optional.ofNullable(BulkOptionsHolder.getBulkOptions()).map(BulkOptions::getRefreshPolicy)
        .ifPresent(refreshPolicy -> request.setRefreshPolicy(String.valueOf(refreshPolicy)));
    return res;
  }

}
