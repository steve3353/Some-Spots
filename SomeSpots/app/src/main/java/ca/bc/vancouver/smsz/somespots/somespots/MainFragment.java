package ca.bc.vancouver.smsz.somespots.somespots;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private ArrayAdapter<String> mainItemAdapter;


    public MainFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        String mainItem1= "Water Fountains (City of Vancouver) ";
        String mainItem2= "Parks with Public Washrooms (City of Vancouver)";
        String mainItem3= "Bike Racks (City of Vancouver)";
        String mainItem4= "Bikeways in City of Vancouver";
        String mainItem5= "Bike Pumps in City of Vancouver";

        View rootView = inflater.inflate(R.layout.main_fragment, container,false);

        rootView.setBackgroundResource(R.drawable.mainbackground);


        List<String> mainItems = new ArrayList<String>();

        mainItems.add(mainItem1);
        mainItems.add(mainItem2);
        mainItems.add(mainItem3);
        mainItems.add(mainItem4);
        mainItems.add(mainItem5);

        mainItemAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_main,
                R.id.list_item_main_textview,
                mainItems);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_main);

        listView.setAdapter(mainItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent openMapIntent = new Intent(getActivity(), DisplayMapActivity.class).putExtra(Intent.EXTRA_TEXT, mainItemAdapter.getItem(i));
                startActivity(openMapIntent);

            }
        });

        return rootView;

    }


}
