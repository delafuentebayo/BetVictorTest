package betvictor.testing.betvictortest;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import betvictor.testing.data.TweetData;

/**
 * Created by Jesus Miguel de la Fuente on 04/03/2015.
 * Class to declare the adapter for the listview of the tweets
 */
public class TweetsAdapter extends ArrayAdapter<TweetData>{

        private final Context context;
        private final ArrayList<TweetData> values;

        public TweetsAdapter(Context context, ArrayList<TweetData> values) {
            super(context, R.layout.list_tweets, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.list_tweets, parent, false);
            TextView content = (TextView) rowView.findViewById(R.id.tweet);
            TextView author = (TextView) rowView.findViewById(R.id.author);
            TextView location = (TextView) rowView.findViewById(R.id.location);
            String s = values.get(position).getContent();
            content.setText(s);
            author.setText(values.get(position).getAuthor());
            location.setText(values.get(position).getLocation());
            return rowView;

    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}
