package ie.app.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ie.app.models.Donation;

public class FirebaseAPI {

    static FirebaseDatabase database;
    static final String instance = "https://donation-ce0ba-default-rtdb.asia-southeast1.firebasedatabase.app";

    public static Task<List<Donation>> getAll(String call) {
        database = FirebaseDatabase.getInstance(instance);
        DatabaseReference ref = database.getReference(call);

        TaskCompletionSource<List<Donation>> taskCompletionSource = new TaskCompletionSource<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Donation> ret = new ArrayList<Donation>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Donation data = childSnapshot.getValue(Donation.class);
                    ret.add(data);
                }
                taskCompletionSource.setResult(ret);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setException(error.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    public static Donation get(String call, String donationId) {
        DatabaseReference ref = FirebaseDatabase.getInstance(instance).getReference(call).child(donationId);

        DataSnapshot dataSnapshot = null;
        try {
            dataSnapshot = Tasks.await(ref.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (dataSnapshot == null || !dataSnapshot.exists()) {
            // Không tìm thấy đối tượng Donation với ID tương ứng
            return null;
        } else {
            // Lấy thông tin của đối tượng Donation
            Donation donation = dataSnapshot.getValue(Donation.class);
            return donation;
        }
    }

    public static  String insert(String call, Donation donation) {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance(instance).getReference(call);
        String donationKey = donation._id;
        Log.v("Firebase", "donationKey:" + donationKey );
        donationsRef.child(donationKey).setValue(donation);
        return "added to clould";
    }

    public static String delete(String call, String donationId) {
        DatabaseReference ref = FirebaseDatabase.getInstance(instance).getReference(call).child(donationId);
        final String[] result = new String[1];
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Xóa đối tượng Donation thành công
                    result[0] = "Delete successful";
                    Log.d("TAG", "Donation deleted successfully");
                } else {
                    // Xóa đối tượng Donation thất bại
                    result[0] = "Delete failed";
                    Log.w("TAG", "Failed to delete donation", databaseError.toException());
                }
            }
        });
        return result[0];
    }

    public static String deleteAll(String call) {
        DatabaseReference ref = FirebaseDatabase.getInstance(instance).getReference(call);
        final String[] result = new String[1];

        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Xóa toàn bộ cơ sở dữ liệu thành công
                    result[0] = "Delete successful";
                    Log.d("TAG", "All data deleted successfully");
                } else {
                    result[0] = "Delete failed";
                    // Xóa toàn bộ cơ sở dữ liệu thất bại
                    Log.w("TAG", "Failed to delete all data", databaseError.toException());
                }
            }
        });

        return result[0];
    }
}
