package com.ycombinator.news.dto;

import com.xavierlepretre.hackernewsbrowser.Collapsible;

public class CollapsedState implements Collapsible
{
    public final int zeroBasedDepth;
    private boolean collapsed;

    public CollapsedState(int zeroBasedDepth)
    {
        this.zeroBasedDepth = zeroBasedDepth;
        this.collapsed = true;
    }

    public CollapsedState(boolean collapsed)
    {
        this.zeroBasedDepth = 0;
        this.collapsed = collapsed;
    }

    @Override public boolean isCollapsed()
    {
        return collapsed;
    }

    @Override public void setCollapsed(boolean collapsed)
    {
        this.collapsed = collapsed;
    }

    @Override public void toggleCollapsed()
    {
        setCollapsed(!collapsed);
    }
}
