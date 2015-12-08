package com.jobs.lib_v1.app;

import java.lang.reflect.Method;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Activity 栈管理器
 * 
 * @author solomon.wen
 * @date 2012-09-06
 */
public class AppActivities {
	private static Activity mCurActivity = null; // 当前活动 Activity
	private final static Stack<Activity> mActivityStack = new Stack<Activity>(); // Activity 栈

	/**
	 * 设置当前活动的 Activity
	 * 
	 * @param activity 当前被激活的 Activity
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static void setCurrentActivity(Activity activity) {
		mCurActivity = activity;
	}

	/**
	 * 获取栈中 Activity 的数量
	 * 
	 * @author janzon.tang
	 * @date 2013-5-23
	 */
	public static int getActivityStackSize() {
		int stackSize = 0;

		synchronized (AppActivities.class) {
			stackSize = mActivityStack.size();
        }

		return stackSize;
	}

	/**
	 * 通知栈里面所有 activity 调用 method 方法
	 * 
	 * @param method 字符串方法名
	 * @param isLocalMethod true 表示获取本类所有成员方法; false 表示取该类中包括父类所有 public 方法
	 */
	public static void noticeActivity(final String method, final boolean isLocalMethod) {
		noticeActivity(method, isLocalMethod, null, null);
	}

