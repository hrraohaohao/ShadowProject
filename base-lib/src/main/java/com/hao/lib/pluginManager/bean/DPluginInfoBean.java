package com.hao.lib.pluginManager.bean;

/**
 * @author：atar
 * @date: 2021/6/5
 * @description:
 */
public class DPluginInfoBean extends DManagerInfoBean {

    private int plugin_version_code;
    private String plugin_version_name;
    private String plugin_url;

    public int getPlugin_version_code() {
        return plugin_version_code;
    }

    public void setPlugin_version_code(int plugin_version_code) {
        this.plugin_version_code = plugin_version_code;
    }

    public String getPlugin_version_name() {
        return plugin_version_name;
    }

    public void setPlugin_version_name(String plugin_version_name) {
        this.plugin_version_name = plugin_version_name;
    }

    public String getPlugin_url() {
        return plugin_url;
    }

    public void setPlugin_url(String plugin_url) {
        this.plugin_url = plugin_url;
    }
}
