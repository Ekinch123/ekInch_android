package com.skinfotech.ekinch.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PDFPrint;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.navigation.Navigation;

import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.CertificateBody;
import com.skinfotech.ekinch.models.requests.CommonRequest;
import com.skinfotech.ekinch.models.requests.ProfileRequest;
import com.skinfotech.ekinch.models.responses.CertificateResponce;
import com.skinfotech.ekinch.models.responses.CommonResponse;
import com.skinfotech.ekinch.models.responses.LocationResponse;
import com.skinfotech.ekinch.models.responses.ProfessionDatum;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.skinfotech.ekinch.views.MainActivity;
import com.skinfotech.ekinch.views.WebViewActivity;
import com.squareup.picasso.Picasso;
import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.skinfotech.ekinch.constants.Constants.USER_ID;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";
    private EditText mobileTextView, organizationET, locationET;
    private EditText aadharTextView;
    private EditText nameTextView;
    private TextView userNameToolbarTextView, professionET;
    private List<LocationResponse.LocationItem> mLocationList = new ArrayList<>();
    private List<ProfessionDatum> professions = new ArrayList<>();
    private TextView locationTextView;
    private String mLocationId = "";
    protected static String sProfilePhotoImagePath = "";
    protected static File sProfilePhotoFile = null;
    private ImageView mUserProfileImageView;
    private String mLastProfilePhotoStr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_profile, container, false);
        mContentView.findViewById(R.id.notificationImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.changePasswordTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.markSheetImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.userProfileImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.markSheetTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeBottomTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.logoutTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.updateProfileTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.locationSpinner).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.editUserProfileImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.shareImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.getCerificatTV).setOnClickListener(this);
        mContentView.findViewById(R.id.professionET).setOnClickListener(this);
        mUserProfileImageView = mContentView.findViewById(R.id.userProfileImageView);
        mobileTextView = mContentView.findViewById(R.id.mobileTextView);
        organizationET = mContentView.findViewById(R.id.organizationET);
        locationET = mContentView.findViewById(R.id.locationET);
        aadharTextView = mContentView.findViewById(R.id.aadharTextView);
        nameTextView = mContentView.findViewById(R.id.nameTextView);
        professionET = mContentView.findViewById(R.id.professionET);
        locationTextView = mContentView.findViewById(R.id.locationSpinner);
        userNameToolbarTextView = mContentView.findViewById(R.id.userNameTextView);
        fetchLocationsServerCall();
        showProgress();
        fetchProfile_();
        getProfession();
        return mContentView;
    }

    private void showListAlertDialog(final String[] list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getString(R.string.profession));
        builder.setItems(list, (dialogInterface, i) -> {
            mLocationId = mLocationList.get(i).getLocationId();
            locationTextView.setText(mLocationList.get(i).getLocationName());
            dialogInterface.dismiss();
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showProfessionAlertDialog(final String[] list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getString(R.string.please_select_location));
        builder.setItems(list, (dialogInterface, i) -> {
//            profession = mLocationList.get(i).getLocationId();
            professionET.setText(professions.get(i).getValue());
            dialogInterface.dismiss();
        });
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void fetchLocationsServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<LocationResponse> call = RetrofitApi.getAppServicesObject().fetchLocationsServerCall();
                    final Response<LocationResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    LocationResponse locationResponse = response.body();
                    if (locationResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(locationResponse.getErrorCode())) {
                            mLocationList = locationResponse.getLocationList();
                        } else {
                            showToast(locationResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notificationImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_profileFragment_to_notificationsFragment);
                break;
            case R.id.logoutTextView:
                showLogoutAlertDialog();
                break;
            case R.id.userProfileImageView:
                showImageDialog(mLastProfilePhotoStr);
                break;
            case R.id.shareImageView:
                shareApplication();
                break;
            case R.id.editUserProfileImageView:
                sProfilePhotoFile = null;
                sProfilePhotoImagePath = null;
                openImagePickerIntent();
                break;
            case R.id.changePasswordTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_profileFragment_to_changePasswordFragment);
                break;
            case R.id.homeBottomTextView:
            case R.id.homeImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_profileFragment_to_homeFragment);
                break;
            case R.id.updateProfileTextView:
                saveProfileServerCall();
                break;
            case R.id.locationSpinner:
                String[] locationArray = new String[mLocationList.size()];
                for (int position = 0; position < mLocationList.size(); position++) {
                    locationArray[position] = mLocationList.get(position).getLocationName();
                }
                showListAlertDialog(locationArray);
                break;
            case R.id.professionET:
                String[] professionArray = new String[professions.size()];
                for (int position = 0; position < professions.size(); position++) {
                    professionArray[position] = professions.get(position).getValue();
                }
                showProfessionAlertDialog(professionArray);
                break;
            case R.id.markSheetImageView:
            case R.id.markSheetTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_profileFragment_to_markSheetFragment);
                break;
            case R.id.libraryImageView:
            case R.id.libraryTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_profileFragment_to_libraryFragment);
                break;
            case R.id.getCerificatTV:
                getCertificate();
                break;
            default:
                break;
        }
    }

    private void showImageDialog(String imageStr) {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_dialog);
        ImageView imageView = dialog.findViewById(R.id.imageView);
        if (Utility.isEmpty(imageStr)) {
            return;
        } else {
            Picasso.get().load(imageStr).placeholder(R.drawable.logo).into(imageView);
        }
        dialog.show();
    }

    private void openImagePickerIntent() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                TakePictureFromCameraIntent();
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pick Image"), Constants.IImagePickConstants.GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void TakePictureFromCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            sProfilePhotoFile = null;
            try {
                sProfilePhotoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (sProfilePhotoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity,
                        "com.skinfotech.ekinch.android.fileprovider",
                        sProfilePhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.IImagePickConstants.CAMERA);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.IImagePickConstants.CAMERA) {
                setImageToBitmap();
                showProfilePhotoUploadDialog();
            } else if (requestCode == Constants.IImagePickConstants.GALLERY && null != data) {
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), selectedImage);
                    mUserProfileImageView.setImageBitmap(bitmap);
                    sProfilePhotoFile = new File(getRealPathFromURI(getImageUri(bitmap)));
                    showProfilePhotoUploadDialog();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    private void showProfilePhotoUploadDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity);
        builder.setMessage(getString(R.string.upload_image_message));
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> uploadFile());
        builder.setNegativeButton("No", (dialogInterface, i) -> {
            if (Utility.isEmpty(mLastProfilePhotoStr)) {
                Picasso.get().load(R.drawable.logo).into(mUserProfileImageView);
            } else {
                Picasso.get().load(mLastProfilePhotoStr).placeholder(R.drawable.logo).into(mUserProfileImageView);
            }
            dialogInterface.dismiss();
        });
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void uploadFile() {
        if (sProfilePhotoFile == null) {
            showToast("Something went wrong");
            return;
        }
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), sProfilePhotoFile);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", sProfilePhotoFile.getName(), requestBody);
                    RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), getStringDataFromSharedPref(USER_ID));
                    Call<ProfileResponse> call = RetrofitApi.getAppServicesObject().uploadProfilePhoto(fileToUpload, userIdBody);
                    final Response<ProfileResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    if (profileResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(profileResponse.getErrorCode())) {
                            setupProfileDetails(profileResponse);
                        }
                        showToast(profileResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap, "profile_photo", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        ContentResolver contentResolver = mActivity.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = contentResolver.query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void setImageToBitmap() {
        // Get the dimensions of the View
        int targetW = mUserProfileImageView.getWidth();
        int targetH = mUserProfileImageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sProfilePhotoImagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(sProfilePhotoImagePath, bmOptions);
        mUserProfileImageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        sProfilePhotoImagePath = image.getAbsolutePath();
        return image;
    }

    private void saveProfileServerCall() {
        String mobileStr = mobileTextView.getText().toString();
        String aadharStr = aadharTextView.getText().toString();
        String nameStr = nameTextView.getText().toString();
        if (Utility.isEmpty(aadharStr)) {
            aadharTextView.setError(getString(R.string.msg_mandatory));
            aadharTextView.requestFocus();
            return;
        }
        if (getResources().getInteger(R.integer.aadhar_length) != aadharStr.length()) {
            aadharTextView.setError(getString(R.string.not_valid_aadhar));
            aadharTextView.requestFocus();
            return;
        }
        if (Utility.isEmpty(nameStr)) {
            nameTextView.setError(getString(R.string.msg_mandatory));
            nameTextView.requestFocus();
            return;
        }

        if (Utility.isEmpty(locationET.getText().toString())){
            locationET.setError(getString(R.string.msg_mandatory));
            locationET.requestFocus();
        }
        ProfileRequest request = new ProfileRequest();
        request.setAadharNumber(aadharStr);
        request.setMobileNumber(mobileStr);
        request.setUserName(nameStr);
        request.setProfession(professionET.getText().toString());
        request.setLocationName(locationET.getText().toString());
        request.setLocationId(mLocationId);
        request.setOrganisationName(mLocationId);
        request.setOrganisationName(organizationET.getText().toString().trim());
        request.setUserId(getStringDataFromSharedPref(USER_ID));
        saveProfile(request);
    }

    private void saveProfile(ProfileRequest request) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().saveProfile(request);
                    final Response<CommonResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    CommonResponse commonResponse = response.body();
                    if (commonResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(commonResponse.getErrorCode())) {
                            Navigation.findNavController(mContentView).navigate(R.id.action_profileFragment_to_homeFragment);
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void getProfession() {
//        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<List<ProfessionDatum>> call = RetrofitApi.getAppServicesObject().getProfession();
                    final Response<List<ProfessionDatum>> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<List<ProfessionDatum>> response) {
                if (response.isSuccessful()) {
                    professions = response.body();
                }
//                stopProgress();
            }
        }).start();
    }

    private void getCertificate() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Call<List<CertificateResponce>> call = RetrofitApi.getAppServicesObject().getCertificate("24");
                    final Response<List<CertificateResponce>> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<List<CertificateResponce>> response) {
                if (response.isSuccessful()) {
                    createPdf("");
                }
                stopProgress();
            }
        }).start();
    }

    private void createPdf(String html) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        startActivity(intent);

        return;
