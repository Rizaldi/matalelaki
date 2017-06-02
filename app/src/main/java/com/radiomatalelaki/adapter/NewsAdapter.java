package com.radiomatalelaki.adapter;

/**
 * Created by rianpradana on 5/18/17.
 */
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.radiomatalelaki.R;
import com.radiomatalelaki.app.AppController;
import com.radiomatalelaki.data.NewsData;
import com.radiomatalelaki.FeedImageView;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
        private Activity activity;
        private LayoutInflater inflater;
        private List<NewsData> newsItems;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        public NewsAdapter(Activity activity, List<NewsData> newsItems) {
            this.activity = activity;
            this.newsItems = newsItems;
        }

        @Override
        public int getCount() {
            return newsItems.size();
        }

        @Override
        public Object getItem(int location) {
            return newsItems.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_news, null);

            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            FeedImageView thumbNail = (FeedImageView) convertView.findViewById(R.id.news_gambar);
            TextView judul = (TextView) convertView.findViewById(R.id.news_judul);
            TextView timestamp = (TextView) convertView.findViewById(R.id.news_timestamp);
            TextView isi = (TextView) convertView.findViewById(R.id.news_isi);

            NewsData news = newsItems.get(position);

            thumbNail.setImageUrl(news.getGambar(), imageLoader);
            judul.setText(news.getJudul());
            timestamp.setText(news.getDatetime());
            isi.setText(Html.fromHtml(news.getIsi()));
// Feed image
            if (news.getGambar() != null) {
                thumbNail.setImageUrl(news.getGambar(), imageLoader);
                thumbNail.setVisibility(View.VISIBLE);
                thumbNail
                        .setResponseObserver(new FeedImageView.ResponseObserver() {
                            @Override
                            public void onError() {
                            }

                            @Override
                            public void onSuccess() {
                            }
                        });
            } else {
                thumbNail.setVisibility(View.GONE);
            }
            return convertView;
        }

    }
