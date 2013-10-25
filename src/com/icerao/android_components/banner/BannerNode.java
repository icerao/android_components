package com.icerao.android_components.banner;

/**
 * 每个banner节点展示用到的数据结构，包括图片url，点击后的intent uri
 * User: icerao
 * Date: 13-10-23
 * Time: 下午3:03
 */
public class BannerNode
{
	public String title;
	public int resId;
	public String actionUrl;

	public BannerNode(String actionUrl, int resId, String title)
	{
		this.actionUrl = actionUrl;
		this.resId = resId;
		this.title = title;
	}
}
