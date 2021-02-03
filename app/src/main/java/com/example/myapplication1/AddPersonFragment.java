package com.example.myapplication1;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class AddPersonFragment extends DialogFragment {
    private static final int MIN_PHONE_LENGTH = 5;
    private static final int MAX_PHONE_LENGTH = 25;
    private static final int MAX_FIELD_LENGTH = 15;

    public int editPosition, editID; // позиция карточки и id в бд не совпадают из-за пропусков в нумерации при удалении контактов из бд

    public boolean editButtonPressedFlag = false; // флаг для определения, вызывается диалоговое окно добавления или редактирования
    public boolean checkPhoneFlag = false; // флаг для проверки совпадающих номеров

    private DBHelper dbHelper;
    private RVAdapter adapter;

    private static String lastNameInput, nameInput, middleNameInput, phoneInput;

    public EditText lastNameEditText, nameEditText, middleNameEditText, phoneEditText;
    public TextView lastNameTextView, nameTextView, middleNameTextView, phoneTextView;

    private Person person;

    private List<Person> personsList;
    public  List<Person> searchPersonsList;
    private List<String> phonesList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);
        return inflater.inflate(R.layout.add_person_fragment, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.add_person_fragment, null);

        lastNameEditText = mView.findViewById(R.id.lastNameEditText);
        nameEditText = mView.findViewById(R.id.nameEditText);
        middleNameEditText = mView.findViewById(R.id.middleNameEditText);
        phoneEditText = mView.findViewById(R.id.phoneEditText);

        lastNameTextView = mView.findViewById(R.id.lastNameTextView);
        nameTextView = mView.findViewById(R.id.nameTextView);
        middleNameTextView = mView.findViewById(R.id.middleNameTextView);
        phoneTextView = mView.findViewById(R.id.phoneTextView);

        lastNameEditText.addTextChangedListener(watcher);
        nameEditText.addTextChangedListener(watcher);
        phoneEditText.addTextChangedListener(watcher);

        lastNameEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_FIELD_LENGTH), filter});
        nameEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_FIELD_LENGTH), filter});
        middleNameEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_FIELD_LENGTH), filter});
        phoneEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_PHONE_LENGTH)});

        if (editButtonPressedFlag) {
            dbHelper = new DBHelper(getActivity());

            if (searchPersonsList != null)
                personsList = searchPersonsList;
            else
                personsList = dbHelper.getAllData();

            person = personsList.get(editPosition);

            editID = person.getID();
            lastNameEditText.setText(person.getLastName());
            nameEditText.setText(person.getName());
            middleNameEditText.setText(person.getMiddleName());
            phoneEditText.setText(person.getPhone());
        }

        final AlertDialog builder = new AlertDialog.Builder(getContext())
                .setTitle("Enter personal data")
                .setIcon(R.drawable.ic_person_add)
                .setView(mView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();
        builder.setOnShowListener(dialog -> {

            Button positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                dbHelper = new DBHelper(getActivity());

                lastNameInput = lastNameEditText.getText().toString();
                nameInput = nameEditText.getText().toString();
                middleNameInput = middleNameEditText.getText().toString();
                phoneInput = phoneEditText.getText().toString();

                checkPhone();

                if (lastNameInput.isEmpty() | nameInput.isEmpty() | phoneInput.isEmpty()
                        | phoneInput.length() < MIN_PHONE_LENGTH | checkPhoneFlag) {
                    checkField();
                } else if (editButtonPressedFlag) {
                    dbHelper.updateContact(editID, lastNameInput,
                            nameInput, middleNameInput, phoneInput);
                    Toast.makeText(getContext(),
                            "Contact is updated successfully", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    dbHelper.createNewContact(lastNameInput, nameInput,
                            middleNameInput, phoneInput);
                    String message = lastNameInput + " " + nameInput + " " +
                            middleNameInput + " " + phoneInput + " added successfully";
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                updateAdapter();
            });
        });
        return builder;
    }

    /**Обновление адаптера*/
    public void updateAdapter() {
        MainActivity activity = (MainActivity) getActivity();
        adapter = activity.adapter;
        personsList = dbHelper.getAllData();
        adapter.updateData(personsList);
    }

    /**Обработка заполнения полей. Вывод красной надписи при неверном заполнении,
     * удаление красной надписи, когда все верно*/
    TextWatcher watcher = new TextWatcher() {
        @Override
    public void beforeTextChanged(CharSequence editableText, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence editableText, int start, int before, int count) {
    }

    /**Проверка полей при изменении вводимого в них*/
    @Override
    public void afterTextChanged(Editable editableText) {
        if (editableText == lastNameEditText.getEditableText() && editableText.toString().isEmpty())
            lastNameTextView.setText("Please, enter Last Name");
        else lastNameTextView.setText("");
        if (editableText == nameEditText.getEditableText() && editableText.toString().isEmpty())
            nameTextView.setText("Please, enter Name");
        else nameTextView.setText("");
        if (editableText == phoneEditText.getEditableText()
                && editableText.toString().length() < MIN_PHONE_LENGTH)
            phoneTextView.setText("Phone number should contain no less 5 characters");
        else phoneTextView.setText("");
    }
    };

    /**Проверка полей на пустоту*/
    public  void  checkField(){
        if (lastNameInput.isEmpty()) lastNameTextView.setText("Please, enter Last Name");
        if (nameInput.isEmpty()) nameTextView.setText("Please, enter Name");
        if (phoneInput.isEmpty()) phoneTextView.setText("Please, enter phone");
        if (checkPhoneFlag) {
            phoneTextView.setText("This number already exists");
            checkPhoneFlag = false;
        }
    }

    /** Проверка, нет ли введенного номера в базе уже*/
    public void checkPhone(){
        phonesList = dbHelper.getAllPhones();
        for (String phone:phonesList) {
            if (!editButtonPressedFlag && phone.equals(phoneInput))
                checkPhoneFlag = true;
            else if (phone.equals(phoneInput) && !phone.equals(person.getPhone()))
                checkPhoneFlag = true;
        }
    }

    /**
     * Блокировка ввода иных символов кроме букв
     */
    InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (!Character.isLetter(source.charAt(i))) {
                return "";
            }
        }
        return null;

    };
}