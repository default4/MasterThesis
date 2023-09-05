package com.example.masterrunning.presentation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.masterrunning.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends Fragment {

    private SharedPreferences sharedPreferences;
    private TextView eventDetails;
    private Button unselectEventButton;

    TextView selectedDateTextView;
    private MaterialCalendarView calendarView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendar, container, false);

        sharedPreferences = getContext().getSharedPreferences("calendar_events", Context.MODE_PRIVATE);
        String savedEvent = sharedPreferences.getString("selected_event", null);
        String savedDate = sharedPreferences.getString("selected_date", null);

        calendarView = view.findViewById(R.id.calendarView);

        CalendarDay selectedEventDate = null;
        if (savedDate != null) {
            String[] dateParts = savedDate.split("-");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int year = Integer.parseInt(dateParts[2]);
            selectedEventDate = CalendarDay.from(year, month, day);
        }

        List<CalendarDay> selectedEventDates = new ArrayList<>();
        if (selectedEventDate != null) {
            selectedEventDates.add(selectedEventDate);
        }

        if (!selectedEventDates.isEmpty()) {
            calendarView.addDecorator(new EventDecorator(Color.RED, selectedEventDates));
        }

        calendarView = view.findViewById(R.id.calendarView);
        eventDetails = view.findViewById(R.id.eventDetails);
        unselectEventButton = view.findViewById(R.id.unselectEventButton);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);

        // Set the event indicator for a particular date


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String selectedDate = date.getDay() + "-" + (date.getMonth() + 1) + "-" + date.getYear();
                showEventDialog(selectedDate);
            }
        });

        // Update event details based on saved data
        if (savedEvent != null && savedDate != null) {
            eventDetails.setText("Event on " + savedDate + ": " + savedEvent);
        }

        // Load saved event details
        updateEventDetails();

        // Button to unselect the event
        unselectEventButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("selected_event");
            editor.remove("selected_date");
            editor.apply();
            updateEventDetails();
        });

        return view;
    }

    private void showEventDialog(final String date) {
        String savedDate = sharedPreferences.getString("selected_date", null);
        String savedEvent = sharedPreferences.getString("selected_event", null);

        if (savedDate != null) {
            if (savedDate.equals(date)) {
                // Calculate the number of days between today and the saved event date
                String[] dateParts = savedDate.split("-");
                Calendar savedEventDate = Calendar.getInstance();
                savedEventDate.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]));

                Calendar today = Calendar.getInstance();
                long diff = savedEventDate.getTimeInMillis() - today.getTimeInMillis();
                long days = diff / (24 * 60 * 60 * 1000);

                // Get the saved event name
                savedEvent = sharedPreferences.getString("selected_event", "No event");

                // Create the message for the AlertDialog
                String message = "Event: " + savedEvent + "\nDays remaining: " + days + "\n\nWould you like to remove the existing event?";

                // Show dialog to remove the existing event
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("")
                        .setMessage(message)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("selected_event");
                            editor.remove("selected_date");
                            editor.apply();
                            selectedDateTextView.setText("");
                            selectedDateTextView.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Event removed", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                // Show toast for other dates
                Toast.makeText(getContext(), "Only 1 event can be selected", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        final String[] events = new String[]{
                "5K",
                "10K",
                "Marathon",
                "Ultramarathon"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an event for " + date)
                .setItems(events, (dialog, which) -> {
                    if (events[which].equals("Remove Event")) {
                        // Remove the event
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("selected_event");
                        editor.remove("selected_date");
                        editor.apply();
                        selectedDateTextView.setText("");
                        selectedDateTextView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Event removed", Toast.LENGTH_SHORT).show();
                    }
                    // Save the selected event, date, and days to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("selected_event", events[which]);
                    editor.putString("selected_date", date);


                    // Calculate the number of days between today and the selected date
                    String[] dateParts = date.split("-");
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]));

                    Calendar today = Calendar.getInstance();
                    long diff = selectedDate.getTimeInMillis() - today.getTimeInMillis();
                    long days = diff / (24 * 60 * 60 * 1000);

                    editor.putLong("days_from_today", days);
                    editor.apply();

                    // Update the event details TextView
                    eventDetails.setText("Event on " + date + ": " + events[which]);

                    Toast.makeText(getContext(), "Event set for " + date + ": " + events[which], Toast.LENGTH_SHORT).show();

                    // Navigate back to the previous Fragment or Activity
                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack();
                    }
                });

        builder.create().show();
        updateEventDetails();
    }

    private void updateEventDetails() {
        String savedEvent = sharedPreferences.getString("selected_event", null);
        String savedDate = sharedPreferences.getString("selected_date", null);
        if (savedEvent != null && savedDate != null) {
            // Highlight the saved date in red
            String[] dateParts = savedDate.split("-");
            CalendarDay day = CalendarDay.from(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]));
            calendarView.setDateSelected(day, true);
            calendarView.setSelectionColor(Color.RED);
            eventDetails.setText("Event on " + savedDate + ": " + savedEvent);
            eventDetails.setVisibility(View.VISIBLE);
            unselectEventButton.setVisibility(View.VISIBLE);
        } else {
            eventDetails.setVisibility(View.GONE);
            unselectEventButton.setVisibility(View.GONE);
        }
    }



}
