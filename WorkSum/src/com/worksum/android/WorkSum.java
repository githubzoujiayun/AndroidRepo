package com.worksum.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;

import com.facebook.FacebookSdk;
import com.jobs.lib_v1.app.AppMain;
import com.jobs.lib_v1.device.DeviceUtil;
import com.jobs.lib_v1.device.Storage;
import com.jobs.lib_v1.imageloader.core.DisplayImageOptions;
import com.jobs.lib_v1.imageloader.core.ImageLoader;
import com.jobs.lib_v1.imageloader.core.ImageLoaderConfiguration;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.UserPreferences;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimStrings;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.worksum.android.controller.DataManager;
import com.worksum.android.controller.LoginManager;
import com.worksum.android.login.NimLoginManager;
import com.worksum.android.nim.GlobalCache;
import com.worksum.android.nim.NimLocationProvider;
import com.worksum.android.nim.common.util.sys.SystemUtil;
import com.worksum.android.nim.session.SessionHelper;
import com.worksum.android.preferences.NimPreferences;
import com.worksum.android.preferences.Preferences;

import java.util.List;
import java.util.Map;

/**
 */
public class WorkSum extends AppMain {

    private UserInfoProvider mUserInfoProvider = new UserInfoProvider() {
        @Override
        public UserInfoProvider.UserInfo getUserInfo(String account) {
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }

            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.drawable.ic_launcher;
        }

        @Override
        public Bitmap getTeamIcon(String tid) {
            return null;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            return null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId,
                SessionTypeEnum sessionType) {
            return null;
        }
    };

    private ContactProvider mContactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            return null;
        }

        @Override
        public int getMyFriendsCount() {
            return 0;
        }

        @Override
        public String getUserDisplayName(String account) {
            return null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();

        builder.defaultDisplayImageOptions(options);
        imageLoader.init(builder.build());

        FacebookSdk.sdkInitialize(this);


        DataManager.getInstance().init(this);

        Preferences.init(this);

        NIMClient.init(this,loginInfo(),options());

        GlobalCache.init(this);


        if (inMainProcess()) {
            // init pinyin
            PinYin.init(this);
            PinYin.validate();

            // 初始化UIKit模块
            initUiKit();

            NimLoginManager.init(this);
            NimLoginManager.ObserveLoginStatus();

            // 注册通知消息过滤器
            registerIMMessageFilter();

            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());


            // 注册语言变化监听
            registerLocaleReceiver(true);
        }
    }

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }

    /**
     * 通知消息过滤器（如果过滤则该消息不存储不上报）
     */
    private void registerIMMessageFilter() {
        NIMClient.getService(MsgService.class).registerIMMessageFilter(new IMMessageFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                if (UserPreferences.getMsgIgnore() && message.getAttachment() != null) {
                    if (message.getAttachment() instanceof UpdateTeamAttachment) {
                        UpdateTeamAttachment attachment = (UpdateTeamAttachment) message.getAttachment();
                        for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet()) {
                            if (field.getKey() == TeamFieldEnum.ICON) {
                                return true;
                            }
                        }
                    } else if (message.getAttachment() instanceof AVChatAttachment) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private BroadcastReceiver localeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                updateLocale();
            }
        }
    };

    private void registerLocaleReceiver(boolean register) {
        if (register) {
            updateLocale();
            IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
            registerReceiver(localeReceiver, filter);
        } else {
            unregisterReceiver(localeReceiver);
        }
    }

    private void updateLocale() {
        NimStrings strings = new NimStrings();
        strings.status_bar_multi_messages_incoming = getString(R.string.nim_status_bar_multi_messages_incoming);
        strings.status_bar_image_message = getString(R.string.nim_status_bar_image_message);
        strings.status_bar_audio_message = getString(R.string.nim_status_bar_audio_message);
        strings.status_bar_custom_message = getString(R.string.nim_status_bar_custom_message);
        strings.status_bar_file_message = getString(R.string.nim_status_bar_file_message);
        strings.status_bar_location_message = getString(R.string.nim_status_bar_location_message);
        strings.status_bar_notification_message = getString(R.string.nim_status_bar_notification_message);
        strings.status_bar_ticker_text = getString(R.string.nim_status_bar_ticker_text);
        strings.status_bar_unsupported_message = getString(R.string.nim_status_bar_unsupported_message);
        strings.status_bar_video_message = getString(R.string.nim_status_bar_video_message);
        strings.status_bar_hidden_message_content = getString(R.string.nim_status_bar_hidden_msg_content);
        NIMClient.updateStrings(strings);
    }

    private void initUiKit() {
        GlobalCache.setAccount(NimPreferences.getAccountId());

        // 设置用户资料提供者（必须）,通讯录提供者（必须）
        NimUIKit.init(this, mUserInfoProvider, mContactProvider);

        // 注册定位信息提供者类（可选）,如果需要发送地理位置消息，必须提供。
        // demo中使用高德地图实现了该提供者，开发者可以根据自身需求，选用高德，百度，google等任意第三方地图和定位SDK。
        NimUIKit.setLocationProvider(new NimLocationProvider());

        // 会话窗口的定制初始化: 示例代码可详见demo源码中的SessionHelper类。
        // 1.注册自定义消息附件解析器（可选）
        // 2.注册各种扩展消息类型的显示ViewHolder（可选）
        // 3.设置会话中点击事件响应处理（一般需要）
        SessionHelper.init();

        // 通讯录列表定制初始化：示例代码可详见demo源码中的ContactHelper类。
        // 1.设置通讯录列表中点击事响应处理（一般需要）
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = Main.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.drawable.ic_launcher;

        // 呼吸灯配置
        config.ledARGB = Color.BLUE;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = Storage.getAppCacheDataDir() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = DeviceUtil.getScreenPixelsWidth() / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = mUserInfoProvider;
        return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {

        String accountId = NimPreferences.getAccountId();
        String token = NimPreferences.getAccountToken();

        if (!LoginManager.hasLogined()) {
            return null;
        }

        if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(accountId,token);
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ImageLoader.getInstance().destroy();
    }
}
