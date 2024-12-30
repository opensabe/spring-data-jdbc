package io.github.mado.jdbc.core.repository;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author heng.ma
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends
        InsertRepository<T>,
        UpdateRepository<T>,
        DeleteRepository<T, ID>,
        CountExecutor<T>,
        ListByLimitExecutor<T>,
        OptionalExecutor<T, ID>,
        PageAndSortingExecutor<T>,
        ListPageAndSortingExecutor<T>,
        ListQueryExecutor<T, ID>,
        ExistsExecutor<T, ID>
{
}
