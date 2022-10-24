package run.halo.img.error;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * prism js 集成
 *
 * @author liuzhihang
 * @date 2022/10/23
 */
@Component
public class LazyLoadPlugin extends BasePlugin {

    public LazyLoadPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
