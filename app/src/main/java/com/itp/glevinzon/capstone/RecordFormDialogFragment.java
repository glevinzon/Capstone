package com.itp.glevinzon.capstone;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle=this.getArguments();
        audioPath = bundle.getString("path");

        View rootView = inflater.inflate(R.layout.dialogfragment_recordform, container,
                false);
        getDialog().setTitle("New Record");

        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences pref = getContext().getSharedPreferences("CapstonePref", 0); // 0 - for private mode
        String tags = pref.getString("tags", null);
        Toast.makeText(getContext(), tags, Toast.LENGTH_LONG).show();

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

        Toast.makeText(getContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
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
        String note = inputNote.getText().toString().trim();

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
        String tag = multiTextAutoComplete.getText().toString().trim();

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