package com.zhongkexinli.micro.serv.common.constant;

/**
 * Created by ace on 2017/8/29.
 */
public final class CommonConstants {

  private CommonConstants() {
   //单例
  }
  
  public static final String RESOURCE_TYPE_MENU = "menu";
  public static final String RESOURCE_TYPE_BTN = "button";
  // 用户token异常
  public static final Integer EX_USER_INVALID_CODE = 40101;
  // 客户端token异常
  public static final Integer EX_CLIENT_INVALID_CODE = 40301;
  public static final Integer EX_CLIENT_FORBIDDEN_CODE = 40331;
  public static final Integer EX_OTHER_CODE = 500;
  public static final String CONTEXT_KEY_USER_ID = "currentUserId";
  public static final String CONTEXT_KEY_USERNAME = "currentUserName";
  public static final String CONTEXT_KEY_USER_NAME = "currentUser";
  public static final String CONTEXT_KEY_USER_TOKEN = "currentUserToken";
  public static final String JWT_KEY_USER_ID = "userId";
  public static final String JWT_KEY_NAME = "name";

  public static final String ENCODING_UTF_8 = "UTF-8";

  public static final Integer ERROR = 0;

  public static final String FAIL = "0";

  public static final Integer SUCCESS = 1;

  public static final String SUCCESS_MSG = "成功";
    
}
