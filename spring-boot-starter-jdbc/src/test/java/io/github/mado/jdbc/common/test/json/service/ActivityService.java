package io.github.mado.jdbc.common.test.json.service;

import io.github.mado.jdbc.common.test.vo.Activity;
import io.github.mado.jdbc.core.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author heng.ma
 */
@Service
public class ActivityService extends BaseService<Activity, String> {

    @Transactional(readOnly = true)
    public List<Activity> findAll () {
        return getRepository().findAll();
    }
}
