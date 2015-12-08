package com.jobs.lib_v1.misc.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.jobs.lib_v1.data.ObjectSessionStore;

/**
 * 全局主线程消息发送器
 * 
 * @author solomon.wen
 * @date 2014-01-24
 */
public class MessageSender {
	private final static String mObjectName = "messageTargetObject";
	private static Handler mHandler = null;

    /**
     * Pushes a message onto the end of the message queue after all pending messages
     * before the current time. It will be received in {@link #handleMessage},
     * in the thread attached to this handler.
     *  
     * @return Returns true if the message was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.
     */
	public final static boolean sendMessage(MessageHandler messageTargetObject, Message msg) {
    	initHandler();
        return mHandler.sendMessage(getMessage(messageTargetObject, msg));
	}

    /**
     * Sends a Message containing only the what value.
     *  
     * @return Returns true if the message was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.
     */
	public final static boolean sendEmptyMessage(MessageHandler messageTargetObject, int what) {
    	initHandler();
        Message msg = mHandler.obtainMessage();
        msg.what = what;
		return sendMessage(messageTargetObject, msg);
	}

    /**
     * Sends a Message containing only the what value, to be delivered
     * after the specified amount of time elapses.
     * @see #sendMessageDelayed(android.os.Message, long) 
     * 
     * @return Returns true if the message was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.
     */
	public final static boolean sendEmptyMessageDelayed(MessageHandler messageTargetObject, int what, long delayMillis) {
    	initHandler();
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        return mHandler.sendMessageDelayed(getMessage(messageTargetObject, msg), delayMillis);
    }

	/**
     * Enqueue a message into the message queue after all pending messages
     * before (current time + delayMillis). You will receive it in
     * {@link #handleMessage}, in the thread attached to this handler.
     *  
     * @return Returns true if the message was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.  Note that a
     *         result of true does not mean the message will be processed -- if
     *         the looper is quit before the delivery time of the message
     *         occurs then the message will be dropped.
     */
    public final static boolean sendMessageDelayed(MessageHandler messageTargetObject, Message msg, long delayMillis){
    	initHandler();
        return mHandler.sendMessageDelayed(getMessage(messageTargetObject, msg), delayMillis);
    }

    /**
     * Enqueue a message into the message queue after all pending messages
     * before the absolute time (in milliseconds) <var>uptimeMillis</var>.
     * <b>The time-base is {@link android.os.SystemClock#uptimeMillis}.</b>
     * You will receive it in {@link #handleMessage}, in the thread attached
     * to this handler.
     * 
     * @param uptimeMillis The absolute time at which the message should be
     *         delivered, using the
     *         {@link android.os.SystemClock#uptimeMillis} time-base.
     *         
     * @return Returns true if the message was successfully placed in to the 
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.  Note that a
     *         result of true does not mean the message will be processed -- if
     *         the looper is quit before the delivery time of the message
     *         occurs then the message will be dropped.
     */
	public final static boolean sendMessageAtTime(MessageHandler messageTargetObject, Message msg, long uptimeMillis) {
    	initHandler();
		return mHandler.sendMessageAtTime(getMessage(messageTargetObject, msg), uptimeMillis);
	}

    /**
     * Returns a new {@link android.os.Message Message} from the global message pool. More efficient than
     * creating and allocating new instances. The retrieved message has its handler set to this instance (Message.target == this).
     *  If you don't want that facility, just call Message.obtain() instead.
     */
    public static final Message obtainMessage(){
    	initHandler();
        return Message.obtain(mHandler);
    }

    /**
     * Same as {@link #obtainMessage()}, except that it also sets the what member of the returned Message.
     * 
     * @param what Value to assign to the returned Message.what field.
     * @return A Message from the global message pool.
     */
    public static final Message obtainMessage(int what){
    	initHandler();
        return Message.obtain(mHandler, what);
    }

	private static final Message getMessage(MessageHandler messageTargetObject, Message msg) {
		if (null == messageTargetObject) {
			return msg;
		}

		if (null == msg) {
			msg = obtainMessage();
		}

		Bundle data = null;

		if (null != msg) {
			data = msg.getData();
		}

		if (null == data) {
			data = new Bundle();
		}

		data.putString(mObjectName, ObjectSessionStore.insertObject(messageTargetObject));

		msg.setData(data);

		return msg;
	}

	private static synchronized void initHandler(){
		if(null != mHandler){
			return;
		}

		mHandler = new Handler(Looper.getMainLooper()){
			public void handleMessage(Message msg) {
				if(null == msg){
					return;
				}

				Bundle data = msg.getData();
				if(null == data){
					return;
				}
				
				MessageHandler object = (MessageHandler) ObjectSessionStore.popObject(data.getString(mObjectName));
				if(null == object){
					return;
				}
				
				if (object instanceof MessageHandler) {
					object.handleMessage(msg);
				}
			}
		};
	}
}
