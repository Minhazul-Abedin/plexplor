package com.example.finalproject;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.finalproject.Models.Post;
import com.example.finalproject.fragment.aboutFragment;
import com.example.finalproject.fragment.mapFragment;
import com.example.finalproject.fragment.settingFragment;
import com.example.finalproject.fragment.visitedFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
private  static final int PReqCode=2;
private  static final int REQUESCODE=2;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentuser;
    Dialog popAddPost;
    ImageView popupUserImage,popupPostImage,popupAddBtn;
    TextView popupTitle,popupDescription;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        firebaseAuth=FirebaseAuth.getInstance();
        currentuser=firebaseAuth.getCurrentUser();
        
        iniPopup();
        setupPopupImageClick();



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        updateNavHeader();


// default  map fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.con, new mapFragment()).commit();



    }

    private void setupPopupImageClick() {

        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAndRequestForPermission();
                //open gallery
            }
        });
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(navigation.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(navigation.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(navigation.this,"Please accept for required permission", Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(navigation.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostImage.setImageURI(pickedImgUri);

        }


    }

    private void iniPopup() {
        popAddPost = new Dialog(this);

 popAddPost.setContentView(R.layout.popup_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        Glide.with(navigation.this).load(currentuser.getPhotoUrl()).into(popupUserImage);
popupAddBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        popupAddBtn.setVisibility(View.INVISIBLE);
        popupClickProgress.setVisibility(View.VISIBLE);

        if(!popupTitle.getText().toString().isEmpty()&&
        !popupDescription.getText().toString().isEmpty()&&
                pickedImgUri !=null ){

            StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Place_images");
            final StorageReference imageFilePath=storageReference.child(pickedImgUri.getLastPathSegment());
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageDownloadlink= uri.toString();
                            Post post=new Post(popupTitle.getText().toString(),
                           popupDescription.getText().toString(),
                                    imageDownloadlink,currentuser.getUid(),
                                    currentuser.getPhotoUrl().toString());
                            addPost(post);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error in uploading
                             popupClickProgress.setVisibility(View.INVISIBLE);
                             popupAddBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });



        }
        else{
            Toast.makeText(navigation.this,"Please Verify all input fields and choose a pic",Toast.LENGTH_SHORT).show();
            popupAddBtn.setVisibility(View.VISIBLE);
            popupClickProgress.setVisibility(View.INVISIBLE);
        }
    }
});


    }

    private void addPost(Post post) {

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myref=database.getReference("Posts").push();

        String key=myref.getKey();
        post.setPostKey(key);

        myref.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(navigation.this,"Post Added Successfully",Toast.LENGTH_SHORT).show();
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");

            getSupportFragmentManager().beginTransaction().replace(R.id.con, new mapFragment()).commit();

            // Handle the camera action
        } else if (id == R.id.visited) {
            getSupportActionBar().setTitle("Visited");
            getSupportFragmentManager().beginTransaction().replace(R.id.con, new visitedFragment()).commit();

        } else if (id == R.id.setting) {
            getSupportActionBar().setTitle("Setting");

            getSupportFragmentManager().beginTransaction().replace(R.id.con, new settingFragment()).commit();


        } else if (id == R.id.about) {
            getSupportActionBar().setTitle("About");
            getSupportFragmentManager().beginTransaction().replace(R.id.con, new aboutFragment()).commit();

        } else if (id == R.id.nav_share) {
            getSupportActionBar().setTitle("Share");

        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this,login.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview=navigationView.getHeaderView(0);
        TextView navname=headerview.findViewById(R.id.nav_name);
        TextView navemail=headerview.findViewById(R.id.nav_email);
        ImageView navuserphot=headerview.findViewById(R.id.nav_user_photo);

        navemail.setText(currentuser.getEmail());
        navname.setText(currentuser.getDisplayName());
      // for image load
        Glide.with(navigation.this).load(currentuser.getPhotoUrl()).into(navuserphot);
    }







}
