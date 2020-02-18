package org.slowcoders.serialize.android.preference;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.slowcoders.io.settings.AtomicOption;
import org.slowcoders.io.settings.MutableOption;
import org.slowcoders.io.settings.ObservableOption;
import org.slowcoders.serialize.R;
import org.slowcoders.serialize.android.preference.base.SCDialogFragment;
import org.slowcoders.serialize.sample.AndroidSettings;

import java.util.ArrayList;
import java.util.List;

import androidx.preference.PreferenceDialogFragmentCompat;

public class PersonDialogFragment extends SCDialogFragment {

    private EditText name;
    private EditText emailAddress1;
    private EditText emailAddress2;
    private EditText emailAddress3;
    private EditText age;
    private EditText dogName;
    private EditText dogAge;

    public PersonDialogFragment(String key, AtomicOption<AndroidSettings.Person> option) {
        super(key, option);
    }

    @Override
    protected View onCreateDialogView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.preference_fragment_person, null);

        name = view.findViewById(R.id.etx_person_name);
        emailAddress1 = view.findViewById(R.id.etx_person_emailAddress1);
        emailAddress2 = view.findViewById(R.id.etx_person_emailAddress2);
        emailAddress3 = view.findViewById(R.id.etx_person_emailAddress3);
        age = view.findViewById(R.id.etx_person_age);
        dogName = view.findViewById(R.id.etx_dog_name);
        dogAge = view.findViewById(R.id.etx_dog_age);

        ObservableOption option = getOption();
        AndroidSettings.Person person = (AndroidSettings.Person) option.get();
        if (person != null) {
            name.setText(person.getName());
            List<String> emails = person.getEmailAddresses();
            int i = 0;
            while (emails != null && emails.size() > i) {
                EditText v;
                if (i == 0) {
                    v = emailAddress1;
                } else if (i == 1) {
                    v = emailAddress2;
                } else {
                    v = emailAddress3;
                }
                v.setText(emails.get(i++));
            }
            age.setText(String.valueOf(person.getAge()));

            AndroidSettings.Person.Dog dog = person.getDog();
            if (dog != null) {
                dogName.setText(dog.getName());
                dogAge.setText(String.valueOf(dog.getAge()));
            }
        }
        return view;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            AndroidSettings.Person.Builder builder = new AndroidSettings.Person.Builder();
            builder.setName(name.getText().toString());

            ArrayList<String> list = new ArrayList<>();
            list.add(emailAddress1.getText().toString());
            list.add(emailAddress2.getText().toString());
            list.add(emailAddress3.getText().toString());
            builder.setEmailAddresses(list);

            String ageStr = age.getText().toString();
            if (ageStr.length() > 0) {
                builder.setAge(Integer.parseInt(ageStr));
            }

            AndroidSettings.Person.Dog.Builder dogBuilder = new AndroidSettings.Person.Dog.Builder();
            dogBuilder.setName(dogName.getText().toString());

            String dogAgeStr = dogAge.getText().toString();
            if (dogAgeStr.length() > 0) {
                dogBuilder.setAge(Integer.parseInt(dogAgeStr));
            }

            builder.setDog(dogBuilder.build());

            ObservableOption option = getOption();
            ((MutableOption)option).set(builder.build());
        }
    }
}
