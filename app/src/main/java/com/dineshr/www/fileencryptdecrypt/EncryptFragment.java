package com.dineshr.www.fileencryptdecrypt;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.regex.Pattern;

public class EncryptFragment extends Fragment {
    Context mContext;
    FilePickerDialog dialog;
    String path;
    TextView p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_encrypt, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        {
            super.onActivityCreated(savedInstanceState);
            Button selectfile, encrypt;
             final EditText key;

            final TextView tvp;
            this.mContext = mContext;
            //View view = inflater.inflate(R.layout.key_layout, container, false);
            View v = getView();


            selectfile = (Button) v.findViewById(R.id.b_selectfile);
            encrypt = (Button) v.findViewById(R.id.b_decrypt);

            p = (TextView) v.findViewById(R.id.textView5);
            key = (EditText) v.findViewById(R.id.editText2);
            //edmsg = (EditText) v.findViewById(R.id.editText2);
            //tvpr = (TextView) v.findViewById(R.id.textView_pr);
            //final String finalPk = pk;
            //final String finalPrk = prk;


            selectfile.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
            //https://github.com/Angads25/android-filepicker
                    DialogProperties properties = new DialogProperties();
                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                    properties.selection_type = DialogConfigs.FILE_SELECT;
                    properties.root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());//DialogConfigs.DEFAULT_DIR
                    properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                    properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                    properties.extensions = null;
                     dialog = new FilePickerDialog(getActivity(), properties);
                    dialog.setTitle("Select a File");

                    dialog.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            //files is the array of the paths of files selected by the Application User.
                            path = files[0];
                            p.setText(path);
                        }
                    });
                    dialog.show();

                }
            });

            encrypt.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String pass = key.getText().toString().trim();
                    if(TextUtils.isEmpty(pass) || pass.length() < 16 || pass.length() > 16)
                    {
                        key.setError("You must have 16 characters key");
                        return;
                    }
                    if(TextUtils.isEmpty(path)){
                        Toast.makeText(getActivity(), "Please choose the File!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (key.getText().toString().length() != 0) {
                        String keyc = key.getText().toString();
                        File inputFile = new File(path); //new File("/storage/emulated/0/Download/Backup-codes-dineshr93.txt");//
                        /*String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                        Log.e("msg",baseDir);*/

                        String correctFileName;
                        if(Pattern.compile(Pattern.quote("decrypted_"), Pattern.CASE_INSENSITIVE).matcher(inputFile.getName()).find()) {

                            correctFileName = inputFile.getName().replace("decrypted_", "");
                        }else{
                            correctFileName=inputFile.getName();
                        }

                        File encryptedFile = new File(inputFile.getParent()+"/encrypted_"+correctFileName);
                        //File decryptedFile = new File("document.decrypted.srt");
                        int error =1;
                        try {
                            CryptoUtils.encrypt(keyc, inputFile, encryptedFile);
                            error =0;
                           // CryptoUtils.decrypt(keyc, encryptedFile, decryptedFile);
                        } catch (CryptoException ex) {
                            Toast.makeText(getActivity(), "Wrong Key!!", Toast.LENGTH_LONG).show();
                        }
                        if(error==0){
                            Toast.makeText(getActivity(), "Successfully encrypted!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Enter the key!", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(dialog!=null)
                    {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(getActivity(),"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }

        }
    }




}