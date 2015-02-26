package com.xavierlepretre.hackernewsbrowser;

import android.test.ActivityInstrumentationTestCase2;

public class ViewItemActivityTest extends ActivityInstrumentationTestCase2<ViewItemActivity>
{
    public ViewItemActivityTest()
    {
        super(ViewItemActivity.class);
    }

    @Override protected void setUp() throws Exception
    {
        super.setUp();

        setActivityInitialTouchMode(true);
    }
}
