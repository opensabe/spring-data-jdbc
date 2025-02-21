package io.github.mado.jdbc.observation.jfr;

import io.micrometer.observation.Observation;

/**
 * @author heng.ma
 */
public abstract class ObservationToJFRGenerator<T extends Observation.Context> {

    /**
     * 匹配 Context 类型
     *
     * @return
     */
    public abstract Class<T> getContextClazz();

    /**
     * @param context
     * @see JFRObservationHandler#onStop(Observation.Context)
     */
    public void onStop(Observation.Context context) {
        T cast = getContextClazz().cast(context);
        if (shouldCommitOnStop(cast)) {
            commitOnStop(cast);
        }
    }

    /**
     * @param context
     * @see JFRObservationHandler#onStart(Observation.Context)
     */
    public void onStart(Observation.Context context) {
        T cast = getContextClazz().cast(context);
        if (shouldGenerateOnStart(cast)) {
            generateOnStart(cast);
        }
    }

    /**
     * 是否在 stop 的时候 commit JFR 事件
     *
     * @param context
     * @return
     */
    protected abstract boolean shouldCommitOnStop(T context);

    /**
     * 是否在 start 的时候生成 JFR 事件
     *
     * @param context
     * @return
     */
    protected abstract boolean shouldGenerateOnStart(T context);

    /**
     * 在 stop 的时候 commit JFR 事件
     *
     * @param context
     * @return
     */
    protected abstract void commitOnStop(T context);

    /**
     * 在 start 的时候生成 JFR 事件
     *
     * @param context
     * @return
     */
    protected abstract void generateOnStart(T context);
}