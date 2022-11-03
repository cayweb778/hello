package org.boozsoft.rest;

import org.springbooz.core.tool.result.R

public class LoginResult {
    companion object {

        fun ok(code:String ,msg:String ,content :Any? ): R<Any> {
            return R.ok(mapOf(
                    "code" to code,
                    "msg" to msg,
                    "content" to content
            ))
        }

        fun getUsernameOrPasswordErrorResult(): R<Any> {
            return R.ok(mapOf(
                    "code" to "3002",
                    "msg" to "用户名或者密码错误"
            ))
        }

        fun getAlreadyLoggedResult(): R<Any> {

            return R.ok(
                    mapOf(
                            "code" to "3001",
                            "msg" to "already logged"
                    )
            )
        }
    }

}
