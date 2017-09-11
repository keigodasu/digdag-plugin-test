package io.digdag.plugin.ecs;

import io.digdag.spi.OperatorFactory;
import io.digdag.spi.OperatorProvider;
import io.digdag.spi.Plugin;
import io.digdag.spi.TemplateEngine;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class EcsRunTaskPlugin implements Plugin {
    @Override
    public <T> Class<? extends T> getServiceProvider(Class<T> type) {
        if (type == OperatorProvider.class) {
            return EcsRunTaskOperatorProvider.class.asSubclass(type);
        } else {
            return null;
        }
    }

    public static class EcsRunTaskOperatorProvider implements OperatorProvider {
        @Inject
        protected TemplateEngine templateEngine;

        @Override
        public List<OperatorFactory> get() {
            return Arrays.asList(new EcsRunTaskFactory(templateEngine));
        }
    }
}
