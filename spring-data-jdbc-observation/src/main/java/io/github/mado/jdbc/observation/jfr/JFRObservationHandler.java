package io.github.mado.jdbc.observation.jfr;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author heng.ma
 */
public class JFRObservationHandler<T extends Observation.Context> implements ObservationHandler<T> {


    private final Map<Class<? extends Observation.Context>, List<ObservationToJFRGenerator<? extends Observation.Context>>> generatorMap;

    public JFRObservationHandler(List<ObservationToJFRGenerator<T>> generators) {
        this.generatorMap = generators.stream().collect(
                Collectors.groupingBy(ObservationToJFRGenerator::getContextClazz)
        );
    }

    @Override
    public void onStart(Observation.Context context) {
        List<ObservationToJFRGenerator<? extends Observation.Context>> observationToJFRGenerators = generatorMap.get(context.getClass());
        if (observationToJFRGenerators != null) {
//            log.debug("JFRObservationHandler-onStart {} -> observationToJFRGenerators {}", context.getName(), observationToJFRGenerators);
            observationToJFRGenerators.forEach(generator -> {
                try {
                    generator.onStart(context);
                } catch (Exception e) {
//                    log.error("JFRObservationHandler-onStart error {}", e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void onStop(Observation.Context context) {
        List<ObservationToJFRGenerator<? extends Observation.Context>> observationToJFRGenerators = generatorMap.get(context.getClass());
        if (observationToJFRGenerators != null) {
//            log.debug("JFRObservationHandler-onStop {} -> observationToJFRGenerators {}", context.getName(), observationToJFRGenerators);
            observationToJFRGenerators.forEach(generator -> {
                try {
                    generator.onStop(context);
                } catch (Exception e) {
//                    log.error("JFRObservationHandler-onStop error {}", e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }
}