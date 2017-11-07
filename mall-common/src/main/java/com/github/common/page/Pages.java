package com.github.common.page;

import com.github.liuanxin.page.model.PageBounds;
import com.github.liuanxin.page.model.PageList;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 此实体类只在 <span style="color:red">service</span> 中用到分页时使用.
 *
 * &#064;service --> 接收请求中带过来的参数时使用 Page 进行接收
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
 * 这么做的目的是分页包只需要在服务端引入即可. <span style="color:red">service 中不要使用此类</span>, 即不需要引入 mybatis 的分页包
 * </pre>
 */
public final class Pages {

    /** 在 service 的实现类中调用 --> 在 repository 方法上的参数是 PageBounds, service 上的参数是 Page, 使用此方法进行转换 */
    public static PageBounds param(Page page) {
        return page.isWasMobile() ?
                new PageBounds(page.getLimit()) :
                new PageBounds(page.getPage(), page.getLimit());
    }

    /** 在 service 的实现类中调用 --> 在 repository 方法上的返回类型是 List, service 上的返回类型是 PageInfo, 使用此方法进行转换 */
    @SuppressWarnings("unchecked")
    public static PageInfo returnList(List list) {
        PageInfo pageInfo = new PageInfo();
        if (list instanceof PageList) {
            pageInfo.setTotal(((PageList) list).getTotal());
            pageInfo.setList(new ArrayList(list));
        } else {
            pageInfo.setTotal(0);
            pageInfo.setList(list);
        }
        return pageInfo;
    }
}
