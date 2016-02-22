package com.example.kurt.tipcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String currency;
    EditText editBill;
    EditText editTip;
    EditText editNumPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /* Setup bill TextEdit */
        currency = prefs.getString("currency", "$");
        editBill = (EditText) findViewById(R.id.edit_bill);
        editBill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editBill.removeTextChangedListener(this);

                String oldText = editBill.getText().toString();
                String value = oldText.replace(currency, "");
                String newText = value.length() != 0 ? currency + value : "";

                editBill.setText(newText);
                editBill.setSelection(newText.length());
                editBill.addTextChangedListener(this);

                calculateTip();
            }
        });

        /* Setup rating bar */
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                float tip = (int) (10 + ratingBar.getRating() * 2);
                editTip.setText(Double.toString(tip) + "%");
            }
        });

        /* Setup tip TextEdit */
        editTip = (EditText) findViewById(R.id.edit_tip);
        editTip.setText(prefs.getString("default_tip", "15") + "%");
        editTip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editTip.removeTextChangedListener(this);

                String oldText = editTip.getText().toString();
                String value = oldText.replace("%", "");
                String newText = value.length() != 0 ? value + "%" : "";

                editTip.setText(newText);
                editTip.setSelection(Math.max(newText.length() - 1, 0));
                editTip.addTextChangedListener(this);

                calculateTip();
            }
        });

        ImageButton minusTip = (ImageButton) findViewById(R.id.minus_tip);
        minusTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double value;
                try {
                    String text = editTip.getText().toString().replace("%", "");
                    value = Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    value = 0.0;
                }

                editTip.setText(Double.toString(Math.max(value - 1, 0)));
            }
        });

        ImageButton plusTip = (ImageButton) findViewById(R.id.plus_tip);
        plusTip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double value;
                try {
                    String text = editTip.getText().toString().replace("%", "");
                    value = Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    value = 0.0;
                }

                editTip.setText(Double.toString(value + 1));
            }
        });

        editNumPeople = (EditText) findViewById(R.id.edit_num_people);
        editNumPeople.setText("1");

        editNumPeople.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateTip();
            }
        });

        ImageButton minusNumPeople = (ImageButton) findViewById(R.id.minus_num_people);
        minusNumPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numPeople;
                try {
                    String numPeopleText = editNumPeople.getText().toString();
                    numPeople = Integer.parseInt(numPeopleText);
                } catch (NumberFormatException e) {
                    numPeople = 0;
                }

                editNumPeople.setText(Integer.toString(Math.max(numPeople - 1, 1)));
            }
        });

        ImageButton plusNumPeople = (ImageButton) findViewById(R.id.plus_num_people);
        plusNumPeople.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int numPeople;
                try {
                    String numPeopleText = editNumPeople.getText().toString();
                    numPeople = Integer.parseInt(numPeopleText);
                } catch (NumberFormatException e) {
                    numPeople = 1;
                }

                editNumPeople.setText(Integer.toString(numPeople + 1));
            }
        });

        TextView total = (TextView) findViewById(R.id.total);
        total.setText(currency + "0.00");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void calculateTip() {
        double bill, tip, numPeople;

        try {
            String billText = editBill.getText().toString().replace(currency, "");
            bill = Double.parseDouble(billText);
        } catch (NumberFormatException e) {
            bill = 0.0;
        }

        try {
            String tipText = editTip.getText().toString().replace("%", "");
            tip = Double.parseDouble(tipText);
        } catch (NumberFormatException e) {
            tip = 0.0;
        }

        try {
            String numPeopleText = editNumPeople.getText().toString();
            numPeople = Double.parseDouble(numPeopleText);
        } catch (NumberFormatException e) {
            numPeople = 1;
        }

        double total = bill * (1.0 + tip / 100);

        TextView textTotal = (TextView) this.findViewById(R.id.total);
        textTotal.setText(String.format("%s%.2f", currency, total));

        TextView totalSplit = (TextView) this.findViewById(R.id.total_split);
        if (numPeople > 1) {
            totalSplit.setText(String.format("%s%.2f each", currency, total / numPeople));
        } else {
            totalSplit.setText("");
        }
    }

    public void openSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}