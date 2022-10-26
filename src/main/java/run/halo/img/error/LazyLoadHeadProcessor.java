package run.halo.img.error;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

/**
 * img error 插件
 *
 * @author liuzhihang
 * @date 2022/10/23
 */
@Component
public class LazyLoadHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    public LazyLoadHeadProcessor(SettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        return settingFetcher.fetch("basic", BasicConfig.class)
                .map(config -> {
                    final IModelFactory modelFactory = context.getModelFactory();

                    String loadingImg = config.getLoadingImg();
                    String errorImg = config.getErrorImg();

                    if (StringUtils.isBlank(loadingImg)) {
                        loadingImg = "/plugins/PluginLazyLoad/assets/static/loading.gif";
                    }
                    if (StringUtils.isBlank(errorImg)) {
                        errorImg = "/plugins/PluginLazyLoad/assets/static/404.gif";
                    }

                    model.add(modelFactory.createText(lazyLoadScript(loadingImg, errorImg)));
                    return Mono.empty();
                }).orElse(Mono.empty()).then();
    }

    /**
     * 懒加载 js
     *
     * @param loadingImg 懒加载图
     * @param errorImg   加载失败图
     * @return
     */
    private String lazyLoadScript(String loadingImg, String errorImg) {
        // language=html
        return """
                <!-- PluginLazyLoad start -->
                <script src="/plugins/PluginLazyLoad/assets/static/lazyload.min.js"></script> 
                <script>
                    document.addEventListener("DOMContentLoaded",  function () {
                        const imgTags = document.getElementsByTagName("img");
                        for (const imgTag of imgTags) {
                            imgTag.setAttribute("data-src", imgTag.src);
                            imgTag.src = "%s"
                          
                        }
                        
                        var lazyLoadInstance = new LazyLoad({
                            elements_selector: "img",
                            threshold: 0,
                            callback_error: (img) => {
                                img.setAttribute("srcset", "%s");
                            }
                        });
                        lazyLoadInstance.update();
                    })
                </script>
                                
                <!-- PluginLazyLoad end -->
                """.formatted(loadingImg, errorImg);
    }

    @Data
    public static class BasicConfig {
        String loadingImg;
        String errorImg;
    }
}
