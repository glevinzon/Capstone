package com.itp.glevinzon.capstone;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.itp.glevinzon.capstone.api.CapstoneApi;
import com.itp.glevinzon.capstone.api.CapstoneService;
import com.itp.glevinzon.capstone.models.Equation;
import com.itp.glevinzon.capstone.utils.Utils;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Glevinzon on 4/30/2017.
 */

public class RecordFormDialogFragment extends DialogFragment {

    private MultiAutoCompleteTextView multiTextAutoComplete;

    private String audioPath = "";

    private static final String TAG = "RecordForm";

    private String[] cities ={""};

    private EditText inputName, inputNote;
    private TextInputLayout inputLayoutName, inputLayoutNote, inputLayoutTag;

    private RequestBody name, note, tags;

    ProgressDialog progressDialog;

    private CapstoneService equationService;

    private MultipartBody.Part fileToUpload;
    private RequestBody filename;

    private RequestBody username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle=this.getArguments();
        audioPath = bundle.getString("path");

        View rootView = inflater.inflate(R.layout.dialogfragment_recordform, container,
                false);
        getDialog().setTitle("New Record");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");

        equationService = CapstoneApi.getClient().create(CapstoneService.class);

        String str = Utils.readSharedSetting(getContext(), HomeActivity.PREF_USER_NAME, null);
        username = RequestBody.create(MediaType.parse("text/plain"), str);

        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences pref = getContext().getSharedPreferences("CapstonePref", 0); // 0 - for private mode
        String tags = pref.getString("tags", null);
//        Toast.makeText(getContext(), tags, Toast.LENGTH_LONG).show();

        inputLayoutName = (TextInputLayout) view.findViewById(R.id.equation_input_layout_name);
        inputLayoutNote = (TextInputLayout) view.findViewById(R.id.equation_input_layout_note);
        inputLayoutTag = (TextInputLayout) view.findViewById(R.id.equation_input_layout_tags);
        inputName = (EditText) view.findViewById(R.id.equation_input_name);
        inputNote = (EditText) view.findViewById(R.id.equation_input_note);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputNote.addTextChangedListener(new MyTextWatcher(inputNote));

        Button btnSave = (Button) view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getDialog().dismiss();
                submitForm();
//                Toast.makeText(getContext(), "Path: " + audioPath, Toast.LENGTH_LONG).show();
            }
        });

        List<String> myList = new ArrayList<String>(Arrays.asList(tags.split("\",\"")));

        ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1, myList);

        multiTextAutoComplete =(MultiAutoCompleteTextView) view.findViewById(R.id.multiAutoCompleteTextView1);
        multiTextAutoComplete.setAdapter(adapter);
        multiTextAutoComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        multiTextAutoComplete.addTextChangedListener(new MyTextWatcher(multiTextAutoComplete));
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateNote()) {
            return;
        }

        if (!validateTag()) {
            return;
        }

        progressDialog.show();

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(audioPath);

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        callSaveEquationApi().enqueue(new Callback<Equation>() {
            @Override
            public void onResponse(Call<Equation> call, Response<Equation> response) {
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Equation> call, Throwable t) {

            }
        });

        getDialog().dismiss();
    }

    private Call<Equation> callSaveEquationApi() {
        return equationService.saveEquation(
                fileToUpload,
                username,
                name,
                note,
                tags
        );
    }

    private boolean validateName() {
        String str = inputNote.getText().toString().trim();
        name = RequestBody.create(MediaType.parse("text/plain"), str);

        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateNote() {
        String str = inputNote.getText().toString().trim();
        note = RequestBody.create(MediaType.parse("text/plain"), str);

        if (inputNote.getText().toString().trim().isEmpty()) {
            inputLayoutNote.setError(getString(R.string.err_msg_note));
            requestFocus(inputNote);
            return false;
        } else {
            inputLayoutNote.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateTag() {
        String str = multiTextAutoComplete.getText().toString().trim();
//        str = str.replaceAll("\\s","");
        ArrayList<String> arrList = new ArrayList<String>(Arrays.asList(str.toString().split(",")));
//        Toast.makeText(getContext(), tags.toString(),Toast.LENGTH_LONG).show();
//        tag = RequestBody.create(MediaType.parse("text/plain"), str);
//        String temp = myList.toString();
//        tags = RequestBody.create(MediaType.parse("text/plain"), temp);
        JSONArray jsArray = new JSONArray(arrList);
        tags = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsArray.toString());

        if (multiTextAutoComplete.getText().toString().trim().isEmpty()) {
            inputLayoutTag.setError(getString(R.string.err_msg_note));
            requestFocus(multiTextAutoComplete);
            return false;
        } else {
            inputLayoutTag.setErrorEnabled(false);
        }

        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.equation_input_name:
                    validateName();
                    break;
                case R.id.equation_input_note:
                    validateNote();
                    break;
                case R.id.multiAutoCompleteTextView1:
                    validateNote();
                    break;
            }
        }
    }

    public static RecordFormDialogFragment newInstance() {
        return new RecordFormDialogFragment();
    }
}