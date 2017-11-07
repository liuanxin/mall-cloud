package com.github.web;

import com.github.common.json.JsonResult;
import com.github.common.page.Page;
import com.github.user.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@SuppressWarnings("unchecked")
public class UserController {

    @Autowired
    private UserClient userClient;

    @GetMapping("/check")
    public JsonResult info(String phone, Page page) {
//        Map map = JsonUtil.convert(page, Map.class);
//        map.put("phone", phone);
//        return userClient.demo(phone, page.getPage(), page.getLimit());
        return JsonResult.success("xx", userClient.demo(phone, page.getPage(), page.getLimit()));
    }
}
