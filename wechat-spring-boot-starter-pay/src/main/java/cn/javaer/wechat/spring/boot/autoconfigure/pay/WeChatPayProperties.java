/*
 * Copyright (c) 2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.javaer.wechat.spring.boot.autoconfigure.pay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信支付-配置.
 *
 * @author zhangpeng
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "wechat.pay")
public class WeChatPayProperties implements InitializingBean {
    public static final String NOTIFY_RESULT_PATH = "/public/wechat/pay/notify_result";
    private String appid;
    private String mchId;
    private String mchKey;
    private String notifyUrl;
    private String spbillCreateIp;
    private String basePath;
    /**
     * 接收支付结果通知 Controller 的 path.
     */
    private String notifyResultPath = NOTIFY_RESULT_PATH;

    @Override
    public void afterPropertiesSet() {
        // TODO 
//        WeChatPayConfigurator.DEFAULT.setAppid(this.appid);
//        WeChatPayConfigurator.DEFAULT.setMchId(this.mchId);
//        WeChatPayConfigurator.DEFAULT.setMchKey(this.mchKey);
//        WeChatPayConfigurator.DEFAULT.setNotifyUrl(this.notifyUrl);
//        WeChatPayConfigurator.DEFAULT.setSpbillCreateIp(this.spbillCreateIp);
//        WeChatPayConfigurator.DEFAULT.setBasePath(this.basePath);
    }
}