//        final File savedPDFFile = FileManager.getInstance().createTempFile(getActivity().getApplicationContext(), "pdf", false);
// Generate Pdf From Html
//        PDFUtil.generatePDFFromHTML(getActivity().getApplicationContext(), savedPDFFile, " <!DOCTYPE html>\n" +
//                "<html>\n" +
//                "    <head>\n" +
//                "        <meta charset=\"UTF-8\">\n" +
//                "            <title>Example</title>\n" +
//                "        </head>\n" +
//                "        <body>\n" +
//                "            <h1>Hello</h1>\n" +
//                "        </body>\n" +
//                "    </html>", new PDFPrint.OnPDFPrintListener() {
//            @Override
//            public void onSuccess(File file) {
//                 Open Pdf Viewer
//                Uri pdfUri = Uri.fromFile(savedPDFFile);
//
//                Intent intentPdfViewer = new Intent(getActivity(), PDFViewerActivity.class);
//                intentPdfViewer.putExtra(PDFViewerActivity.PDF_FILE_URI, pdfUri);

//                startActivity(intentPdfViewer);
//            }
//
//            @Override
//            public void onError(Exception exception) {
//                exception.printStackTrace();
//            }
//        });
    }


    private void setupProfileDetails(ProfileResponse profileResponse) {
        String userName = profileResponse.getUserName();
        mobileTextView.setText(profileResponse.getMobileNumber());
        organizationET.setText(profileResponse.getOrganisatonName());
        mobileTextView.setEnabled(false);
        aadharTextView.setText(profileResponse.getAadharNumber());
        nameTextView.setText(userName);
        locationET.setText(profileResponse.getLocation_name());
        professionET.setText(profileResponse.getProfession());
        userNameToolbarTextView.setText(userName);
        locationTextView.setText(profileResponse.getLocationName());
        mLastProfilePhotoStr = profileResponse.getPhotoUrl();
        if (!mLastProfilePhotoStr.isEmpty()) {
            Picasso.get().load(mLastProfilePhotoStr).placeholder(R.drawable.logo).into(mUserProfileImageView);
        }
    }

    private void showLogoutAlertDialog() {
        new AlertDialog.Builder(mActivity)
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    storeStringDataInSharedPref(Constants.USER_LOGIN_DONE, Constants.NO);
                    Navigation.findNavController(mContentView).navigate(R.id.action_profileFragment_to_loginFragment);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void fetchProfile_() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProfileResponse> call = RetrofitApi.getAppServicesObject().fetchProfile(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<ProfileResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    if (profileResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(profileResponse.getErrorCode())) {
                            setupProfileDetails(profileResponse);
                        } else {
                            showToast(profileResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

}
