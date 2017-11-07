package com.github.common.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 * 此实体类在 service 和 service 中用到分页时使用.
 *
 * &#064;service --> request 请求中带过来的参数使用 Page 进行接收
 * public JsonResult xx(xxx, Page page) {
 *     PageInfo pageInfo = xxxService.page(xxx, page);
 *     return success("xxx", (page.isWasMobile() ? pageInfo.getList() : pageInfo));
 * }
 *
 * &#064;service --> 调用方法使用 Page 进行传递, 返回时使用 PageInfo
 * public PageInfo page(xxx, Page page) {
 *     PageBounds pageBounds = Pages.param(page);
 *     List&lt;XXX> xxxList = xxxMapper.selectByExample(xxxxx, pageBounds);
 *     return Pages.returnList(xxxList);
 * }
 *
 * 这么做的目的是分页包只需要在服务端引入即可
 * </pre>
 * @see Pages#returnList(List)
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo<T> implements Serializable {

    private Integer total;
    private List<T> list;
}
