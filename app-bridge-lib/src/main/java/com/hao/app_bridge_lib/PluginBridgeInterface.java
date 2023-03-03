package com.hao.app_bridge_lib;

import android.content.Context;
import android.os.Bundle;

/**
 * @author raohaohao
 * @version 1.0
 * @data 2023/2/7
 */
public interface PluginBridgeInterface {
    /**
     * @param context
     * @param formId  业务诉求
     * @param bundle  业务描述
     */
    Object event(Context context, long formId, Bundle bundle);

}
