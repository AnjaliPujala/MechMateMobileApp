
package com.example.mechmate;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

    public class HistoryActivity extends AppCompatActivity {
        private ListView historyListView;
        private ArrayList<Requests> requestsHistory;
        private RequestsAdapter requestsAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_history);

            historyListView = findViewById(R.id.historyListView);
            requestsHistory = (ArrayList<Requests>) getIntent().getSerializableExtra("requestHistory");

            requestsAdapter = new RequestsAdapter(this, requestsHistory);
            historyListView.setAdapter(requestsAdapter);
        }
    }

