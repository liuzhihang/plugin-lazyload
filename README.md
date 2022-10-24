# plugin-img-error

当图片异常时，显示 404 图片

## 说明

本插件仅一句 JS 如下：

```js
<!-- 图片 404 -->
function imgError(url) {
    $("img").one("error", function () {
        $(this).attr("src", url);
    });
}
```

用户可在后台设置图片获取失败时的 URL，默认使用插件自带的 404 图片
