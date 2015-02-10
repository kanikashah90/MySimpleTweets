package com.codepath.apps.mysimpletweets;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThread = 4;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startPageIndex = 0;

    public EndlessScrollListener(int visibleThread) {
        this.visibleThread = visibleThread;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if(totalItemCount == 0) {
                this.loading = true;
            }
        }

        if(loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        if(!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThread)) {
            onLoadMore(currentPage+1, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

}
