package com.gcu.mobilecoursework;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gcu.mobilecoursework.Model.TrafficFeedModel;
import com.gcu.mobilecoursework.Tasks.AsyncTrafficDataTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyRecyclerViewAdapter.ItemClickListener{
    private TextView rawDataDisplay;
    private Button startButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter recycleViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.trafficFeedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recycleViewAdapter = new MyRecyclerViewAdapter(this, new ArrayList<TrafficFeedModel>());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        this.recyclerView.setAdapter(recycleViewAdapter);
        this.recycleViewAdapter.setClickListener(this);

        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);



    }

    public void onClick(View aview) {
           new AsyncTrafficDataTask(recycleViewAdapter, progressBar, BuildConfig.PLANNED_ROADWORKS).execute();

//            Intent mpd = new Intent(getApplicationContext(), TrafficFeedActivity.class);
//            switch(aview.getId()){
//                case R.id.Trafficbtn1:
//                    mpd.putExtra("rssLink", RSSTrafficLinks.get(0));
//                    startActivity(mpd);
//                    break;
//                case R.id.Trafficbtn2:
//                    mpd.putExtra("rssLink", RSSTrafficLinks.get(1));
//                    startActivity(mpd);
//                    break;
//                case R.id.Trafficbtn3:
//                    mpd.putExtra("rssLink", RSSTrafficLinks.get(2));
//                    startActivity(mpd);
//                    break;
//            }

    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + recycleViewAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }
} // End of MainActivity