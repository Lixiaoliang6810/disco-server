package com.miner.disco.netease.support.request;

import com.miner.disco.netease.support.NeteaseImObject;
import com.miner.disco.netease.support.NeteaseImRequest;
import com.miner.disco.netease.support.response.NeteaseImSendBatchMsgResponse;

/**
 * Created by linjw on 2017/8/21.
 */
public class NeteaseImSendBatchMsgRequest implements NeteaseImRequest<NeteaseImSendBatchMsgResponse> {

    private final static String URL = "https://api.netease.im/nimserver/msg/sendBatchAttachMsg.action";
    private final static String METHOD = "POST";
    private NeteaseImObject bizModel = null;

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public String getUrl() {
        return URL;
    }

    @Override
    public void setBizModel(NeteaseImObject bizModel) {
        this.bizModel = bizModel;
    }

    @Override
    public NeteaseImObject getBizModel() {
        return this.bizModel;
    }

    @Override
    public Class<NeteaseImSendBatchMsgResponse> getResponseClass() {
        return NeteaseImSendBatchMsgResponse.class;
    }
}
