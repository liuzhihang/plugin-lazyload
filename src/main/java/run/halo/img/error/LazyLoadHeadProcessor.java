package run.halo.img.error;

import lombok.Data;
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
                    model.add(modelFactory.createText(highlightJsScript(config.getJquery(), config.getUrl())));
                    return Mono.empty();
                }).orElse(Mono.empty()).then();
    }

    private String highlightJsScript(Boolean jquery, String url) {
        return """
                <script>
                    document.addEventListener("DOMContentLoaded", async function () {
                    
                         if (%s) {
                            
                            await loadScript(`/plugins/PluginPrismJS/assets/static/jquery-3.6.1.min.js`);
                            
                            console.log("js");
                         }
                    
                    });
                    
                    function loadScript(url) {
                      return new Promise(function (resolve, reject) {
                        const script = document.createElement("script");
                        script.type = "text/javascript";
                        script.src = url;
                        script.onload = resolve;
                        script.onerror = reject;
                        document.head.appendChild(script);
                      });
                    }
                    
                    <!-- 图片 404 -->
                    function imgError(url) {
                      $("img").one("error", function () {
                          $(this).attr("src", url);
                      });
                    }
                    
                    var url = %s;
                    
                    if (url !== null || url !== undefined || url !== '') {
                        imgError(url);
                    } else {
                        imgError(`/plugins/PluginPrismJS/assets/static/404.gif`);
                    }
                    
                    
                    
                </script>
                                
                <!-- PluginPrismJS end -->
                """.formatted(jquery, url);
    }

    @Data
    public static class BasicConfig {
        Boolean jquery;
        String url;
    }
}
