package com.icerao.android_components;

import android.app.Activity;
import android.os.Bundle;
import com.icerao.android_components.banner.BannerNode;
import com.icerao.android_components.banner.HomeBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * User: icerao
 * Date: 13-10-24
 * Time: 下午2:59
 */
public class BannerDemo extends Activity
{

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		HomeBanner banner=(HomeBanner)this.findViewById(R.id.banner);
		List<BannerNode> nodeList=new ArrayList<BannerNode>();
		BannerNode n1=new BannerNode("http://www.baidu.com",R.drawable.b1,"test1");
		BannerNode n2=new BannerNode("a2",R.drawable.b2,"test2");
		BannerNode n3=new BannerNode("a3",R.drawable.b3,"test3");
		BannerNode n4=new BannerNode("a4",R.drawable.b4,"test4");
		nodeList.add(n1);
		nodeList.add(n2);
		nodeList.add(n3);
		nodeList.add(n4);
		banner.refresh(nodeList);
	}
}