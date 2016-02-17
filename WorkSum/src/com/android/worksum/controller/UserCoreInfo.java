package com.android.worksum.controller;

import com.jobs.lib_v1.app.AppActivities;
import com.jobs.lib_v1.app.AppCoreInfo;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;
import com.jobs.lib_v1.data.encrypt.SimpleEncrypt;
import com.jobs.lib_v1.db.DataAppCoreDB;

/**
 * 用户信息存取
 * 
 */
public class UserCoreInfo {
	private static String CORE_USER_INFO_DB_KEY = "UserCoreInfoKey"; // 用户信息保存的key值
	private static DataItemDetail mUserInfo = null; // 用户信息对象
	private static boolean mIsMoreApp = false; // 是否显示其他应用推荐
	private static boolean mIsBusySeason = false; // 淡旺季标识
	private static String mMapType = ""; // 地图类型
	private static String mServerIp = ""; // api域名对应的ip

	//登录的几种状态类型 shuai.yang 2016.1.21
	public static final int USER_LOGIN_DEFAULT = 0;
	public static final int USER_LOGIN_MANUAL = 1;
	public static final int USER_LOGIN_AUTO = 2;


	/**
	 * 从数据库中取出数据，并返回登录用户对象，
	 */
	private static DataItemDetail getUserCoreInfo() {
		if (mUserInfo == null) {
			mUserInfo = new DataItemDetail();
			loadUserInfoFormDB();
		}

		return mUserInfo;
	}

	/**
	 * 从数据库中获取用户信息
	 * 
	 * @author yuye.zou
	 * @date 2012-11-9
	 */
	private static void loadUserInfoFormDB() {
		DataAppCoreDB db = AppCoreInfo.getCoreDB();

		/* 保证上次登录成功会话有效期为15分钟的办法是：清理掉超过指定时间的会话数据 (By solomon.wen / 2012-12-05) */
        //此处无需删掉数据，不然自动登录时，九宫格首页右上角按钮文字会看到从登录变成注销的过程，现在通过登录时间进行判断
//		db.clearItemDataType(Store.CORE_USER_INFO, CORE_USER_INFO_DB_KEY, AppSettingStore.USER_SESSION_TIMEOUT);

		/* 从数据库中读取用户会话信息 */
		mUserInfo.clear().append(db.getItemCache(Store.CORE_USER_INFO, CORE_USER_INFO_DB_KEY));

		/* 更新用户状态 */
		updateUserStatus();
	}


	/**
	 * 默认的将接口数据保存到数据库的方法
	 * shuai.yang
	 * 2016.1.21
	 * @param item
	 * @param lockedUserUpdate
	 */
	public static void setUserLoginInfo(DataItemResult item, Boolean lockedUserUpdate) {
		setUserLoginInfo(item, lockedUserUpdate, USER_LOGIN_DEFAULT);
	}

	/**
	 * 将接口返回数据保存到数据库中
	 * 
	 * @author yuye.zou
	 * @date 2012-11-8
	 */
	public static void setUserLoginInfo(DataItemResult item, Boolean lockedUserUpdate,int loaginType) {
		if (null == item || item.hasError) {
			return;
		}

		DataItemDetail info = getUserCoreInfo();
		DataItemDetail item_info = item.detailInfo;

		// 登录接口中不返回mobilenation, mobilenationname这两个字段,在个人信息界面中会设置这两个字段的值
		/*
		 * 2.6.0 增加 
		 * hasfullresume 是否有完整简历
		 * isbuysecretary 是否买过无忧小秘书服务
		 * ispresentsecretary 是否可以赠送无忧小秘书服务（待加功能）
		 * isopensecretary 是否开启无忧小秘书服务（待加功能）
		 * 
		 * 2.7.0
		 * 删除
		 * resumeview_paid 有谁看了我的简历接口直接返回
		 * 增加
		 * username 真实用户名
		 * bindmobilephone 是否绑定手机号
		 * bindemail 是否绑定邮箱
		 *
		 * 3.0.0
		 * 增加
		 * isRefresh 是否刷新首页
		 * 去除手机地域的信息mobilenationname
		 */
					
		String updatekeys[] = { "accountid", "mobilephone", "lastupdate", 
								"key", "email", "mobilenation", 
								"hasfullresume", "isbuysecretary", "ispresentsecretary",
								"isopensecretary", "username", "bindmobilephone", "bindemail","isRefresh"};

		for (String updatekey : updatekeys) {
			// 这里加一个lockedUserUpdate参数用来将用户锁定状态时的accountid和key置为""
			// 具体可查看哪里传true的lockedUserUpdate了 grace 20151204
			if (item_info.getString(updatekey).length() > 0 || lockedUserUpdate) {
				info.setStringValue(updatekey, item_info.getString(updatekey));
			}
		}

		syncToDB();
		updateUserStatus(loaginType);
	}


