package com.yaskovdev.motivated.stay;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
class UserController {

    @RequestMapping({"/user", "/me"})
    Principal user(final Principal principal) {
        return principal;
    }
}