	/**
	 * 通知栈里面所有 activity 调用 method 方法；带参数
	 * 
	 * @param method 字符串方法名
	 * @param isLocalMethod true 表示获取本类所有成员方法; false 表示取该类中包括父类所有 public 方法
	 * @param parType 参数类型数组
	 * @param obj 参数列表
	 */
	public static void noticeActivity(final String method, final boolean isLocalMethod, final Class<?>[] parType, final Object[] obj) {
		new Thread() { // 启用新线程的目的是不影响当前业务的进行
			@Override
			public void run() {
				synchronized (AppActivities.class) {
					for (final Activity activity : mActivityStack) {
						try {
							if (activity.isFinishing()) {
								continue;
							}

							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										if (activity.isFinishing()) {
											return;
										}

										Class<? extends Activity> activityClass = activity.getClass();
										Method activityMethod;

										if (!isLocalMethod) {
											// getDeclaredMethod得到所有权限的方法，getMethod只能获取到public类型的方法
											activityMethod = activityClass.getDeclaredMethod(method, parType);
										} else {
											// getMethod可以获取到包括父类中的public方法 替代以前的使用getSuperClass，这样避免不确定性因素
											activityMethod = activityClass.getMethod(method, parType);
										}

										// 设置该方法的访问权限
										activityMethod.setAccessible(true);

										// 调用该方法
										activityMethod.invoke(activity, obj);
									} catch (Throwable e) {
										// 如果出错，不需要做处理，也不需要打印
									}
								}
							});
						} catch (Throwable e) {
							// 如果出错，不需要做处理，也不需要打印
						}
					}
				}
			}
		}.start();
	}

	/**
	 * 获取当前活动的 Activity
	 * 
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static Activity getCurrentActivity() {
		return mCurActivity;
	}

    /**
     * 获取指定的 Activity
     *
     * @author oliver.chen
     * @date 2015-02-15
     */
    public synchronized static Activity getTheActivity(Class<?> cls) {
        if (null == cls) {
            return null;
        }

        if (mActivityStack.size() > 0) {
            for (int i = mActivityStack.size() - 1; i >= 0; i--) {
                Activity activity = mActivityStack.get(i);
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        }

        return null;
    }

	/**
	 * 退出栈中所有 Activity
	 * 
	 * @author solomon.wen
	 * @date 2012-12-03
	 */
	public synchronized static void finishAllActivities() {
		while (true) {
			if (mActivityStack.size() < 1) {
				return;
			}

			Activity curActivity = mActivityStack.lastElement();

			if (curActivity == null) {
				break;
			}

			curActivity.finish();
			mActivityStack.remove(curActivity);
		}
	}

	/**
	 * Activity前向跳转：从栈尾开始销毁指定 Activity 类型之后的所有 Activity
	 * 
	 * @param cls 指定 Activity 类型
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static void popToActivity(Class<?> cls) {
		if (null == cls) {
			return;
		}

		while (true) {
			if (mActivityStack.size() < 1) {
				return;
			}

			Activity curActivity = mActivityStack.lastElement();

			if (curActivity == null) {
				break;
			}

			if (curActivity.getClass().equals(cls)) {
				break;
			}

			curActivity.finish();
			mActivityStack.remove(curActivity);
		}
	}
	
	/**
	 * 移除掉堆栈中所有指定的activity
	 * 
	 * @author janzon.tang
	 * @time 2014-4-4
	 * @param cls
	 */
	public synchronized static void finishTheActivity(Class<?> cls) {
		if (null == cls) {
			return;
		}

		while (mActivityStack.size() > 0) {
			boolean needLoop = false;

			for (int i = 0; i < mActivityStack.size(); i++) {
				Activity activity = mActivityStack.get(i);
				if (activity.getClass().equals(cls)) {
					needLoop = true;
					activity.finish();
					mActivityStack.remove(activity);
					break;
				}
			}

			if (!needLoop) {
				break;
			}
		}
	}

    /**
     * 移除掉堆栈中指定的两个activity
     * @author brightmoon
     * @date 2015-03-18
     */
    public synchronized static void finishTheActivity(Class<?> cls, Class<?> nextCls) {
        if (null == cls && null == nextCls) {
            return;
        }

        while (mActivityStack.size() > 0) {
            boolean needLoop = false;

            for (int i = 0; i < mActivityStack.size(); i++) {
                Activity activity = mActivityStack.get(i);
                if (activity.getClass().equals(cls) || activity.getClass().equals(nextCls)) {
                    needLoop = true;
                    activity.finish();
                    mActivityStack.remove(activity);
                    break;
                }
            }

            if (!needLoop) {
                break;
            }
        }
    }

	/**
	 * Activity前向跳转：从栈尾开始销毁指定 Activity 对象之后的所有 Activity
	 * 
	 * @param activity 指定 Activity 对象
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static void popToActivity(Activity activity) {
		if (null == activity) {
			return;
		}

		while (true) {
			if (mActivityStack.size() < 1) {
				return;
			}

			Activity curActivity = mActivityStack.lastElement();

			if (curActivity == null) {
				break;
			}

			if (curActivity.equals(activity)) {
				break;
			}

			curActivity.finish();
			mActivityStack.remove(curActivity);
		}
	}

	/**
	 * 获取当前 Activity 的完整路径
	 * 
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static String getActivityPath() {
		if (mActivityStack.size() < 1) {
			return "/";
		}

		StringBuffer message = new StringBuffer();

		for (Activity x : mActivityStack) {
			message.append(String.format("/%s", AppUtil.getClassName(x)));
		}

		return message.toString();
	}

	/**
	 * Activity入栈
	 * 
	 * @param activity Activity 对象
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static void pushActivity(Activity activity) {
		if (null == activity) {
			return;
		}

		AppTasks.createActivityTasks(activity);

		if(mActivityStack.contains(activity)){
			mActivityStack.remove(activity);
		}

		mActivityStack.add(activity);
	}

	/**
	 * 从栈尾开始，把指定类名的 Activity 从 activityStack 中移除 遇到不是指定类名的 Activity 则终止
	 * 
	 * @param cls 指定类名的 Activity
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static void popLastActivity(Class<?> cls) {
		if (null == cls) {
			return;
		}

		if (mActivityStack.size() < 1) {
			return;
		}

		while (true) {
			Activity curActivity = mActivityStack.lastElement();

			if (curActivity == null) {
				break;
			}

			if (!curActivity.getClass().equals(cls)) {
				break;
			}

			curActivity.finish();
			mActivityStack.remove(curActivity);
		}
	}

	/**
	 * 把指定 Activity 从 activityStack 中移除
	 * 
	 * @param activity 指定 Activity
	 * @author solomon.wen
	 * @date 2012-09-06
	 */
	public synchronized static void removeActivity(Activity activity) {
		if (mActivityStack.size() < 1 || null == activity) {
			return;
		}

		AppTasks.RemoveActivityTasks(activity);

		for (Activity x : mActivityStack) {
			if (x.equals(activity)) {
				mActivityStack.remove(x);
				break;
			}
		}
	}

    /**
     * 返回上一级页面
     * @author brightmoon
     * @date 2015-11-03
     */
    public static void gotoLastActivity(){
        if(mActivityStack != null && mActivityStack.size() > 0){
            Activity lastActivity = mActivityStack.lastElement();
            Activity curActivity = getCurrentActivity();
            Intent intent = new Intent(curActivity, lastActivity.getClass());
            curActivity.startActivity(intent);
        }
    }
}
