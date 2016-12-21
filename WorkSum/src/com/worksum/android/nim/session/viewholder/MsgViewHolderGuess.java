package com.worksum.android.nim.session.viewholder;

import com.netease.nim.uikit.session.viewholder.MsgViewHolderText;
import com.worksum.android.nim.session.extension.GuessAttachment;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderGuess extends MsgViewHolderText {

    @Override
    protected String getDisplayText() {
        GuessAttachment attachment = (GuessAttachment) message.getAttachment();

        return attachment.getValue().getDesc() + "!";
    }
}