	/**
	 * 更新用户登录状态
	 */
	public static void updateUserStatus() {
		updateUserStatus(USER_LOGIN_DEFAULT);
	}

	/**
	 * @param loginType 登录类型
	 */
	public static void updateUserStatus(int loginType) {
		AppActivities.noticeActivity("onUserStatusChanged", false, new Class[]{Integer.class}, new Integer[]{loginType});
	}

	/**
	 * 保存用户信息到数据库
	 */
	public synchronized static void syncToDB() {
		AppCoreInfo.getCoreDB().saveItemCache(Store.CORE_USER_INFO, CORE_USER_INFO_DB_KEY, getUserCoreInfo());
	}

	/**
	 * 保存用户名
	 */
	public static boolean setUserName(String name) {
		String nameEncrypted = SimpleEncrypt.encrypt(name);
		return AppCoreInfo.getCoreDB().setStrValue(Store.CORE_USER_INFO, "UserName", nameEncrypted) > 0;
	}

	/**
	 * 返回用户名
	 */
	public static String getUserName() {
		String nameEncrypted = AppCoreInfo.getCoreDB().getStrValue(Store.CORE_USER_INFO, "UserName");
		return SimpleEncrypt.decrypt(nameEncrypted);
	}

	/**
	 * 保存密码
	 */
	public static boolean setPassword(String password) {
		String passwordEncrypted = SimpleEncrypt.encrypt(password);
		return AppCoreInfo.getCoreDB().setStrValue(Store.CORE_USER_INFO, "Password", passwordEncrypted) > 0;
	}

	/**
	 * 返回密码
	 */
	public static String getPassword() {
		String passwordEncrypted = AppCoreInfo.getCoreDB().getStrValue(Store.CORE_USER_INFO, "Password");
		return SimpleEncrypt.decrypt(passwordEncrypted);
	}

	/**
	 * 注销时删除密码
	 */
	public static boolean removeUserPassword() {
		return AppCoreInfo.getCoreDB().removeStrItem(Store.CORE_USER_INFO, "Password") > 0;
	}

