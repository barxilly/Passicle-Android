package sbs.barxsmith.passicle;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import sbs.barxsmith.passicle.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;


    public String password;

    public Boolean show = false;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object acc = binding.service.getText();
                Object key = binding.key.getText();
                if (acc == null || key == null) {
                    Snackbar.make(view, "Please enter a service and a key", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.button)
                            .setAction("Action", null).show();
                } else {
                    String encrypted = encrypt(acc.toString().toLowerCase(), key.toString().toLowerCase());
                    password = encrypted;
                    // Set binding.generated to the encrypted password but replace the characters with *
                    binding.generated.setText(encrypted.replaceAll(".", "*"));
                    binding.showButton.setText("Show");
                    binding.copyButton.setVisibility(View.VISIBLE);
                    binding.showButton.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Copy the password to the clipboard
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", password);
                clipboard.setPrimaryClip(clip);
                // Send "Copied to clipboard" notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "passicle")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Copied to clipboard")
                        .setContentText("Your password has been copied to the clipboard")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
                notificationManager.notify(0, notification);
            }
        });

        binding.showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show) {
                    // Set binding.generated to the encrypted password but replace the characters with *
                    binding.generated.setText(password.replaceAll(".", "*"));
                    binding.showButton.setText("Show");
                    show = false;
                } else {
                    // Set binding.generated to the encrypted password
                    binding.generated.setText(password);
                    binding.showButton.setText("Hide");
                    show = true;
                }
            }
        });

        return binding.getRoot();

    }

    public String encrypt(String acc, String key) {
        String acceptable = "]ezdESiKJGPIX(*@m374=+Z6faRN\"2&y^9VqY8A1B`%hs.v}#L0pQD:o;C-x,!ul)bHU{wW/tF'r~Ong?c[_MjT$k5";
        String randomAcceptable = "3h*4c&P(H?dY}t=%!GXjT@{/pF1r7glOqf:SEubA5LB#vW[w)-R.V,U`zy\"e;ZN0]$6^mJ2'sxM9k+_~a8KinICoDQ";
        String encrypted = "";
        for (int i = 0; i < acc.length(); i++) {
            // Get the character
            char currentChar = acc.charAt(i);
            // Get the index of the character
            int index = acceptable.indexOf(currentChar);
            // Get the character at the index of the key
            char k = key.charAt(index % key.length());
            // Get the index of the key character
            int kindex = acceptable.indexOf(k);
            // Get the index of the new character
            int newindex = (index + kindex + i) % acceptable.length();
            // If the index is even get the character at the index of the randomAcceptable
            char newchar;
            if (i % 2 == 0) {
                newchar = acceptable.charAt(newindex);
            } else {
                // Else get the character at the index of the acceptable
                newchar = randomAcceptable.charAt(newindex);
            }
            // Add the new character to the result
            encrypted += newchar;
        }
        // Add the character with the length of the key in acceptable
        encrypted += acceptable.charAt(key.length());
        // Add the character with the length of the data in acceptable
        encrypted += acceptable.charAt(acc.length());
        // Add the character with the length of the sum of both in acceptable
        encrypted += acceptable.charAt(key.length() + acc.length());
        // Add the character with the length of the result in acceptable
        encrypted += acceptable.charAt(encrypted.length());
        // Return the result
        return encrypted;
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}