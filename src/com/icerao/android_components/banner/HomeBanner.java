package com.icerao.android_components.banner;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.icerao.android_components.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以循环播放的首页图片banner实现。需要依赖android-support-v4.jar
 * User: icerao
 * Date: 13-10-23
 * Time: 下午3:00
 */
public class HomeBanner extends RelativeLayout
{
	private static final long PLAY_TIME = 5000;
	private static final int MSG_PLAY = 1;

	private Context context;
	private ViewPager viewPager;
	private BannerPagerAdapter bannerPagerAdapter;
	private LayoutInflater layoutInflater;
	private List<BannerNode> cards = null;
	private ViewGroup points;
	private boolean isPlaying = false;

	public HomeBanner(Context context)
	{
		super(context);
		init(context);
	}

	public HomeBanner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public HomeBanner(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.banner, this);
		viewPager = (ViewPager) findViewById(R.id.images);
		int h = 200;//这里的高度最好根据用户的dpi来计算
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, h);
		viewPager.setLayoutParams(params);
		bannerPagerAdapter = new BannerPagerAdapter();
		viewPager.setAdapter(bannerPagerAdapter);
		points = (ViewGroup) findViewById(R.id.points);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int i, float v, int i2)
			{
			}

			@Override
			public void onPageSelected(int i)
			{
				updateDot(i);
			}

			@Override
			public void onPageScrollStateChanged(int i)
			{
				if (i == ViewPager.SCROLL_STATE_IDLE)
				{
					startPlay();
				}
				else
				{
					requestDisallowInterceptTouchEvent(true);
					stopPlay();
				}
			}
		});
	}

	/**
	 * 更新dot的位置
	 *
	 * @param index
	 */
	private void updateDot(int index)
	{
		points.removeAllViews();
		if (cards == null || cards.size() < 2)
		{
			return;
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		//dot点的一些布局参数，一般用sp，dp等。需要转换计算
		params.leftMargin = 2;
		params.rightMargin = 2;
		params.height = 8;
		params.width = 8;
		ImageView dot = null;
		int current = index % cards.size();//index可能无限大，因为需要循环播放
		for (int i = 0; i < cards.size(); i++)
		{
			dot = new ImageView(context);
			dot.setLayoutParams(params);
			if (current == i)
			{
				dot.setImageResource(R.drawable.dot_highlight);
			}
			else
			{
				dot.setImageResource(R.drawable.dot);
			}
			points.addView(dot);
		}
	}

	public void refresh(List<BannerNode> colorCardItems)
	{
		stopPlay();
		this.cards = colorCardItems;
		if (colorCardItems != null && colorCardItems.size() > 0)
		{
			List<View> bannerViewList = new ArrayList<View>(cards.size());
			BannerNode node;
			View view;
			TextView textView;
			ImageView imageView;
			for (int i = 0; i < cards.size(); i++)
			{
				node = cards.get(i);
				view = layoutInflater.inflate(R.layout.banner_imagenode, null);
				textView = (TextView) view.findViewById(R.id.default_pic_txt);
				textView.setText(node.title);
				imageView = (ImageView) view.findViewById(R.id.pic);
				imageView.setImageResource(node.resId);
				final String url = node.actionUrl;
				imageView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						//todo 根据url处理跳转
						Intent intent=new Intent(url);
						HomeBanner.this.context.startActivity(intent);
					}
				});

				bannerViewList.add(view);
			}
			bannerPagerAdapter.setViewList(bannerViewList);
			bannerPagerAdapter.notifyDataSetChanged();
			viewPager.setCurrentItem(0);
			updateDot(0);
			if (cards.size() > 1)
			{
				startPlay();
			}
		}
		else
		{
			setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
			bannerPagerAdapter.setViewList(null);
			bannerPagerAdapter.notifyDataSetChanged();
			updateDot(0);
		}
	}

	private class BannerPagerAdapter extends PagerAdapter
	{
		private List<View> viewList = null;

		public void setViewList(List<View> viewList)
		{
			this.viewList = viewList;

		}

		@Override
		public int getCount()
		{
			if (viewList == null || viewList.size() == 0)
			{
				return 0;
			}
			if (viewList.size() == 1)
			{
				return 1;
			}
			return Integer.MAX_VALUE;//为了实现无限循环播放
		}

		@Override
		public boolean isViewFromObject(View view, Object o)
		{
			return view == o;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			int current = position % viewList.size();
			View v=viewList.get(current);//todo 有个潜在的问题，循环播放时，如果条目太少（2个时）因为cache会要初始化一些后面的显示节点，但是跟当前的节点冲突了，当前节点可能在显示所以parent还是有引用。可能需要clone多分才行
			container.addView(v);
			return viewList.get(current);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
		}
	}


	private Handler playHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case MSG_PLAY:
					if (isPlaying)
					{
						viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
					}
					if (playHandler != null && isPlaying)
					{
						playHandler.sendEmptyMessageDelayed(MSG_PLAY, PLAY_TIME);
					}
					break;
				default:
					break;
			}
		}
	};

	private void startPlay()
	{
		isPlaying = true;//todo
		if (playHandler != null)
		{
			playHandler.removeMessages(MSG_PLAY);
			playHandler.sendEmptyMessageDelayed(MSG_PLAY, PLAY_TIME);
		}
	}

	private void stopPlay()
	{
		isPlaying = false;
		if (playHandler != null)
		{
			playHandler.removeMessages(MSG_PLAY);
		}
	}

	public void recycle()
	{
		if (cards != null)
		{
			cards.clear();
		}
		if (playHandler != null)
		{
			playHandler.removeMessages(MSG_PLAY);
			playHandler = null;
		}
	}
}
