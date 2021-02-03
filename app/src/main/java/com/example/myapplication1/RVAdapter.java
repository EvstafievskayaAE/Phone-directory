package com.example.myapplication1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    private DBHelper dbHelper;

    private Context context;

    private List<Person> personsList;
    private ArrayList<Person> arrayList;

    private View view;

    private int editPosition;

    public RVAdapter(Context context, List<Person> personsList) {
        this.context = context;
        this.personsList = personsList;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(personsList);
    }

    /**
     * Инициализация элементов карточки (разметка)
     */
    static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView;
        Button editButton, deleteButton;

        public PersonViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.card_view, null);
        return new PersonViewHolder(view);
    }

    /**
     * Заполнение полей карточки. Вызов методов работы с ней
     */
    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        Person person = personsList.get(position);
        int idPerson = person.getID();
        dbHelper = new DBHelper(context);

        String fullName = String.format("%s %s %s",
                person.getLastName(), person.getName(), person.getMiddleName());

        holder.nameTextView.setText(fullName);
        holder.phoneTextView.setText(person.getPhone());

        holder.editButton.setOnClickListener(v -> {
            editPosition = position;
            showDialogFragment(view);
        });

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteContact(idPerson);
            personsList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            updateData(personsList);
            Toast.makeText(context, "deleted", Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Метод создания и вывода диалогового окна из адаптера
     */
    public void showDialogFragment(View view) {
        AddPersonFragment dialogFragment = new AddPersonFragment();
        dialogFragment.editButtonPressedFlag = true;
        dialogFragment.editPosition = editPosition;
        dialogFragment.searchPersonsList = personsList;
        MainActivity activity = ((MainActivity) view.getContext());
        dialogFragment.show(activity.getSupportFragmentManager(), null);
    }

    /**Подсчет количества контактов*/
    @Override
    public int getItemCount() {
        return personsList.size();
    }

    /**
     * Обновление карточки
     */
    public void updateData(List<Person> personsList) {
        this.personsList = personsList;
        notifyDataSetChanged();
    }

    /**
     * Реализация поиска по фамилии, имени или номеру телефона
     */
    public void searchFilter(String charText) {
        charText = charText.toLowerCase();
        personsList = dbHelper.getAllData();
        arrayList.clear();
        arrayList.addAll(personsList);
        personsList.clear();

        if (charText.length() == 0) {
            personsList.addAll(arrayList);
        } else {
            for (Person person : arrayList) {
                if (person.getLastName().toLowerCase().contains(charText)
                        || person.getName().toLowerCase().contains(charText)
                        || person.getPhone().toLowerCase().contains(charText)) {
                    personsList.add(person);
                }
            }
        }
        notifyDataSetChanged();
    }
}

