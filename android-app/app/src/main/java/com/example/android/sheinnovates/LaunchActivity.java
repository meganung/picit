package com.example.android.sheinnovates;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_MULTIPLE = 1001;
    private Uri imageUri;
    private static final String TAG = "LaunchActivity";

    //fake data i used before but not really anymore i dont think
    public static ArrayList<JSONObject> imagesdata = new ArrayList<JSONObject>();
    public static JSONObject imagesjsonobj = new JSONObject();

    //the real data
    public static ArrayList<ImageData> thedata = new ArrayList<ImageData>();
    public static CheckLabels thelabels = new CheckLabels();
    // references to our images
    public static Integer[] mThumbIds = {
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5,
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5,
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5,
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5
    };
    public static ArrayList<Uri> mThumbUris = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent myIntent = new Intent(LaunchActivity.this, MainActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                LaunchActivity.this.startActivity(myIntent);
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click upload image
                startChooseImageIntentForResult();

            }
        });

    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            List<String> imagesEncodedList = new ArrayList<String>();
            if(data.getData()!=null) {
                imageUri = data.getData();
                // Get the cursor
                Cursor cursor = getContentResolver().query(imageUri,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imageEncoded = cursor.getString(columnIndex);
                cursor.close();

                mThumbUris.add(imageUri);
                ImageData theimage = new ImageData(imageUri);
                thedata.add(theimage);
                processing(theimage);

            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mThumbUris.add(uri);
                        ImageData theimage = new ImageData(uri);
                        thedata.add(theimage);
                        processing(theimage);

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imageEncoded  = cursor.getString(columnIndex);
                        imagesEncodedList.add(imageEncoded);
                        cursor.close();
                    }
                    Log.v("LOG_TAG", "Selected Images" + mThumbUris.size());
                }
            }
            gotoGallery();
        }
    }

    private void gotoGallery() {

        Intent myIntent = new Intent(LaunchActivity.this, MainActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        LaunchActivity.this.startActivity(myIntent);
    }

    private void processing(final ImageData imageData){
        FirebaseApp.initializeApp(this);
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageData.uri);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            getLabels(imageData,image);
            getFaceDetection(imageData,image);

            // microsoft face api

            final String apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
            final String subscriptionKey = "800728d646444ac785808f3b7e47da09";

            final FaceServiceClient faceServiceClient =
                    new FaceServiceRestClient(apiEndpoint, subscriptionKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream((outputStream.toByteArray()));

            AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
                private ProgressDialog pd = new ProgressDialog(LaunchActivity.this);

                @Override
                protected Face[] doInBackground(InputStream... inputStreams) {

                    publishProgress("Detecting...");
                    FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]{
                            FaceServiceClient.FaceAttributeType.HeadPose,
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.FacialHair
                    };

                    try {
                        Face[] result = faceServiceClient.detect(inputStreams[0],
                                true,
                                false,
                                faceAttr);

                        if (result == null) {
                            publishProgress("Detection Finished. Nothing Detected.");
                            return null;
                        }

                        publishProgress(String.format("Detection Finished. %d face(s) detected",
                                result.length));
                        return result;
                    } catch (Exception e) {
                        publishProgress("Detection Failed");
                        return null;
                    }

                }

                @Override
                protected void onPreExecute() {
                    pd.show();
                }

                @Override
                protected void onPostExecute(Face[] faces) {
                    pd.dismiss();
                    Gson gson =  new Gson();
                    String data = gson.toJson(faces);
                    if (data.length() > 2) {

                        int gender_index = data.indexOf("gender");
                        int gender_index_end = data.indexOf(",", gender_index);

                        String gender_data = "";
                        if ((gender_index + 9 < data.length()) && (gender_index_end - 1 < data.length())){
                            gender_data = data.substring(gender_index + 9, gender_index_end - 1);
                        }
                        imageData.setGender(gender_data);

                        float gender_factor = (float) 0.0;
                        if (gender_data == "female")
                        {
                            gender_factor = (float) 1.0;
                        }

                        int smile_index = data.indexOf("smile");
                        int smile_index_end = data.indexOf(",", smile_index);

                        float smile_data = (float) 0;
                        if ((smile_index + 7 < data.length()) && (smile_index_end - 1 < data.length()))
                        {
                            smile_data = Float.parseFloat(data.substring(smile_index + 7, smile_index_end - 1));
                        }

                        float currentScore = imageData.score;
                        float newScore = (float)(currentScore + (gender_factor + smile_data) / 1.0 * 2.0);

                        imageData.score = newScore;
                    }



                }

                @Override
                protected void onProgressUpdate(String... values) {
                    pd.setMessage(values[0]);
                }
            };

            detectTask.execute(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap image");
        }

        imageData.processed = true;
        Log.e("thedata",thedata.toString());
        Log.e("thedata-inside",thedata.get(0).labels.toString());
    }
    private void getLabels(final ImageData imageData, FirebaseVisionImage image) {
        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();
        FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance()
                .getVisionCloudLabelDetector(options);
        Task<List<FirebaseVisionCloudLabel>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                                        // Task completed successfully
                                        JSONObject labeljsonobj = new JSONObject();
                                        for (FirebaseVisionCloudLabel label : labels) {
                                            String text = label.getLabel();
                                            //String entityid = label.getEntityId();
                                            float confidence = label.getConfidence();
                                            if (confidence > 0.7) {
                                                imageData.addLabel(new Label(text, confidence));
                                                try {
                                                    labeljsonobj.put(label.getLabel(), confidence);
                                                } catch (JSONException e) {
                                                    //failed
                                                }
                                                Log.e("labelprocessing", text + confidence);
                                            }
                                        }


                                    }

                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...

                                    }
                                });
    }
    private void getFaceDetection(final ImageData imageData, FirebaseVisionImage image) {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        float smileProb = 0;
                                        float eyeOpenProb = 0;
                                        for (FirebaseVisionFace face : faces) {
                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                smileProb = smileProb + face.getSmilingProbability();
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                eyeOpenProb = eyeOpenProb + face.getRightEyeOpenProbability();
                                            }
                                            if (face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                eyeOpenProb = eyeOpenProb + face.getLeftEyeOpenProbability();
                                            }

                                        }
                                        float qualityscore = 0;
                                        Log.e("faces size: ",Integer.toString(faces.size()));
                                        if (faces.size() != 0) {
                                            smileProb = smileProb / faces.size();
                                            eyeOpenProb = eyeOpenProb / (2 * faces.size());
                                            qualityscore = 2*smileProb + eyeOpenProb;
                                        }
                                        Log.e("SCORE",Float.toString(qualityscore));

                                        imageData.score = imageData.score + qualityscore;
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }

}