	/**
	 * 设定 自动登录 标志位
	 */
	public static boolean setAutoLogin(boolean autoLogin) {
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "AutoLogin", autoLogin ? 1 : 0) > 0;
	}

	/**
	 * 获取 是否需要自动登录 标志位
	 */
	public synchronized static boolean hasAutoLogin() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "AutoLogin")) {
			return true;
		}

		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "AutoLogin") != 0;
	}
	
	/**
	 * 设定 安全登录 标志位
	 */
	public static boolean setSSLLogin(boolean SSLLogin) {
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "SSLLogin", SSLLogin ? 1 : 0) > 0;
	}

	/**
	 * 获取 是否需要安全登录 标志位
	 */
	public synchronized static boolean hasSSLLogin() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "SSLLogin")) {
			return true;
		}

		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "SSLLogin") != 0;
	}

	/**
	 * 获取用户id
	 */
	public static String getAccountid() {
		return getUserCoreInfo().getString("accountid");
	}

	/**
	 * 获取用户key
	 */
	public static String getKey() {
		return getUserCoreInfo().getString("key");
	}

	/**
	 * 返回用户简历最新更新时间
	 */
	public static String getLastUpdate() {
		return getUserCoreInfo().getString("lastupdate");
	}

	/**
	 * 获取用户手机号码
	 */
	public static String getMobilePhone() {
		return getUserCoreInfo().getString("mobilephone");
	}

    /**
     * .返回是否刷新首页
     * @return
     */
    public static boolean getIsRefresh(){
        return getUserCoreInfo().getBoolean("isRefresh");
    }


	/**
	 * 保存用户手机号码
	 */
	public static void setMobilePhone(String phone) {
		if (null != phone && phone.length() > 0) {
			getUserCoreInfo().setStringValue("mobilephone", phone);
			setReportPhone(phone);
		}
	}
	
	/**
	 * 保存手机地域code
	 */
	public static void setMobileNationCode(String nationCode){
		if (null != nationCode && nationCode.length() > 0) {
			getUserCoreInfo().setStringValue("mobilenation", nationCode);
		}
	}
	
	/**
	 * 设置用户邮箱
	 */
	public static void setEmail(String email) {
		if (null != email && email.length() > 0) {
			getUserCoreInfo().setStringValue("email", email);
			syncToDB();
		}
	}

	/**
	 * 获取用户邮箱
	 */
	public static String getEmail() {
		return getUserCoreInfo().getString("email");
	}

	/**
	 * 获取举报邮箱(这不是服务器返回的节点，主要是记录用户自己手动填写的 email 地址)
	 */
	public static String getReportEmail() {
		String email = getUserCoreInfo().getString("report-email");
		if (email.length() < 1) {
			email = getEmail();
		}
		return email;
	}

	/**
	 * 获取举报电话(这不是服务器返回的节点，主要是记录用户自己手动填写的 联系电话)
	 */
	public static String getReportPhone() {
		String phone = getUserCoreInfo().getString("report-phone");

		if (phone.length() < 1) {
			phone = getMobilePhone();
		}

		return phone;
	}

	/**
	 * 设置举报邮箱
	 */
	public static void setReportEmail(String email) {
		if (null != email && email.length() > 0) {
			getUserCoreInfo().setStringValue("report-email", email);
			syncToDB();
		}
	}

	/**
	 * 设置举报电话
	 */
	public static void setReportPhone(String phone) {
		if (null != phone && phone.length() > 0) {
			getUserCoreInfo().setStringValue("report-phone", phone);
			syncToDB();
		}
	}
	
	/**
	 * 获取用户是否有完整简历
	 */
	public static boolean hasFullResume(){
		return getUserCoreInfo().getBoolean("hasfullresume");
	}
	
	/**
	 * 设置用户是否有完整简历
	 */
	public static boolean setFullResume(boolean isFullResume){
		return getUserCoreInfo().setBooleanValue("hasfullresume", isFullResume);
	}
	
	/**
	 * 是否买过无忧小秘书服务
	 */
	public static boolean isBuySecretary(){
		return getUserCoreInfo().getBoolean("isbuysecretary");
	}
	
	/**
	 * 设置用户是否买过无忧小秘书服务
	 */
	public static boolean setBuySecretary(boolean isBuySecretary){
		return getUserCoreInfo().setBooleanValue("isbuysecretary", isBuySecretary);
	}
	
	/**
	 * 是否可以赠送无忧小秘书服务
	 */
	public static boolean isPresentSecretary(){
		return getUserCoreInfo().getBoolean("ispresentsecretary");
	}
	
	/**
	 * 设置是否可以赠送无忧小秘书服务
	 */
	public static boolean setPresentSecretary(boolean isPresentSecretary){
		return getUserCoreInfo().setBooleanValue("isPresentSecretary", isPresentSecretary);
	}
	
	/**
	 * 是否开启无忧小秘书服务
	 */
	public static boolean isOpenSecretary(){
		return getUserCoreInfo().getBoolean("isopensecretary");
	}
	
	/**
	 * 设置是否开启无忧小秘书服务
	 * @author eric.huang
	 */
	public static boolean setOpenSecretary(boolean isOpenSecretary){
		return getUserCoreInfo().setBooleanValue("isOpenSecretary", isOpenSecretary);
	}
	
	/**
	 * 获取用户是否绑定手机号
	 * @author eric.huang
	 */
	public static boolean hasBindMobilephone(){
		return getUserCoreInfo().getBoolean("bindmobilephone");
	}
	
	/**
	 * 设置用户是否绑定手机号
	 * @author eric.huang
	 */
	public static boolean setBindMobilephone(boolean isBindMobilephone){
		return getUserCoreInfo().setBooleanValue("bindmobilephone", isBindMobilephone);
	}
	
	/**
	 * 获取用户是否绑定邮箱
	 * @author eric.huang
	 */
	public static boolean hasBindEmail(){
		return getUserCoreInfo().getBoolean("bindemail");
	}
	
	/**
	 * 设置用户是否绑定邮箱
	 * @author eric.huang
	 */
	public static boolean setBindEmail(boolean isBindEmail){
		return getUserCoreInfo().setBooleanValue("bindemail", isBindEmail);
	}
	
	/**
	 * 获取显示用户名(个人中心显示用户名)
	 * @author eric.huang
	 */
	public static String getDisplayUserName(){
		return getUserCoreInfo().getString("username");
	}
	
	/**
	 * 设置显示用户名
	 * @author eric.huang
	 */
	public static void setDisplayUserName(String username){
		getUserCoreInfo().setStringValue("username", username);
	}

	/**
	 * 返回用户登录状态 (根据 accountid 和 key 的值来判断是否登录成功)
	 */
	public synchronized static boolean hasLogined() {
		DataItemDetail info = getUserCoreInfo();
		return info.getString("accountid").length() > 0 && info.getString("key").length() > 0;
	}

	/**
	 * 是否获取到激活接口返回的核心信息
	 * 
	 * @author eric.huang
	 * @date 2013-3-29
	 * @return
	 */
	public static boolean hasInitUserCoreInfo() {
		DataItemDetail item = getUserCoreInfo();
		if (item.hasKey("show_suggest_apps") && item.hasKey("isbusyseason") && item.hasKey("maptype") && item.hasKey("serverip")) {
			return true;
		}
		return false;
	}

	/**
	 * 设置用户是否显示其他应用推荐 (这个值不需要同步到数据库中，反正默认为false)
	 * 
	 * @param show_suggest_apps
	 */
	public static void setShow_suggest_apps(boolean show_suggest_apps) {
		mIsMoreApp = show_suggest_apps;
	}

	/**
	 * 返回用户是否显示其他应用推荐，默认为false，不显示
	 */
	public static boolean isShow_suggest_apps() {
		return mIsMoreApp;
	}

	/**
	 * 设置淡旺季标识
	 * 
	 * @author yuye.zou
	 * @date 2013-1-15
	 * @param isbusyseason 当前是淡季还是旺季
	 */
	public static void setBusySeason(boolean isbusyseason) {
		mIsBusySeason = isbusyseason;
	}

	/**
	 * 返回淡旺季标识
	 * 
	 * @author yuye.zou
	 * @date 2013-1-15
	 * @return 返回true为旺季，返回false为淡季
	 */
	public static boolean isBusySeason() {
		return mIsBusySeason;
	}

	/**
	 * 设置是地图类型
	 * 
	 * @author yuye.zou
	 * @date 2013-2-1
	 * @param maptype 地图类型
	 */
	public static void setMapType(String maptype) {
		mMapType = maptype;
	}

	/**
	 * 返回地图类型
	 * 
	 * @author yuye.zou
	 * @date 2013-2-1
	 * @return 地图类型
	 */
	public static String getMapType() {
		return mMapType;
	}

	/**
	 * 设置api域名对应的ip
	 * 
	 * @author yuye.zou
	 * @date 2013-2-1
	 * @param serverip api域名对应的ip
	 */
	public static void setServerIp(String serverip) {
		mServerIp = serverip;
	}

	/**
	 * 返回api域名对应的ip
	 * 
	 * @author yuye.zou
	 * @date 2013-2-1
	 * @return api域名对应的ip
	 */
	public static String getServerIp() {
		return mServerIp;
	}
	
	/**
	 * 设定 投递设置 标志位
	 */
	public static boolean setAutoDeliver(boolean autoDeliver){
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "AutoDeliver", autoDeliver ? 1 : 0) > 0;
	}
	
	/**
	 * 获取 投递设置 标志位
	 */
	public synchronized static boolean hasAutoDeliver() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "AutoDeliver")) {
			return false;
		}

		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "AutoDeliver") != 0;
	}
	
	/**
	 * 设定 推送开关 标志位
	 */
	public static boolean setAcceptPush(boolean autoPush){
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "AutoPush", autoPush ? 1 : 0) > 0;
	}
	
	/**
	 * 获取 推送开关 标志位
	 */
	public synchronized static boolean hasAcceptPush() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "AutoPush")) {
			return false;
		}

		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "AutoPush") != 0;
	}
	
	/**
	 * 设定 通知栏是否显示 标志位
	 */
	public static boolean setPushIcon(boolean pushIcon){
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "PushIcon", pushIcon ? 1 : 0) > 0;
	}
	
	/**
	 * 获取 通知栏是否显示标志位
	 */
	public synchronized static boolean hasPushIcon() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "PushIcon")) {
			return true;
		}

		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "PushIcon") != 0;
	}
	
	/**
	 * 设定 简历是否被看过的标志位
	 */
	public static boolean setNotifyResumeViewed(boolean notifyResumeViewed){
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "NotifyResumeViewed", notifyResumeViewed ? 1 : 0) > 0;
	}
	
	/**
	 * 获取 简历是否被看过的标志位
	 */
	public synchronized static boolean hasNotifyResumeViewed() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "NotifyResumeViewed")) {
			return false;
		}
		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "NotifyResumeViewed") != 0;
	}
	
	/**
	 * 设定 是否有人事经理来信的标志位
	 */
	public static boolean setNotifyHRMessage(boolean notifyHRMessage){
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "NotifyHRMessage", notifyHRMessage ? 1 : 0) > 0;
	}
	
	/**
	 * 获取 是否有人事经理来信的标志位
	 */
	public synchronized static boolean hasNotifyHRMessage() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "NotifyHRMessage")) {
			return false;
		}
		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "NotifyHRMessage") != 0;
	}

	/**
	 * 设定 app前台开启的标志位
	 */
	public static boolean setAppForegroundOpen(boolean appForegroundOpen){
		return AppCoreInfo.getCoreDB().setIntValue(Store.CORE_USER_INFO, "appForegroundOpen", appForegroundOpen ? 1 : 0) > 0;
	}

	/**
	 * 获取 app前台开启的标志位
	 */
	public synchronized static boolean getAppForegroundOpen() {
		if (!AppCoreInfo.getCoreDB().hasIntItem(Store.CORE_USER_INFO, "appForegroundOpen")) {
			return false;
		}
		return AppCoreInfo.getCoreDB().getIntValue(Store.CORE_USER_INFO, "appForegroundOpen") != 0;
	}

	/**
	 * 删除 app前台开启的标志位
	 */
	public static void removeAppForegroundOpen(){
		AppCoreInfo.getCoreDB().removeIntItem(Store.CORE_USER_INFO, "appForegroundOpen");
	}
}