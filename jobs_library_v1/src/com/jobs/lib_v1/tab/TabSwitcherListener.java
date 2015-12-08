package com.jobs.lib_v1.tab;

public interface TabSwitcherListener {
	// tab 选项卡点击触发的事件
	public void onClick(TabSwitcher tabSwitcher, int tabIndex);

	// tab 切换时触发该事件
	public void onChange(TabSwitcher tabSwitcher, int tabIndex);

	// 如果某项 tab 已处于激活状态，再点击它时，会触发该事件
	public void onReClick(TabSwitcher tabSwitcher, int tabIndex);
}
