package com.example.finalmadproject.Settings;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.finalmadproject.Database.DatabaseHelper;
import com.example.finalmadproject.R;

import java.util.ArrayList;


public class AudioListFragment extends DialogFragment {
    ListView myListViewforSongs;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    private static final int MY_PERMISSION_REQUEST = 1;
    Button SoundButton;
    DatabaseHelper myDB;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_audio_list,container,false);
        myListViewforSongs = v.findViewById(R.id.mySongListView);
        SoundButton = v.findViewById(R.id.InsertSong);
        SoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() , Sounds.class);
                startActivity(intent);
            }
        });
        myDB = new DatabaseHelper(getActivity());
        //check whether the manifest permission already granted to read the external storage
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //app has requested this permission previously and the user denied the request
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                //request permition from user
                ActivityCompat.requestPermissions(getActivity(),new String []{Manifest.permission.READ_EXTERNAL_STORAGE} , MY_PERMISSION_REQUEST);
            }
            else {
                //permision request is not asked previously
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }else{
            dostuff();
        }
        return v;
    }

    public void dostuff() {
        arrayList = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//this shows all the audio uris in external storage
        // are added into Uri object
        final Cursor songCursor = contentResolver.query(songUri,null,null,null,null);
        if(songCursor != null && songCursor.moveToFirst()){
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);//pass the audio file location here

            do {
                //String currentTitle = songCursor.getString(songtitle);
                String currentLocation = songCursor.getString(songLocation);// pass the string value of the songLocation
                //arrayList.add(currentTitle);
                arrayList.add(currentLocation);//adding into the arrayList
                adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1 , arrayList);//here we are passing the arrayLIst to the adapter
                myListViewforSongs.setAdapter(adapter);//adapter is set to a listView
                //here when we click on an adapter
                myListViewforSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String title = myListViewforSongs.getItemAtPosition(i).toString();//pass the string value at the postion
                        String location = myListViewforSongs.getItemAtPosition(i).toString();//pass the string value at the postion
                       // if (title.equals())
                        String songname = title.substring(title.length() - 10);//sub string to song title to 10 words


                        boolean inserted = myDB.insertData(songname , location);//callling the insert method
                        if (inserted == true) {
                            Toast.makeText(getActivity() , "data added" , Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity() , Sounds.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "data declined", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity() , Sounds.class);
                            startActivity(intent);
                        }
                    }
                });


            }while (songCursor.moveToNext());
        }

    }
    //this shows the permission message to access external storage to user
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch(requestCode){
            //when permission request
            case MY_PERMISSION_REQUEST: {
                //check granted result length is grater than 0 and
                //granted result is equal to permission granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //check the permission granted
                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                        dostuff();
                    }else{
                        Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                    return;
                }
            }
        }
    }
}